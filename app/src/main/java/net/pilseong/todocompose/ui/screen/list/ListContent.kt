package net.pilseong.todocompose.ui.screen.list

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import net.pilseong.todocompose.data.model.State
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.theme.HighPriorityColor
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.MediumPriorityColor
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.WEEKDAY_COLOR
import net.pilseong.todocompose.ui.theme.WEEKEND_COLOR
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.theme.mediumGray
import java.time.ZonedDateTime


@Composable
fun ListContent(
    tasks: LazyPagingItems<TodoTask>,
    toTaskScreen: (Int) -> Unit,
//    onSwipeToDelete: (Action, TodoTask) -> Unit,
    onSwipeToEdit: (Int, TodoTask, List<TodoTask>) -> Unit,
    header: Boolean = false,
    dateEnabled: Boolean = false,
    onFavoriteClick: (TodoTask) -> Unit,
    onLongClickReleased: (Int) -> Unit,
    onLongClickApplied: (Int) -> Unit,
    selectedItemsIds: SnapshotStateList<Int>,
    onStateSelected: (TodoTask, State) -> Unit,
) {

    if (tasks.loadState.refresh is LoadState.NotLoading) {
        DisplayTasks(
            tasks = tasks,
            toTaskScreen = toTaskScreen,
//            onSwipeToDelete = onSwipeToDelete,
            onSwipeToEdit = onSwipeToEdit,
            header = header,
            dateEnabled = dateEnabled,
            onFavoriteClick = onFavoriteClick,
            onLongClickReleased = onLongClickReleased,
            onLongClickApplied = onLongClickApplied,
            selectedItemsIds = selectedItemsIds,
            onStateSelected = onStateSelected,
        )
    }
//    } else {
//        LoadingContent()
//    }
}

// tasks 가 있는 경우에 표출


@Composable
fun DisplayTasks(
    tasks: LazyPagingItems<TodoTask>,
    toTaskScreen: (Int) -> Unit,
//    onSwipeToDelete: (Action, TodoTask) -> Unit,
    onSwipeToEdit: (Int, TodoTask, List<TodoTask>) -> Unit,
    header: Boolean = false,
    dateEnabled: Boolean = false,
    onFavoriteClick: (TodoTask) -> Unit,
    onLongClickReleased: (Int) -> Unit,
    onLongClickApplied: (Int) -> Unit,
    selectedItemsIds: SnapshotStateList<Int>,
    onStateSelected: (TodoTask, State) -> Unit,
) {
//    Log.i("PHILIP", "[DisplayTasks] tasks is ${tasks.itemCount}")
    if (tasks.itemCount == 0) {
        EmptyContent()
    } else {
        LazyItemList(
            tasks = tasks,
            header = header,
            dateEnabled = dateEnabled,
            onSwipeToEdit = onSwipeToEdit,
            toTaskScreen = toTaskScreen,
            onFavoriteClick = onFavoriteClick,
            onLongClickReleased = onLongClickReleased,
            onLongClickApplied = onLongClickApplied,
            selectedItemsIds = selectedItemsIds,
            onStateSelected = onStateSelected,
        )
    }
}

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
)
@Composable
fun LazyItemList(
    tasks: LazyPagingItems<TodoTask>,
    header: Boolean = false,
    dateEnabled: Boolean = false,
    onSwipeToEdit: (Int, TodoTask, List<TodoTask>) -> Unit,
    toTaskScreen: (Int) -> Unit,
    onFavoriteClick: (TodoTask) -> Unit,
    onLongClickReleased: (Int) -> Unit,
    onLongClickApplied: (Int) -> Unit,
    selectedItemsIds: SnapshotStateList<Int>,
    onStateSelected: (TodoTask, State) -> Unit,
) {
    // 화면 의 크기의 반을 swipe 한 경우 처리
    val threshold = LocalConfiguration.current.screenWidthDp / 3

    // lazyColume의 화면 데이터 사용
    val listState = rememberLazyListState()
    val date: MutableState<String> = remember { mutableStateOf("") }

    val task = if (tasks.itemCount > listState.firstVisibleItemIndex)
        tasks.peek(listState.firstVisibleItemIndex) else tasks.peek(tasks.itemCount - 1)

    val currentDate: ZonedDateTime? = if (dateEnabled) {
        task?.createdAt
    } else {
        task?.updatedAt
    }

    date.value = currentDate?.toLocalDate().toString()

    if (header) {
        Surface {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = XLARGE_PADDING)
            ) {
                DateHeader(currentDate!!)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = XLARGE_PADDING),
        state = listState
    ) {
        items(
            count = tasks.itemCount,
            key = tasks.itemKey(key = { item -> item.id }),
            contentType = tasks.itemContentType(null)
        ) { index ->
//            if (header && index != 0) {
            if (header && index != 0) {
                TaskItemHeader(dateEnabled, tasks, index)
            }

            val drawEndEdge = remember(dateEnabled, tasks, index) {
                val currentDate: ZonedDateTime?
                val nextDate: ZonedDateTime?

                if (dateEnabled) {
                    currentDate = tasks.peek(index)?.createdAt
                    nextDate = if (index == tasks.itemCount - 1) null
                    else tasks.peek(index + 1)?.createdAt
                } else {
                    currentDate = tasks.peek(index)?.updatedAt
                    nextDate = if (index == tasks.itemCount - 1) null
                    else tasks.peek(index + 1)?.updatedAt
                }

                currentDate?.toLocalDate().toString() != nextDate?.toLocalDate().toString()
            }

//            val currentItem by rememberUpdatedState(newValue = tasks.peek(index)!!)
//            var currentItem by remember {
//                mutableStateOf(tasks.get(index)!!)
//            }.apply { value =  tasks.get(index)!! }
//            var currentItem by remember {
//                mutableStateOf(tasks.peek(index)!!)
//            }

//            if (currentItem.id == 75) {
//                Log.i("PHILIP", "id 75 currentItem favortie ${currentItem.title}, ${currentItem.favorite}")
//            }

            if (tasks.peek(index)!!.id == 75) {
                Log.i(
                    "PHILIP",
                    "id 75 currentItem favortie ${tasks.peek(index)!!.title}, ${tasks.peek(index)!!.favorite}"
                )
            }
            val dismissState = rememberDismissState(
                confirmValueChange = {
                    when (it) {
                        DismissValue.Default -> false
                        DismissValue.DismissedToEnd -> {
                            onSwipeToEdit(
                                index,
                                tasks.peek(index)!!,
                                tasks.itemSnapshotList.items
                            )
                        }

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

            if (dismissState.currentValue != DismissValue.Default) {
                LaunchedEffect(Unit) {
                    dismissState.reset()
                }
            }

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
//                        todoTask = currentItem,
                        todoTask = tasks.peek(index)!!,
                        drawEndEdge = drawEndEdge,
                        toTaskScreen = {
                            toTaskScreen(index)
                        },
//                        onLongClick = {
//                            Toast.makeText(context, "long click activated", Toast.LENGTH_SHORT)
//                                .show()
//                        },
//                        onDeselectedClick = {
//
//                        },
                        datetime = if (dateEnabled) tasks.peek(index)!!.createdAt
                        else tasks.peek(index)!!.updatedAt,
                        // favorite 을 클릭했을 때 화면에만 반영하기 위해서 snapshot의 상태만 변경한다.
                        // list가 이동하는 순간 snapshot이 그려지기 때문에 snapshot을 변경해야 한다.
                        // 클릭하는 순간에는 어떤 방법으로도 snapshot이 반영되지 않았다. 그래서
                        // 하위 컴포넌트에서 별도의 상태를 관리하도록 rememberUpdateState를 사용하였다.
                        onFavoriteClick = {
                            tasks.itemSnapshotList[index]!!.favorite =
                                !tasks.itemSnapshotList[index]!!.favorite
                            onFavoriteClick(tasks.itemSnapshotList[index]!!)
                        },
                        onLongClickReleased = onLongClickReleased,
                        onLongClickApplied = onLongClickApplied,
                        selectedItemsIds = selectedItemsIds,
                        onStateSelected = { todo, state ->
                            tasks.itemSnapshotList[index]!!.progression = state
                            onStateSelected(todo, state)
                        },
                    )
                },
                directions = setOf(DismissDirection.StartToEnd)
            )
        }
    }
}

