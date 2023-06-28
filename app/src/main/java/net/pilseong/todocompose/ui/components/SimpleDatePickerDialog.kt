package net.pilseong.todocompose.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
        initialDisplayMode = DisplayMode.Picker
    )
    if (enabled) {
        DatePickerDialog(
            modifier = Modifier.fillMaxSize(),
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
                Log.d("PHILIP", "start ${dateRangePickerState.selectedStartDateMillis}")
                Log.d("PHILIP", "end ${dateRangePickerState.selectedEndDateMillis}")
                Button(
                    onClick = {
//                        Log.d("PHILIP", "${dateRangePickerState.selectedStartDateMillis}")
//                        Log.d("PHILIP", "${dateRangePickerState.selectedEndDateMillis}")
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
            Surface(modifier = Modifier.fillMaxSize()) {
                Column() {
                    DateRangePicker(
                        modifier = Modifier.padding(MEDIUM_PADDING),
                        dateFormatter = DatePickerDefaults.dateFormatter(
                            selectedDateSkeleton = "MM/dd"
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
                                    text = stringResource(id = R.string.date_picker_title),
                                    fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                                )
                            }
                        },
                    )

                }
            }

        }
    }
}


@Preview
@Composable
fun PreviewSimpleDatePickerDialog() {
    MaterialTheme {
        SimpleDatePickerDialog(
            enabled = true,
            onDismiss = { /*TODO*/ },
            onConfirmClick = { a, b ->
                Log.d("test", "test")
            })
    }
}