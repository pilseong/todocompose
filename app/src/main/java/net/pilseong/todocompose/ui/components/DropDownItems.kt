package net.pilseong.todocompose.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.State
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.PRIORITY_INDICATOR_SIZE

@Composable
fun PriorityItem(
    priority: Priority
) {
    Row(
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
            modifier = Modifier.padding(LARGE_PADDING),
            text = stringResource(id = priority.label),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
@Preview
fun PriorityItemPreview() {
    PriorityItem(priority = Priority.LOW)
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
        Checkbox(checked = enabled, onCheckedChange = {
            onclick()
        })
    }
}

@Composable
fun StateItem(
    enabled: Boolean,
    state: State,
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
                drawCircle(color = state.color)
            }
            Text(
                modifier = Modifier.padding(start = LARGE_PADDING),
                text = text,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row {
            Checkbox(checked = enabled, onCheckedChange = {
                onclick()
            })
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
        state = State.CLOSED,
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
fun StateMenuItems(
    stateClosed: Boolean = true,
    stateOnit: Boolean = true,
    stateSuspended: Boolean = true,
    stateOpen: Boolean = true,
    stateNone: Boolean = true,
    onStateSelected: (State) -> Unit
) {
    DropdownMenuItem(
        text = {
            StateItem(state = State.CLOSED,
                enabled = stateClosed,
                text = stringResource(id = State.CLOSED.label),
                onclick = {
                    onStateSelected(State.CLOSED)
                }
            )
        },
        onClick = {
            onStateSelected(State.CLOSED)
        })
    DropdownMenuItem(
        text = {
            StateItem(state = State.ONIT,
                enabled = stateOnit,
                text = stringResource(id = State.ONIT.label),
                onclick = {
                    onStateSelected(State.ONIT)
                }
            )
        },
        onClick = {
            onStateSelected(State.ONIT)
        })
    DropdownMenuItem(
        text = {
            StateItem(state = State.SUSPENDED,
                enabled = stateSuspended,
                text = stringResource(id = State.SUSPENDED.label),
                onclick = {
                    onStateSelected(State.SUSPENDED)
                }
            )
        },
        onClick = {
            onStateSelected(State.SUSPENDED)
        })
    DropdownMenuItem(
        text = {
            StateItem(state = State.OPEN,
                enabled = stateOpen,
                text = stringResource(id = State.OPEN.label),
                onclick = {
                    onStateSelected(State.OPEN)
                }
            )
        },
        onClick = {
            onStateSelected(State.OPEN)
        })
    DropdownMenuItem(
        text = {
            StateItem(state = State.NONE,
                enabled = stateNone,
                text = stringResource(id = State.NONE.label),
                onclick = {
                    onStateSelected(State.NONE)
                }
            )
        },
        onClick = {
            onStateSelected(State.NONE)
        })
}


@Composable
fun StateMenuListItems(
    onStateSelected: (State) -> Unit
) {
    DropdownMenuItem(
        text = {
            StateListItem(state = State.CLOSED,
                onclick = {
                    onStateSelected(State.CLOSED)
                }
            )
        },
        onClick = {
            onStateSelected(State.CLOSED)
        })
    DropdownMenuItem(
        text = {
            StateListItem(state = State.ONIT,
                onclick = {
                    onStateSelected(State.ONIT)
                }
            )
        },
        onClick = {
            onStateSelected(State.ONIT)
        })
    DropdownMenuItem(
        text = {
            StateListItem(state = State.SUSPENDED,
                onclick = {
                    onStateSelected(State.SUSPENDED)
                }
            )
        },
        onClick = {
            onStateSelected(State.SUSPENDED)
        })
    DropdownMenuItem(
        text = {
            StateListItem(state = State.OPEN,
                onclick = {
                    onStateSelected(State.OPEN)
                }
            )
        },
        onClick = {
            onStateSelected(State.OPEN)
        })
    DropdownMenuItem(
        text = {
            StateListItem(state = State.NONE,
                onclick = {
                    onStateSelected(State.NONE)
                }
            )
        },
        onClick = {
            onStateSelected(State.NONE)
        })
}


@Composable
fun StateListItem(
    state: State,
    onclick: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable {
            onclick()
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
//                modifier = Modifier.padding(start = LARGE_PADDING),
                text = stringResource(id = state.label),
                style = MaterialTheme.typography.labelMedium,
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
@Preview
fun StateListItemPreview() {
    StateListItem(
        state = State.CLOSED,
        onclick = {}
    )
}
