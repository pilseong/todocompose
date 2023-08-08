package net.pilseong.todocompose.ui.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.ReminderType
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.ui.theme.ALPHA_MEDIUM
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.PRIORITY_DROPDOWN_HEIGHT
import net.pilseong.todocompose.ui.theme.PRIORITY_INDICATOR_SIZE
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import java.util.Calendar

@Composable
fun PriorityDropDown(
    isNew: Boolean = false,
    priority: Priority,
    onPrioritySelected: (Priority) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val angle by animateFloatAsState(
        targetValue = if (expanded) 180F else 0F
    )
    val focusManager = LocalFocusManager.current
    var showInitialValue by remember { mutableStateOf(isNew) }

    Row(
        modifier = Modifier
            .height(PRIORITY_DROPDOWN_HEIGHT)
            .clickable {
                expanded = true
                focusManager.clearFocus()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!showInitialValue) {
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
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                modifier = Modifier
                    .padding(start = XLARGE_PADDING)
                    .weight(1F),
                text = stringResource(id = R.string.badge_priority_label),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
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
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Priority.values().forEach { priority ->
                DropdownMenuItem(
                    text = { PriorityItem(priority = priority) },
                    onClick = {
                        expanded = false
                        onPrioritySelected(priority)
                        if (showInitialValue) showInitialValue = false
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebooksDropDown(
    notebooks: List<NotebookWithCount>,
    notebookTitle: String?,
    onNotebookSelected: (Long) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    var notebookTitleInside by remember { mutableStateOf(notebookTitle) }
    val angle by animateFloatAsState(
        targetValue = if (expanded) 180F else 0F
    )
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .height(PRIORITY_DROPDOWN_HEIGHT)
            .clickable {
                expanded = true
                focusManager.clearFocus()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Log.d("PHILIP", "notebook data - $notebookTitleInside")
        Text(
            modifier = Modifier
                .padding(start = XLARGE_PADDING)
                .weight(1F),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            text = if (notebookTitleInside.isNullOrEmpty()) stringResource(id = R.string.default_note_title)
            else notebookTitleInside!!,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

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
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Surface(
                modifier = Modifier
                    .height(300.dp)
//                    .padding(XLARGE_PADDING)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(4.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
            ) {
                Box(modifier = Modifier.size(width = 300.dp, height = 300.dp)) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(LARGE_PADDING)) {
                        items(
                            items = notebooks,
                            key = { notebook ->
                                notebook.id
                            }
                        ) { item ->
                            DropdownMenuItem(text = {
                                Surface(
                                    modifier = Modifier
                                        .height(56.dp)
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(4.dp),
                                    color = item.priority.color.copy(alpha = 0.4F),
                                    tonalElevation = 6.dp,
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .wrapContentHeight(align = Alignment.CenterVertically)
                                            .padding(start = LARGE_PADDING),
                                        text = item.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        color = Color.Transparent
                                    ) {
                                        Row(horizontalArrangement = Arrangement.End) {
                                            Badge {
                                                Text(text = item.memoTotalCount.toString())
                                            }
                                        }
                                    }
                                }
                            }, onClick = {
                                Log.d("PHILIP", "clicked $item")
                                notebookTitleInside = item.title
                                onNotebookSelected(item.id)
                                expanded = false
                            })

                        }
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotebooksDropDownPreview() {
    TodoComposeTheme {
        NotebooksDropDown(
            notebooks = emptyList(),
            notebookTitle = "",
            onNotebookSelected = {}
        )
    }

}


@Composable
fun StatusDropDown(
    isNew: Boolean = false,
    state: State,
    onStateSelected: (State) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val angle by animateFloatAsState(
        targetValue = if (expanded) 180F else 0F
    )
    val focusManager = LocalFocusManager.current
    var showInitialValue by remember { mutableStateOf(isNew) }

    Row(
        modifier = Modifier
            .height(PRIORITY_DROPDOWN_HEIGHT)
            .clickable {
                expanded = true
                focusManager.clearFocus()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!showInitialValue) {
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
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            State.values().reversed().forEach { state ->
                DropdownMenuItem(
                    leadingIcon = {
                        Canvas(
                            modifier = Modifier
                                .offset(0.dp, 0.8.dp)
                                .size(PRIORITY_INDICATOR_SIZE)
                        ) {
                            drawCircle(color = state.color)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(id = state.label),
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = MaterialTheme.typography.labelMedium.fontSize,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        onStateSelected(state)
                        if (showInitialValue) showInitialValue = false
                    })
            }
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


@Composable
fun ReminderDropDown(
    modifier: Modifier = Modifier,
    isNew: Boolean = false,
    expanded: Boolean = false,
    enabled: Boolean = false,
    targetTime: Long? = null,
    onTimeSelected: (ReminderType) -> Unit,
    onButtonClicked: () -> Unit,
    onDismissRequest: () -> Unit,
    content: @Composable (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showInitialValue by remember { mutableStateOf(isNew) }

    Row(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = if (!enabled) null else LocalIndication.current
            ) {
                onButtonClicked()
                focusManager.clearFocus()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content(
            showInitialValue,
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onDismissRequest() }
        ) {
            ReminderType.values()
                .filter {
                    (Calendar.getInstance().timeInMillis) < (targetTime ?: Calendar.getInstance().timeInMillis) - it.timeInMillis
                }
                .forEach { time ->
                    DropdownMenuItem(
                        text = { DropDownItem(option = time) },
                        onClick = {
                            onTimeSelected(time)
                            if (showInitialValue) showInitialValue = false
                        })
                }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReminderDropDownPreview() {
    TodoComposeTheme {
        ReminderDropDown(
            expanded = false,
            isNew = true,
            targetTime = 0L,
            onTimeSelected = {},
            onButtonClicked = {},
            onDismissRequest = {},
        ) {

        }
    }

}
