package net.pilseong.todocompose.ui.viewmodel

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Photo
import net.pilseong.todocompose.data.model.ui.DefaultNoteMemoCount
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.data.repository.DataStoreRepository
import net.pilseong.todocompose.data.repository.NotebookRepository
import net.pilseong.todocompose.data.repository.TodoRepository
import net.pilseong.todocompose.data.repository.ZonedDateTypeAdapter
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants.NEW_ITEM_ID
import net.pilseong.todocompose.util.NoteSortingOption
import net.pilseong.todocompose.util.SearchAppBarState
import net.pilseong.todocompose.util.SortOption
import net.pilseong.todocompose.util.StateEntity
import net.pilseong.todocompose.util.TaskAppBarState
import net.pilseong.todocompose.util.deleteFileFromUri
import java.io.File
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.math.abs
import kotlin.random.Random


@HiltViewModel
class MemoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val notebookRepository: NotebookRepository,
    private val dataStoreRepository: DataStoreRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var openDialog by mutableStateOf(false)
    var infoDialogTitle by mutableStateOf(R.string.info_import_fail_title)
    var infoDialogContent by mutableStateOf(R.string.info_import_fail_content)
    var infoDialogCDismissLabel by mutableStateOf(R.string.info_dialog_dismiss_label)


    @Stable
    var tasks = MutableStateFlow<PagingData<MemoWithNotebook>>(PagingData.empty())
        private set

    var selectedNotebook = mutableStateOf(Notebook.instance())

    var progressVisible by mutableStateOf(false)

    /**
     * 화면의 state 를 관리 하는 변수들 선언
     */

    // 현재 보여 지거나 수정 중인 인덱스 가지고 있는 변수
    var index by mutableStateOf(0)

    // list screen 에 있는 search bar 의 표시 상태를 저장 하는 변수
    var searchAppBarState by mutableStateOf(SearchAppBarState.CLOSE)

    var taskAppBarState by mutableStateOf(TaskAppBarState.VIEWER)
        private set

    // 오직 action 이 실행 되었을 경우를 구분 하기 위한 변수
    // view model 의 action 으로는 같은 action 이 두번 실행된 경우 확인할 수 없다.
    // 오직 가드 로만 활용 한다.
    var actionPerformed by mutableStateOf(Random.Default.nextBytes(4))
        private set

    var searchTextString by mutableStateOf("")
    var searchRangeAll by mutableStateOf(false)
    var searchNoFilterState by mutableStateOf(false)    // 검색 시 모든 필터 제거
    var defaultNoteMemoCount by mutableStateOf(DefaultNoteMemoCount(0, 0, 0, 0, 0))

    // snack 바에 결과를 보여주기 위해서 마지막 action의 상태를 저장한다.
    var savedLastMemoTask = MemoTask.instance()

    fun updateIndex(index: Int) {
        this.index = index
    }

    // 화면 인덱스 이동 - delay 를 준 것은 swipeToDismiss 에서 swipe animation 구동 시에
    // 전환 된 화면이 화면에 표출 되는 것을 막기 위함
    fun incrementIndex() {
        viewModelScope.launch {
            delay(100)
            index++
            Log.d("PHILIP", "[MemoViewModel] incrementIndex $index")
        }
    }

    fun decrementIndex() {
        viewModelScope.launch {
            delay(100)
            index--
            Log.d("PHILIP", "[MemoViewModel] decrementIndex $index")
        }
    }

    // 명령어 의 흐름이 다른 경우 별도의 변수를 사용 하였다.
    // 맨 처음 로딩 시에 date store 에서 받아온 값으로 정렬을 하는데
    // 현재 3개의 값을 저장 하고 있다. observer 들은 이전 값에 변동이 없는 경우는 그리지 않는데
    // 맨 처음 에는 저장된 값이 같더 라도 그려 줘야 한다.
