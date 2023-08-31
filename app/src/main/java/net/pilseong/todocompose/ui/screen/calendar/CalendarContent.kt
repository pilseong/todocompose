package net.pilseong.todocompose.ui.screen.calendar

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.ui.components.NoteEditor
import net.pilseong.todocompose.ui.components.NoteEditorMode
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import net.pilseong.todocompose.ui.viewmodel.TaskUiState
import net.pilseong.todocompose.util.MonthViewCalendar
import net.pilseong.todocompose.util.calculateExpandedCalendarDays
import net.pilseong.todocompose.util.calculateSwipeNext
import net.pilseong.todocompose.util.calculateSwipePrev
import net.pilseong.todocompose.util.yearMonth
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CalendarContent(
    tasks: List<MemoWithNotebook>,
    taskUiState: TaskUiState,
    selectedDate: LocalDate,
    taskUiStateList: SnapshotStateList<TaskUiState> = SnapshotStateList(),
    selectedMonth: YearMonth,
    selectedNotebook: Notebook,
    editorExpanded: Boolean = false,
    onMonthChange: (YearMonth) -> Unit,
    onValueChange: (TaskDetails) -> Unit,
    onTaskUiStateListClean: () -> Unit,
    onDayClick: (LocalDate, List<MemoWithNotebook>) -> Unit,
    onDayLongClick: (LocalDate, List<MemoWithNotebook>) -> Unit,
    onEditorExpanded: (Boolean) -> Unit,
    onEditClicked: (MemoWithNotebook) -> Unit,
    onDeleteClicked: (MemoWithNotebook) -> Unit,
) {

    Log.d("PHILIP", "[CalendarContent] size of tasks ${tasks.size}")

    var loadedDates by rememberSaveable {
        mutableStateOf(
            calculateExpandedCalendarDays(
                selectedMonth
                    .minusMonths(1)
                    .atDay(1)
            )
        )
    }

    Surface {
        if (!editorExpanded) {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(color = MaterialTheme.colorScheme.primary) {
                    Box(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(vertical = LARGE_PADDING),
                            text = selectedMonth.format(
                                DateTimeFormatter.ofPattern(
                                    stringResource(
                                        id = R.string.calendar_title
                                    )
                                )
                            ),
                            fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.padding(end = LARGE_PADDING),
                                imageVector = Icons.Default.Menu, contentDescription = "Add icon"
                            )
                        }
                    }
                }
                MonthViewCalendar(
                    tasks = tasks,
                    loadedDates,
                    selectedDate,
                    currentMonth = selectedMonth,
                    onSwipeNext = { yearMonth ->
                        Log.d("PHILIP", "onSwipeNext")
                        loadedDates = calculateSwipeNext(yearMonth.atDay(1), loadedDates)

                        onMonthChange(yearMonth.plusMonths(1))
                    },
                    onSwipePrev = { yearMonth ->
                        Log.d("PHILIP", "onSwipePrev")
                        loadedDates = calculateSwipePrev(yearMonth.atDay(1), loadedDates)

                        onMonthChange(yearMonth.plusMonths(1))

                    },
                    loadDatesForMonth = { yearMonth ->
                        Log.d("PHILIP", "get yearMonth $yearMonth")
                        loadedDates = calculateExpandedCalendarDays(yearMonth.atDay(1))
                    },
                    onDayClick = onDayClick,
                    onDayLongClick = onDayLongClick,
                )
            }
        } else {
            Column(
                modifier = Modifier.padding(
                    start = LARGE_PADDING,
                    end = LARGE_PADDING,
                    bottom = LARGE_PADDING
                )
            ) {
                NoteEditor(
                    mode = NoteEditorMode.CALENDAR_ADD,
                    taskUiState = taskUiState,
                    notebook = selectedNotebook,
                    onValueChange = onValueChange,
                )
            }
        }
    }
}

@Preview
@Composable
fun CalendarContentPreview() {
    TodoComposeTheme {
        CalendarContent(
            tasks = listOf(
                MemoWithNotebook(
                    memo = MemoTask(
                        1,
                        "필성 힘내!!!",
                        "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                        Priority.HIGH,
                        notebookId = -1,
                        dueDate = ZonedDateTime.now()
                    ),
                    notebook = Notebook.instance(),
                    total = 1,
                    photos = emptyList()
                )
            ),
            taskUiState = TaskUiState(),
            selectedNotebook = Notebook.instance(),
            selectedMonth = LocalDate.now().yearMonth(),
            selectedDate = LocalDate.now(),
            onMonthChange = {},
            onValueChange = {},
            onEditorExpanded = {},
            onEditClicked = {},
            onTaskUiStateListClean = {},
            onDeleteClicked = {},
            onDayClick = { _, _ -> },
            onDayLongClick = { _, _ -> },
        )
    }
}