package net.pilseong.todocompose.ui.screen.calendar

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.data.repository.DataStoreRepository
import net.pilseong.todocompose.data.repository.NotebookRepository
import net.pilseong.todocompose.ui.viewmodel.UiState
import net.pilseong.todocompose.util.getNextDates
import net.pilseong.todocompose.util.getRemainingDatesInMonth
import net.pilseong.todocompose.util.getRemainingDatesInWeek
import net.pilseong.todocompose.util.getWeekStartDate
import net.pilseong.todocompose.util.yearMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class MemoCalendarViewModel @Inject constructor(
    private val notebookRepository: NotebookRepository,
    private val dataStoreRepository: DataStoreRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    var selectedNotebook by mutableStateOf(Notebook.instance())
    var uiState: UserData by mutableStateOf(UserData())

    private val uiStateFlow: StateFlow<UiState> =
        dataStoreRepository.userData.map {
            UiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    private suspend fun getNotebook(id: Long) {
        // -1 이면 기본 노트 선택 title 이 설정 되어야 하기 때문에 title 를 지정해 준다.
        selectedNotebook = if (id == -1L) Notebook.instance(
            title = context.resources.getString(R.string.default_note_title)
        )
        else notebookRepository.getNotebook(id)
    }


    private fun observeUiState() {
        Log.d("PHILIP", "[MemoViewModel] observeUiState() called")
        viewModelScope.launch {
            uiStateFlow
                .onEach {
                    when (it) {
                        is UiState.Success -> {
                            uiState = it.userData
                            // find the tasks
//                            refreshAllTasks()
                            getNotebook(uiState.notebookIdState)
                        }

                        else -> {}
                    }
                }
                .collect {
                    Log.d("PHILIP", "[MemoTaskManagerViewModel] observeUiState() executed")
                }
        }
    }

    private val _visibleDates =
        MutableStateFlow(
            calculateExpandedCalendarDays(
                startDate = LocalDate.now().yearMonth().minusMonths(1).atDay(1)
            )
        )

    val visibleDates: StateFlow<Array<List<LocalDate>>> = _visibleDates

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    val currentMonth: StateFlow<YearMonth>
        get() = visibleDates.map { dates ->
            dates[1][dates[1].size / 2].yearMonth()
        }.stateIn(viewModelScope, SharingStarted.Eagerly, LocalDate.now().yearMonth())


    fun onIntent(intent: CalendarIntent) {
        when (intent) {
            CalendarIntent.ExpandCalendar -> {
                calculateCalendarDates(
                    startDate = currentMonth.value.minusMonths(1).atDay(1)
                )
            }

            is CalendarIntent.LoadNextDates -> {
                calculateCalendarDates(
                    startDate = intent.startDate
                )
            }

            is CalendarIntent.SelectDate -> {
                viewModelScope.launch {
                    _selectedDate.emit(intent.date)
                }
            }
        }
    }

    private fun calculateCalendarDates(
        startDate: LocalDate,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _visibleDates.emit(calculateExpandedCalendarDays(startDate))
        }
    }

    private fun calculateExpandedCalendarDays(startDate: LocalDate): Array<List<LocalDate>> {
        val array = Array(3) { monthIndex ->
            val monthFirstDate = startDate.plusMonths(monthIndex.toLong())
            val monthLastDate = monthFirstDate.plusMonths(1).minusDays(1)
            val weekBeginningDate = monthFirstDate.getWeekStartDate(DayOfWeek.SUNDAY)
            if (weekBeginningDate != monthFirstDate) {
                weekBeginningDate.getRemainingDatesInMonth()
            } else {
                listOf()
            }.plus(
                monthFirstDate.getNextDates(monthFirstDate.month.length(monthFirstDate.isLeapYear)) +
                        monthLastDate.getRemainingDatesInWeek(DayOfWeek.SUNDAY)
            )
        }

        array.forEach {
            Log.d("TTEST", "array ${it.toString()}")
        }

        return array
    }

    init {
        observeUiState()
    }
}

sealed class CalendarIntent {
    class LoadNextDates(
        val startDate: LocalDate,
    ) : CalendarIntent()

    class SelectDate(val date: LocalDate) : CalendarIntent()

    object ExpandCalendar : CalendarIntent()
}