//    var firstFetch = true

    // 아래 두 변수는 snack bar 를 그려 줄 때 현재 Action 에 대한 처리를 하는데
    // 상태 가 필요한 경우 에는 그 상태 를 받아 와서 보여 주어야 한다.
    // date store 에 저장된 경우는 persist 하고 읽는 것 까지 시간이 걸리기 때문에
    // 엑션이 일어난 경우 바로 알 수 있도록 처리를 해 주어야 한다.
    var snackBarOrderEnabled = false
    var snackBarDateEnabled = false

    var startDate: Long? = null
    var endDate: Long? = null

    val selectedItems = mutableStateListOf<Long>()


    fun appendMultiSelectedItem(id: Long) {
        if (selectedItems.contains(id)) {
            selectedItems.remove(id)
        } else {
            selectedItems.add(id)
        }
    }

    fun removeMultiSelectedItem(id: Long) {
        selectedItems.remove(id)
    }


    fun getDefaultNoteCount() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.getMemoCount(-1).collectLatest {
                defaultNoteMemoCount = it
            }
        }
    }

    fun refreshAllTasks() {
        Log.d(
            "PHILIP",
            "[MemoViewModel] refreshAllTasks condition with ${uiState.dateOrderState}, notebook_id: $uiState.notebookIdState"
        )
        viewModelScope.launch {
            todoRepository.getTasks(
                query = searchTextString,
                searchNoFilterState = searchNoFilterState,
                searchRangeAll = searchRangeAll,
                sortCondition = uiState.dateOrderState.ordinal,
                priority = uiState.prioritySortState,
                notebookId = uiState.notebookIdState,
                startDate = startDate,
                endDate = endDate,
                isFavoriteOn = uiState.sortFavorite,
                stateCompleted = uiState.stateCompleted,
                stateCancelled = uiState.stateCancelled,
                stateActive = uiState.stateActive,
                stateSuspended = uiState.stateSuspended,
                stateWaiting = uiState.stateWaiting,
                stateNone = uiState.stateNone,
                priorityHigh = uiState.priorityHigh,
                priorityMedium = uiState.priorityMedium,
                priorityLow = uiState.priorityLow,
                priorityNone = uiState.priorityNone,
            ).cachedIn(viewModelScope)
                .collectLatest {
                    Log.d(
                        "PHILIP",
                        "[MemoViewModel] refreshAllTasks how many"
                    )
                    tasks.value = it
                    if (progressVisible) {
                        progressVisible = false
                    }
                }
        }
    }

    var notebooks = notebookRepository.getNotebooksAsFlow(NoteSortingOption.ACCESS_AT)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private suspend fun getNotebook(id: Long) {
        // -1 이면 기본 노트 선택 title 이 설정 되어야 하기 때문에 title 를 지정해 준다.
        if (id == -1L) selectedNotebook.value =
            Notebook.instance(title = context.resources.getString(R.string.default_note_title))
        else selectedNotebook.value = notebookRepository.getNotebook(id)
    }


    private suspend fun applyStatusLineOrder(statusEntity: StateEntity) {
        val state = uiState.statusLineOrderState.toMutableList()

        Log.d("PHILIP", "before status order ${state.indexOf(statusEntity)}")
        state.remove(statusEntity)
        state.add(0, statusEntity)

        dataStoreRepository.persistStatusLineOrderState(state)
    }


    // for saving priority sort state
    private fun persistPrioritySortState(priority: Priority, statusLineOrderUpdate: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.persistPrioritySortState(priority)

            if (statusLineOrderUpdate)
                applyStatusLineOrder(StateEntity.PRIORITY_ORDER)
        }
    }

    // for saving date sort state
    private fun persistDateOrderState(dateOrderState: SortOption, statusLineOrderUpdate: Boolean) {
        viewModelScope.launch {

            Log.d(
                "PHILIP",
                "before ${uiState.dateOrderState.ordinal} after ${dateOrderState.ordinal} ${
                    abs(uiState.dateOrderState.ordinal - dateOrderState.ordinal)
                }"
            )
            if (statusLineOrderUpdate) {
                if (abs(uiState.dateOrderState.ordinal - dateOrderState.ordinal) > 1) {
                    applyStatusLineOrder(StateEntity.DATE_BASE_ORDER)
                } else {
                    applyStatusLineOrder(StateEntity.SORTING_ORDER)
                }
            }

            dataStoreRepository.persistDateOrderState(dateOrderState.ordinal)

        }
    }

    // for saving favorite filter state
    private fun persistFavoriteEnabledState(favorite: Boolean, statusLineOrderUpdate: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.persistFavoriteEnabledState(favorite)

            if (statusLineOrderUpdate)
                applyStatusLineOrder(StateEntity.FAVORITE_FILTER)
        }
    }

    private fun persistStateState(state: Int) {
        viewModelScope.launch {
            dataStoreRepository.persistStateState(state)
        }
    }


    private fun persistPriorityFilterState(state: Int, statusLineOrderUpdate: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.persistPriorityFilterState(state)

            if (statusLineOrderUpdate)
                applyStatusLineOrder(StateEntity.PRIORITY_FILTER)
        }
    }


    private fun persistNotebookIdState(id: Long) {
        if (uiState.notebookIdState != id) {
            val noteIdsList = mutableListOf<String>()
            noteIdsList.add(id.toString())
            noteIdsList.add(uiState.notebookIdState.toString())

            if (uiState.firstRecentNotebookId != null) {
                noteIdsList.add(uiState.firstRecentNotebookId.toString())
            }

            viewModelScope.launch {
                dataStoreRepository.persistRecentNoteIds(noteIdsList)
                updateActionPerformed()
            }
        }
    }

    // action 은 실시간 으로 감시 하는 state 가 되면 안 된다. 처리 후 NONE 으로 변경 해야
    // 중복 메시지 수신 에도 이벤트 를 구분 할 수 있다.
    var action = Action.NO_ACTION
        private set

    fun updateAction(action: Action) {
        this.action = action
        Log.d("PHILIP", "[MemoViewModel] updateAction to ${action.name}")
    }

    private fun updateActionPerformed() {
        this.actionPerformed = Random.nextBytes(4)
    }

    fun setTaskScreenToEditorMode(task: MemoWithNotebook = MemoWithNotebook.instance(notebookId = uiState.notebookIdState)) {
        taskAppBarState = TaskAppBarState.EDITOR
        updateUiState(
            if (task.memo.id == NEW_ITEM_ID) TaskDetails().copy(notebookId = uiState.notebookIdState)
            else task.toTaskDetails()
        )
    }


    fun setTaskScreenToViewerMode(task: MemoTask = MemoTask.instance(notebookId = uiState.notebookIdState)) {
        taskAppBarState = TaskAppBarState.VIEWER
        updateUiState(task.toTaskDetails())
    }


    fun handleActions(
        action: Action,
        memo: MemoTask = MemoTask.instance(),
        photos: List<Photo> = emptyList(),
        priority: Priority = Priority.NONE,
        sortOrderEnabled: Boolean = false,
        sortDateEnabled: Boolean = false,
        statusLineOrderUpdate: Boolean = false,
        startDate: Long? = null,
        endDate: Long? = null,
        favorite: Boolean = false,
        notebookId: Long = -1,
        state: State = State.NONE,
        stateEntity: StateEntity = StateEntity.NOTE_FILTER,
        searchRangeAll: Boolean = false,
        stateInt: Int = 0,
    ) {
        Log.d(
            "PHILIP",
            "[MemoViewModel] handleActions performed with $action"
        )
        when (action) {
            Action.ADD -> {
                addTask()
                updateActionPerformed()
            }

            // 업데이트를 실행할 때 원래 사진 리스트와 수정할 사진 리스트를 비교해야 한다.
            // photos는 원래 사진 리스트를 가지고 있다.
            Action.UPDATE -> {
                updateTask(photos)
                updateActionPerformed()
            }

            Action.DELETE -> {
                deleteTask(memo)
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

            Action.MOVE_TO -> {
                updateAction(action)
                moveToTask(notebookId)
                updateActionPerformed()
            }

            Action.COPY_TO -> {
                updateAction(action)
                copyToTask(notebookId)
                updateActionPerformed()
            }

            Action.FAVORITE_UPDATE -> {
                updateFavorite(memo)
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
                // 변경 될 설정이 NONE 인 경우는 all tasks 가 보여 져야 한다.
                if (priority != uiState.prioritySortState) {
                    persistPrioritySortState(priority, statusLineOrderUpdate)
                    updateActionPerformed()
                }
            }

            Action.SORT_ORDER_CHANGE -> {
                Log.d(
                    "PHILIP",
                    "[MemoViewModel] handleActions performed with $uiState.dateOrderState, $sortOrderEnabled"
                )
                updateAction(action)
                when (uiState.dateOrderState) {
                    SortOption.UPDATED_AT_DESC -> {
                        if (sortOrderEnabled) {
                            snackBarOrderEnabled = true
                            persistDateOrderState(SortOption.UPDATED_AT_ASC, statusLineOrderUpdate)
                            updateActionPerformed()
                        }
                    }

                    SortOption.UPDATED_AT_ASC -> {
                        if (!sortOrderEnabled) {
                            snackBarOrderEnabled = false
                            persistDateOrderState(SortOption.UPDATED_AT_DESC, statusLineOrderUpdate)
                            updateActionPerformed()
                        }
                    }

                    SortOption.CREATED_AT_DESC -> {
                        if (sortOrderEnabled) {
                            snackBarOrderEnabled = true
                            persistDateOrderState(SortOption.CREATED_AT_ASC, statusLineOrderUpdate)
                            updateActionPerformed()
                        }
                    }

                    SortOption.CREATED_AT_ASC -> {
                        if (!sortOrderEnabled) {
                            snackBarOrderEnabled = false
                            persistDateOrderState(SortOption.CREATED_AT_DESC, statusLineOrderUpdate)
                            updateActionPerformed()
                        }
                    }
                }
            }

            Action.SORT_DATE_CHANGE -> {
                Log.d(
                    "PHILIP",
                    "[MemoViewModel] handleActions performed with ${uiState.dateOrderState}, $sortDateEnabled"
                )
                updateAction(action)
                when (uiState.dateOrderState) {
                    SortOption.UPDATED_AT_DESC -> {
                        if (sortDateEnabled) {
                            snackBarDateEnabled = true
                            persistDateOrderState(SortOption.CREATED_AT_DESC, statusLineOrderUpdate)
                            updateActionPerformed()
                        }
                    }

                    SortOption.UPDATED_AT_ASC -> {
                        if (sortDateEnabled) {
                            snackBarDateEnabled = true
                            persistDateOrderState(SortOption.CREATED_AT_ASC, statusLineOrderUpdate)
                            updateActionPerformed()
                        }
                    }

                    SortOption.CREATED_AT_DESC -> {
                        if (!sortDateEnabled) {
                            snackBarDateEnabled = false
                            persistDateOrderState(SortOption.UPDATED_AT_DESC, statusLineOrderUpdate)
                            updateActionPerformed()
                        }
                    }

                    SortOption.CREATED_AT_ASC -> {
                        if (!sortDateEnabled) {
                            snackBarDateEnabled = false
                            persistDateOrderState(SortOption.UPDATED_AT_DESC, statusLineOrderUpdate)
                            updateActionPerformed()
                        }
                    }
                }
            }

            Action.SORT_FAVORITE_CHANGE -> {
                updateAction(action)
                persistFavoriteEnabledState(favorite, statusLineOrderUpdate)
                if (favorite) {
                    updateActionPerformed()
                }
            }

            Action.NOTEBOOK_CHANGE -> {
                updateAction(action)
                persistNotebookIdState(notebookId)
                updateActionPerformed()
            }

            Action.STATE_FILTER_CHANGE -> {
                val result = stateBinaryCalculation(state)
                persistStateState(result)
            }

            Action.STATE_MULTIPLE_FILTER_CHANGE -> {
                persistStateState(stateInt)
            }

            Action.STATE_CHANGE -> {
                updateState(memo, state)
            }

            Action.STATE_CHANGE_MULTIPLE -> {
                updateStateForMultiple(state)
            }

            Action.PRIORITY_FILTER_CHANGE -> {
                val result = priorityBinaryCalculation(priority)
                persistPriorityFilterState(result, statusLineOrderUpdate)
            }

            Action.SEARCH_NO_FILTER_CHANGE -> {
                updateSearchNoFilterState(searchRangeAll)
                updateActionPerformed()
            }

            Action.SEARCH_RANGE_CHANGE -> {
                updateAction(action)
                updateSearchRange(searchRangeAll, statusLineOrderUpdate)
                updateActionPerformed()
            }

            Action.STATUS_LINE_UPDATE -> {
                viewModelScope.launch {
                    applyStatusLineOrder(stateEntity)
                }
            }

            Action.NO_ACTION -> {
                this.action = Action.NO_ACTION
            }
        }
    }

    private fun stateBinaryCalculation(state: State): Int {
        val result = when (state) {
            State.NONE -> {
                if ((uiState.stateState and 1) == 1)
                    uiState.stateState - 1
                else
                    uiState.stateState + 1
            }

            State.WAITING -> {
                if ((uiState.stateState and 2) == 2)
                    uiState.stateState - 2
                else
                    uiState.stateState + 2
            }

            State.SUSPENDED -> {
                if ((uiState.stateState and 4) == 4)
                    uiState.stateState - 4
                else
                    uiState.stateState + 4
            }

            State.ACTIVE -> {
                if ((uiState.stateState and 8) == 8)
                    uiState.stateState - 8
                else
                    uiState.stateState + 8
            }

            State.CANCELLED -> {
                if ((uiState.stateState and 16) == 16)
                    uiState.stateState - 16
                else
                    uiState.stateState + 16
            }

            State.COMPLETED -> {
                if ((uiState.stateState and 32) == 32)
                    uiState.stateState - 32
                else
                    uiState.stateState + 32
            }
        }
        return result
    }

    private fun priorityBinaryCalculation(priority: Priority): Int {
        val result = when (priority) {
            Priority.NONE -> {
                if ((uiState.priorityFilterState and 1) == 1)
                    uiState.priorityFilterState - 1
                else
                    uiState.priorityFilterState + 1
            }

            Priority.LOW -> {
                if ((uiState.priorityFilterState and 2) == 2)
                    uiState.priorityFilterState - 2
                else
                    uiState.priorityFilterState + 2
            }

            Priority.MEDIUM -> {
                if ((uiState.priorityFilterState and 4) == 4)
                    uiState.priorityFilterState - 4
                else
                    uiState.priorityFilterState + 4
            }

            Priority.HIGH -> {
                if ((uiState.priorityFilterState and 8) == 8)
                    uiState.priorityFilterState - 8
                else
                    uiState.priorityFilterState + 8
            }
        }
        return result
    }

    // 검색 app bar 가 닫힐 때 설정된 우선 순위에 따른 결과가 나와야 한다.
    fun onCloseSearchBar() {
        searchTextString = ""
        searchAppBarState = SearchAppBarState.CLOSE
//        refreshAllTasks()
    }

    fun onOpenSearchBar() {
        searchAppBarState = SearchAppBarState.OPEN
    }

    private fun addTask() {
        Log.d(
            "PHILIP",
            "[MemoViewModel] addTask performed with $taskUiState"
        )

        if (taskUiState.taskDetails.progression == State.COMPLETED || taskUiState.taskDetails.progression == State.CANCELLED) {
            updateUiState(
                taskUiState.taskDetails.copy(
                    finishedAt = ZonedDateTime.now()
                )
            )
        }

        savedLastMemoTask = taskUiState.taskDetails.toMemoTask()

        viewModelScope.launch {
            todoRepository.addMemo(taskUiState.taskDetails)
            refreshAllTasks()
        }
        this.action = Action.ADD
    }

    private fun updateTask(initialPhotos: List<Photo>) {
        Log.d(
            "PHILIP",
            "[MemoViewModel] updateTask performed with $taskUiState"
        )
        // 상태를 완료 변경할 경우는 종결일 을 넣어 주어야 한다.
        if (taskUiState.taskDetails.progression == State.COMPLETED || taskUiState.taskDetails.progression == State.CANCELLED) {
            updateUiState(
                taskUiState.taskDetails.copy(
                    finishedAt = ZonedDateTime.now()
                )
            )
        }

        savedLastMemoTask = taskUiState.taskDetails.toMemoTask()

        viewModelScope.launch {
            val deletedPhotosIds = todoRepository.updateMemo(taskUiState.taskDetails)
            Log.d("PHILIP", "delete ids $deletedPhotosIds")
            initialPhotos.forEach { photo ->
                Log.d("PHILIP", "each $photo")
                if (deletedPhotosIds.contains(photo.id)) {
                    deleteFileFromUri(photo.uri.toUri())
                }
            }
            refreshAllTasks()
        }
        this.action = Action.UPDATE
    }

    private fun deleteTask(task: MemoTask) {
        savedLastMemoTask = task.copy()

        viewModelScope.launch {
            todoRepository.deleteMemo(task.id)
            refreshAllTasks()
        }
        this.action = Action.DELETE
    }


    private fun moveToTask(destinationNoteId: Long) {
        viewModelScope.launch {
            Log.d(
                "PHILIP",
                "[MemoViewModel] moveToTask performed with ${selectedItems.toList()} to notebook id with $destinationNoteId "
            )

            todoRepository.moveMultipleMemos(selectedItems.toList(), destinationNoteId)
            selectedItems.clear()
            refreshAllTasks()
        }
    }

    private fun copyToTask(destinationNoteId: Long) {
        viewModelScope.launch {
            Log.d(
                "PHILIP",
                "[MemoViewModel] copyToTask performed with ${selectedItems.toList()} to notebook id with $destinationNoteId "
            )

            todoRepository.copyMultipleMemosToNote(selectedItems.toList(), destinationNoteId)
            selectedItems.clear()
            refreshAllTasks()
        }
    }

    private fun undoTask() {
        viewModelScope.launch {
            Log.d(
                "PHILIP",
                "[MemoViewModel] undoTask - undo with $savedLastMemoTask"
            )
            todoRepository.updateMemoWithoutUpdatedAt(
                savedLastMemoTask.copy(deleted = false)
            )
            refreshAllTasks()
        }
        this.action = Action.UNDO
    }

    private fun updateFavorite(memo: MemoTask) {
        viewModelScope.launch {
            Log.d("PHILIP", "updateFavorite ${memo.favorite}")
            todoRepository.updateMemoWithoutUpdatedAt(memo)
            // 화면을 리 프레시 하는 타이밍 도 중요 하다. 업데이트 가 완료된  후에 최신 정보를 가져와야 한다.
            if (uiState.sortFavorite) refreshAllTasks()
        }
    }

    private fun updateStateForMultiple(state: State) {
        viewModelScope.launch {
            Log.d(
                "PHILIP",
                "[MemoViewModel] updateStateForMultiple performed with ${selectedItems.toList()} to state $state "
            )

            todoRepository.updatesStateForMultipleMemos(selectedItems.toList(), state)
            selectedItems.clear()
            refreshAllTasks()
        }
    }

    // 상태 변경 -> updatedAt 이 변경 되지 않는다.
    private fun updateState(todo: MemoTask, state: State) {
        viewModelScope.launch {
            todoRepository.updateMemoWithoutUpdatedAt(
                if (state == State.COMPLETED || state == State.CANCELLED) {
                    todo.copy(
                        progression = state,
                        finishedAt = ZonedDateTime.now()
                    )
                } else {
                    todo.copy(
                        progression = state,
                    )
                }
            )
            // 화면을 리 프레시 하는 타이밍 도 중요 하다. 업데이트 가 완료된  후에 최신 정보를 가져와야 한다.
            when (state) {
                State.NONE -> {
                    if (!uiState.stateNone) refreshAllTasks()
                }

                State.WAITING -> {
                    if (!uiState.stateWaiting) refreshAllTasks()
                }

                State.SUSPENDED -> {
                    if (!uiState.stateSuspended) refreshAllTasks()
                }

                State.ACTIVE -> {
                    if (!uiState.stateActive) refreshAllTasks()
                }

                State.CANCELLED -> {
                    if (!uiState.stateActive) refreshAllTasks()
                }

                State.COMPLETED -> {
                    if (!uiState.stateCompleted) refreshAllTasks()
                }
            }
        }
    }

    private fun deleteAllTasks() {
        viewModelScope.launch {
            todoRepository.deleteAllMemosInNote(uiState.notebookIdState)
            refreshAllTasks()
        }
        this.action = Action.DELETE_ALL
    }

    private fun deleteSelectedTasks() {
        viewModelScope.launch {
            todoRepository.deleteSelectedMemos(selectedItems)
            selectedItems.clear()
            refreshAllTasks()
        }
        this.action = Action.DELETE_SELECTED_ITEMS
    }

    private fun updateSearchRange(
        searchRangeAllParam: Boolean = false,
        statusLineOrderUpdate: Boolean
    ) {
        searchRangeAll = searchRangeAllParam
        refreshAllTasks()

        if (statusLineOrderUpdate) {
            viewModelScope.launch {
                applyStatusLineOrder(StateEntity.NOTE_FILTER)
            }
        }
    }

    private fun updateSearchNoFilterState(searchRangeAllParam: Boolean = false) {
        searchNoFilterState = searchRangeAllParam
        refreshAllTasks()
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
            progressVisible = true
            val memoString = String(bytes, Charsets.UTF_8)
            val dbTables = memoString.split("pilseong")

            if (dbTables.size != 2) {
                progressVisible = false

                // 실패 대화박스 표출
                infoDialogTitle = R.string.info_import_fail_title
                infoDialogContent = R.string.info_import_fail_content
                openDialog = true
                Log.d("PHILIP", "error while parsing tables")
                item.close()
                return
            }

            try {
                val memoListType = object : TypeToken<List<MemoTask>>() {}.type
                val noteListType = object : TypeToken<List<Notebook>>() {}.type

                val memos = gson.fromJson<List<MemoTask>>(dbTables[0], memoListType)
                val notes = gson.fromJson<List<Notebook>>(dbTables[1], noteListType)

                Log.d(
                    "PHILIP",
                    "[MemoViewModel] handleImport uri: $uri, size of data: ${memos.size}"
                )

                viewModelScope.launch {
                    todoRepository.insertMultipleMemos(memos)
                    notebookRepository.insertMultipleNotebooks(notes)
                    delay(1000)
                    refreshAllTasks()
                }
            } catch (e: JsonParseException) {
                progressVisible = false

                infoDialogTitle = R.string.info_import_fail_title
                infoDialogContent = R.string.info_import_fail_content
                openDialog = true

                Log.d("PHILIP", "error while importing ${e.message}")
                item.close()
                return
            }
        }
        item?.close()
    }

    fun exportData() {
        viewModelScope.launch {
            val allMemoData = todoRepository.getAllTasks()
            var memoJson = gson?.toJson(allMemoData)
            val filename = "idea_note.txt"

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
        Log.d("PHILIP", "Sending Log ...###### ")
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

        intent.resolveActivity(context.packageManager)?.let {
            try {
                context.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Log.d("PHILIP", "No Intent matcher found")
            }
        }
    }

    var taskUiState by mutableStateOf(TaskUiState())
        private set

    fun updateUiState(taskDetails: TaskDetails) {
        Log.d("PHILIP", "updateUIState ${taskDetails}")
        taskUiState =
            TaskUiState(taskDetails = taskDetails, isEntryValid = validateInput(taskDetails))
    }

    private fun validateInput(uiState: TaskDetails = taskUiState.taskDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && description.isNotBlank()
        }
    }


    var uiState: UserData by mutableStateOf(UserData())

    private val uiStateFlow: StateFlow<UiState> =
        dataStoreRepository.userData.map {
            UiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )


    private fun observeUiState() {
        Log.d("PHILIP", "[MemoViewModel] observeUiState() called")
        viewModelScope.launch {
            uiStateFlow
                .onEach {
                    when (it) {
                        is UiState.Success -> {
                            uiState = it.userData
                            refreshAllTasks()
                            getNotebook(uiState.notebookIdState)
                        }

                        else -> {}
                    }
                }
                .collect {
//                    Log.d("PHILIP", "[MemoViewModel] observeUiState() executed")
                }
        }
    }

    init {
        observeUiState()
        Log.d("PHILIP", "[MemoViewModel] init -> version: ${Build.VERSION.RELEASE}")
    }
}

