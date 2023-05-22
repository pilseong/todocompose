package net.pilseong.todocompose.ui.screen.task

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.components.DisplayAlertDialog
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.topBarContainerColor
import net.pilseong.todocompose.ui.theme.topBarContentColor
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants.NEW_ITEM_ID
import net.pilseong.todocompose.util.TaskAppBarState

@Composable
fun TaskAppBar(
    taskAppBarState: TaskAppBarState = TaskAppBarState.VIEWER,
    todoTask: TodoTask,
    toListScreen: (Action) -> Unit,
    onCopyClicked: () -> Unit,
    onEditClicked: () -> Unit
) {
    when (taskAppBarState) {
        TaskAppBarState.VIEWER -> {
            Log.i("PHILIP", "[TaskAppBar] todo received ${todoTask.title}")
            DetailTaskBar(
                todoTask = todoTask,
                toListScreen = toListScreen,
                onCopyClicked = onCopyClicked,
                onEditClicked = onEditClicked
            )
        }

        TaskAppBarState.EDITOR -> {
            Log.i("PHILIP", "[TaskAppBar] todo received ${todoTask.id != NEW_ITEM_ID}")
            EditTaskBar(toListScreen = toListScreen, edit = todoTask.id != NEW_ITEM_ID)

        }
    }
}


// 새로운 메모 작성 이나 기존 메모 수정을 위한 화면의 app bar
// edit 이 true 일 경우는 기존 메모 수정 false 일 경우는 신규 메모
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskBar(
    toListScreen: (Action) -> Unit,
    edit: Boolean = false
) {
    TopAppBar(
        navigationIcon = {
            CommonAction(
                onClicked = { toListScreen(Action.NO_ACTION) },
                icon = Icons.Default.ArrowBack,
                description = "Arrow backwards Icon"
            )
        },
        title = {
            Text(
                text = if (edit) stringResource(id = R.string.edit_task_appbar_title)
                else stringResource(id = R.string.new_task_appbar_title),
                color = MaterialTheme.colorScheme.topBarContentColor
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.topBarContainerColor
        ),
        actions = {
            // done action
            CommonAction(
                onClicked = {
                    if (edit) {
                        toListScreen(Action.UPDATE)
                    } else {
                        toListScreen(Action.ADD)
                    }
                },
                icon = Icons.Default.Check,
                description = "Check Icon for new task"
            )
        }
    )
}

@Composable
fun CommonAction(
    onClicked: () -> Unit,
    icon: ImageVector? = null,
    painter: Painter? = null,
    description: String,
    tint: Color = MaterialTheme.colorScheme.topBarContentColor
) {
    IconButton(onClick = { onClicked() }) {
        if (icon == null && painter != null) {
            Icon(
                painter = painter,
                contentDescription = description,
                tint = tint
            )
        } else if (icon != null && painter == null) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = tint
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTaskBar(
    todoTask: TodoTask,
    toListScreen: (Action) -> Unit,
    onCopyClicked: () -> Unit,
    onEditClicked: () -> Unit
) {
    Log.i("PHILIP", "[DetailTaskBar] todo received $todoTask")
    TopAppBar(
        navigationIcon = {
            CommonAction(
                onClicked = { toListScreen(Action.NO_ACTION) },
                icon = Icons.Default.Close,
                description = stringResource(
                    R.string.default_task_bar_close_icon
                )
            )
        },
        title = {
            Text(
                text = todoTask.title,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.topBarContentColor
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.topBarContainerColor
        ),
        actions = {
            DetailTaskBarActions(
                todoTask = todoTask,
                toListScreen = toListScreen,
                onCopyClicked = onCopyClicked,
                onEditClicked = onEditClicked
            )
        }
    )
}

@Composable
fun DetailTaskBarActions(
    todoTask: TodoTask,
    toListScreen: (Action) -> Unit,
    onCopyClicked: () -> Unit,
    onEditClicked: () -> Unit
) {
    // deletion confirmation popup state
    var expanded by remember { mutableStateOf(false) }

    // 경고 팝업
    DisplayAlertDialog(
        title = stringResource(id = R.string.delete_task_dialog_title, todoTask.title),
        message = stringResource(id = R.string.delete_task_dialog_confirmation, todoTask.title),
        openDialog = expanded,
        onCloseDialog = { expanded = false },
        onYesClicked = { toListScreen(Action.DELETE) }
    )
    // copy to clipboard
    CommonAction(
        onClicked = { onCopyClicked() },
        painter = painterResource(id = R.drawable.ic_baseline_content_copy_24),
        description = stringResource(id = R.string.viewer_appbar_clipboard_icon)
    )

    // Edit
    CommonAction(
        onClicked = { onEditClicked() },
        icon = Icons.Default.Edit,
        description = stringResource(id = R.string.viewer_appbar_update_icon)
    )

    // delete
    CommonAction(
        onClicked = {
            expanded = true
        },
        icon = Icons.Default.Delete,
        description = stringResource(id = R.string.viewer_appbar_delete_icon)
    )
}

@Preview
@Composable
fun EditTaskBarPreview() {
    TodoComposeTheme {
        EditTaskBar(toListScreen = {})
    }
}


@Preview
@Composable
fun DetailTaskBarPreview() {
    TodoComposeTheme {
        DetailTaskBar(
            todoTask = TodoTask(
                1, "필성 힘내!!!",
                "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                Priority.HIGH
            ),
            toListScreen = {},
            onCopyClicked = {},
            onEditClicked = {}
        )
    }
}