@Composable
fun DateHeader(time: ZonedDateTime) {
    val backColor = if (time.toLocalDate().dayOfWeek.toString().take(3) == "SUN" ||
        time.toLocalDate().dayOfWeek.toString().take(3) == "SAT"
    ) {
        WEEKEND_COLOR
    } else {
        WEEKDAY_COLOR
    }

    Row(
        modifier = Modifier.padding(
            top = LARGE_PADDING,
            bottom = SMALL_PADDING
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(42.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = String.format("%02d", time.toLocalDate().dayOfMonth),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Light
                ),
                color = backColor
            )
            Text(
                text = time.toLocalDate().dayOfWeek.toString().take(3),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Light
                ),
                color = backColor
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = time.toLocalDate().month.toString().lowercase().replaceFirstChar {
                    it.titlecase()
                },
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Light
                ),
                color = backColor
            )
            Text(
                text = time.toLocalDate().year.toString(),
                color = MaterialTheme.colorScheme.onSurface,
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Light
                )
            )
        }
    }
}


@Preview
@Composable
fun DateHeaderPreview() {
    DateHeader(ZonedDateTime.now())
}

@Composable
private fun TaskItemHeader(
    dateEnabled: Boolean,
    tasks: LazyPagingItems<TodoTask>,
    index: Int
) {
    val currentDate: ZonedDateTime?
    val prevDate: ZonedDateTime?

    if (dateEnabled) {
        currentDate = tasks.peek(index)?.createdAt
        prevDate = if (index == 0) null
        else tasks.peek(index - 1)?.createdAt
    } else {
        currentDate = tasks.peek(index)?.updatedAt
        prevDate = if (index == 0) null
        else tasks.peek(index - 1)?.updatedAt
    }

    if (currentDate?.toLocalDate().toString() != prevDate?.toLocalDate().toString()) {
        DateHeader(currentDate!!)
    }
//    val currentDateString = currentDate?.toLocalDateTime()?.format(
//        DateTimeFormatter.ofPattern("yyyy-MM-dd")
//    ).toString()
//
//    val prevDateString = if (index == 0) null
//    else prevDate?.toLocalDateTime()?.format(
//        DateTimeFormatter.ofPattern("yyyy-MM-dd")
//    ).toString()
//
//    if (currentDateString != prevDateString) {
//        DateHeader(currentDate!!)
//    }
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


@Composable
@Preview
fun ListContentPreview() {
    MaterialTheme {
        ListContent(
            tasks = flowOf(
                PagingData.from<TodoTask>(
                    listOf(
                        TodoTask(
                            1,
                            "필성 힘내!!!",
                            "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                            Priority.HIGH,
                            notebookId = -1
                        )
                    )
                )
            ).collectAsLazyPagingItems(),
            toTaskScreen = {},
//            onSwipeToDelete = { a, b -> },
            onSwipeToEdit = { a, b, c ->},
//            screenMode = ScreenMode.NORMAL,
            onFavoriteClick = {},
            onLongClickReleased = {},
            onLongClickApplied = {},
            selectedItemsIds = SnapshotStateList(),
            onStateSelected = { todotask, state -> }
        )
    }
}
