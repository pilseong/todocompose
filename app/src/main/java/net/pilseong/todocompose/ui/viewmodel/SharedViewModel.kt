package net.pilseong.todocompose.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.data.repository.DataStoreRepository
import net.pilseong.todocompose.data.repository.TodoRepository
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants.MAX_TITLE_LENGTH
import net.pilseong.todocompose.util.ScreenMode
import net.pilseong.todocompose.util.SearchAppBarState
import net.pilseong.todocompose.util.SortOption
import net.pilseong.todocompose.util.StreamState
import net.pilseong.todocompose.util.TaskAppBarState
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    /**
     * 화면의 state 를 관리 하는 변수들 선언
     */
    // 현재 스트림 을 저장 한다.
    private var currentStream by mutableStateOf(StreamState.DEFAULT)
    var screenMode by mutableStateOf(ScreenMode.NORMAL)

    // 명령어 의 흐름이 다른 경우 별도의 변수를 사용 하였다.
    // 맨 처음 로딩 시에 date store 에서 받아온 값으로 정렬을 하는데
    // 현재 3개의 값을 저장 하고 있다. observer 들은 이전 값에 변동이 없는 경우는 그리지 않는데
    // 맨 처음 에는 저장된 값이 같더 라도 그려 줘야 한다.
    private var firstFetch = false
    // 아래 두 변수는 snack bar 를 그려 줄 때 현재 Action 에 대한 처리를 하는데
    // 상태 가 필요한 경우 에는 그 상태 를 받아 와서 보여 주어야 한다.
    // date store 에 저장된 경우는 persist 하고 읽는 것 까지 시간이 걸리기 때문에
    // 엑션이 일어난 경우 바로 알 수 있도록 처리를 해 주어야 한다.
    var snackBarOrderEnabled = false
    var snackBarDateEnabled = false

    // 현재 보여 지거나 수정 중인 인덱스 가지고 있는 변수
    var index by mutableStateOf(0)
        private set

    fun updateIndex(index: Int) {
        this.index = index
    }

    // 화면 인덱스 이동 - delay 를 준 것은 swipeToDismiss 에서 swipe animation 구동 시에
    // 전환 된 화면이 화면에 표출 되는 것을 막기 위함
    fun incrementIndex() {
        Log.i("PHILIP", "[SharedViewModel] index: $index, snapshot ${snapshotTasks.size}")
        if (this.index < snapshotTasks.size - 1) {
            viewModelScope.launch {
                delay(100)
                index++
                Log.i("PHILIP", "[SharedViewModel] incrementIndex $index")
            }
        }
    }

    fun decrementIndex() {
        if (this.index > 0) {
            viewModelScope.launch {
                delay(300)
                index--
                Log.i("PHILIP", "[SharedViewModel] decrementIndex $index")
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

        Log.i("PHILIP", "refreshAllTasks condition with ${condition.ordinal}")
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.getAllTasks(
                query = searchTextString,
                sortCondition = condition.ordinal,
                priority = prioritySortState
            )
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Lazily,
                    initialValue = PagingData.empty()
                )
                .cachedIn(viewModelScope)
                .collect {
                    tasks.value = it
                    currentStream = StreamState.DEFAULT
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


    // sort property 를 읽어 온다. 읽으면 _prioritySortState 가 변경 된댜.
    fun observePrioritySortState() {
        Log.i("PHILIP", "[SharedViewModel] observePrioritySortState() executed")
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            dataStoreRepository.readPrioritySortState
                .map { Priority.valueOf(it) }
                .collect { priority ->
                    if (priority != prioritySortState || !firstFetch) {
                        if (!firstFetch) firstFetch = true

                        prioritySortState = priority
                        Log.i(
                            "PHILIP",
                            "[SharedViewModel] refreshAllTasks() executed with priority $prioritySortState"
                        )
                        refreshAllTasks()
                    }

                }
        }
    }

    // sort property 를 읽어 온다. 읽으면 _prioritySortState 가 변경 된댜.
    fun observeOrderEnabledState() {
        Log.i("PHILIP", "[SharedViewModel] observeOrderEnabledState() executed")
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            dataStoreRepository.readOrderEnabledState
                .map { it.toBoolean() }
                .collect { state ->
                    if (state != orderEnabled || !firstFetch) {
                        if (!firstFetch) firstFetch = true

                        orderEnabled = state
                        Log.i(
                            "PHILIP",
                            "[SharedViewModel] refreshAllTasks() executed with orderEnabled $orderEnabled"
                        )
                        refreshAllTasks()
                    }
                }
        }
    }

    // sort property 를 읽어 온다. 읽으면 _prioritySortState 가 변경 된댜.
    fun observeDateEnabledState() {
        Log.i("PHILIP", "[SharedViewModel] observeDateEnabledState() executed")
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            dataStoreRepository.readDateEnabledState
                .map { it.toBoolean() }
                .collect { state ->
                    if (state != dateEnabled || !firstFetch) {
                        if (!firstFetch) firstFetch = true
                        dateEnabled = state

                        Log.i(
                            "PHILIP",
                            "[SharedViewModel] refreshAllTasks() executed with dateEnabled $dateEnabled"
                        )
                        refreshAllTasks()
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

    var tasks = MutableStateFlow<PagingData<TodoTask>>(PagingData.empty())
        private set

    var action by mutableStateOf(Action.NO_ACTION)
        private set

    fun updateAction(action: Action) {
        this.action = action
        Log.i("PHILIP", "[SharedViewModel] updateAction to ${action.name}")
    }

    // 오직 action 이 실행 되었을 경우를 구분 하기 위한 변수
// view model 의 action 으로는 같은 action 이 두번 실행된 경우 확인할 수 없다.
// 오직 가드 로만 활용 한다.
    var actionPerformed by mutableStateOf(Random.Default.nextBytes(4))
        private set

    private fun updateActionPerformed() {
        this.actionPerformed = Random.nextBytes(4)
    }

    // list screen 에 있는 search bar 의 표시 상태를 저장 하는 변수
    val searchAppBarState: MutableState<SearchAppBarState> =
        mutableStateOf(SearchAppBarState.CLOSE)

    var taskAppBarState by mutableStateOf(TaskAppBarState.VIEWER)
        private set

    fun setTaskScreenToEditorMode() {
        taskAppBarState = TaskAppBarState.EDITOR
    }

    fun setTaskScreenToViewerMode() {
        taskAppBarState = TaskAppBarState.VIEWER
    }

    var searchTextString by mutableStateOf("")

    var id by mutableStateOf(-1)
        private set
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var priority by mutableStateOf(Priority.LOW)
    var createdAt: ZonedDateTime by mutableStateOf(ZonedDateTime.now())
    var updatedAt: ZonedDateTime by mutableStateOf(ZonedDateTime.now())

    var selectedTask by mutableStateOf(TodoTask(-1, "", "", Priority.NONE))
        private set

    fun updateSelectedTask(task: TodoTask) {
        selectedTask = task
        updateTaskContent(task)
    }


    /**
     * 이벤트 함수들 정의
     */

    private fun updateTaskContent(todoTask: TodoTask?) {
        Log.i("PHILIP", "[SharedViewModel] updateTaskContent with $todoTask")
        // null 이면 새로 todoTask 를 만드는 요청 이므로 초기화 한다.
        if (todoTask == null) {
            id = 0
            title = ""
            description = ""
            priority = Priority.LOW
            createdAt = ZonedDateTime.now()
            updatedAt = ZonedDateTime.now()
        } else {
            id = todoTask.id
            title = todoTask.title
            description = todoTask.description
            priority = todoTask.priority
            createdAt = todoTask.createdAt
            updatedAt = todoTask.updatedAt
        }
    }

    fun updateTitle(newTitle: String) {
        if (newTitle.length <= MAX_TITLE_LENGTH) {
            title = newTitle
        }
    }

    fun handleActions(
        action: Action,
        todoId: Int = id,
        priority: Priority = Priority.NONE,
        sortOrderEnabled: Boolean = false,
        sortDateEnabled: Boolean = false
    ) {
        Log.i("PHILIP", "[SharedViewModel] handleActions performed with $action $priority")
        when (action) {
            Action.ADD -> {
                addTask()
                refreshStream()
                updateActionPerformed()
            }

            Action.UPDATE -> {
                updateTask()
                refreshStream()
                updateActionPerformed()
            }

            Action.DELETE -> {
                deleteTask(todoId)
                refreshStream()
                updateActionPerformed()
            }

            Action.DELETE_ALL -> {
                deleteAllTasks()
                refreshStream()
                updateActionPerformed()
            }

            Action.UNDO -> {
                undoTask()
                refreshStream()
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

    // 현재 스트림 을 기준 으로 목록을 갱신 한다.
    private fun refreshStream() {
        when (currentStream) {
            StreamState.DEFAULT -> refreshAllTasks()
            StreamState.SEARCH -> refreshAllTasks()
            StreamState.PRIORITY -> refreshAllTasks()
        }
    }

    private fun addTask() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i(
                "PHILIP",
                "[SharedViewModel] addTask performed with $title, $description, $priority"
            )
            todoRepository.addTask(TodoTask(0, title, description, priority))
        }
        this.action = Action.ADD
    }

    private fun undoTask() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.addTask(TodoTask(0, title, description, priority, createdAt = createdAt))
        }
        this.action = Action.UNDO
    }

    private fun updateTask() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.updateTask(
                TodoTask(
                    id,
                    title,
                    description,
                    priority,
                    createdAt = createdAt
                )
            )
        }
        this.action = Action.UPDATE
    }

    private fun deleteTask(todoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.deleteTask(todoId)
        }
        this.action = Action.DELETE
    }

    private fun deleteAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.deleteAllTasks()
        }
        this.action = Action.DELETE_ALL
    }
}