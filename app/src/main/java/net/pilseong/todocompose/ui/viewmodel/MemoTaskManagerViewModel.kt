package net.pilseong.todocompose.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.DefaultNoteMemoCount
import net.pilseong.todocompose.data.model.ui.MemoDateSortingOption
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.NoteSortingOption
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.SortOption
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.data.repository.DataStoreRepository
import net.pilseong.todocompose.data.repository.NotebookRepository
import net.pilseong.todocompose.data.repository.TodoRepository
import net.pilseong.todocompose.ui.screen.calendar.CalendarAction
import net.pilseong.todocompose.ui.screen.list.MemoAction
import net.pilseong.todocompose.ui.screen.taskmanager.TaskManagerAction
import net.pilseong.todocompose.ui.viewmodel.UiState
import net.pilseong.todocompose.util.Constants
import net.pilseong.todocompose.util.TaskAppBarState
import net.pilseong.todocompose.util.yearMonth
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class MemoTaskManagerViewModel @Inject constructor(
    private val notebookRepository: NotebookRepository,
    private val todoRepository: TodoRepository,
    private val dataStoreRepository: DataStoreRepository,
    @ApplicationContext private val context: Context,
): ViewModel() {

    @Stable
    var tasks = MutableStateFlow<PagingData<MemoWithNotebook>>(PagingData.empty())
        private set

    // 현재 보여 지거나 수정 중인 인덱스 가지고 있는 변수
    var index by mutableIntStateOf(0)

    var selectedNotebook by mutableStateOf(Notebook.instance())
    var userData: UserData by mutableStateOf(UserData())

    var defaultNoteMemoCount by mutableStateOf(DefaultNoteMemoCount(0, 0, 0, 0, 0))


    var notebooks = notebookRepository.getNotebooksAsFlow(NoteSortingOption.ACCESS_AT)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val uiStateFlow: StateFlow<UiState> =
        dataStoreRepository.userData.map {
            UiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    fun updateIndex(index: Int) {
        this.index = index
    }

    fun getDefaultNoteCount() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.getMemoCount(-1).collectLatest {
                defaultNoteMemoCount = it
            }
        }
    }

    var memoAction = MemoAction.NO_ACTION
        private set

    fun updateAction(memoAction: MemoAction) {
        this.memoAction = memoAction
        Log.d("PHILIP", "[MemoViewModel] updateAction to ${memoAction.name}")
    }

    var taskAppBarState by mutableStateOf(TaskAppBarState.VIEWER)
        private set


    var taskUiState by mutableStateOf(TaskUiState())
        private set


    private fun validateInput(uiState: TaskDetails = taskUiState.taskDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && !(isTask && (dueDate == null && progression == State.NONE))
        }
    }


    fun updateUiState(taskDetails: TaskDetails) {
        Log.d("PHILIP", "updateUIState $taskDetails")
        taskUiState =
            TaskUiState(taskDetails = taskDetails, isEntryValid = validateInput(taskDetails))
    }

    fun setTaskScreenToEditorMode(task: MemoWithNotebook = MemoWithNotebook.instance(notebookId = userData.notebookIdState)) {
        taskAppBarState = TaskAppBarState.EDITOR
        updateUiState(
            if (task.memo.id == Constants.NEW_ITEM_ID) TaskDetails().copy(notebookId = userData.notebookIdState)
            else task.toTaskDetails()
        )
    }

    private suspend fun getNotebook(id: Long) {
        // -1 이면 기본 노트 선택 title 이 설정 되어야 하기 때문에 title 를 지정해 준다.
        selectedNotebook = if (id == -1L) Notebook.instance(
            title = context.resources.getString(R.string.default_note_title)
        )
        else notebookRepository.getNotebook(id)
    }

    fun handleActions(
        taskManagerAction: TaskManagerAction,
        currentMemoTask: MemoWithNotebook = MemoWithNotebook.instance(),
        notebookId: Long = -1L,
        boolParam: Boolean = false,
    ) {
        Log.d("PHILIP", "[MemoTaskManagerViewModel] handleAction $taskManagerAction boolParam: $boolParam")
        when (taskManagerAction) {
            TaskManagerAction.NOTE_SWITCH -> TODO()
            TaskManagerAction.SEARCH_RANGE_CHANGE -> {
                viewModelScope.launch {
                    dataStoreRepository.persistSearchRangeAllState(searchRangeAll = boolParam)
                }
            }
        }
    }

    private var tasksJob: Job? = null

    private fun refreshAllTasks() {
        Log.d(
            "PHILIP",
            "[MemoTaskManagerViewModel] refreshAllTasks condition with ${userData.searchRangeAll}"
        )
        viewModelScope.launch {
            todoRepository.getTasks(
                searchRangeAll = userData.searchRangeAll,
                memoDateSortState = MemoDateSortingOption.DUE_DATE,
                memoOrderState = SortOption.DESC,
                priority = Priority.NONE,
                notebookId = userData.notebookIdState,
                stateCompleted = false,
                stateCancelled = false,
                stateActive = true,
                stateSuspended = true,
                stateWaiting = true,
                stateNone = true,
                priorityHigh = true,
                priorityMedium = true,
                priorityLow = true,
                priorityNone = true,
            ).cachedIn(viewModelScope)
                .collectLatest {
                    Log.d(
                        "PHILIP",
                        "[MemoTaskManagerViewModel] refreshAllTasks how many"
                    )
                    tasks.value = it
                }
        }
    }




    private fun observeUiState() {
        Log.d("PHILIP", "[MemoTaskManagerViewModel] observeUiState() called")
        viewModelScope.launch {
            uiStateFlow
                .onEach {
                    when (it) {
                        is UiState.Success -> {
                            userData = it.userData
                            // find the tasks
                            refreshAllTasks()
                            getNotebook(userData.notebookIdState)
                        }

                        else -> {}
                    }
                }
                .collect {
                    Log.d("PHILIP", "[MemoTaskManagerViewModel] observeUiState() executed")
                }
        }
    }

    init {
        observeUiState()
    }
}