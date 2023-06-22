package net.pilseong.todocompose.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.State
import net.pilseong.todocompose.ui.theme.ALPHA_MEDIUM
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.PRIORITY_DROPDOWN_HEIGHT
import net.pilseong.todocompose.ui.theme.PRIORITY_INDICATOR_SIZE
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING

@Composable
fun PriorityDropDown(
    modifier: Modifier = Modifier,
    isNew: Boolean = false,
    priority: Priority,
    onPrioritySelected: (Priority) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val angle by animateFloatAsState(
        targetValue = if (expanded) 180F else 0F
    )
    val focusManager = LocalFocusManager.current
    var showInitalValue by remember { mutableStateOf(isNew) }

    Row(
        modifier = Modifier
            .height(PRIORITY_DROPDOWN_HEIGHT)
            .clickable {
                expanded = true
                expanded = true
                focusManager.clearFocus()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!showInitalValue) {
            Canvas(
                modifier = Modifier
                    .padding(horizontal = LARGE_PADDING)
                    .size(PRIORITY_INDICATOR_SIZE)
            ) {
                drawCircle(color = priority.color)
            }

            Text(
                modifier = Modifier.weight(1F),
                text = stringResource(id = priority.label),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Text(
                modifier = Modifier
                    .padding(start = XLARGE_PADDING)
                    .weight(1F),
                text = stringResource(id = R.string.badge_priority_label),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        IconButton(
            modifier = Modifier
                .alpha(ALPHA_MEDIUM)
                .rotate(angle),
            onClick = {
                expanded = true
                focusManager.clearFocus()
            }
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = stringResource(R.string.drop_down_menu_icon),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        DropdownMenu(
            modifier = modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { PriorityItem(priority = Priority.HIGH) },
                onClick = {
                    expanded = false
                    onPrioritySelected(Priority.HIGH)
                    if (showInitalValue) showInitalValue = false
                })
            DropdownMenuItem(
                text = { PriorityItem(priority = Priority.MEDIUM) },
                onClick = {
                    expanded = false
                    onPrioritySelected(Priority.MEDIUM)
                    if (showInitalValue) showInitalValue = false
                })
            DropdownMenuItem(
                text = { PriorityItem(priority = Priority.LOW) },
                onClick = {
                    expanded = false
                    onPrioritySelected(Priority.LOW)
                    if (showInitalValue) showInitalValue = false
                })
        }
    }
}

@Composable
fun StatusDropDown(
    modifier: Modifier = Modifier,
    isNew: Boolean = false,
    state: State,
    onStateSelected: (State) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val angle by animateFloatAsState(
        targetValue = if (expanded) 180F else 0F
    )
    val focusManager = LocalFocusManager.current
    var showInitalValue by remember { mutableStateOf(isNew) }

    Row(
        modifier = Modifier
            .height(PRIORITY_DROPDOWN_HEIGHT)
            .clickable {
                expanded = true
                expanded = true
                focusManager.clearFocus()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!showInitalValue) {
            Canvas(
                modifier = Modifier
                    .padding(horizontal = LARGE_PADDING)
                    .size(PRIORITY_INDICATOR_SIZE)
            ) {
                drawCircle(color = state.color)
            }

            Text(
                modifier = Modifier.weight(1F),
                text = stringResource(id = state.label),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Text(
                modifier = Modifier
                    .padding(start = XLARGE_PADDING)
                    .weight(1F),
                text = stringResource(id = R.string.badge_state_label),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        IconButton(
            modifier = Modifier
                .alpha(ALPHA_MEDIUM)
                .rotate(angle),
            onClick = {
                expanded = true
                focusManager.clearFocus()
            }
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = stringResource(R.string.drop_down_menu_icon),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        DropdownMenu(
            modifier = modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            DropdownMenuItem(
                text = { StateItem(state = State.COMPLETED) },
                onClick = {
                    expanded = false
                    onStateSelected(State.COMPLETED)
                    if (showInitalValue) showInitalValue = false
                })
            DropdownMenuItem(
                text = { StateItem(state = State.ACTIVE) },
                onClick = {
                    expanded = false
                    onStateSelected(State.ACTIVE)
                    if (showInitalValue) showInitalValue = false
                })
            DropdownMenuItem(
                text = { StateItem(state = State.SUSPENDED) },
                onClick = {
                    expanded = false
                    onStateSelected(State.SUSPENDED)
                    if (showInitalValue) showInitalValue = false
                })
            DropdownMenuItem(
                text = { StateItem(state = State.WAITING) },
                onClick = {
                    expanded = false
                    onStateSelected(State.WAITING)
                    if (showInitalValue) showInitalValue = false
                })
            DropdownMenuItem(
                text = { StateItem(state = State.NONE) },
                onClick = {
                    expanded = false
                    onStateSelected(State.NONE)
                    if (showInitalValue) showInitalValue = false
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PriorityDropDownPreview() {

    TodoComposeTheme {
        PriorityDropDown(
            priority = Priority.HIGH,
            onPrioritySelected = {}
        )
    }

}