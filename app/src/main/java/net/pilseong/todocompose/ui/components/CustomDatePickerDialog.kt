package net.pilseong.todocompose.ui.components


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import net.pilseong.todocompose.R
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDateRangePickerSheet(
    titleResource: Int,
    datePickerExpanded: Boolean = false,
    onDismissRequest: () -> Unit,
    onConfirmClick: (Long?, Long?) -> Unit
) {
    if (datePickerExpanded) {
        val state = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = convertLocalTime(
                OffsetDateTime.now().minusDays(7).toInstant()
                    .toEpochMilli()
            ),
            initialSelectedEndDateMillis = convertLocalTime(Instant.now().toEpochMilli()),
            yearRange = IntRange(2000, 2100),
            initialDisplayMode = DisplayMode.Picker
        )

        ModalBottomSheet(
            onDismissRequest = {
                Log.d("PHILIP", "SimpleDateRangePickerSheet onDismissRequest")
                onDismissRequest()
            },
            sheetState = state,
        ) {
            Column(modifier = Modifier.padding(horizontal = XLARGE_PADDING)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(700.dp)
                ) {
                    CustomDateRangePicker(
                        titleResource = titleResource,
                        state = dateRangePickerState,
                        onDismissRequest = onDismissRequest,
                        onConfirmClick = onConfirmClick,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDatePickerSheet(
    titleResource: Int,
    datePickerExpanded: Boolean = false,
    onDismissRequest: () -> Unit,
    onConfirmClick: (Long?) -> Unit
) {
    if (datePickerExpanded) {
        val state = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = convertToLocalEndTime(
                ZonedDateTime.now().toEpochSecond() * 1000, false
            ),
            yearRange = IntRange(2000, 2100),
            initialDisplayMode = DisplayMode.Picker
        )

        ModalBottomSheet(
            onDismissRequest = {
                Log.d("PHILIP", "SimpleDateRangePickerSheet onDismissRequest")
                onDismissRequest()
            },
            sheetState = state,
        ) {
            Column(modifier = Modifier.padding(horizontal = XLARGE_PADDING)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(700.dp)
                ) {
                    CustomDatePicker(
                        titleResource = titleResource,
                        state = datePickerState,
                        onDismissRequest = onDismissRequest,
                        onConfirmClick = onConfirmClick,
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 720, heightDp = 360)
@Composable
fun PreviewSimpleDateRangePickerSheet() {
    MaterialTheme {
        SimpleDateRangePickerSheet(
            titleResource = R.string.date_picker_title,
            datePickerExpanded = true,
            onDismissRequest = { /*TODO*/ },
            onConfirmClick = { _, _ -> })
    }

}

@Composable
fun getFormattedDate(timeInMillis: Long): String {
    val calender = Calendar.getInstance()
    calender.timeInMillis = timeInMillis
    val dateFormat = SimpleDateFormat(stringResource(id = R.string.datepicker_date_format))
    return dateFormat.format(calender.timeInMillis)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateRangePicker(
    state: DateRangePickerState,
    titleResource: Int,
    onDismissRequest: () -> Unit,
    onConfirmClick: (Long?, Long?) -> Unit
) {
    DateRangePicker(
        state,
        modifier = Modifier,
        dateFormatter = DatePickerFormatter(
            selectedDateSkeleton = "MM/dd"
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MEDIUM_PADDING),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = titleResource),
                    fontStyle = MaterialTheme.typography.titleMedium.fontStyle,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }
        },
        headline = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = XLARGE_PADDING,
                        vertical = LARGE_PADDING
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (state.selectedStartDateMillis != null)
                                getFormattedDate(state.selectedStartDateMillis!!)
                            else stringResource(id = R.string.datepicker_start_label),
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )

                    }
                    Row(
                        modifier = Modifier.padding(horizontal = SMALL_PADDING),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "~",
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (state.selectedEndDateMillis != null)
                                getFormattedDate(state.selectedEndDateMillis!!)
                            else stringResource(id = R.string.datepicker_end_label),
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {
                                onDismissRequest()
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                    Spacer(modifier = Modifier.width(LARGE_PADDING))
                    Row(
                        modifier = Modifier
                            .clickable {
                                onConfirmClick(
                                    convertToLocalEndTime(state.selectedStartDateMillis, true),
                                    convertToLocalEndTime(state.selectedEndDateMillis, false)
                                )
                                onDismissRequest()
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = "Ok")
                    }
                }

            }
        },
        showModeToggle = false,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    state: DatePickerState,
    titleResource: Int,
    onDismissRequest: () -> Unit,
    onConfirmClick: (Long?) -> Unit
) {
    DatePicker(
        state = state,
        modifier = Modifier,
        dateFormatter = DatePickerFormatter(
            selectedDateSkeleton = "MM/dd"
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MEDIUM_PADDING),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = titleResource),
                    fontStyle = MaterialTheme.typography.titleMedium.fontStyle,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }
        },
        headline = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = XLARGE_PADDING,
                        vertical = LARGE_PADDING
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (state.selectedDateMillis != null)
                                getFormattedDate(state.selectedDateMillis!!)
                            else stringResource(id = R.string.datepicker_start_label),
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {
                                onDismissRequest()
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                    Spacer(modifier = Modifier.width(LARGE_PADDING))
                    Row(
                        modifier = Modifier
                            .clickable {
                                onConfirmClick(
                                    convertToLocalEndTime(state.selectedDateMillis, true),
                                )
                                onDismissRequest()
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = "Ok")
                    }
                }

            }
        },
        showModeToggle = false,
    )
}

