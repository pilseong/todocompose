package net.pilseong.todocompose.ui.screen.task

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.MemoWithNotebook
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.State
import net.pilseong.todocompose.ui.components.PriorityDropDown
import net.pilseong.todocompose.ui.components.StatusDropDown
import net.pilseong.todocompose.ui.screen.list.ColorBackGround
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import net.pilseong.todocompose.ui.viewmodel.TaskUiState
import net.pilseong.todocompose.util.Constants.MAX_CONTENT_LENGTH
import net.pilseong.todocompose.util.Constants.MAX_TITLE_LENGTH
import net.pilseong.todocompose.util.Constants.NEW_ITEM_ID
import net.pilseong.todocompose.util.TaskAppBarState
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskContent(
    task: MemoWithNotebook,
    taskUiState: TaskUiState,
    taskIndex: Int = 0,
    taskSize: Int = 0,
    taskAppBarState: TaskAppBarState = TaskAppBarState.VIEWER,
    onValueChange: (TaskDetails) -> Unit,
    onSwipeRightOnViewer: () -> Unit,
    onSwipeLeftOnViewer: () -> Unit
) {

    Log.d("PHILIP", "size : $taskSize, taskInded : $taskIndex")
    if (taskAppBarState == TaskAppBarState.VIEWER) {

        // 화면 전환의 기준 점 계산 화면의 3분의 1이상 swipe 할 경우 전환
        val threshold = LocalConfiguration.current.screenWidthDp / 3

        val dismissState = rememberDismissState(
            confirmValueChange = {
                when (it) {
                    DismissValue.Default -> false
                    DismissValue.DismissedToEnd -> {
                        onSwipeRightOnViewer()
                        true

                    }

                    DismissValue.DismissedToStart -> {
                        onSwipeLeftOnViewer()
                        true
                    }
                }
            },
            positionalThreshold = { threshold.dp.toPx() }
        )

        // index 의 이동이 일어난 경우 실행 된다. 동일한 인덱스 로 이동 하는 경우는 없기 때문에
        // 중복 이벤트 발생에 대한 대처를 할 필요가 없다.
        // index 가 변경 된 상태 변경이 확인 되는 경우에 실행 된다.
        LaunchedEffect(key1 = taskIndex) {
            Log.d("PHILIP", "inside effect size : $taskSize, taskInded : $taskIndex")
            if (dismissState.dismissDirection == DismissDirection.StartToEnd) {
                dismissState.snapTo(DismissValue.DismissedToStart)
            } else {
                dismissState.snapTo(DismissValue.DismissedToEnd)
            }
            dismissState.reset()
        }

        SwipeToDismiss(
            state = dismissState,
            background = {
                ColorBackGround(
                    dismissState = dismissState,
                    leftToRightColor = MaterialTheme.colorScheme.surface,
                    rightToLeftColor = MaterialTheme.colorScheme.surface,
                    leftIcon = Icons.Default.KeyboardArrowLeft,
                    rightIcon = Icons.Default.KeyboardArrowRight
                )
            },
            dismissContent = {
                ViewerContent(
                    task = task
                )
            },
            directions = getDirections(taskIndex, taskSize - 1)
        )

    } else {
        EditorContent(
            taskUiState = taskUiState,
            onValueChange = onValueChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun getDirections(selectedIndex: Int, endIndex: Int): Set<DismissDirection> {
    val directions = mutableSetOf(
        DismissDirection.StartToEnd, DismissDirection.EndToStart
    )

    if (selectedIndex == 0)
        directions.remove(DismissDirection.StartToEnd)

    if (selectedIndex == endIndex)
        directions.remove(DismissDirection.EndToStart)

    return directions
}

@Composable
private fun ViewerContent(
    task: MemoWithNotebook
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .padding(start = XLARGE_PADDING, end = XLARGE_PADDING, bottom = SMALL_PADDING)
                    .fillMaxWidth()
            ) {
                // 헤더 부분
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1.5F / 12)) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                        )
                    }
                    Column(modifier = Modifier.weight(3.5F / 12)) {
                        Column {
                            Text(
                                stringResource(id = R.string.task_content_notebook_name),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            )
                            Text(
                                text = stringResource(id = R.string.info_created_at),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            )
                            Text(
                                text = stringResource(id = R.string.info_updated_at),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            )
                            if (task.memo.finishedAt != null) {
                                Text(
                                    stringResource(id = R.string.info_finished_at),
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                )
                            }
                            Text(
                                stringResource(id = R.string.badge_priority_label),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            )
                            Text(
                                stringResource(id = R.string.badge_state_label),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(7F / 12),
                        horizontalAlignment = Alignment.End
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = task.notebook?.title
                                    ?: stringResource(id = R.string.default_note_title),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Text(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = task.memo.updatedAt.toLocalDateTime()
                                    .format(
                                        DateTimeFormatter.ofPattern(
                                            stringResource(id = R.string.task_content_dateformat)
                                        )
                                    )
                            )
                            Text(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = task.memo.createdAt.toLocalDateTime()
                                    .format(
                                        DateTimeFormatter.ofPattern(
                                            stringResource(id = R.string.task_content_dateformat)
                                        )
                                    )
                            )
                            if (task.memo.finishedAt != null) {
                                Text(
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                    text = task.memo.finishedAt.toLocalDateTime()
                                        .format(
                                            DateTimeFormatter.ofPattern(
                                                stringResource(id = R.string.task_content_dateformat)
                                            )
                                        )
                                )
                            }
                            Text(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = stringResource(id = task.memo.priority.label)
                            )
                            Text(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = stringResource(id = task.memo.progression.label),
                            )
                        }
                    }
                }
                // 제목
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = SMALL_PADDING),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Column(
                        modifier = Modifier.weight(1.5F / 12),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Title,
                            contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                        )
                    }
                    Text(
                        modifier = Modifier.weight(10.5F / 12),
                        text = task.memo.title
                    )

                }
            }
        }
        Divider()

        Spacer(
            modifier = Modifier.height(MEDIUM_PADDING),
        )

        Card(
            shape = RoundedCornerShape(4.dp)
        ) {
            SelectionContainer {
                Text(
                    modifier = Modifier
                        .padding(LARGE_PADDING),
                    text = task.memo.description
                )
            }
        }
    }
}

