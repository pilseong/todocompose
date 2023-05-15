package net.pilseong.todocompose.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import net.pilseong.todocompose.R
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import java.time.Instant
import java.time.OffsetDateTime

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SimpleDatePickerDialog(
    enabled: Boolean = false,
    onDismiss: () -> Unit,
    onConfirmClick: (Long?, Long?) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = OffsetDateTime.now().minusDays(7).toInstant()
            .toEpochMilli(),
        initialSelectedEndDateMillis = Instant.now().toEpochMilli(),
        yearRange = IntRange(2000, 2100), // available years
        initialDisplayMode = DisplayMode.Input
    )
    if (enabled) {
        DatePickerDialog(
            onDismissRequest = {
                onDismiss()
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { onDismiss() },
                ) {
                    Text(text = stringResource(id = R.string.label_no))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
//                        Log.i("PHILIP", "${dateRangePickerState.selectedStartDateMillis}")
//                        Log.i("PHILIP", "${dateRangePickerState.selectedEndDateMillis}")
//                        if (dateRangePickerState.selectedStartDateMillis != null &&
//                            dateRangePickerState.selectedEndDateMillis != null
//                        ) {
                        onConfirmClick(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                        onDismiss()
//                        } else {
//                            Toast.makeText(content, "Not Valid Date Range", Toast.LENGTH_SHORT)
//                                .show()
//                        }
                    },
                ) {
                    Text(text = stringResource(id = R.string.label_yes))
                }
            }
        ) {
            DateRangePicker(
                modifier = Modifier.padding(MEDIUM_PADDING),
                dateFormatter = DatePickerFormatter(
                    selectedDateSkeleton = "yy/MM/dd"
                ),
                state = dateRangePickerState,
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MEDIUM_PADDING),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Pick up a date range",
                        )
                    }
                },
            )
        }
    }
}
