package net.pilseong.todocompose.ui.screen.list

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.flowOf
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.theme.FavoriteYellow
import net.pilseong.todocompose.ui.theme.HighPriorityColor
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.LowPriorityColor
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import net.pilseong.todocompose.ui.theme.MediumPriorityColor
import net.pilseong.todocompose.ui.theme.NonePriorityColor
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.mediumGray
import net.pilseong.todocompose.ui.theme.taskItemContentColor
import net.pilseong.todocompose.util.ScreenMode
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ListContent(
    tasks: LazyPagingItems<TodoTask>,
    toTaskScreen: (Int) -> Unit,
//    onSwipeToDelete: (Action, TodoTask) -> Unit,
    onSwipeToUpdate: (Int) -> Unit,
    header: Boolean = false,
    screenMode: ScreenMode,
    dateEnabled: Boolean = false,
    onFavoriteClick: (TodoTask) -> Unit,

    ) {

    if (tasks.loadState.refresh is LoadState.NotLoading) {
        DisplayTasks(
            tasks = tasks,
            toTaskScreen = toTaskScreen,
//            onSwipeToDelete = onSwipeToDelete,
            onSwipeToUpdate = onSwipeToUpdate,
            header = header,
            dateEnabled = dateEnabled,
            onFavoriteClick = onFavoriteClick
        )
    } else {
        LoadingContent()
    }
}

// tasks 가 있는 경우에 표출
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DisplayTasks(
    tasks: LazyPagingItems<TodoTask>,
    toTaskScreen: (Int) -> Unit,
//    onSwipeToDelete: (Action, TodoTask) -> Unit,
    onSwipeToUpdate: (Int) -> Unit,
    header: Boolean = false,
    dateEnabled: Boolean = false,
    onFavoriteClick: (TodoTask) -> Unit
) {
    val context = LocalContext.current
    Log.i("PHILIP", "[DisplayTasks] tasks is ${tasks.itemCount}")
    if (tasks.itemCount == 0) {
        EmptyContent()
    } else {
        // 화면 의 크기의 반을 swipe 한 경우 처리
        val threshold = LocalConfiguration.current.screenWidthDp / 3
        LazyColumn(
            modifier = Modifier.padding(all = LARGE_PADDING),
            verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        ) {
            items(
                count = tasks.itemCount,
                key = tasks.itemKey(key = { item -> item.id }),
                contentType = tasks.itemContentType(null)
            ) { index ->

                TaskItemHeader(header, dateEnabled, tasks, index)

                val currentItem by rememberUpdatedState(newValue = tasks[index])
                val dismissState = rememberDismissState(
                    confirmValueChange = {
                        when (it) {
                            DismissValue.Default -> false
                            DismissValue.DismissedToEnd -> onSwipeToUpdate(index)
                            DismissValue.DismissedToStart -> {}
//                                onSwipeToDelete(
//                                    Action.DELETE,
//                                    currentItem!!
//                                )
                        }
                        true
                    },
                    positionalThreshold = { threshold.dp.toPx() }
                )

                SwipeToDismiss(
                    state = dismissState,
                    background = {
                        ColorBackGround(
                            dismissState = dismissState,
                            leftToRightColor = MediumPriorityColor,
                            rightToLeftColor = HighPriorityColor,
                            leftIcon = Icons.Default.Edit,
                            rightIcon = Icons.Default.Delete
                        )
                    },
                    dismissContent = {
                        TaskItem(
                            modifier = Modifier.animateItemPlacement(),
                            todoTask = currentItem!!,
                            toTaskScreen = {
                                toTaskScreen(index)
                            },
                            onLongClick = {
                                Toast.makeText(context, "long click activated", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            onDeselectedClick = {

                            },
                            datetime = if (dateEnabled) currentItem!!.createdAt
                            else currentItem!!.updatedAt,
                            onFavoriteClick = {
                                onFavoriteClick(currentItem!!)
                            }
                        )
                    },
                    directions = setOf(DismissDirection.StartToEnd)
                )
            }
        }
    }
}

@Composable
private fun TaskItemHeader(
    header: Boolean,
    dateEnabled: Boolean,
    tasks: LazyPagingItems<TodoTask>,
    index: Int
) {
    var currentDate: ZonedDateTime? = null
    var prevDate: ZonedDateTime? = null

    if (header) {
        if (dateEnabled) {
            currentDate = tasks[index]?.createdAt
            prevDate = if (index == 0) null
            else tasks[index - 1]?.createdAt
        } else {
            currentDate = tasks[index]?.updatedAt
            prevDate = if (index == 0) null
            else tasks[index - 1]?.updatedAt
        }
        var currentDateString = currentDate?.toLocalDateTime()?.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        ).toString()

        val prevDateString = if (index == 0) null
        else prevDate?.toLocalDateTime()?.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        ).toString()

        if (currentDateString != prevDateString) {
            Text(
                modifier = Modifier.padding(
                    PaddingValues(
                        top = MEDIUM_PADDING,
                        start = LARGE_PADDING
                    )
                ),
                text = currentDateString,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorBackGround(
    dismissState: DismissState,
    leftToRightColor: Color,
    rightToLeftColor: Color,
    leftIcon: ImageVector,
    rightIcon: ImageVector
) {
    val dismissDirection = dismissState.dismissDirection ?: return
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> MaterialTheme.colorScheme.surface
            DismissValue.DismissedToEnd -> leftToRightColor
            DismissValue.DismissedToStart -> rightToLeftColor
        }
    )
    val alignment = when (dismissDirection) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
    }
    val icon = when (dismissDirection) {
        DismissDirection.StartToEnd -> leftIcon
        DismissDirection.EndToStart -> rightIcon
    }
    val label = when (dismissDirection) {
        DismissDirection.StartToEnd -> stringResource(id = R.string.update_label)
        DismissDirection.EndToStart -> stringResource(id = R.string.delete_label)
    }

    val degrees by animateFloatAsState(
        targetValue = if (dismissState.targetValue == DismissValue.Default) 0F
        else -45F
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 24.dp),
        contentAlignment = alignment
    ) {
        Icon(
            modifier = Modifier
                .rotate(degrees = degrees),
            imageVector = icon,
            contentDescription = label,
            tint = Color.White
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    todoTask: TodoTask,
    toTaskScreen: (Int) -> Unit,
    onLongClick: () -> Unit,
    onDeselectedClick: () -> Unit,
    datetime: ZonedDateTime,
    onFavoriteClick: () -> Unit
) {
    var selected by remember { mutableStateOf(false) }
    var favoriteOn by remember { mutableStateOf(todoTask.favorite) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    toTaskScreen(todoTask.id)

                },
                onLongClick = {
                    selected = true
                    onLongClick()
                }
            ),
        tonalElevation = 1.dp,
        color = Color.Transparent,
        shape = RectangleShape,

        ) {
        val tintColor = when (todoTask.priority) {
            Priority.HIGH -> HighPriorityColor
            Priority.MEDIUM -> MediumPriorityColor
            Priority.LOW -> LowPriorityColor
            Priority.NONE -> NonePriorityColor
        }
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
            ),
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = LARGE_PADDING)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.clickable(enabled = selected) {
                            selected = false
                            onDeselectedClick()
                        },
                        painter = if (selected)
                            painterResource(id = R.drawable.ic_baseline_check_circle_24)
                        else
                            painterResource(id = R.drawable.ic_baseline_circle_24),
                        contentDescription = if (selected) "Checked Circle" else "Circle",
                        tint = if (selected) MaterialTheme.colorScheme.primary else tintColor
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(11f),
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = todoTask.title,
                            color = MaterialTheme.colorScheme.taskItemContentColor,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(SMALL_PADDING))
                        Text(
                            text = datetime.toLocalTime()
                                .format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                        )
                    }
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = todoTask.description,
                        color = MaterialTheme.colorScheme.taskItemContentColor,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(PaddingValues(end = SMALL_PADDING))
                        .fillMaxHeight()
                        .weight(2.2f),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            text = "${datetime.month.name} ${datetime.dayOfMonth}"
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onFavoriteClick()
                                favoriteOn = !favoriteOn
                            },
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(id = R.string.task_item_star_description),
                            tint = if (favoriteOn) FavoriteYellow else Color.White
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun TaskItemPreview() {
    TodoComposeTheme {
        TaskItem(
            todoTask = TodoTask(
                1, "필성 힘내!!!",
                "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                Priority.HIGH
            ),
            toTaskScreen = {},
            onLongClick = {},
            onDeselectedClick = {},
            datetime = ZonedDateTime.now(),
            onFavoriteClick = {}
        )
    }
}

