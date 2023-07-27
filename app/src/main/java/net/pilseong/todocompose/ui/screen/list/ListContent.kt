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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState.NotLoading.*
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.flowOf
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.MemoDateSortingOption
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.State
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


@Composable
fun ListContent(
    tasks: LazyPagingItems<MemoWithNotebook>,
    toTaskScreen: (Int) -> Unit,
//    onSwipeToDelete: (Action, MemoWithNotebook) -> Unit,
    onSwipeToEdit: (Int, MemoWithNotebook) -> Unit,
    header: Boolean = false,
    memoDateBaseOption: MemoDateSortingOption = MemoDateSortingOption.UPDATED_AT,
    onFavoriteClick: (MemoWithNotebook) -> Unit,
    onLongClickApplied: (Long) -> Unit,
    selectedItemsIds: SnapshotStateList<Long>,
    onStateSelected: (MemoWithNotebook, State) -> Unit,
) {
    if (tasks.itemCount == 0) {
        EmptyContent()
    } else {
        LazyItemList(
            tasks = tasks,
            header = header,
            memoDateBaseOption = memoDateBaseOption,
            onSwipeToEdit = onSwipeToEdit,
            toTaskScreen = toTaskScreen,
            onFavoriteClick = onFavoriteClick,
            onLongClickApplied = onLongClickApplied,
            selectedItemsIds = selectedItemsIds,
            onStateSelected = onStateSelected,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LazyItemList(
    tasks: LazyPagingItems<MemoWithNotebook>,
    header: Boolean = false,
    memoDateBaseOption: MemoDateSortingOption = MemoDateSortingOption.UPDATED_AT,
    onSwipeToEdit: (Int, MemoWithNotebook) -> Unit,
    toTaskScreen: (Int) -> Unit,
    onFavoriteClick: (MemoWithNotebook) -> Unit,
    onLongClickApplied: (Long) -> Unit,
    selectedItemsIds: SnapshotStateList<Long>,
    onStateSelected: (MemoWithNotebook, State) -> Unit,
) {
    // lazy Column 의 화면 데이터 사용
    val listState = rememberLazyListState()
    val headerIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    // 100개의 리스트 의 50번째가 firstVisibleItem 이 었다가 20개 짜리 리스트 로 노트를 변경할 경우
    // index 가 리스트 보다 많아 지는 경우가 일시적 으로 발생 한다. 예외 처리
    val realIndex = remember(tasks.itemCount, headerIndex) {
        if (headerIndex >= tasks.itemCount) tasks.itemCount - 1 else headerIndex
    }

    // 추가 할 때 스크롤 을 제일 위로 올리기 위함
    LaunchedEffect(key1 = tasks) {
        Log.d("PHILIP", "item count changed to ${tasks.itemCount}")
        listState.scrollToItem(0, 0)
    }

//    val timeData by remember(memoDateBaseOption, realIndex) {
    val timeData by rememberUpdatedState(
        when (memoDateBaseOption) {
            MemoDateSortingOption.CREATED_AT -> tasks.peek(realIndex)?.memo?.createdAt
            MemoDateSortingOption.UPDATED_AT -> tasks.peek(realIndex)?.memo?.updatedAt
            MemoDateSortingOption.FINISHED_AT -> tasks.peek(realIndex)?.memo?.finishedAt
            MemoDateSortingOption.DUE_DATE -> tasks.peek(realIndex)?.memo?.dueDate
        }
    )

//    Log.d("PHILIP", "result of timeData $memoDateBaseOption,  $timeData")

    val totalData by remember(tasks.peek(0)?.total) {
        mutableStateOf(tasks.peek(0)?.total ?: 0)
    }
    StatusHeader(
        modifier = Modifier.padding(
            top = LARGE_PADDING,
            start = XLARGE_PADDING,
            end = XLARGE_PADDING,
            bottom = SMALL_PADDING
        ),
        time = timeData,
        total = totalData
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = XLARGE_PADDING)
            .navigationBarsPadding(),
        state = listState
    ) {
        items(
            count = tasks.itemCount,
            key = tasks.itemKey(key = { item -> item.memo.id }),
        ) { index ->
            // index 를 동일한 경우로만 remember 하면 내용이 변경된 경우에도 제대로 처리가 되지 않는다.
            val taskInside = remember(index, tasks[index]!!.toString()) { tasks[index]!! }

//            val currentDate = remember(dateEnabled, index) {
//                if (dateEnabled) taskInside.memo.createdAt else taskInside.memo.updatedAt
//            }

            val currentDate = remember(memoDateBaseOption, index) {
                when (memoDateBaseOption) {
                    MemoDateSortingOption.CREATED_AT -> taskInside.memo.createdAt
                    MemoDateSortingOption.UPDATED_AT -> taskInside.memo.updatedAt
                    MemoDateSortingOption.FINISHED_AT -> taskInside.memo.finishedAt
                    MemoDateSortingOption.DUE_DATE -> taskInside.memo.dueDate
                }
            }


            val prevDate = remember(memoDateBaseOption, index) {
                if (index == 0) null
                else {
                    when (memoDateBaseOption) {
                        MemoDateSortingOption.CREATED_AT -> tasks.peek(index - 1)?.memo?.createdAt
                        MemoDateSortingOption.UPDATED_AT -> tasks.peek(index - 1)?.memo?.updatedAt
                        MemoDateSortingOption.FINISHED_AT -> tasks.peek(index - 1)?.memo?.finishedAt
                        MemoDateSortingOption.DUE_DATE -> tasks.peek(index - 1)?.memo?.dueDate
                    }
                }
            }


            val drawEndEdge by remember(memoDateBaseOption, index) {
                val nextDate =

                    if (index == tasks.itemCount - 1) null
                    else {
                        when (memoDateBaseOption) {
                            MemoDateSortingOption.CREATED_AT -> tasks.peek(index + 1)?.memo?.createdAt
                            MemoDateSortingOption.UPDATED_AT -> tasks.peek(index + 1)?.memo?.updatedAt
                            MemoDateSortingOption.FINISHED_AT -> tasks.peek(index + 1)?.memo?.finishedAt
                            MemoDateSortingOption.DUE_DATE -> tasks.peek(index + 1)?.memo?.dueDate
                        }
                    }
                mutableStateOf(
                    currentDate?.toLocalDate().toString() != nextDate?.toLocalDate().toString()
                )
            }

            // 화면 의 크기의 반을 swipe 한 경우 처리
            val screenWidth = LocalConfiguration.current.screenWidthDp
            val threshold = remember(screenWidth) { screenWidth * (3 / 5F) }
            var currentFraction by remember { mutableStateOf(0f) }

            val dismissState = rememberDismissState(
                confirmValueChange = {
                    when (it) {
                        DismissValue.DismissedToEnd -> {
                            Log.d("PHILIP", "current $currentFraction")
                            if (currentFraction >= 0.4f && currentFraction < 1f) {
                                onSwipeToEdit(
                                    index,
                                    tasks.peek(index)!!,
                                )
                            }
                        }

                        else -> {}
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

            // lambda 를 통한 클로저 의 반복 적인 생성 으로 인한 recomposition 을 방지 한다.
            // index 가 변경할 때 해당 함수가 다시 생성 되어야 한다.
            val favoriteChangeLambda = remember(index) {
                {
                    // favorite 을 클릭 했을 때 화면에 만 반영 하기 위해서 snapshot 의 상태만 변경 한다.
                    // list 가 이동 하는 순간 snapshot 이 그려 지기 때문에 snapshot 을 변경 해야 한다.
                    // 클릭 하는 순간 에는 어떤 방법 으로도 snapshot 이 반영 되지 않았다. 그래서
                    // 하위 컴포 넌트 에서 별도의 상태를 관리 하도록 rememberUpdateState 를 사용 하였다.
                    Log.d("PHILIP", "inside lambda $index")
                    tasks.itemSnapshotList[index]!!.memo.favorite =
                        !tasks.itemSnapshotList[index]!!.memo.favorite

                    onFavoriteClick(tasks.itemSnapshotList[index]!!)
                }
            }

            val headerEnabled = remember(header, index, currentDate, prevDate) {
                header && index != 0 && currentDate?.toLocalDate()
                    .toString() != prevDate?.toLocalDate().toString()
            }

            currentFraction = dismissState.progress
            SwipeToDismiss(
                modifier = Modifier.animateItemPlacement(),
                state = dismissState,
                background = {
                    ColorBackGround(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = if (headerEnabled) 38.dp else 0.dp)
                            .height(72.dp),
                        dismissState = dismissState,
                        leftToRightColor = MediumPriorityColor,
                        rightToLeftColor = HighPriorityColor,
                        leftIcon = Icons.Default.Edit,
                        rightIcon = Icons.Default.Delete,
                    )
                },
                dismissContent = {
                    TaskItem(
                        todoTask = taskInside,
                        drawEndEdge = drawEndEdge,
                        toTaskScreen = {
                            toTaskScreen(index)
                        },
                        headerEnabled = headerEnabled,
                        datetime = currentDate,
                        onFavoriteClick = favoriteChangeLambda,
                        onLongClickApplied = onLongClickApplied,
                        selectedItemsIds = selectedItemsIds,
                        onStateSelected = { todo, state ->
                            tasks.itemSnapshotList[index]!!.memo.progression = state
                            // 설정이 종료 일 경우 종료 정보를 snapshot 에 저장 해야 뷰어 에서 종료 시간을 볼 수 있다.
                            if (state == State.COMPLETED)
                                tasks.itemSnapshotList[index]!!.memo.finishedAt =
                                    ZonedDateTime.now()
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
    modifier: Modifier = Modifier,
    time: ZonedDateTime?,
    total: Int,
) {
    val innerTime by rememberUpdatedState(time)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DateHeader(time = innerTime)
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(id = R.string.total_label, total),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
fun StatusHeaderPreview() {
    MaterialTheme {
        StatusHeader(time = ZonedDateTime.now(), total = 1)
    }
}

@Composable
fun DateHeader(
    modifier: Modifier = Modifier,
    time: ZonedDateTime?
) {
    val innerTime by rememberUpdatedState(newValue = time)

    val backgroundColor by remember(innerTime) {
        mutableStateOf(
            if (time?.toLocalDate()?.dayOfWeek.toString().take(3) == "SUN" ||
                time?.toLocalDate()?.dayOfWeek.toString().take(3) == "SAT"
            ) {
                WEEKEND_COLOR
            } else {
                WEEKDAY_COLOR
            }
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                color = backgroundColor.copy(alpha = 0.8f),
                text = innerTime?.toLocalDate()?.format(
                    DateTimeFormatter.ofPattern(
                        stringResource(id = R.string.note_content_dateformat)
                    )
                ) ?: "",
                fontStyle = MaterialTheme.typography.titleSmall.fontStyle,
                fontSize = MaterialTheme.typography.titleSmall.fontSize
            )
        }
    }
}


@Preview
@Composable
fun DateHeaderPreview() {
    DateHeader(
        modifier = Modifier,
        ZonedDateTime.now()
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorBackGround(
    modifier: Modifier = Modifier,
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
        modifier = modifier.background(color),
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
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 24.dp),
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
                PagingData.from(
                    data =
                    listOf(
                        MemoWithNotebook(
                            memo = MemoTask(
                                1,
                                "필성 힘내!!!",
                                "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                                Priority.HIGH,
                                notebookId = -1
                            ),
                            notebook = Notebook.instance(),
                            total = 1,
                            photos = emptyList()
                        )
                    ),
                )
            ).collectAsLazyPagingItems(),
            toTaskScreen = {},
//            onSwipeToDelete = { _, _ -> },
            onSwipeToEdit = { _, _ -> },
            onFavoriteClick = {},
            onLongClickApplied = {},
            selectedItemsIds = SnapshotStateList(),
            onStateSelected = { _, _ -> }
        )
    }
}
