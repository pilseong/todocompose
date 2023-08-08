package net.pilseong.todocompose.ui.screen.calendar

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.DecorationBox
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
import net.pilseong.todocompose.ui.components.ComposeGallery
import net.pilseong.todocompose.ui.components.DefaultDatePickerDialog
import net.pilseong.todocompose.ui.components.DefaultTimePickerDialog
import net.pilseong.todocompose.ui.components.DisplayAlertDialog
import net.pilseong.todocompose.ui.components.ReminderDropDown
import net.pilseong.todocompose.ui.components.TaskHeader
import net.pilseong.todocompose.ui.components.TaskHeaderType
import net.pilseong.todocompose.ui.components.convertToLocalEndTime
import net.pilseong.todocompose.ui.screen.list.MemoAction
import net.pilseong.todocompose.ui.theme.ALPHA_MEDIUM
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
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListSheet(
    selectedDate: LocalDate,
    taskUiStateList: SnapshotStateList<TaskUiState> = SnapshotStateList(),
    notes: List<MemoWithNotebook>,
    expanded: Boolean = false,
    onDismissRequest: () -> Unit,
    onAddClicked: () -> Unit,
    onValueChange: (TaskDetails) -> Unit,
    onEditClicked: (MemoWithNotebook) -> Unit,
    onDeleteClicked: (MemoWithNotebook) -> Unit,
) {
    Log.d("PHILIP", "[ScheduleListSheet] called")
    if (expanded) {
        val state = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = {
                Log.d("PHILIP", "SimpleDateRangePickerSheet onDismissRequest")
                onDismissRequest()
            },
            sheetState = state,
        ) {
            CalendarNoteList(
                selectedDate = selectedDate,
                taskUiStateList = taskUiStateList,
                notes = notes,
                onDismissRequest = onDismissRequest,
                onAddClicked = onAddClicked,
                onEditClicked = onEditClicked,
                onValueChange = onValueChange,
                onDeleteClicked = onDeleteClicked,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CalendarNoteList(
    selectedDate: LocalDate,
    taskUiStateList: SnapshotStateList<TaskUiState> = SnapshotStateList(),
    notes: List<MemoWithNotebook>,
    onDismissRequest: () -> Unit,
    onAddClicked: () -> Unit,
    onEditClicked: (MemoWithNotebook) -> Unit,
    onDeleteClicked: (MemoWithNotebook) -> Unit,
    onValueChange: (TaskDetails) -> Unit,
) {
    Column {
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

                        var expanded by remember { mutableStateOf(false) }
                        ReminderDropDown(
                            isNew = item.memo.id == Constants.NEW_ITEM_ID,
                            expanded = expanded,
                            enabled = item.memo.dueDate != null,
                            targetTime = item.memo.dueDate?.toInstant()
                                ?.toEpochMilli(),
                            onTimeSelected = { reminderType ->
                                expanded = false
                                onValueChange(
                                    item.toTaskDetails().copy(reminderType = reminderType)
                                )
                                onEditClicked(item)
                            },
                            onButtonClicked = {
                                if (item.memo.dueDate != null) expanded = true
                            },
                            onDismissRequest = { expanded = false }
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
                            .padding(vertical = MEDIUM_PADDING)
                            .fillMaxHeight()
                            .weight(if (item.memo.progression != State.NONE) 7 / 12f else 9 / 12f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.memo.title,
                            color = MaterialTheme.colorScheme.taskItemContentColor,
                            style = if (item.memo.description.isNotBlank()) MaterialTheme.typography.bodyLarge
                            else MaterialTheme.typography.bodySmall,
                            maxLines = if (item.memo.description.isNotBlank()) 1 else 4
                        )

                        if (item.memo.description.isNotBlank()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = item.memo.description,
                                color = MaterialTheme.colorScheme.taskItemContentColor,
                                style = MaterialTheme.typography.bodySmall,
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
                                text = taskUiState.taskDetails.dueDate!!.toLocalDateTime()
                                    .format(
                                        DateTimeFormatter.ofPattern(
                                            stringResource(id = R.string.task_content_dateformat)
                                        )
                                    ),
                                lineHeight = 20.sp
                            )

                            Row {
                                // 취소 하기
                                Icon(
                                    modifier = Modifier.clickable {
                                        isEditMode = false
                                    },
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "cancel icon"
                                )
                                Spacer(modifier = Modifier.width(LARGE_PADDING))
                                // 저장 하기
                                Icon(
                                    modifier = Modifier.clickable {
                                        onEditClicked(item)
                                        isEditMode = false
                                    },
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "done icon"
                                )
                            }
                        }
                        Surface(
                            color = if (item.memo.priority == Priority.NONE) MaterialTheme.colorScheme.surface else
                                item.memo.priority.color.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(
                                        start = XLARGE_PADDING,
                                        end = XLARGE_PADDING,
                                        bottom = SMALL_PADDING,
                                        top = SMALL_PADDING
                                    )
                                    .fillMaxWidth()
                            ) {
                                // 헤더 부분
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1.5F / 12)) {
                                        Icon(
                                            Icons.Filled.Info,
                                            contentDescription = "Localized description",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(3.5F / 12)) {
                                        Column {
                                            Text(
                                                text = stringResource(id = R.string.task_content_notebook_name),
                                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                                lineHeight = 20.sp
                                            )
                                            Text(
                                                stringResource(id = R.string.info_reminder),
                                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                                lineHeight = 20.sp
                                            )
                                            Text(
                                                stringResource(id = R.string.info_due_date),
                                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }
                                    Column(
                                        modifier = Modifier.weight(7F / 12),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            Text(
                                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                                text = item.notebook?.title
                                                    ?: stringResource(id = R.string.default_note_title),
                                                overflow = TextOverflow.Ellipsis,
                                                lineHeight = 20.sp,
                                                maxLines = 1
                                            )
                                            Row {
                                                var expanded by remember { mutableStateOf(false) }
                                                val focusManager = LocalFocusManager.current
                                                val angle by animateFloatAsState(
                                                    targetValue = if (expanded) 180F else 0F,
                                                    label = "expand icon"
                                                )
                                                ReminderDropDown(
                                                    isNew = taskUiState.taskDetails.id == Constants.NEW_ITEM_ID,
                                                    expanded = expanded,
                                                    enabled = true,
                                                    targetTime = taskUiState.taskDetails.dueDate?.toInstant()
                                                        ?.toEpochMilli(),
                                                    onTimeSelected = {
                                                        expanded = false
                                                        onValueChange(
                                                            taskUiState.taskDetails.copy(
                                                                reminderType = it
                                                            )
                                                        )
                                                    },
                                                    onButtonClicked = {
                                                        if (taskUiState.taskDetails.dueDate != null) expanded =
                                                            true
                                                    },
                                                    onDismissRequest = { expanded = false }
                                                ) {
                                                    Text(
                                                        modifier = Modifier
                                                            .padding(start = XLARGE_PADDING),
                                                        text = stringResource(id = taskUiState.taskDetails.reminderType.label),
                                                        fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                                                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Icon(
                                                        modifier = Modifier
                                                            .alpha(ALPHA_MEDIUM)
                                                            .rotate(angle)
                                                            .size(16.dp)
                                                            .clickable {
                                                                expanded = true
                                                                focusManager.clearFocus()
                                                            },
                                                        imageVector = Icons.Filled.ArrowDropDown,
                                                        contentDescription = stringResource(R.string.drop_down_menu_icon),
                                                    )
                                                }
                                            }

                                            val focusManager = LocalFocusManager.current

                                            var showDatePicker by remember { mutableStateOf(false) }
                                            var showTimePicker by remember { mutableStateOf(false) }
                                            val timeFormatter = remember {
                                                SimpleDateFormat(
                                                    "hh:mm a",
                                                    Locale.getDefault()
                                                )
                                            }
                                            var timeHours by remember { mutableStateOf<Int?>(null) }
                                            var timeMinutes by remember { mutableStateOf<Int?>(null) }

                                            val timePickerState = rememberTimePickerState()
                                            DefaultDatePickerDialog(
                                                openDialog = showDatePicker,
                                                onConfirm = { it ->
                                                    if (it != null) {
                                                        var instant =
                                                            convertToLocalEndTime(it, true)
                                                        var offset = OffsetDateTime.ofInstant(
                                                            Instant.ofEpochMilli(instant!!),
                                                            ZoneId.systemDefault()
                                                        )
                                                        Log.d(
                                                            "PHILIP",
                                                            "current time ${
                                                                Instant.ofEpochMilli(instant)
                                                            }"
                                                        )
                                                        Log.d("PHILIP", "current time $offset")

                                                        if (timeHours != null && timeMinutes != null) {
                                                            offset =
                                                                offset.plusHours(timeHours!!.toLong())
                                                                    .plusMinutes(timeMinutes!!.toLong())
                                                        }

                                                        onValueChange(
                                                            taskUiState.taskDetails.copy(
                                                                dueDate = ZonedDateTime.ofInstant(
                                                                    Instant.ofEpochMilli(offset.toEpochSecond() * 1000),
                                                                    ZoneId.systemDefault()
                                                                )
                                                            )
                                                        )

                                                        showDatePicker = false
                                                    }
                                                },
                                                onDismissRequest = {
                                                    showDatePicker = false
                                                }
                                            )

                                            DefaultTimePickerDialog(
                                                state = timePickerState,
                                                showTimePicker = showTimePicker,
                                                onConfirm = {
                                                    timeHours = timePickerState.hour
                                                    timeMinutes = timePickerState.minute

                                                    if (taskUiState.taskDetails.dueDate != null) {
                                                        var date = ZonedDateTime.of(
                                                            taskUiState.taskDetails.dueDate.year,
                                                            taskUiState.taskDetails.dueDate.monthValue,
                                                            taskUiState.taskDetails.dueDate.dayOfMonth,
                                                            0,
                                                            0,
                                                            0,
                                                            0,
                                                            ZoneId.systemDefault()
                                                        )
                                                        date =
                                                            date.plusHours(timeHours!!.toLong())
                                                                .plusMinutes(timeMinutes!!.toLong())

                                                        Log.d(
                                                            "PHILIP",
                                                            "DATE FROM ${date.toOffsetDateTime()}"
                                                        )
                                                        onValueChange(
                                                            taskUiState.taskDetails.copy(
                                                                dueDate = date
                                                            )
                                                        )
                                                    }
                                                    showTimePicker = false
                                                },
                                                onDismissRequest = {
                                                    showTimePicker = false
                                                }
                                            )

                                            Row {
                                                Row(
                                                    modifier = Modifier
                                                        .weight(6 / 12F),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Text(
                                                        modifier = Modifier
                                                            .weight(1F)
                                                            .padding(start = XLARGE_PADDING),
                                                        text = if (taskUiState.taskDetails.dueDate == null)
                                                            stringResource(id = R.string.memo_sorting_option_due_date)
                                                        else {
                                                            taskUiState.taskDetails.dueDate
                                                                .format(
                                                                    DateTimeFormatter.ofPattern(
                                                                        stringResource(id = R.string.datepicker_date_format)
                                                                    )
                                                                )
                                                        },
                                                        fontSize = 8.sp,
                                                        fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
                                                        color = MaterialTheme.colorScheme.onBackground
                                                    )
                                                    Icon(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clickable {
                                                                showDatePicker = true
                                                                focusManager.clearFocus()
                                                            },
                                                        imageVector = Icons.Default.EditCalendar,
                                                        contentDescription = "마감일 지정",
                                                        tint = MaterialTheme.colorScheme.onBackground
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier
                                                        .weight(6 / 12F),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Text(
                                                        modifier = Modifier
                                                            .weight(1F)
                                                            .padding(start = XLARGE_PADDING),
                                                        text = if (taskUiState.taskDetails.dueDate == null) {
                                                            if (timeHours != null && timeMinutes != null) {
                                                                val cal = Calendar.getInstance()
                                                                cal.set(
                                                                    Calendar.HOUR_OF_DAY,
                                                                    timeHours!!
                                                                )
                                                                cal.set(
                                                                    Calendar.MINUTE,
                                                                    timeMinutes!!
                                                                )
                                                                cal.set(Calendar.SECOND, 0)
                                                                cal.set(Calendar.MILLISECOND, 0)
                                                                timeFormatter.format(cal.time)
                                                            } else
                                                                stringResource(id = R.string.info_due_time)
                                                        } else {
                                                            taskUiState.taskDetails.dueDate.toLocalDateTime()
                                                                .format(
                                                                    DateTimeFormatter.ofPattern(
                                                                        "hh:mma"
                                                                    )
                                                                )
                                                        },
                                                        fontSize = 8.sp,
                                                        fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
                                                        color = MaterialTheme.colorScheme.onBackground
                                                    )
                                                    Icon(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clickable {
                                                                showTimePicker = true
                                                                focusManager.clearFocus()
                                                            },
                                                        imageVector = Icons.Default.LockClock,
                                                        contentDescription = "시간 지정",
                                                        tint = MaterialTheme.colorScheme.onBackground
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // 사진
                                if (item.photos.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = SMALL_PADDING),
                                        horizontalArrangement = Arrangement.Start,
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1.4F / 12),
                                        ) {
                                            Icon(
                                                modifier = Modifier.padding(top = 2.dp),
                                                imageVector = Icons.Filled.Photo,
                                                contentDescription = "Localized description",
                                                tint = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.8F
                                                )
                                            )
                                        }
                                        Column(modifier = Modifier.weight(10.6F / 12)) {
                                            ComposeGallery(
                                                photos = item.photos,
                                                onAddClicked = { /*TODO*/ },
                                                onImageClicked = {

                                                },
                                                onCameraClicked = {},
                                                onImagesSelected = {}
                                            )
                                        }
                                    }
                                }

                                // 제목
                                Row(
                                    modifier = Modifier
                                        .height(IntrinsicSize.Min)
                                        .fillMaxWidth()
                                        .padding(top = SMALL_PADDING),
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .weight(1.5F / 12),
                                        verticalArrangement = Arrangement.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Title,
                                            contentDescription = "Localized description",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.8F
                                            )
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(10.5F / 12),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        BasicTextField(
                                            value = taskUiState.taskDetails.title,
                                            onValueChange = {
                                                if (it.length <= Constants.MAX_CONTENT_LENGTH)
                                                    onValueChange(
                                                        taskUiState.taskDetails.copy(
                                                            title = it
                                                        )
                                                    )
                                            },
                                            modifier = Modifier
                                                .imePadding()
                                                .navigationBarsPadding()
                                                .weight(1F)
                                                .fillMaxWidth(),
                                            textStyle = TextStyle(fontSize = MaterialTheme.typography.bodySmall.fontSize),
                                            maxLines = 5,

                                            ) { innerTextField ->
                                            DecorationBox(
                                                value = taskUiState.taskDetails.title,
                                                innerTextField = innerTextField,
                                                enabled = true,
                                                singleLine = false,
                                                visualTransformation = VisualTransformation.None,
                                                interactionSource = remember { MutableInteractionSource() },
                                                contentPadding = PaddingValues(0.dp),
                                                supportingText = {
                                                    Text(
                                                        text = "${taskUiState.taskDetails.title.length} / ${Constants.MAX_TITLE_LENGTH}",
                                                        fontSize = 8.sp,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textAlign = TextAlign.End,
                                                    )
                                                },
                                                colors = TextFieldDefaults.colors(
                                                    unfocusedContainerColor = Color.Transparent,
                                                    focusedContainerColor = Color.Transparent,
                                                ),
                                            )
                                        }
                                    }
                                }

                                Row {
                                    BasicTextField(
                                        value = taskUiState.taskDetails.description,
                                        onValueChange = {
                                            if (it.length <= Constants.MAX_CONTENT_LENGTH)
                                                onValueChange(
                                                    taskUiState.taskDetails.copy(
                                                        description = it
                                                    )
                                                )
                                        },
                                        modifier = Modifier
                                            .imePadding()
                                            .navigationBarsPadding()
                                            .weight(1F)
                                            .fillMaxWidth(),
                                        textStyle = TextStyle(fontSize = MaterialTheme.typography.bodySmall.fontSize),
                                        maxLines = 5,
                                    ) { innerTextField ->
                                        DecorationBox(
                                            value = taskUiState.taskDetails.description,
                                            innerTextField = innerTextField,
                                            enabled = true,
                                            singleLine = false,
                                            visualTransformation = VisualTransformation.None,
                                            interactionSource = remember { MutableInteractionSource() },
                                            contentPadding = PaddingValues(0.dp),
                                            supportingText = {
                                                Text(
                                                    text = "${taskUiState.taskDetails.description.length} / ${Constants.MAX_CONTENT_LENGTH}",
                                                    fontSize = 8.sp,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.End,
                                                )
                                            },
                                            colors = TextFieldDefaults.colors(
                                                unfocusedContainerColor = Color.Transparent,
                                                focusedContainerColor = Color.Transparent,
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // 열린 채 뷰 모드
                } else {

                    var confirmExpanded by remember { mutableStateOf(false) }
                    DisplayAlertDialog(
                        title = stringResource(
                            id = R.string.delete_task_dialog_title,
                            item.memo.title
                        ),
                        message = stringResource(
                            id = R.string.delete_task_dialog_confirmation,
                            item.memo.title
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