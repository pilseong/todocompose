package net.pilseong.todocompose.ui.screen.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.ReminderType
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.ui.components.DisplayAlertDialog
import net.pilseong.todocompose.ui.components.ReminderDropDown
import net.pilseong.todocompose.ui.components.TaskHeader
import net.pilseong.todocompose.ui.components.TaskHeaderType
import net.pilseong.todocompose.ui.screen.list.EmptyContent
import net.pilseong.todocompose.ui.screen.note.CalendarInnerEditor
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.theme.taskItemContentColor
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import net.pilseong.todocompose.ui.viewmodel.TaskUiState
import net.pilseong.todocompose.ui.viewmodel.toTaskDetails
import net.pilseong.todocompose.util.Constants
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ScheduleListSheet(
//    selectedDate: LocalDate,
//    taskUiStateList: SnapshotStateList<TaskUiState> = SnapshotStateList(),
//    notes: List<MemoWithNotebook>,
//    expanded: Boolean = false,
//    onDismissRequest: () -> Unit,
//    onAddClicked: () -> Unit,
//    onValueChange: (TaskDetails) -> Unit,
//    onEditClicked: (MemoWithNotebook) -> Unit,
//    onDeleteClicked: (MemoWithNotebook) -> Unit,
//) {
//    Log.d("PHILIP", "[ScheduleListSheet] called")
//    if (expanded) {
//        val state = rememberModalBottomSheetState(
//            skipPartiallyExpanded = true
//        )
//
//        ModalBottomSheet(
//            onDismissRequest = {
//                Log.d("PHILIP", "SimpleDateRangePickerSheet onDismissRequest")
//                onDismissRequest()
//            },
//            sheetState = state,
//        ) {
//            CalendarNoteList(
//                selectedDate = selectedDate,
//                taskUiStateList = taskUiStateList,
//                notes = notes,
//                onDismissRequest = onDismissRequest,
//                onAddClicked = onAddClicked,
//                onEditClicked = onEditClicked,
//                onValueChange = onValueChange,
//                onDeleteClicked = onDeleteClicked,
//            )
//        }
//    }
//}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CalendarNoteList(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    taskUiStateList: SnapshotStateList<TaskUiState> = SnapshotStateList(),
    notes: List<MemoWithNotebook>,
    onDismissRequest: () -> Unit,
    onAddClicked: () -> Unit,
    onEditClicked: (MemoWithNotebook) -> Unit,
    onDeleteClicked: (MemoWithNotebook) -> Unit,
    onValueChange: (TaskDetails) -> Unit,
) {
    Column(modifier = modifier) {
        CenterAlignedTopAppBar(
            navigationIcon = {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = XLARGE_PADDING)
                        .clickable {
                            onDismissRequest()
                        },
                    imageVector = Icons.Default.Close, contentDescription = "close button"
                )

            },
            title = {
                Text(
                    text = selectedDate.format(
                        DateTimeFormatter.ofPattern(
                            stringResource(id = R.string.note_content_dateformat),
                            Locale.getDefault()
                        )
                    )
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            actions = {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = XLARGE_PADDING)
                        .clickable {
                            onAddClicked()
                        },
                    imageVector = Icons.Default.NoteAdd,
                    contentDescription = "add note"
                )

            },
            windowInsets = WindowInsets(top = 0.dp)
        )
        Surface {
            if (notes.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(700.dp)
                        .padding(
                            vertical = LARGE_PADDING,
                            horizontal = XLARGE_PADDING
                        ),
                ) {
                    items(
                        items = notes,
                        key = { item -> item.memo.id },
                    ) { item ->


                        val resultTaskUiState = taskUiStateList.filter { task ->
                            task.taskDetails.id == item.memo.id
                        }

                        val taskUiState = if (resultTaskUiState.isEmpty())
                            TaskUiState(item.toTaskDetails(), false)
                        else resultTaskUiState[0]


                        ScheduleItem(
                            modifier = Modifier.padding(bottom = SMALL_PADDING),
                            taskUiState = taskUiState,
                            item = item,
                            onEditClicked = onEditClicked,
                            onDeleteClicked = onDeleteClicked,
                            onValueChange = onValueChange,
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(700.dp)
                        .padding(
                            vertical = LARGE_PADDING,
                            horizontal = XLARGE_PADDING
                        ),
                ) {
                    EmptyContent()
                }
            }
        }
    }
}

