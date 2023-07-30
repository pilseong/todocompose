package net.pilseong.todocompose.ui.screen.task

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.ui.components.DisplayAlertDialog
import net.pilseong.todocompose.ui.screen.list.MemoAction
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import net.pilseong.todocompose.ui.viewmodel.TaskUiState
import net.pilseong.todocompose.util.Constants.NEW_ITEM_ID
import net.pilseong.todocompose.util.TaskAppBarState

@Composable
fun TaskAppBar(
    task: MemoWithNotebook,
    taskAppBarState: TaskAppBarState = TaskAppBarState.VIEWER,
    taskUiState: TaskUiState,
    onBackClick: () -> Unit,
    toListScreen: (MemoAction) -> Unit,
    onCopyClicked: () -> Unit,
    onEditClicked: () -> Unit,
    clearAddedPhotos: () -> Unit,
    onValueChange: (TaskDetails) -> Unit,
) {
    when (taskAppBarState) {
        TaskAppBarState.VIEWER -> {
            Log.d("PHILIP", "[TaskAppBar] todo received ${taskUiState.taskDetails.title}")
            DetailTaskBar(
                task = task,
                toListScreen = toListScreen,
                onCopyClicked = onCopyClicked,
                onEditClicked = onEditClicked,
                onBackClick = onBackClick
            )
        }

        TaskAppBarState.EDITOR -> {
            EditTaskBar(
                uiState = taskUiState,
                mode =
                if (taskUiState.taskDetails.id == NEW_ITEM_ID) EditTaskBarMode.ADD
                else EditTaskBarMode.EDIT,
                onConfirm = {
                    toListScreen(
                        if (taskUiState.taskDetails.id == NEW_ITEM_ID)
                            MemoAction.ADD
                        else
                            MemoAction.UPDATE
                    )
                },
                onBackClick = onBackClick,
                clearAddedPhotos = clearAddedPhotos,
                onValueChange = onValueChange,
            )

        }
    }
}


enum class EditTaskBarMode {
    ADD,
    EDIT,
    CALENDAR_ADD
}

// 새로운 메모 작성 이나 기존 메모 수정을 위한 화면의 app bar
// edit 이 true 일 경우는 기존 메모 수정 false 일 경우는 신규 메모
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskBar(
    uiState: TaskUiState,
    mode: EditTaskBarMode = EditTaskBarMode.EDIT,
    onConfirm: () -> Unit,
    onBackClick: () -> Unit,
    clearAddedPhotos: () -> Unit,
    onValueChange: (TaskDetails) -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            CommonAction(
                onClicked = {
                    clearAddedPhotos()
                    onBackClick()
                },
                icon = Icons.Default.Close,
                description = stringResource(id = R.string.default_task_bar_close_icon)
            )
        },
        title = {
            Text(
                text = if (mode == EditTaskBarMode.EDIT) stringResource(id = R.string.edit_task_appbar_title)
                else stringResource(id = R.string.new_task_appbar_title),
            )
        },
        actions = {
            if (mode != EditTaskBarMode.CALENDAR_ADD) {
                Switch(
                    modifier = Modifier.padding(end = LARGE_PADDING),
                    thumbContent = {
                        if (!uiState.taskDetails.isTask) {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                imageVector = Icons.Default.EditCalendar,
                                contentDescription = "clock icon"
                            )
                        }
                    },
                    checked = uiState.taskDetails.isTask,
                    onCheckedChange = {
                        onValueChange(uiState.taskDetails.copy(isTask = !uiState.taskDetails.isTask))

                    })
            }
            // done action
            CommonAction(
                enabled = uiState.isEntryValid,
                onClicked = {
                    onConfirm()
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
    toListScreen: (MemoAction) -> Unit,
    onBackClick: () -> Unit,
    onCopyClicked: () -> Unit,
    onEditClicked: () -> Unit
) {
    Log.d("PHILIP", "[DetailTaskBar] todo received $task")
    TopAppBar(
        navigationIcon = {
            CommonAction(
                onClicked = { onBackClick() },
                icon = Icons.Default.ArrowBackIosNew,
                description = stringResource(
                    R.string.default_task_bar_close_icon
                )
            )
        },
        title = {
            Text(
                text = task.notebook?.title ?: stringResource(id = R.string.default_note_title),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = task.notebook?.priority?.color?.copy(alpha = 0.5F)
                ?: Priority.NONE.color.copy(alpha = 0.5F)
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
    toListScreen: (MemoAction) -> Unit,
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
        onYesClicked = { toListScreen(MemoAction.DELETE) }
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
            onBackClick = {},
            onConfirm = {},
            clearAddedPhotos = {},
            onValueChange = {}
        )
    }
}


@Preview
@Composable
fun DetailTaskBarPreview() {
    TodoComposeTheme {
        DetailTaskBar(
            task = MemoWithNotebook(
                memo = MemoTask(
                    id = -1,
                    title = "필성 힘내!!!",
                    description = "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                    priority = Priority.HIGH,
                    progression = State.NONE,
                    notebookId = -1,
                ),
                notebook = Notebook.instance(),
                total = 1,
                photos = emptyList(),
            ),
            onBackClick = {},
            toListScreen = {},
            onCopyClicked = {},
            onEditClicked = {}
        )
    }
}