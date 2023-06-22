package net.pilseong.todocompose.ui.screen.task

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import net.pilseong.todocompose.data.model.MemoWithNotebook
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.State
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.components.DisplayAlertDialog
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.viewmodel.TaskUiState
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants.NEW_ITEM_ID
import net.pilseong.todocompose.util.TaskAppBarState

@Composable
fun TaskAppBar(
    task: MemoWithNotebook,
    taskAppBarState: TaskAppBarState = TaskAppBarState.VIEWER,
    taskUiState: TaskUiState,
    toListScreen: (Action) -> Unit,
    onCopyClicked: () -> Unit,
    onEditClicked: () -> Unit
) {
    when (taskAppBarState) {
        TaskAppBarState.VIEWER -> {
            Log.i("PHILIP", "[TaskAppBar] todo received ${taskUiState.taskDetails.title}")
            DetailTaskBar(
                task = task,
                toListScreen = toListScreen,
                onCopyClicked = onCopyClicked,
                onEditClicked = onEditClicked
            )
        }

        TaskAppBarState.EDITOR -> {
            EditTaskBar(
                uiState = taskUiState,
                toListScreen = toListScreen,
                edit = taskUiState.taskDetails.id != NEW_ITEM_ID
            )

        }
    }
}


// 새로운 메모 작성 이나 기존 메모 수정을 위한 화면의 app bar
// edit 이 true 일 경우는 기존 메모 수정 false 일 경우는 신규 메모
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskBar(
    uiState: TaskUiState,
    toListScreen: (Action) -> Unit,
    edit: Boolean = false
) {
    TopAppBar(
        navigationIcon = {
            CommonAction(
                onClicked = { toListScreen(Action.NO_ACTION) },
                icon = Icons.Default.Close,
                description = stringResource(id = R.string.default_task_bar_close_icon)
            )
        },
        title = {
            Text(
                text = if (edit) stringResource(id = R.string.edit_task_appbar_title)
                else stringResource(id = R.string.new_task_appbar_title),
            )
        },
        actions = {
            // done action
            CommonAction(
                enabled = uiState.isEntryValid,
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
    enabled: Boolean = true,
    onClicked: () -> Unit,
    icon: ImageVector? = null,
    painter: Painter? = null,
    description: String,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    IconButton(
        enabled = enabled,
        onClick = { onClicked() }) {
        if (icon == null && painter != null) {
            Icon(
                painter = painter,
                contentDescription = description,
                tint = if (enabled) tint else tint.copy(alpha = 0.2F)
            )
        } else if (icon != null && painter == null) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = if (enabled) tint else tint.copy(alpha = 0.2F)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTaskBar(
    task: MemoWithNotebook,
    toListScreen: (Action) -> Unit,
    onCopyClicked: () -> Unit,
    onEditClicked: () -> Unit
) {
    Log.i("PHILIP", "[DetailTaskBar] todo received $task")
    TopAppBar(
        navigationIcon = {
            CommonAction(
                onClicked = { toListScreen(Action.NO_ACTION) },
                icon = Icons.Default.ArrowBackIosNew,
                description = stringResource(
                    R.string.default_task_bar_close_icon
                )
            )
        },
        title = {
//            FittedTextTitle(
//                onAppBarTitleClick = { },
//                appbarTitle = task.memo.title,
//                clickEnabled = false
//            )
            Text(
                text = task.memo.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = task.memo.priority.color.copy(alpha = 0.5F)
        ),
        actions = {
            DetailTaskBarActions(
                task = task,
                toListScreen = toListScreen,
                onCopyClicked = onCopyClicked,
                onEditClicked = onEditClicked
            )
        }
    )
}

@Composable
fun DetailTaskBarActions(
    task: MemoWithNotebook,
    toListScreen: (Action) -> Unit,
    onCopyClicked: () -> Unit,
    onEditClicked: () -> Unit
) {
    // deletion confirmation popup state
    var expanded by remember { mutableStateOf(false) }

    // 경고 팝업
    DisplayAlertDialog(
        title = stringResource(
            id = R.string.delete_task_dialog_title,
            task.memo.title
        ),
        message = stringResource(
            id = R.string.delete_task_dialog_confirmation,
            task.memo.title
        ),
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
        EditTaskBar(
            uiState = TaskUiState(),
            toListScreen = {}
        )
    }
}


@Preview
@Composable
fun DetailTaskBarPreview() {
    TodoComposeTheme {
        DetailTaskBar(
            task = MemoWithNotebook(
                memo = TodoTask(
                    id = -1,
                    title = "필성 힘내!!!",
                    description = "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                    priority = Priority.HIGH,
                    progression = State.NONE,
                    notebookId = -1,
                ),
                notebook = Notebook.instance(),
                total = 1
            ),
            toListScreen = {},
            onCopyClicked = {},
            onEditClicked = {}
        )
    }
}