// 달력에서 선택한 날짜는 UTC기준의 날짜가 나온다. 그래서 로컬시각은 각 날짜에서 offset을 빼주어야 local time과 일치하게 된다.
// 예를 들어 7월 1일을 선택하면 7월1일 00:00Z 가 반환되는데 이 시간은 7월 1일 09:00+09:00 으로 변환된다.
// 그래서 달려 기준으로 날짜를 맞추고 싶으면 해당 timestamp에서 offset을 빼주어야 하는 것이다.
fun convertToLocalEndTime(timestamp: Long?, isStart: Boolean): Long? {
    if (timestamp == null) return null

    val instant = Instant.ofEpochMilli(timestamp)
    val zoned = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())


    // 시작과 끝을 달리해야 한다.
    // 시작은 6월 1일이면 기본적으로 UTC 기준으로 날짜가 계산되므로 offset을 뺀다.
    // 그러면 정확한 local 0시가 나온다.
    // 끝날은 6월 1일이면 그날의 23시59분까지 범위를 조회하므로 offset을 뺀 후에 하루를 더 해준다. 그리고 1초를 빼준다.
    return if (isStart) {
        val mutated = Instant.ofEpochSecond(instant.epochSecond - zoned.offset.totalSeconds)
        mutated.toEpochMilli()
    } else {
        val mutated = Instant.ofEpochSecond(instant.epochSecond - zoned.offset.totalSeconds - 1)
        val corrected = ZonedDateTime.ofInstant(mutated, ZoneId.systemDefault()).plusDays(1)
        corrected.toInstant().toEpochMilli()
    }
}


// 달력은 현재 로컬의 날짜가 보여기게 된다. 현재 시간을 달력 표기에 맞추기 위해서는 UTC기준인 날짜 + offset을 해주어야 한다.
fun convertLocalTime(timestamp: Long?): Long? {
    if (timestamp == null) return null

    val instant = Instant.ofEpochMilli(timestamp)
    val zoned = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())

    val changedTime = Instant.ofEpochSecond(instant.epochSecond + zoned.offset.totalSeconds)
    Log.d("PHILIP", "time change ${OffsetDateTime.ofInstant(changedTime, ZoneId.systemDefault())}")
    Log.d("PHILIP", "time change ${OffsetDateTime.ofInstant(instant, ZoneId.systemDefault())}")
    return changedTime.toEpochMilli()
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview16() {
    MaterialTheme {
        SimpleDateRangePickerSheet(
            titleResource = R.string.date_picker_title,
            datePickerExpanded = true,
            onDismissRequest = {},
            onConfirmClick = { _, _ -> },

            )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewDateRangePickerSample() {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = convertLocalTime(
            OffsetDateTime.now().minusDays(7).toInstant()
                .toEpochMilli()
        ),
        initialSelectedEndDateMillis = convertLocalTime(Instant.now().toEpochMilli()),
        yearRange = IntRange(2000, 2100), // available years
        initialDisplayMode = DisplayMode.Picker
    )
    MaterialTheme {
        CustomDateRangePicker(
            titleResource = R.string.date_picker_title,
            state = dateRangePickerState,
            onConfirmClick = { _, _ -> },
            onDismissRequest = {}
        )
    }
}


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultDatePickerDialog(
    openDialog: Boolean = false,
    onConfirm: (Long?) -> Unit,
    onDismissRequest: () -> Unit,
) {

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = convertToLocalEndTime(
            ZonedDateTime.now().toEpochSecond() * 1000, false
        ),
        yearRange = IntRange(2000, 2100),
        initialDisplayMode = DisplayMode.Picker
    )


    if (openDialog) {
        val confirmEnabled by derivedStateOf { datePickerState.selectedDateMillis != null }
        DatePickerDialog(
            onDismissRequest = { onDismissRequest() },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(datePickerState.selectedDateMillis)
                    },
                    enabled = confirmEnabled
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismissRequest() }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview
@Composable
fun PreViewDefaultDatePickerDialog(
) {
    TodoComposeTheme() {
        DefaultDatePickerDialog(
            openDialog = true,
            onConfirm = { /*TODO*/ }) { }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTimePickerDialog(
    state : TimePickerState,
    showTimePicker: Boolean = false,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val showingPicker = remember { mutableStateOf(true) }


    if (showTimePicker) {
        TimePickerDialog(
            title = if (showingPicker.value) {
                "Select Time "
            } else {
                "Enter Time"
            },
            onCancel = onDismissRequest,
            onConfirm = {
//                val cal = Calendar.getInstance()
//                cal.set(Calendar.HOUR_OF_DAY, state.hour)
//                cal.set(Calendar.MINUTE, state.minute)
//                cal.set(Calendar.SECOND, 0)
//                cal.set(Calendar.MILLISECOND, 0)
//
//                cal.isLenient = false

                onConfirm()

            },
        ) {
            TimePicker(state = state)
        }
    }
}


@Composable
fun TimePickerDialog(
    title: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = {
        onCancel()
    }) {
        Surface(
            modifier = Modifier
                .wrapContentHeight()
                .width(320.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(XLARGE_PADDING),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = title)
                }

                content()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = XLARGE_PADDING, bottom = XLARGE_PADDING),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onCancel() }) {
                        Text(text = stringResource(id = R.string.label_no))
                    }
                    TextButton(onClick = { onConfirm() }) {
                        Text(text = stringResource(id = R.string.label_yes))
                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewDefaultTimePickerDialog() {
    TodoComposeTheme {
        DefaultTimePickerDialog(
            state = rememberTimePickerState(),
            showTimePicker = true,
            onConfirm = {},
            onDismissRequest = {}
        )
    }
}