@Composable
private fun EditorContent(
    taskUiState: TaskUiState,
    onValueChange: (TaskDetails) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
    ) {
        Row {
            Column(modifier = Modifier.weight(1F)) {
                Surface(tonalElevation = 1.dp) {
                    PriorityDropDown(
                        isNew = taskUiState.taskDetails.id == NEW_ITEM_ID,
                        priority = taskUiState.taskDetails.priority,
                        onPrioritySelected = { onValueChange(taskUiState.taskDetails.copy(priority = it)) }
                    )
                }
            }
            Column(modifier = Modifier.weight(1F)) {
                Surface(tonalElevation = 1.dp) {
                    StatusDropDown(
                        isNew = taskUiState.taskDetails.id == NEW_ITEM_ID,
                        state = taskUiState.taskDetails.progression,
                        onStateSelected = { onValueChange(taskUiState.taskDetails.copy(progression = it)) }
                    )
                }
            }
        }
        Divider(
            modifier = Modifier
                .height(0.7.dp),
            color = MaterialTheme.colorScheme.onSurface,
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
            value = taskUiState.taskDetails.title,
            label = {
                Text(text = stringResource(id = R.string.new_task_title_placeholder))
            },
            onValueChange = {
                if (it.length <= MAX_TITLE_LENGTH)
                    onValueChange(taskUiState.taskDetails.copy(title = it))

            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    1.dp
                ),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    1.dp
                )
            ),
            singleLine = false,
            maxLines = 3,
            supportingText = {
                Text(
                    text = "${taskUiState.taskDetails.title.length} / $MAX_TITLE_LENGTH",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        )
        Surface {
            TextField(
                modifier = Modifier
                    .imePadding()
                    .fillMaxSize(),
                value = taskUiState.taskDetails.description,
                label = {
                    Text(
                        text = stringResource(id = R.string.new_task_description_placeholder)
                    )
                },
                onValueChange = {
                    if (it.length <= MAX_CONTENT_LENGTH)
                        onValueChange(taskUiState.taskDetails.copy(description = it))

                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        1.dp
                    ),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        1.dp
                    )
                ),
                supportingText = {
                    Text(
                        text = "${taskUiState.taskDetails.description.length} / $MAX_CONTENT_LENGTH",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun ViewerContentPreview() {
    TodoComposeTheme {
        TaskContent(
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
                total = 1
            ),
            taskIndex = 0,
            taskSize = 1,
            taskUiState = TaskUiState(
                taskDetails = TaskDetails(
                    id = -1,
                    title = "필성 힘내!!!",
                    description = "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                    priority = Priority.HIGH,
                    progression = State.NONE,
                    notebookId = -1,
                )
            ),
            onValueChange = {},
            onSwipeRightOnViewer = {},
            onSwipeLeftOnViewer = {},
            taskAppBarState = TaskAppBarState.VIEWER
        )
    }
}


@Preview
@Composable
fun EditorContentPrevie() {
    MaterialTheme {
        EditorContent(
            taskUiState = TaskUiState(
                taskDetails = TaskDetails(
                    id = -1,
                    title = "필성 힘내!!!",
                    description = "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                    priority = Priority.HIGH,
                    progression = State.NONE,
                    notebookId = -1,
                )
            ),
            onValueChange = {}
        )
    }
}