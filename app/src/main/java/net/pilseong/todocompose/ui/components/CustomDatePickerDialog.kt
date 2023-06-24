package net.pilseong.todocompose.ui.components


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
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
    datePickerExpanded: Boolean = false,
    onDismissRequest: () -> Unit,
    onConfirmClick: (Long?, Long?) -> Unit
) {
    if (datePickerExpanded) {
        val state = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = OffsetDateTime.now().minusDays(7).toInstant()
                .toEpochMilli(),
            initialSelectedEndDateMillis = Instant.now().toEpochMilli(),
            yearRange = IntRange(2000, 2100),
            initialDisplayMode = DisplayMode.Picker
        )

        ModalBottomSheet(
            onDismissRequest = {
                Log.i("PHILIP", "SimpleDateRangePickerSheet onDismissRequest")
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
                        state = dateRangePickerState,
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
            datePickerExpanded = true,
            onDismissRequest = { /*TODO*/ }, onConfirmClick = { _, _ -> })
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
    onDismissRequest: () -> Unit,
    onConfirmClick: (Long?, Long?) -> Unit
) {
    DateRangePicker(
        state,
        modifier = Modifier,
        dateFormatter = DatePickerDefaults.dateFormatter(
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
                    text = stringResource(id = R.string.date_picker_title),
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

private fun convertToLocalEndTime(timestamp: Long?, isStart: Boolean): Long? {
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


@Preview(showBackground = true)
@Composable
fun GreetingPreview16() {
    MaterialTheme {
        SimpleDateRangePickerSheet(
            datePickerExpanded = true,
            onDismissRequest = {},
            onConfirmClick = { a, b -> },

            )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewDateRangePickerSample() {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = OffsetDateTime.now().minusDays(7).toInstant()
            .toEpochMilli(),
        initialSelectedEndDateMillis = Instant.now().toEpochMilli(),
        yearRange = IntRange(2000, 2100), // available years
        initialDisplayMode = DisplayMode.Picker
    )
    MaterialTheme {
        CustomDateRangePicker(
            state = dateRangePickerState,
            onConfirmClick = { a, b -> },
            onDismissRequest = {}
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestCode() {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

// App content
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            Modifier.toggleable(
                value = skipPartiallyExpanded,
                role = Role.Checkbox,
                onValueChange = { checked -> skipPartiallyExpanded = checked }
            )
        ) {
            Checkbox(checked = skipPartiallyExpanded, onCheckedChange = null)
            Spacer(Modifier.width(16.dp))
            Text("Skip partially expanded State")
        }
        Button(onClick = { openBottomSheet = !openBottomSheet }) {
            Text(text = "Show Bottom Sheet")
        }
    }

// Sheet content
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    // Note: If you provide logic outside of onDismissRequest to remove the sheet,
                    // you must additionally handle intended state cleanup, if any.
                    onClick = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openBottomSheet = false
                            }
                        }
                    }
                ) {
                    Text("Hide Bottom Sheet")
                }
            }
            var text by remember { mutableStateOf("") }
            OutlinedTextField(value = text, onValueChange = { text = it })
            LazyColumn {
                items(50) {
                    ListItem(
                        headlineContent = { Text("Item $it") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Localized description"
                            )
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewTest() {
    MaterialTheme {
        TestCode()
    }
}

