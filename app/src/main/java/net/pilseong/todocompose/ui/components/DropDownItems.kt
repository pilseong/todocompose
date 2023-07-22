package net.pilseong.todocompose.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.ReminderTime
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.PRIORITY_INDICATOR_SIZE
import net.pilseong.todocompose.data.model.ui.NoteSortingOption

@Composable
fun PriorityItem(
    priority: Priority,
    label: Int? = null,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(
            modifier = Modifier
                .offset(0.dp, 0.8.dp)
                .size(PRIORITY_INDICATOR_SIZE)
        ) { drawCircle(color = priority.color) }
        Text(
            modifier = Modifier.padding(LARGE_PADDING),
            text = if (label == null) stringResource(id = priority.label) else  stringResource(id = label),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

//@Composable
//fun StateItem(
//    state: State
//) {
//    Row(verticalAlignment = Alignment.CenterVertically) {
//        Canvas(
//            modifier = Modifier
//                .offset(0.dp, 0.8.dp)
//                .size(PRIORITY_INDICATOR_SIZE)
//        ) { drawCircle(color = state.color) }
//        Text(
//            modifier = Modifier.padding(LARGE_PADDING),
//            text = stringResource(id = state.label),
//            style = MaterialTheme.typography.titleSmall,
//            color = MaterialTheme.colorScheme.onSurface
//        )
//    }
//}

@Composable
@Preview
fun PriorityItemPreview() {
    PriorityItem(priority = Priority.LOW)
}

@Composable
fun NoteSortItem(
    noteSortingOption: NoteSortingOption
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = noteSortingOption.label),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DropDownItem(
    option: ReminderTime
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = option.label),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
@Preview
fun PreviewNoteSortItem() {
    NoteSortItem(noteSortingOption = NoteSortingOption.ACCESS_AT)
}

@Composable
fun SortItem(
    enabled: Boolean,
    onclick: () -> Unit,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(LARGE_PADDING),
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Checkbox(checked = enabled, onCheckedChange = { onclick() })
    }
}

@Composable
fun StateItem(
    enabled: Boolean,
    state: State,
    onclick: () -> Unit,
    text: String
) {

    val noneColor = MaterialTheme.colorScheme.surface
    Row(
        modifier = Modifier.wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            Modifier.weight(1F),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(
                modifier = Modifier
                    .offset(0.dp, 0.8.dp)
                    .size(PRIORITY_INDICATOR_SIZE)
            ) {
                drawCircle(color = if (state != State.NONE) state.color else noneColor)
            }
            Text(
                modifier = Modifier.padding(start = LARGE_PADDING),
                text = text,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row { Checkbox(checked = enabled, onCheckedChange = { onclick() }) }
    }
}

@Composable
fun PriorityItem(
    enabled: Boolean,
    priority: Priority,
    onclick: () -> Unit,
    text: String
) {

    Row(
        modifier = Modifier.wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            Modifier.weight(1F),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(
                modifier = Modifier
                    .offset(0.dp, 0.8.dp)
                    .size(PRIORITY_INDICATOR_SIZE)
            ) {
                drawCircle(color = priority.color)
            }
            Text(
                modifier = Modifier.padding(start = LARGE_PADDING),
                text = text,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row {
            Checkbox(checked = enabled, onCheckedChange = { onclick() })
        }
    }
}


@Composable
@Preview
fun SortItemPreview() {
    SortItem(text = "오름차순",
        enabled = false,
        onclick = {}
    )
}

@Composable
@Preview
fun StateItemPreview() {
    StateItem(text = "오름차순",
        enabled = false,
        state = State.COMPLETED,
        onclick = {}
    )
}


@Composable
fun PriorityMenuItems(
    onPrioritySelected: (Priority) -> Unit
) {
    DropdownMenuItem(
        text = { PriorityItem(priority = Priority.HIGH) },
        onClick = {
            onPrioritySelected(Priority.HIGH)
        })
    DropdownMenuItem(
        text = { PriorityItem(priority = Priority.LOW) },
        onClick = {
            onPrioritySelected(Priority.LOW)
        })
    DropdownMenuItem(
        text = { PriorityItem(priority = Priority.NONE) },
        onClick = {
            onPrioritySelected(Priority.NONE)
        })
}

@Composable
fun SortMenuItems(
    onOptionSelected: (NoteSortingOption) -> Unit
) {
    DropdownMenuItem(
        text = { NoteSortItem(noteSortingOption = NoteSortingOption.ACCESS_AT) },
        onClick = {
            onOptionSelected(NoteSortingOption.ACCESS_AT)
        })
    DropdownMenuItem(
        text = { NoteSortItem(noteSortingOption = NoteSortingOption.UPDATED_AT) },
        onClick = {
            onOptionSelected(NoteSortingOption.UPDATED_AT)
        })
    DropdownMenuItem(
        text = { NoteSortItem(noteSortingOption = NoteSortingOption.CREATED_AT) },
        onClick = {
            onOptionSelected(NoteSortingOption.CREATED_AT)
        })
}

@Preview
@Composable
fun SortMenuItemsPreview() {
    MaterialTheme {
        SortMenuItems(onOptionSelected = {})
    }
}


@Composable
fun StateMenuItems(
    stateCompleted: Boolean = true,
    stateCancelled: Boolean = true,
    stateActive: Boolean = true,
    stateSuspended: Boolean = true,
    stateWaiting: Boolean = true,
    stateNone: Boolean = true,
    onStateSelected: (State) -> Unit,
    onToggleClicked: () -> Unit,
    onSetAllOrNothingClicked: (Boolean) -> Unit,
) {
    var trueCounter = 0

    State.values().reversed().forEach { state ->
        val enabled = when (state) {
            State.NONE -> stateNone
            State.WAITING -> stateWaiting
            State.SUSPENDED -> stateSuspended
            State.ACTIVE -> stateActive
            State.CANCELLED -> stateCancelled
            State.COMPLETED -> stateCompleted
        }
        if (enabled) trueCounter++
        DropdownMenuItem(
            text = {
                StateItem(state = state,
                    enabled = enabled,
                    text = stringResource(id = state.label),
                    onclick = { onStateSelected(state) }
                )
            },
            onClick = { onStateSelected(state) })
    }

    DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.status_toggle)) },
        onClick = onToggleClicked
    )

    DropdownMenuItem(
        text = {
            Text(
                text = if (trueCounter <= 3) stringResource(id = R.string.status_select_all)
                else stringResource(id = R.string.status_select_none)
            )
        },
        onClick = { onSetAllOrNothingClicked(trueCounter <= 3) })
}