data class TaskDetails(
    val id: Long = NEW_ITEM_ID,
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.NONE,
    var favorite: Boolean = false,
    var progression: State = State.NONE,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val updatedAt: ZonedDateTime = ZonedDateTime.now(),
    val finishedAt: ZonedDateTime? = null,
    val notebookId: Long = -1,
    var photos: MutableList<Photo> = mutableListOf(),
)

fun TaskDetails.toMemoTask() = MemoTask(
    id = id,
    title = title,
    description = description,
    priority = priority,
    favorite = favorite,
    progression = progression,
    createdAt = createdAt,
    updatedAt = updatedAt,
    finishedAt = finishedAt,
    notebookId = notebookId
)

fun MemoTask.toTaskDetails(): TaskDetails = TaskDetails(
    id = id,
    title = title,
    description = description,
    priority = priority,
    favorite = favorite,
    progression = progression,
    createdAt = createdAt,
    updatedAt = updatedAt,
    finishedAt = finishedAt,
    notebookId = notebookId
)

fun MemoWithNotebook.toTaskDetails(): TaskDetails = TaskDetails(
    id = memo.id,
    title = memo.title,
    description = memo.description,
    priority = memo.priority,
    favorite = memo.favorite,
    progression = memo.progression,
    createdAt = memo.createdAt,
    updatedAt = memo.updatedAt,
    finishedAt = memo.finishedAt,
    notebookId = notebook?.id ?: -1,
    photos = photos.toMutableList()
)

fun MemoWithNotebook.toMemoTask(): MemoTask = MemoTask(
    id = memo.id,
    title = memo.title,
    description = memo.description,
    priority = memo.priority,
    favorite = memo.favorite,
    progression = memo.progression,
    createdAt = memo.createdAt,
    updatedAt = memo.updatedAt,
    finishedAt = memo.finishedAt,
    notebookId = memo.notebookId
)

@Stable
sealed interface UiState {
    object Loading : UiState
    data class Success(val userData: UserData) : UiState
}


data class TaskUiState(
    val taskDetails: TaskDetails = TaskDetails(),
    val isEntryValid: Boolean = false
)