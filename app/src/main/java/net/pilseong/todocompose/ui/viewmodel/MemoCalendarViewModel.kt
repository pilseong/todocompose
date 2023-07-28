package net.pilseong.todocompose.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.NoteSortingOption
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.data.repository.DataStoreRepository
import net.pilseong.todocompose.data.repository.NotebookRepository
import net.pilseong.todocompose.data.repository.TodoRepository
import net.pilseong.todocompose.ui.screen.calendar.CalendarAction
import net.pilseong.todocompose.ui.viewmodel.UiState
import net.pilseong.todocompose.util.StateEntity
import net.pilseong.todocompose.util.yearMonth
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class MemoCalendarViewModel @Inject constructor(
    private val notebookRepository: NotebookRepository,
    private val todoRepository: TodoRepository,
    private val dataStoreRepository: DataStoreRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    var selectedNotebook by mutableStateOf(Notebook.instance())
    var uiState: UserData by mutableStateOf(UserData())
    var tasks = MutableStateFlow<List<MemoWithNotebook>>(emptyList())
    var tasksJob: Job? = null
    var selectedMonth by mutableStateOf(LocalDate.now().yearMonth())

    var defaultNoteMemoCount by mutableStateOf(DefaultNoteMemoCount(0, 0, 0, 0, 0))

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
            "[MemoCalendarViewModel] refreshAllTasks condition with ${uiState.dateOrderState}, notebook_id: $uiState.notebookIdState"
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
                searchRangeAll = uiState.searchRangeAll,
                notebookId = uiState.notebookIdState,
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
                            uiState = it.userData
                            // find the tasks
                            refreshAllTasks()
                            getNotebook(uiState.notebookIdState)
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
                if (uiState.notebookIdState != notebookId) {
                    val noteIdsList = mutableListOf<String>()
                    noteIdsList.add(notebookId.toString())
                    noteIdsList.add(uiState.notebookIdState.toString())

                    if (uiState.firstRecentNotebookId != null) {
                        noteIdsList.add(uiState.firstRecentNotebookId.toString())
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
        }
    }

    init {
        observeUiState()
    }
}
