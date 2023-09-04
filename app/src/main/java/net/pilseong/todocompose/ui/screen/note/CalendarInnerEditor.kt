package net.pilseong.todocompose.ui.screen.note

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.ui.components.ComposeGallery
import net.pilseong.todocompose.ui.components.DefaultDatePickerDialog
import net.pilseong.todocompose.ui.components.DefaultTimePickerDialog
import net.pilseong.todocompose.ui.components.ReminderDropDown
import net.pilseong.todocompose.ui.components.StatusDropDown
import net.pilseong.todocompose.ui.components.convertToLocalEndTime
import net.pilseong.todocompose.ui.theme.ALPHA_MEDIUM
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import net.pilseong.todocompose.ui.viewmodel.TaskUiState
import net.pilseong.todocompose.util.Constants
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CalendarInnerEditor(
    taskUiState: TaskUiState,
    onCancelClicked: () -> Unit,
    onEditClicked: (MemoWithNotebook) -> Unit,
    item: MemoWithNotebook,
    onValueChange: (TaskDetails) -> Unit,
    dueDate: ZonedDateTime?
) {
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
                        onCancelClicked()
                    },
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "cancel icon"
                )
                Spacer(modifier = Modifier.width(LARGE_PADDING))
                // 저장 하기
                Icon(
                    modifier = Modifier.clickable {
                        onEditClicked(item)
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
                    // 인포 아이콘
                    Column(modifier = Modifier.weight(1.5F / 12)) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                        )
                    }
                    // 제목 목록
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
                            Text(
                                stringResource(id = R.string.badge_state_label),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // 수정할 정보
                    Column(
                        modifier = Modifier.weight(7F / 12),
                        horizontalAlignment = Alignment.End
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            // 노트 이름
                            Text(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = item.notebook?.title
                                    ?: stringResource(id = R.string.default_note_title),
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 20.sp,
                                maxLines = 1
                            )

                            // 알림 설정
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

                            // 마감일 지정
                            val focusManager = LocalFocusManager.current

                            var showDatePicker by remember { mutableStateOf(false) }
                            var showTimePicker by remember { mutableStateOf(false) }
                            val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault())}
                            var timeHours by remember { mutableStateOf<Int?>(dueDate?.hour) }
                            var timeMinutes by remember { mutableStateOf<Int?>(dueDate?.minute) }

                            Log.d("PHILIP", "time setting ${dueDate?.hour} ${dueDate?.minute}")

                            val timePickerState = rememberTimePickerState(
                                initialHour = dueDate?.hour ?: 0,
                                initialMinute = dueDate?.minute ?: 0,
                            )

                            DefaultDatePickerDialog(
                                date = dueDate,
                                openDialog = showDatePicker,
                                onConfirm = {
                                    if (it != null) {
                                        val instant =
                                            convertToLocalEndTime(it, true)
                                        var offset = OffsetDateTime.ofInstant(
                                            Instant.ofEpochMilli(instant!!),
                                            ZoneId.systemDefault()
                                        )
                                        Log.d("PHILIP","current time ${Instant.ofEpochMilli(instant)}")
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
                                            dueDate!!.year,
                                            dueDate.monthValue,
                                            dueDate.dayOfMonth,
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
                                            dueDate!!
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

                                // due date 설정
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
                                            dueDate!!.toLocalDateTime()
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
                            Row {
                                var expanded by remember { mutableStateOf(false) }
                                val angle by animateFloatAsState(
                                    targetValue = if (expanded) 180F else 0F,
                                    label = "expand icon"
                                )
                                StatusDropDown(
                                    isNew = taskUiState.taskDetails.id == Constants.NEW_ITEM_ID,
                                    expanded = expanded,
                                    enabled = true,
                                    onStateSelected = {
                                        expanded = false
                                        onValueChange(
                                            taskUiState.taskDetails.copy(
                                                progression = it
                                            )
                                        )
                                    },
                                    onButtonClicked = {
                                        expanded = true
                                    },
                                    onDismissRequest = { expanded = false }
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(start = XLARGE_PADDING),
                                        text = stringResource(id = taskUiState.taskDetails.progression.label),
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
                            TextFieldDefaults.DecorationBox(
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
                        TextFieldDefaults.DecorationBox(
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
}