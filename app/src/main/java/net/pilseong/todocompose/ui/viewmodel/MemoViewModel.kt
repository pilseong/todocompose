package net.pilseong.todocompose.ui.viewmodel

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.data.repository.DataStoreRepository
import net.pilseong.todocompose.data.repository.NotebookRepository
import net.pilseong.todocompose.data.repository.TodoRepository
import net.pilseong.todocompose.data.repository.ZonedDateTypeAdapter
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants.MAX_TITLE_LENGTH
import net.pilseong.todocompose.util.Constants.NEW_ITEM_ID
import net.pilseong.todocompose.util.ScreenMode
import net.pilseong.todocompose.util.SearchAppBarState
import net.pilseong.todocompose.util.SortOption
import net.pilseong.todocompose.util.TaskAppBarState
import java.io.File
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class MemoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val notebookRepository: NotebookRepository,
    private val dataStoreRepository: DataStoreRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var tasks = MutableStateFlow<PagingData<TodoTask>>(PagingData.empty())
        private set

    var selectedNotebook = mutableStateOf(Notebook.instance())
    var readyFlagForNotebookId = false
    var readyFlagForPriority = false
    var readyFlagForDateEnabled = false
    var readyFlagForOrderEnabled = false
    var readyFlagForFavorite = false

    /**
     * 화면의 state 를 관리 하는 변수들 선언
     */

    // 화면 갱신이 필요 하기 때문에 state 로 관리 해야 한다.
    var sortFavorite by mutableStateOf(false)

    // 현재 보여 지거나 수정 중인 인덱스 가지고 있는 변수
    var index by mutableStateOf(0)
        private set

    // list screen 에 있는 search bar 의 표시 상태를 저장 하는 변수
    val searchAppBarState = mutableStateOf(SearchAppBarState.CLOSE)

    var taskAppBarState by mutableStateOf(TaskAppBarState.VIEWER)
        private set

    // 오직 action 이 실행 되었을 경우를 구분 하기 위한 변수
    // view model 의 action 으로는 같은 action 이 두번 실행된 경우 확인할 수 없다.
    // 오직 가드 로만 활용 한다.
    var actionPerformed by mutableStateOf(Random.Default.nextBytes(4))
        private set

    var searchTextString by mutableStateOf("")

    // 수정 및 신규 메모 작성에 사용할 변수
    var id by mutableStateOf(NEW_ITEM_ID)
        private set
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var priority by mutableStateOf(Priority.LOW)
    var notebookId by mutableStateOf(-1)
    private var createdAt = ZonedDateTime.now()
    private var updatedAt = ZonedDateTime.now()


    fun updateIndex(index: Int) {
        this.index = index
    }

    // 화면 인덱스 이동 - delay 를 준 것은 swipeToDismiss 에서 swipe animation 구동 시에
    // 전환 된 화면이 화면에 표출 되는 것을 막기 위함
    fun incrementIndex() {
        Log.i("PHILIP", "[MemoViewModel] index: $index, snapshot ${snapshotTasks.size}")
        if (this.index < snapshotTasks.size - 1) {
            viewModelScope.launch {
                delay(100)
                index++
                Log.i("PHILIP", "[MemoViewModel] incrementIndex $index")
            }
        }
    }

    fun decrementIndex() {
        if (this.index > 0) {
            viewModelScope.launch {
                delay(300)
                index--
                Log.i("PHILIP", "[MemoViewModel] decrementIndex $index")
            }
        }
    }

    // 명령어 의 흐름이 다른 경우 별도의 변수를 사용 하였다.
    // 맨 처음 로딩 시에 date store 에서 받아온 값으로 정렬을 하는데
    // 현재 3개의 값을 저장 하고 있다. observer 들은 이전 값에 변동이 없는 경우는 그리지 않는데
    // 맨 처음 에는 저장된 값이 같더 라도 그려 줘야 한다.
    var firstFetch = true

    // 아래 두 변수는 snack bar 를 그려 줄 때 현재 Action 에 대한 처리를 하는데
    // 상태 가 필요한 경우 에는 그 상태 를 받아 와서 보여 주어야 한다.
    // date store 에 저장된 경우는 persist 하고 읽는 것 까지 시간이 걸리기 때문에
    // 엑션이 일어난 경우 바로 알 수 있도록 처리를 해 주어야 한다.
    var snackBarOrderEnabled = false
    var snackBarDateEnabled = false

    var startDate: Long? = null
    var endDate: Long? = null

    val selectedItems = mutableStateListOf<Int>()

    fun appendMultiSelectedItem(id: Int) {
        selectedItems.add(id)
    }

    fun removeMultiSelectedItem(id: Int) {
        selectedItems.remove(id)
    }

    fun getNotebooks() {
        Log.i("PHILIP", "getNotebooks")
        viewModelScope.launch(Dispatchers.IO) {
            notebookRepository.getNotebooks()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Lazily,
                    initialValue = emptyList()
                )
                .collect {
                    notebooks.value = it
                }
        }
    }

    fun refreshAllTasks() {
        val condition = if (orderEnabled) {
            if (dateEnabled) {
                SortOption.CREATED_AT_ASC
            } else {
                SortOption.UPDATED_AT_ASC
            }
        } else {
            if (dateEnabled) {
                SortOption.CREATED_AT_DESC
            } else {
                SortOption.UPDATED_AT_DESC
            }
        }

        Log.i(
            "PHILIP",
            "[MemoViewModel] refreshAllTasks condition with ${condition.ordinal}, notebook_id: $notebookIdState"
        )
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.getTasks(
                query = searchTextString,
                sortCondition = condition.ordinal,
                priority = prioritySortState,
                notebookId = notebookIdState,
                startDate = startDate,
                endDate = endDate,
                isFavoriteOn = sortFavorite
            )
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Lazily,
                    initialValue = PagingData.empty()
                )
                .cachedIn(viewModelScope)
                .collect {
                    tasks.value = it
                }
        }
    }

    // 공유 정보를 위해서 사용 한다
    // 위의 allTasks 의 snapshot 이 저장 된다.
    var snapshotTasks by mutableStateOf(emptyList<TodoTask>())
        private set

    fun updateSnapshotTasks(tasks: List<TodoTask>) {
        snapshotTasks = tasks
    }

    // for reading the saved priority sort state
    var prioritySortState by mutableStateOf(Priority.NONE)
    var orderEnabled by mutableStateOf(false)
    var dateEnabled by mutableStateOf(false)
    var notebookIdState by mutableStateOf(Int.MIN_VALUE)

    var notebooks = MutableStateFlow<List<Notebook>>(emptyList())

    // sort property 를 읽어 온다. 읽으면 _prioritySortState 가 변경 된댜.
    fun observePrioritySortState() {
        Log.i("PHILIP", "[MemoViewModel] observePrioritySortState() executed")
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            dataStoreRepository.readPrioritySortState
                .map { Priority.valueOf(it) }
                .collect { priority ->

                    if (firstFetch) {
                        prioritySortState = priority
                        if (readyFlagForNotebookId && readyFlagForDateEnabled && readyFlagForFavorite && readyFlagForOrderEnabled) {
                            firstFetch = false
                            refreshAllTasks()
                        } else {
                            readyFlagForPriority = true
                        }
                    } else {
                        if (priority != prioritySortState) {
                            Log.i(
                                "PHILIP",
                                "[MemoViewModel] refreshAllTasks() executed with priority $prioritySortState"
                            )
                            prioritySortState = priority
                            refreshAllTasks()
                        }
                    }
                }
        }
    }

    // sort property 를 읽어 온다. 읽으면 _prioritySortState 가 변경 된댜.
    fun observeOrderEnabledState() {
        Log.i("PHILIP", "[MemoViewModel] observeOrderEnabledState() executed")
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            dataStoreRepository.readOrderEnabledState
                .map { it.toBoolean() }
                .collect { state ->
                    if (firstFetch) {
                        orderEnabled = state
                        if (readyFlagForNotebookId && readyFlagForDateEnabled && readyFlagForFavorite && readyFlagForPriority) {
                            firstFetch = false
                            refreshAllTasks()
                        } else {
                            readyFlagForOrderEnabled = true
                        }
                    } else {
                        if (state != orderEnabled) {
                            Log.i(
                                "PHILIP",
                                "[MemoViewModel] refreshAllTasks() executed with orderEnabled $orderEnabled"
                            )
                            orderEnabled = state
                            refreshAllTasks()
                        }
                    }
                }
        }
    }

    // sort property 를 읽어 온다. 읽으면 _prioritySortState 가 변경 된댜.
    fun observeDateEnabledState() {
        Log.i("PHILIP", "[MemoViewModel] observeDateEnabledState() executed")
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            dataStoreRepository.readDateEnabledState
                .map { it.toBoolean() }
                .collect { state ->
                    if (firstFetch) {
                        dateEnabled = state
                        if (readyFlagForNotebookId && readyFlagForOrderEnabled && readyFlagForFavorite && readyFlagForPriority) {
                            firstFetch = false
                            refreshAllTasks()
                        } else {
                            readyFlagForDateEnabled = true
                        }
                    } else {
                        if (state != dateEnabled) {
                            Log.i(
                                "PHILIP",
                                "[MemoViewModel] refreshAllTasks() executed with dateEnabled $dateEnabled"
                            )
                            dateEnabled = state
                            refreshAllTasks()
                        }
                    }
                }
        }
    }

    fun observeNotebookIdChange() {
        Log.i("PHILIP", "[MemoViewModel] observeNotebookIdChange() executed")
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            dataStoreRepository.readSelectedNotebookId
                .map { it }
                .collect { state ->
                    if (firstFetch) {
                        notebookIdState = state
                        if (readyFlagForFavorite && readyFlagForOrderEnabled && readyFlagForDateEnabled && readyFlagForPriority) {
                            firstFetch = false
                            refreshAllTasks()
                        } else {
                            readyFlagForNotebookId = true
                        }
                    } else {
                        if (state != notebookIdState) {
                            Log.i(
                                "PHILIP",
                                "[MemoViewModel] refreshAllTasks() executed with notebookId: $notebookIdState"
                            )
                            notebookIdState = state
                            refreshAllTasks()
                        }
                    }
                    getNotebook(state)
                }
        }
    }

    private fun getNotebook(id: Int) {
        // -1 이면 기본 노트 선택 title 이 설정되어야 하기 때문에 titile 를 지정해 준다.
        if (id == -1) selectedNotebook.value =
                Notebook.instance(context.resources.getString(R.string.default_note_title))
        else selectedNotebook.value = notebookRepository.getNotebook(id);
    }

    fun observeFavoriteState() {
        Log.i("PHILIP", "[MemoViewModel] observeFavoriteState() executed")
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            dataStoreRepository.readFavoriteState
                .map { it.toBoolean() }
                .collect { state ->

                    if (firstFetch) {
                        sortFavorite = state
                        if (readyFlagForNotebookId && readyFlagForOrderEnabled && readyFlagForDateEnabled && readyFlagForPriority) {
                            firstFetch = false
                            refreshAllTasks()
                        } else {
                            readyFlagForFavorite = true
                        }
                    } else {
                        if (state != sortFavorite) {
                            Log.i(
                                "PHILIP",
                                "[MemoViewModel] refreshAllTasks() executed with sortFavorite $sortFavorite"
                            )
                            sortFavorite = state
                            refreshAllTasks()
                        }
                    }
                }
        }
    }

    // for saving priority sort state
    private fun persistPrioritySortState(priority: Priority) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistPrioritySortState(priority)
        }
    }

    // for saving priority sort state
    private fun persistOrderEnabledState(orderEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistOrderEnabledState(orderEnabled)
        }
    }

    // for saving priority sort state
    private fun persistDateEnabledState(dateEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistDateEnabledState(dateEnabled)
        }
    }

    private fun persistFavoriteEnabledState(favorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistFavoriteEnabledState(favorite)
        }
    }

    private fun persistNotebookIdState(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistSelectedNotebookId(id)
        }
    }

    // action 은 실시간 으로 감시 하는 state 가 되면 안 된다. 처리 후 NONE 으로 변경 해야
    // 중복 메시지 수신 에도 이벤트 를 구분 할 수 있다.
    var action = Action.NO_ACTION
        private set

    fun updateAction(action: Action) {
        this.action = action
        Log.i("PHILIP", "[MemoViewModel] updateAction to ${action.name}")
    }

    private fun updateActionPerformed() {
        this.actionPerformed = Random.nextBytes(4)
    }

    fun setTaskScreenToEditorMode(task: TodoTask = TodoTask.instance(notebookIdState)) {
        taskAppBarState = TaskAppBarState.EDITOR
        copySelectedTaskToEditFields(task)
    }

    private fun copySelectedTaskToEditFields(task: TodoTask) {
        id = task.id
        title = task.title
        description = task.description
        priority = task.priority
        notebookId = task.notebookId
        createdAt = task.createdAt
        updatedAt = task.updatedAt
    }

    fun setTaskScreenToViewerMode() {
        taskAppBarState = TaskAppBarState.VIEWER
    }

    /**
     * 이벤트 함수들 정의
     */

    fun updateTitle(newTitle: String) {
        if (newTitle.length <= MAX_TITLE_LENGTH) {
            title = newTitle
        }
    }

    fun handleActions(
        action: Action,
        todoTask: TodoTask = TodoTask.instance(),
        priority: Priority = Priority.NONE,
        sortOrderEnabled: Boolean = false,
        sortDateEnabled: Boolean = false,
        startDate: Long? = null,
        endDate: Long? = null,
        favorite: Boolean = false,
        notebookId: Int = -1
    ) {
        Log.i(
            "PHILIP",
            "[MemoViewModel] handleActions performed with $action priority: $priority favorite: $favorite"
        )
        when (action) {
            Action.ADD -> {
                addTask()
                updateActionPerformed()
            }

            Action.UPDATE -> {
                updateTask()
                refreshAllTasks()
                updateActionPerformed()
            }

            Action.DELETE -> {
                deleteTask(snapshotTasks[index].id)
                updateActionPerformed()
            }

            Action.DELETE_ALL -> {
                deleteAllTasks()
                updateActionPerformed()
            }

            Action.DELETE_SELECTED_ITEMS -> {
                deleteSelectedTasks()
                updateActionPerformed()
            }

            Action.UNDO -> {
                undoTask()
                updateActionPerformed()
            }

            Action.FAVORITE_UPDATE -> {
                updateFavorite(todoTask)
                // favorite 모드가 활성화 되었을 때만 favorite 삭제시 리프레시
            }

            Action.SEARCH_WITH_DATE_RANGE -> {
                this.startDate = startDate
                this.endDate = endDate
                updateAction(action)

                refreshAllTasks()
                updateActionPerformed()
            }

            // 우선 순위 변화는 좀 신경을 써야 한다.
            Action.PRIORITY_CHANGE -> {
                updateAction(action)
                // 변경 될 설정이 NONE 인 경우는 all tasks 가 보여져야 한다.
                if (priority != prioritySortState) {
                    persistPrioritySortState(priority)
                    updateActionPerformed()
                }
            }

            Action.SORT_ORDER_CHANGE -> {
                updateAction(action)
                // 변경 될 설정이 NONE 인 경우는 all tasks 가 보여 져야 한다.
                if (sortOrderEnabled != orderEnabled) {
                    snackBarOrderEnabled = sortOrderEnabled
                    persistOrderEnabledState(sortOrderEnabled)
                    updateActionPerformed()
                }
            }

            Action.SORT_DATE_CHANGE -> {
                updateAction(action)
                // 변경 될 설정이 NONE 인 경우는 all tasks 가 보여져야 한다.
                if (sortDateEnabled != dateEnabled) {
                    snackBarDateEnabled = sortDateEnabled
                    persistDateEnabledState(sortDateEnabled)
                    updateActionPerformed()
                }
            }

            Action.SORT_FAVORITE_CHANGE -> {
                updateAction(action)
                persistFavoriteEnabledState(favorite)
                if (favorite) {
                    updateActionPerformed()
                }
            }

            Action.NOTEBOOK_CHANGE -> {
                updateAction(action)
                persistNotebookIdState(notebookId)
            }

            Action.NO_ACTION -> {
                this.action = Action.NO_ACTION
            }
        }
    }

    // 검색 app bar 가 닫힐 때 설정된 우선 순위에 따른 결과가 나와야 한다.
    fun onCloseSearchBar() {
        searchTextString = ""
        searchAppBarState.value = SearchAppBarState.CLOSE
        refreshAllTasks()
    }

    private fun addTask() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i(
                "PHILIP",
                "[MemoViewModel] addTask performed with $title, $description, $priority $notebookIdState"
            )
            todoRepository.addTask(
                TodoTask(
                    0,
                    title,
                    description,
                    priority,
                    notebookId = notebookIdState
                )
            )
            refreshAllTasks()
        }
        this.action = Action.ADD
    }

    private fun undoTask() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.addTask(
                TodoTask(
                    0,
                    title,
                    description,
                    priority,
                    notebookId = notebookId,
                    createdAt = createdAt
                )
            )
            refreshAllTasks()
        }
        this.action = Action.UNDO
    }

    private fun updateFavorite(todo: TodoTask) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.updateFavorite(todo.copy(favorite = !todo.favorite))
            // 화면을 리 프레시 하는 타이밍 도 중요 하다. 업데이트 가 완료된  후에 최신 정보를 가져와야 한다.
            if (sortFavorite) refreshAllTasks()
        }
    }

    private fun updateTask() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.updateTask(
                TodoTask(
                    id,
                    title,
                    description,
                    priority,
                    notebookId = notebookId,
                    createdAt = createdAt
                )
            )

        }
        this.action = Action.UPDATE
    }

    private fun deleteTask(todoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.deleteTask(todoId)
            refreshAllTasks()
        }
        this.action = Action.DELETE
    }

    private fun deleteAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.deleteAllTasks()
            refreshAllTasks()
        }
        this.action = Action.DELETE_ALL
    }

    private fun deleteSelectedTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.deleteSelectedTasks(selectedItems)
            selectedItems.clear()
            refreshAllTasks()
        }
        this.action = Action.DELETE_SELECTED_ITEMS
    }

    private var gson = GsonBuilder()
        .registerTypeAdapter(
            ZonedDateTime::class.java,
            ZonedDateTypeAdapter()
        )
        .serializeNulls().setPrettyPrinting().create()

    fun handleImport(uri: Uri?) {
        val item = if (uri != null) context.contentResolver.openInputStream(uri) else null
        val bytes = item?.readBytes()

        if (bytes != null) {
            val memoString = String(bytes, Charsets.UTF_8)
            val dbTables = memoString.split("pilseong")

            val memoListType = object : TypeToken<List<TodoTask>>() {}.type
            val noteListType = object : TypeToken<List<Notebook>>() {}.type

            val memos = gson.fromJson<List<TodoTask>>(dbTables[0], memoListType)
            val notes = gson.fromJson<List<Notebook>>(dbTables[1], noteListType)

            Log.i("PHILIP", "[MemoViewModel] handleImport uri: $uri, size of data: ${memos.size}")

            viewModelScope.launch(Dispatchers.IO) {
                todoRepository.insertMultipleMemos(memos)
                notebookRepository.insertMultipleNotebooks(notes)
            }
        }

        item?.close()
    }

    fun exportData() {
        viewModelScope.launch(Dispatchers.IO) {
            val allMemoData = todoRepository.getAllTasks()
            var memoJson = gson?.toJson(allMemoData)
            val filename = "memos.txt"

            val allNotes = notebookRepository.getAllNotebooks()
            val noteJson = gson?.toJson(allNotes)

            memoJson += "pilseong$noteJson"

            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(memoJson?.toByteArray())
            }

            val todos = File(context.filesDir, filename)
            val contentUri = getUriForFile(context, "net.pilseong.fileprovider", todos)

            sendEmail(contentUri)
        }
    }

    private fun sendEmail(file: Uri) {
        Log.i("PHILIP", "Sending Log ...###### ")
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Export Memos")
        intent.putExtra(Intent.EXTRA_STREAM, file)
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "This is an autogenerated message \n The attachment file includes all the memos in JSON format"
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  //  외부 에서 열려고 할 때
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // provider 를 통한 파일 제공
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        try {
            context.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Log.i("PHILIP", "No Intent matcher found")
        }
    }
}