@Composable
fun PriorityMenuItems(
    priorityHigh: Boolean = true,
    priorityMedium: Boolean = true,
    priorityLow: Boolean = true,
    priorityNone: Boolean = true,
    onPrioritySelected: (Priority) -> Unit,
) {
    DropdownMenuItem(
        text = {
            PriorityItem(priority = Priority.HIGH,
                enabled = priorityHigh,
                text = stringResource(id = Priority.HIGH.label),
                onclick = { onPrioritySelected(Priority.HIGH) }
            )
        },
        onClick = { onPrioritySelected(Priority.HIGH) })
    DropdownMenuItem(
        text = {
            PriorityItem(priority = Priority.MEDIUM,
                enabled = priorityMedium,
                text = stringResource(id = Priority.MEDIUM.label),
                onclick = { onPrioritySelected(Priority.MEDIUM) }
            )
        },
        onClick = { onPrioritySelected(Priority.MEDIUM) })
    DropdownMenuItem(
        text = {
            PriorityItem(priority = Priority.LOW,
                enabled = priorityLow,
                text = stringResource(id = Priority.LOW.label),
                onclick = { onPrioritySelected(Priority.LOW) }
            )
        },
        onClick = { onPrioritySelected(Priority.LOW) })
    DropdownMenuItem(
        text = {
            PriorityItem(priority = Priority.NONE,
                enabled = priorityNone,
                text = stringResource(id = Priority.NONE.label),
                onclick = {
                    onPrioritySelected(Priority.NONE)
                }
            )
        },
        onClick = { onPrioritySelected(Priority.NONE) })
}


@Composable
fun StateListItem(
    state: State,
) {
    Text(
        text = stringResource(id = state.label),
        style = MaterialTheme.typography.labelMedium,
        fontSize = MaterialTheme.typography.labelMedium.fontSize,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
@Preview
fun StateListItemPreview() {
    StateListItem(state = State.COMPLETED)
}
