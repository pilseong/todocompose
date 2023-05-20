package net.pilseong.todocompose.ui.screen.task

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.components.PriorityDropDown
import net.pilseong.todocompose.ui.screen.list.ColorBackGround
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import net.pilseong.todocompose.ui.theme.PRIORITY_INDICATOR_SIZE
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.util.TaskAppBarState
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskContent(
    taskIndex: Int,
    tasks: List<TodoTask>,
    taskAppBarState: TaskAppBarState = TaskAppBarState.VIEWER,
    title: String,
    description: String,
    priority: Priority,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onSwipeRightOnViewer: () -> Unit,
    onSwipeLeftOnViewer: () -> Unit
) {

    Log.i("PHILIP", "[TaskContent] $taskAppBarState")
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
                    title,
                    priority,
                    createdAt,
                    updatedAt,
                    description,
                )
            },
            directions = getDirections(taskIndex, tasks.size - 1)
        )

    } else {
        EditorContent(
            title,
            onTitleChange,
            priority,
            onPriorityChange,
            description,
            onDescriptionChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun getDirections(selectedIndex: Int, endIndex: Int): Set<DismissDirection> {
    val directions = mutableSetOf<DismissDirection>(
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
    title: String,
    priority: Priority,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime,
    description: String,
) {
//    Log.i("PHILIP", "[ViewerContent] $title")

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(all = LARGE_PADDING)
            .verticalScroll(scrollState)

    ) {
        val priorityText = when (priority) {
            Priority.HIGH -> stringResource(id = R.string.priority_high)
            Priority.MEDIUM -> stringResource(id = R.string.priority_medium)
            Priority.LOW -> stringResource(id = R.string.priority_low)
            Priority.NONE -> stringResource(id = R.string.priority_none)
        }
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .padding(horizontal = SMALL_PADDING)
                        .size(PRIORITY_INDICATOR_SIZE)
                ) {
                    drawCircle(color = priority.color)
                }

                Text(
                    modifier = Modifier.weight(1F),
                    text = priorityText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${stringResource(id = R.string.task_content_updated_at_label)}: ${
                        updatedAt.toLocalDateTime()
                            .format(
                                DateTimeFormatter.ofPattern(
                                    stringResource(id = R.string.task_content_dateformat)
                                )
                            )
                    }",
                    fontSize = MaterialTheme.typography.titleSmall.fontSize
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${stringResource(id = R.string.sort_date_label)}: ${
                        createdAt.toLocalDateTime()
                            .format(
                                DateTimeFormatter.ofPattern(
                                    stringResource(id = R.string.task_content_dateformat)
                                )
                            )
                    }",
                    fontSize = MaterialTheme.typography.titleSmall.fontSize
                )
            }
        }

        Divider(
            modifier = Modifier.height(MEDIUM_PADDING),
            color = MaterialTheme.colorScheme.background,
        )
        Card(
//            modifier = Modifier
//                .weight(1F)
//                .fillMaxWidth()
        ) {
            SelectionContainer {
                Text(
                    modifier = Modifier
                        .padding(LARGE_PADDING),
                    text = description
                )
            }
        }
    }
}

@Composable
private fun EditorContent(
    title: String,
    onTitleChange: (String) -> Unit,
    priority: Priority,
    onPriorityChange: (Priority) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(all = LARGE_PADDING)
    ) {
        Card {

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = title,
                label = {
                    Text(text = stringResource(id = R.string.new_task_title_placeholder))
                },
                onValueChange = { onTitleChange(it) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
//                focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
//                disabledIndicatorColor = Color.Transparent
                )
            )
        }
        Divider(
            modifier = Modifier
                .height(MEDIUM_PADDING),
            color = MaterialTheme.colorScheme.background,
        )
        Card {
            PriorityDropDown(
                modifier = Modifier.fillMaxWidth(0.95F),
                priority = priority,
                onPrioritySelected = {
                    onPriorityChange(it)
                }
            )
        }
        Divider(
            modifier = Modifier
                .height(MEDIUM_PADDING),
            color = MaterialTheme.colorScheme.background,
        )
        Card {
            TextField(
                modifier = Modifier.fillMaxSize(),
                value = description,
                label = {
                    Text(
                        text = stringResource(id = R.string.new_task_description_placeholder)
                    )
                },
                onValueChange = { onDescriptionChange(it) },
                colors = TextFieldDefaults.colors(
//                focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
//                disabledIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview
@Composable
fun ViewerContentPreview() {
    TodoComposeTheme {
        TaskContent(
            taskIndex = 0,
            tasks = emptyList(),
            title = "필성 힘내!!!",
            description = "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
            priority = Priority.HIGH,
            onTitleChange = {},
            onDescriptionChange = {},
            onPriorityChange = {},
            createdAt = ZonedDateTime.now(),
            updatedAt = ZonedDateTime.now(),
            onSwipeRightOnViewer = {},
            onSwipeLeftOnViewer = {},
            taskAppBarState = TaskAppBarState.EDITOR
        )
    }
}