package net.pilseong.todocompose.ui.screen.list


import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.flowOf
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.MemoWithNotebook
import net.pilseong.todocompose.data.model.Notebook
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
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun ListContent(
    tasks: LazyPagingItems<MemoWithNotebook>,
    toTaskScreen: (Int) -> Unit,
//    onSwipeToDelete: (Action, MemoWithNotebook) -> Unit,
    onSwipeToEdit: (Int, MemoWithNotebook) -> Unit,
    header: Boolean = false,
    dateEnabled: Boolean = false,
    onFavoriteClick: (MemoWithNotebook) -> Unit,
    onLongClickReleased: (Int) -> Unit,
    onLongClickApplied: (Int) -> Unit,
    selectedItemsIds: SnapshotStateList<Int>,
    onStateSelected: (MemoWithNotebook, State) -> Unit,
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
    tasks: LazyPagingItems<MemoWithNotebook>,
    toTaskScreen: (Int) -> Unit,
//    onSwipeToDelete: (Action, MemoWithNotebook) -> Unit,
    onSwipeToEdit: (Int, MemoWithNotebook) -> Unit,
    header: Boolean = false,
    dateEnabled: Boolean = false,
    onFavoriteClick: (MemoWithNotebook) -> Unit,
    onLongClickReleased: (Int) -> Unit,
    onLongClickApplied: (Int) -> Unit,
    selectedItemsIds: SnapshotStateList<Int>,
    onStateSelected: (MemoWithNotebook, State) -> Unit,
) {
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
    tasks: LazyPagingItems<MemoWithNotebook>,
    header: Boolean = false,
    dateEnabled: Boolean = false,
    onSwipeToEdit: (Int, MemoWithNotebook) -> Unit,
    toTaskScreen: (Int) -> Unit,
    onFavoriteClick: (MemoWithNotebook) -> Unit,
    onLongClickReleased: (Int) -> Unit,
    onLongClickApplied: (Int) -> Unit,
    selectedItemsIds: SnapshotStateList<Int>,
    onStateSelected: (MemoWithNotebook, State) -> Unit,
) {
    // 화면 의 크기의 반을 swipe 한 경우 처리
    val threshold = LocalConfiguration.current.screenWidthDp / 3

    // lazy Column 의 화면 데이터 사용
    val listState = rememberLazyListState()
    if (header) {
        val headerIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
        // 100개의 리스트의 50번째가 firstVisibleItem이었다가 20개 짜리 리스트로 노트를 변경할 경우
        // index가 리스트보다 많아지는 경우가 일시적으로 발생한다. 예외처리
        val realIndex =
            if (headerIndex >= tasks.itemCount) tasks.itemCount - 1 else headerIndex

        val timeData = remember(dateEnabled, realIndex) {
            if (dateEnabled) {
                tasks.peek(realIndex)?.memo?.createdAt
            } else {
                tasks.peek(realIndex)?.memo?.updatedAt
            }
        }

        Log.i(
            "PHILIP",
            "headerIndex: $headerIndex, realIndex $realIndex, itemCount: ${tasks.itemCount}"
        )

        StatusHeader(timeData!!, tasks.peek(realIndex)?.total)
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = XLARGE_PADDING),
        state = listState
    ) {
        items(
            count = tasks.itemCount,
            key = tasks.itemKey(key = { item -> item.memo.id }),
            contentType = tasks.itemContentType(null)
        ) { index ->

            // index를 동일한 경우로만 remember 하면 내용이 변경된 경우에도 제대로 처리가 되지 않는다.
            val taskInside = remember(index, tasks[index]!!.toString()) { tasks[index]!! }

            if (header && index != 0) {
                TaskItemHeader(dateEnabled, tasks, index)
            }

            val drawEndEdge = remember(dateEnabled, index) {
                Log.i("PHILIP", "calculated")
                val today: ZonedDateTime?
                val nextDate: ZonedDateTime?

                if (dateEnabled) {
                    today = tasks[index]?.memo?.createdAt
                    nextDate = if (index == tasks.itemCount - 1) null
                    else tasks[index + 1]?.memo?.createdAt
                } else {
                    today = tasks[index]?.memo?.updatedAt
                    nextDate = if (index == tasks.itemCount - 1) null
                    else tasks[index + 1]?.memo?.updatedAt
                }

                today?.toLocalDate().toString() != nextDate?.toLocalDate().toString()
            }

            @Suppress("UNUSED_EXPRESSION") val dismissState = rememberDismissState(
                confirmValueChange = {
                    when (it) {
                        DismissValue.Default -> false
                        DismissValue.DismissedToEnd -> {
                            onSwipeToEdit(
                                index,
                                tasks.peek(index)!!,
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
                        todoTask = taskInside,
                        drawEndEdge = drawEndEdge,
                        toTaskScreen = {
                            toTaskScreen(index)
                        },
                        datetime = remember(dateEnabled) {
                            if (dateEnabled) taskInside.memo.createdAt
                            else tasks.peek(index)!!.memo.updatedAt
                        },
                        // favorite 을 클릭 했을 때 화면에 만 반영 하기 위해서 snapshot 의 상태만 변경 한다.
                        // list 가 이동 하는 순간 snapshot 이 그려 지기 때문에 snapshot을 변경해야 한다.
                        // 클릭하는 순간에는 어떤 방법으로도 snapshot이 반영되지 않았다. 그래서
                        // 하위 컴포넌트에서 별도의 상태를 관리하도록 rememberUpdateState를 사용하였다.
                        onFavoriteClick = {
                            tasks.itemSnapshotList[index]!!.memo.favorite =
                                !tasks.itemSnapshotList[index]!!.memo.favorite
                            onFavoriteClick(tasks.itemSnapshotList[index]!!)
                        },
                        onLongClickReleased = onLongClickReleased,
                        onLongClickApplied = onLongClickApplied,
                        selectedItemsIds = selectedItemsIds,
                        onStateSelected = { todo, state ->
                            tasks.itemSnapshotList[index]!!.memo.progression = state
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
private fun StatusHeader(
    currentDate: ZonedDateTime,
    total: Int?,
) {
    Surface(
        modifier = Modifier
//            .padding(
//                top = LARGE_PADDING,
//                bottom = SMALL_PADDING
//            )
            .height(IntrinsicSize.Max),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = XLARGE_PADDING)
        ) {
            DateHeader(currentDate)
            Column(
                modifier = Modifier.weight(1F),
            ) {
                Row(
                    modifier = Modifier
                        .padding(bottom = SMALL_PADDING)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(id = R.string.total_label) + ": $total",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun StatusHeaderPreview() {
    MaterialTheme {
        StatusHeader(currentDate = ZonedDateTime.now(), total = 1)
    }
}

@Composable
fun DateHeader(time: ZonedDateTime) {
    Log.i("PHILIP", "DateHeader redrawn")
    val backColor = remember(time.toEpochSecond()) {
        if (time.toLocalDate().dayOfWeek.toString().take(3) == "SUN" ||
            time.toLocalDate().dayOfWeek.toString().take(3) == "SAT"
        ) {
            WEEKEND_COLOR
        } else {
            WEEKDAY_COLOR
        }
    }

    Row(
        modifier = Modifier.padding(
            top = LARGE_PADDING,
            bottom = SMALL_PADDING
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (Locale.getDefault().language == "ko") {
            Column(
//                modifier = Modifier.padding(top = XLARGE_PADDING),
            ) {
                Text(
                    color = Color(
                        ColorUtils.blendARGB(
                            backColor.toArgb(),
                            Color.White.toArgb(),
                            0.1f
                        )
                    ).copy(0.9f),
                    text = time.toLocalDate().format(
                        DateTimeFormatter.ofPattern(
                            stringResource(id = R.string.note_content_dateformat)
                        )
                    ),
                    fontStyle = MaterialTheme.typography.titleSmall.fontStyle,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize
                )
            }
        } else {
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
}


@Preview
@Composable
fun DateHeaderPreview() {
    DateHeader(ZonedDateTime.now())
}

@Composable
private fun TaskItemHeader(
    dateEnabled: Boolean,
    tasks: LazyPagingItems<MemoWithNotebook>,
    index: Int
) {
    val currentDate = remember(dateEnabled, index) {
        if (dateEnabled) tasks.peek(index)?.memo?.createdAt else tasks.peek(index)?.memo?.updatedAt
    }
    val prevDate = remember(dateEnabled, index) {
        if (dateEnabled) {
            if (index == 0) null
            else tasks.peek(index - 1)?.memo?.createdAt
        } else {
            if (index == 0) null
            else tasks.peek(index - 1)?.memo?.updatedAt
        }
    }

    if (currentDate?.toLocalDate().toString() != prevDate?.toLocalDate().toString()) {
        DateHeader(currentDate!!)
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
                PagingData.from<MemoWithNotebook>(
                    listOf(
                        MemoWithNotebook(
                            memo = TodoTask(
                                1,
                                "필성 힘내!!!",
                                "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                                Priority.HIGH,
                                notebookId = -1
                            ),
                            notebook = Notebook.instance(),
                            total = 1
                        )
                    )
                )
            ).collectAsLazyPagingItems(),
            toTaskScreen = {},
//            onSwipeToDelete = { a, b -> },
            onSwipeToEdit = { a, b -> },
            onFavoriteClick = {},
            onLongClickReleased = {},
            onLongClickApplied = {},
            selectedItemsIds = SnapshotStateList(),
            onStateSelected = { todotask, state -> }
        )
    }
}