@Composable
@Preview
fun ListContentPreview() {
    TodoComposeTheme {
        ListContent(
            tasks = flowOf(
                PagingData.from<TodoTask>(
                    listOf(
                        TodoTask(
                            1, "필성 힘내!!!",
                            "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                            Priority.HIGH
                        )
                    )
                )
            ).collectAsLazyPagingItems(),
            toTaskScreen = {},
//            onSwipeToDelete = { a, b -> },
            onSwipeToUpdate = {},
            screenMode = ScreenMode.NORMAL,
            onFavoriteClick = {}
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RedBackGroundPreview() {
    TodoComposeTheme {
        ColorBackGround(
            dismissState = rememberDismissState(),
            leftToRightColor = MediumPriorityColor,
            rightToLeftColor = HighPriorityColor,
            leftIcon = Icons.Default.Edit,
            rightIcon = Icons.Default.Delete
        )
    }
}

@Composable
fun EmptyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(120.dp),
            painter = painterResource(R.drawable.ic_baseline_sentiment_satisfied_alt_24),
            contentDescription = "Sad face Icon",
            tint = MaterialTheme.colorScheme.mediumGray
        )
        Text(
            text = stringResource(id = R.string.empty_list_content_label),
            color = MaterialTheme.colorScheme.mediumGray,
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.displaySmall.fontSize
        )

    }
}

@Composable
fun LoadingContent() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.loading_text),
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Preview
@Composable
fun LoadingContentPreview() {
    TodoComposeTheme {
        LoadingContent()
    }
}


@Preview
@Composable
fun EmptyContentPreview() {
    TodoComposeTheme {
        EmptyContent()
    }
}