@Composable
private fun ScheduleItem(
    modifier: Modifier = Modifier,
    taskUiState: TaskUiState = TaskUiState(),
    item: MemoWithNotebook,
    onEditClicked: (MemoWithNotebook) -> Unit,
    onDeleteClicked: (MemoWithNotebook) -> Unit,
    onValueChange: (TaskDetails) -> Unit,
) {
    val cornerRadius = 8.dp
    val cutCornerSize = 20.dp
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .padding(bottom = if (expanded) LARGE_PADDING else 0.dp),
        shape = RoundedCornerShape(cornerRadius),
        tonalElevation = if (expanded && item.memo.priority == Priority.NONE) 8.dp else 0.dp,
        shadowElevation = if (expanded && item.memo.priority == Priority.NONE) 2.dp else 0.dp,
        color = if (expanded) {
            if (item.memo.priority == Priority.NONE) MaterialTheme.colorScheme.surface
            else item.memo.priority.color.copy(alpha = 0.2F)
        } else Color.Transparent
    ) {

        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (!expanded) {
                Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    val clipPath = Path().apply {
                        lineTo(size.width - cutCornerSize.toPx(), 0f)
                        lineTo(size.width, cutCornerSize.toPx())
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }

                    clipPath(clipPath) {
                        drawRoundRect(
                            color = item.memo.priority.color.copy(
                                alpha = if (item.memo.priority == Priority.NONE) 0.2F else 0.3F
                            ), size = size, cornerRadius = CornerRadius(cornerRadius.toPx())
                        )
                        drawRoundRect(
                            color = Color(
                                ColorUtils.blendARGB(
                                    item.memo.priority.color.copy(
                                        alpha = if (item.memo.priority == Priority.NONE) 0.2F else 0.3F
                                    ).toArgb(), 0x000000, 0.2f
                                )
                            ),
                            topLeft = Offset(size.width - cutCornerSize.toPx(), -100f),
                            size = Size(cutCornerSize.toPx() + 100f, cutCornerSize.toPx() + 100f),
                            cornerRadius = CornerRadius(cornerRadius.toPx())
                        )
                    }
                }
            }
            // 분기를 사용한 이유는 IntrinsicSize
            // Sub compose Layout 를 사용 하는 lazy lists, BoxWithConstraints, TabRow,
            // 가 하위에 포함 되면 안되기 때문 이다.
            if (!expanded) {
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .clickable {
                            expanded = !expanded
                        },
                ) {
                    // 시간 + 중요성
                    Column(
                        modifier = Modifier
                            .padding(vertical = MEDIUM_PADDING + 6.dp)
                            .weight(2 / 12f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        item.memo.dueDate?.toLocalTime()?.let {
                            Text(
                                modifier = Modifier.wrapContentHeight(Alignment.CenterVertically),
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                textAlign = TextAlign.Center,
                                text = it.format(DateTimeFormatter.ofPattern("HH:mm")),
                                color = Color(
                                    ColorUtils.blendARGB(
                                        MaterialTheme.colorScheme.onSurface.toArgb(),
                                        Color.White.toArgb(),
                                        0.1f
                                    )
                                ).copy(0.9f),
                            )
                        }
//                    Spacer(modifier = Modifier.height(4.dp))

                        var reminderExpanded by remember { mutableStateOf(false) }
                        ReminderDropDown(
                            isNew = item.memo.id == Constants.NEW_ITEM_ID,
                            expanded = reminderExpanded,
                            enabled = item.memo.dueDate != null,
                            targetTime = item.memo.dueDate?.toInstant()
                                ?.toEpochMilli(),
                            onTimeSelected = { reminderType ->
                                reminderExpanded = false
                                onValueChange(
                                    item.toTaskDetails().copy(reminderType = reminderType)
                                )
                                onEditClicked(item)
                            },
                            onButtonClicked = {
                                if (item.memo.dueDate != null) reminderExpanded = true
                            },
                            onDismissRequest = { reminderExpanded = false }
                        ) {
                            Surface(color = Color.Transparent) {
                                Icon(
                                    modifier = Modifier
                                        .padding(top = 4.dp, start = 4.dp)
                                        .size(16.dp),
                                    imageVector =
                                    if (item.memo.reminderType == ReminderType.NOT_USED) Icons.Default.AlarmOff
                                    else if (Calendar.getInstance().timeInMillis < item.memo.dueDate!!.toInstant()
                                            .toEpochMilli() - item.memo.reminderType.timeInMillis
                                    ) Icons.Default.AlarmOn
                                    else Icons.Default.Alarm,
                                    contentDescription = "alarm icon",
                                    tint = if (item.memo.reminderType != ReminderType.NOT_USED &&
                                        Calendar.getInstance().timeInMillis < (item.memo.dueDate!!.toInstant()
                                            .toEpochMilli() - item.memo.reminderType.timeInMillis)
                                    ) Color.Red
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }


                    //  제목 내용
                    Column(
                        modifier = Modifier
                            .padding(
                                top = MEDIUM_PADDING,
                                bottom = MEDIUM_PADDING,
                                end = LARGE_PADDING
                            )
                            .fillMaxHeight()
                            .weight(10 / 12f),
//                            .weight(if (item.memo.progression != State.NONE) 7 / 12f else 9 / 12f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.memo.title,
                            color = MaterialTheme.colorScheme.taskItemContentColor,
                            style = TextStyle(
                                textDecoration =
                                if (item.memo.progression == State.COMPLETED ||
                                    item.memo.progression == State.CANCELLED
                                )
                                    TextDecoration.LineThrough
                                else TextDecoration.None
                            ),
                            maxLines = if (item.memo.description.isNotBlank()) 1 else 4,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (item.memo.description.isNotBlank()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = item.memo.description,
                                color = MaterialTheme.colorScheme.taskItemContentColor,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                style = TextStyle(
                                    textDecoration =
                                    if (item.memo.progression == State.COMPLETED ||
                                        item.memo.progression == State.CANCELLED
                                    )
                                        TextDecoration.LineThrough
                                    else TextDecoration.None
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                // 화면이 열려 있을 때
            } else {
                var isEditMode by remember { mutableStateOf(false) }

                // 수정 모드
                if (isEditMode) {
                    CalendarInnerEditor(
                        taskUiState = taskUiState,
                        onCancelClicked = {
                            isEditMode = false
                        },
                        onEditClicked = {
                            onEditClicked(it)
                            isEditMode = false
                        },
                        item = item,
                        onValueChange = onValueChange,
                        dueDate = taskUiState.taskDetails.dueDate
                    )
                    // 열린 채 뷰 모드
                } else {
                    var confirmExpanded by remember { mutableStateOf(false) }
                    DisplayAlertDialog(
                        title = stringResource(
                            id = R.string.delete_task_dialog_title,
                            if (item.memo.title.length > 20) item.memo.title.substring(
                                0,
                                20
                            ) else item.memo.title
                        ),
                        message = stringResource(
                            id = R.string.delete_task_dialog_confirmation,
                            if (item.memo.title.length > 20) item.memo.title.substring(
                                0,
                                20
                            ) else item.memo.title
                        ),
                        openDialog = confirmExpanded,
                        onCloseDialog = { confirmExpanded = false },
                        onYesClicked = {
                            onDeleteClicked(item)
                        }
                    )

                    Row(modifier = Modifier.clickable { expanded = !expanded }) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .padding(
                                        horizontal = XLARGE_PADDING,
                                        vertical = SMALL_PADDING
                                    )
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontStyle = MaterialTheme.typography.titleMedium.fontStyle,
                                    text = item.memo.dueDate!!.toLocalDateTime()
                                        .format(
                                            DateTimeFormatter.ofPattern(
                                                stringResource(id = R.string.task_content_dateformat)
                                            )
                                        ),
                                    lineHeight = 20.sp
                                )
                                Row {
                                    Icon(
                                        modifier = Modifier.clickable {
                                            onValueChange(item.memo.toTaskDetails().copy())
                                            isEditMode = true
                                        },
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "edit icon"
                                    )
                                    Spacer(modifier = Modifier.width(LARGE_PADDING))
                                    Icon(
                                        modifier = Modifier.clickable {
                                            confirmExpanded = true
                                        },
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "delete icon"
                                    )
                                }
                            }
                            TaskHeader(task = item, type = TaskHeaderType.CALENDAR)
                            Column(
                                modifier = Modifier.padding(horizontal = XLARGE_PADDING)
                            ) {
                                SelectionContainer {
                                    Text(
                                        modifier = Modifier.padding(SMALL_PADDING),
                                        text = item.memo.description.ifBlank { item.memo.title },
                                        lineHeight = MaterialTheme.typography.labelSmall.fontSize.times(
                                            1.4f
                                        ),
                                        fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                                        fontSize = MaterialTheme.typography.labelSmall.fontSize
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewScheduleItem() {
    TodoComposeTheme {
        ScheduleItem(
            item = MemoWithNotebook(
                memo = MemoTask(
                    1,
                    "필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!",
                    "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                    Priority.NONE,
                    notebookId = -1,
                    dueDate = ZonedDateTime.now()
                ), notebook = Notebook.instance(), total = 1, photos = emptyList()
            ),
            onEditClicked = {},
            onValueChange = {},
            onDeleteClicked = {},
        )
    }

}

@Preview
@Composable
fun PreviewCalendarNoteList() {
    TodoComposeTheme {
        CalendarNoteList(
            selectedDate = LocalDate.now(),
            notes = listOf(
                MemoWithNotebook(
                    memo = MemoTask(
                        1,
                        "필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!",
                        "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                        Priority.NONE,
                        notebookId = -1,
                        dueDate = ZonedDateTime.now()
                    ), notebook = Notebook.instance(), total = 1, photos = emptyList()
                ),
                MemoWithNotebook(
                    memo = MemoTask(
                        id = 2,
                        title = "필성 힘내!!!필성 힘내!힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!",
                        description = "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                        priority = Priority.NONE,
                        notebookId = -1,
                        dueDate = ZonedDateTime.now()
                    ), notebook = Notebook.instance(), total = 1, photos = emptyList()
                )
            ),
            onDismissRequest = {},
            onAddClicked = {},
            onEditClicked = {},
            onValueChange = {},
            onDeleteClicked = {},
        )
    }

}