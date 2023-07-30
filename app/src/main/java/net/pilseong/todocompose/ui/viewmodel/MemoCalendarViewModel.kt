package net.pilseong.todocompose.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import net.pilseong.todocompose.alarm.ReminderScheduler
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.DefaultNoteMemoCount
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.NoteSortingOption
import net.pilseong.todocompose.data.model.ui.ReminderTime
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.data.repository.DataStoreRepository
import net.pilseong.todocompose.data.repository.NotebookRepository
import net.pilseong.todocompose.data.repository.TodoRepository
import net.pilseong.todocompose.ui.screen.calendar.CalendarAction
import net.pilseong.todocompose.util.yearMonth
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MemoCalendarViewModel @Inject constructor(
    private val notebookRepository: NotebookRepository,
    private val todoRepository: TodoRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val reminderScheduler: ReminderScheduler,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    var selectedNotebook by mutableStateOf(Notebook.instance())
    var userData: UserData by mutableStateOf(UserData())
    var tasks = MutableStateFlow<List<MemoWithNotebook>>(emptyList())
    var tasksJob: Job? = null
    var selectedMonth by mutableStateOf(LocalDate.now().yearMonth())

    // 메모장 선택 시에 기본 메모장 의 데이터 정보를 받기 위한 변수
    var defaultNoteMemoCount by mutableStateOf(DefaultNoteMemoCount(0, 0, 0, 0, 0))

    // 메모 작성 에 팔요한 변수를 가지고 있다.
    var taskUiState by mutableStateOf(
        TaskUiState(
            taskDetails = TaskDetails(
                dueDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
            )
        )
    )
        private set

    fun updateUiState(taskDetails: TaskDetails) {
        Log.d("PHILIP", "[MemoCalendarViewModel] updateUIState $taskDetails")
        taskUiState =
            TaskUiState(taskDetails = taskDetails, isEntryValid = validateInput(taskDetails))
    }

    private fun validateInput(uiState: TaskDetails = taskUiState.taskDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && dueDate != null
        }
    }


    private val uiStateFlow: StateFlow<UiState> =
        dataStoreRepository.userData.map {
            UiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    var notebooks = notebookRepository.getNotebooksAsFlow(NoteSortingOption.ACCESS_AT)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getDefaultNoteCount() {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.getMemoCount(-1).collectLatest {
                defaultNoteMemoCount = it
            }
        }
    }

    private suspend fun getNotebook(id: Long) {
        // -1 이면 기본 노트 선택 title 이 설정 되어야 하기 때문에 title 를 지정해 준다.
        selectedNotebook = if (id == -1L) Notebook.instance(
            title = context.resources.getString(R.string.default_note_title)
        )
        else notebookRepository.getNotebook(id)
    }

    private fun refreshAllTasks() {
        Log.d(
            "PHILIP",
            "[MemoCalendarViewModel] refreshAllTasks condition with ${userData.dateOrderState}, notebook_id: $userData.notebookIdState"
        )
        Log.d(
            "PHILIP",
            "[MemoCalendarViewModel] refreshAllTasks condition with $selectedMonth"
        )

        if (tasksJob != null) {
            tasksJob!!.cancel()
        }
        tasksJob = viewModelScope.launch {
            todoRepository.getMonthlyTasks(
                yearMonth = selectedMonth,
                searchRangeAll = userData.searchRangeAll,
                notebookId = userData.notebookIdState,
            )
                .collectLatest {
                    Log.d(
                        "PHILIP",
                        "[MemoCalendarViewModel] refreshAllTasks how many"
                    )
                    tasks.value = it

                }
        }
    }


    private fun observeUiState() {
        Log.d("PHILIP", "[MemoCalendarViewModel] observeUiState() called")
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
                    Log.d("PHILIP", "[MemoCalendarViewModel] observeUiState() executed")
                }
        }
    }

    fun handleActions(
        calendarAction: CalendarAction,
        month: YearMonth = LocalDate.now().yearMonth(),
        notebookId: Long = -1L,
        boolParam: Boolean = false,
    ) {

        Log.d("PHILIP", "[MemoCalendarViewModel] handleAction $calendarAction $month")
        when (calendarAction) {
            CalendarAction.MONTH_CHANGE -> {
                selectedMonth = month
                refreshAllTasks()
            }

            CalendarAction.NOTE_SWITCH -> {
                if (userData.notebookIdState != notebookId) {
                    val noteIdsList = mutableListOf<String>()
                    noteIdsList.add(notebookId.toString())
                    noteIdsList.add(userData.notebookIdState.toString())

                    if (userData.firstRecentNotebookId != null) {
                        noteIdsList.add(userData.firstRecentNotebookId.toString())
                    }

                    viewModelScope.launch {
                        dataStoreRepository.persistRecentNoteIds(noteIdsList)
                    }
                }
            }

            CalendarAction.SEARCH_RANGE_CHANGE -> {
                viewModelScope.launch {
                    dataStoreRepository.persistSearchRangeAllState(searchRangeAll = boolParam)
                }
            }

            CalendarAction.ADD -> {
                Log.d(
                    "PHILIP",
                    "[MemoCalendarViewModel] addTask performed with $taskUiState $selectedMonth"
                )

                if (taskUiState.taskDetails.progression == State.COMPLETED ||
                    taskUiState.taskDetails.progression == State.CANCELLED
                ) {
                    updateUiState(
                        taskUiState.taskDetails.copy(
                            finishedAt = ZonedDateTime.now()
                        )
                    )
                }

                viewModelScope.launch {
                    // 알림 설정
                    val id = todoRepository.addMemo(taskUiState.taskDetails)

                    // 알람 설정 부분
                    // 알람 설정일 지난 이후에 수정된 것들은 신경 쓸 필요가 없다
                    if (taskUiState.taskDetails.dueDate != null &&
                        Calendar.getInstance().timeInMillis < (taskUiState.taskDetails.dueDate!!.toInstant()
                            .toEpochMilli() - taskUiState.taskDetails.reminderType.timeInMillis)
                    ) {
                        if (taskUiState.taskDetails.reminderType != ReminderTime.NOT_USED)
                            registerNotification(taskUiState.taskDetails.copy(id = id))
                    }

                    refreshAllTasks()
                }
            }
        }
    }

    private fun registerNotification(taskDetails: TaskDetails) {
        reminderScheduler.start(taskDetails)
    }


    init {
        observeUiState()
    }
}
