package net.pilseong.todocompose.ui.screen.list

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.destination.BottomNavBar
import net.pilseong.todocompose.ui.components.PriorityItem
import net.pilseong.todocompose.ui.theme.FavoriteYellow
import net.pilseong.todocompose.ui.theme.HighPriorityColor
import net.pilseong.todocompose.ui.theme.LowPriorityColor
import net.pilseong.todocompose.ui.theme.MediumPriorityColor
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.theme.fabContainerColor
import net.pilseong.todocompose.ui.theme.fabContent
import net.pilseong.todocompose.ui.theme.onPrimaryElevation
import net.pilseong.todocompose.ui.theme.topBarContainerColor
import net.pilseong.todocompose.ui.theme.topBarContentColor
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.SearchAppBarState
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    toTaskScreen: (List<TodoTask>) -> Unit,
    onClickBottomNavBar: (String) -> Unit,
    memoViewModel: MemoViewModel,
) {
    /**
     * view model 을 통제 하는 코드가 여기서 실행 되고 관리 된다.
     */
    // 검색 기능의 상태를 매핑 그냥 view model 을 사용할 수 있지만 편의를 위한
    // composable 의 상태를 가지는 변수들
    val searchAppBarState by memoViewModel.searchAppBarState
    val searchText: String = memoViewModel.searchTextString
    val prioritySortState: Priority = memoViewModel.prioritySortState


    // Flow 에 대한 collection 을 처리 하는 파이프 연결 변수들. 이 변수들 은 외부 데이터 베이스 나 외부 API 에 의존 한다.
    // 모든 task 의 상태를 감시 한다. 리스트 는 nav graph 안에서 변동 될 수 있다.
    val tasks = memoViewModel.tasks.collectAsLazyPagingItems()

    val snackBarHostState = remember { SnackbarHostState() }

    // task screen 에서 요청한 처리의 결과를 보여 준다. undo 의 경우는 특별 하게 처리 한다.
    // enabled 는 화면에 표출될 지를 결정 하는 변수 이다.
    DisplaySnackBar(
        snackBarHostState = snackBarHostState,
        action = memoViewModel.action,
        enabled = memoViewModel.actionPerformed,
        title = memoViewModel.title,
        duration = SnackbarDuration.Short,
        buttonClicked = { selectedAction, result ->
            Log.i("PHILIP", "[ListScreen] button clicked ${selectedAction.name}")

            if (result == SnackbarResult.ActionPerformed
                && memoViewModel.action == Action.DELETE
            ) {
                Log.i("PHILIP", "[ListScreen] undo inside clicked ${selectedAction.name}")
                memoViewModel.handleActions(Action.UNDO)
            } else {
                memoViewModel.updateAction(Action.NO_ACTION)
            }
        },
        orderEnabled = memoViewModel.snackBarOrderEnabled,
        dateEnabled = memoViewModel.snackBarDateEnabled,
        startDate = memoViewModel.startDate,
        endDate = memoViewModel.endDate,
        memoViewModel = memoViewModel
    )

    // 상태 바의 상태가 검색이 열려 있는 경우 뒤로 가기를 하면 기본 상태로 돌아 가게 된다.
    BackHandler(
        enabled = memoViewModel.searchAppBarState.value != SearchAppBarState.CLOSE
    ) {
        memoViewModel.onCloseSearchBar()
    }

    /**
     * view model 을 통제 코드 종료
     */

    // UI 처리 부분
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState
            ) {
                Snackbar(
                    snackbarData = it,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    actionColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        },
        floatingActionButton = {
            AddMemoFab(onFabClicked = {
                memoViewModel.setTaskScreenToEditorMode()
                memoViewModel.updateIndex(-1)
                toTaskScreen(tasks.itemSnapshotList.items)
            })
        },
        topBar = {
            ListAppBar(
                memoViewModel = memoViewModel,
                searchAppBarState = searchAppBarState,
                searchText = searchText
            )
        },
        bottomBar = {
            BottomNavBar(
                onClick = onClickBottomNavBar,
                currentDestination = Screen.MemoList.route
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues = paddingValues)
                .fillMaxSize(),
        ) {
            StatusLine(
                prioritySortState = prioritySortState,
                orderEnabled = memoViewModel.orderEnabled,
                dateEnabled = memoViewModel.dateEnabled,
                startDate = memoViewModel.startDate,
                endDate = memoViewModel.endDate,
                onCloseClick = {
                    memoViewModel.handleActions(
                        Action.SEARCH_WITH_DATE_RANGE,
                        startDate = null,
                        endDate = null
                    )
                },
                favoriteOn = memoViewModel.sortFavorite,
                onFavoriteClick = {
                    memoViewModel.handleActions(
                        action = Action.SORT_FAVORITE_CHANGE,
                        favorite = !memoViewModel.sortFavorite
                    )
                },
                onOrderEnabledClick = {
                    memoViewModel.handleActions(
                        action = Action.SORT_ORDER_CHANGE,
                        sortOrderEnabled = !memoViewModel.orderEnabled
                    )
                },
                onDateEnabledClick = {
                    memoViewModel.handleActions(
                        action = Action.SORT_DATE_CHANGE,
                        sortDateEnabled = !memoViewModel.dateEnabled
                    )
                },
                onPrioritySelected = { priority ->
                    Log.i("PHILIP", "onSortClicked")
                    memoViewModel.handleActions(
                        Action.PRIORITY_CHANGE,
                        priority = priority
                    )
                },
            )

            ListContent(
                tasks = tasks,
                toTaskScreen = { index ->
                    memoViewModel.setTaskScreenToViewerMode()
                    memoViewModel.updateIndex(index)
                    toTaskScreen(tasks.itemSnapshotList.items)
                },
//                    onSwipeToDelete = { action, task ->
//                        // undo 처리를 위해서 데이터 동기화 필요
//                        memoViewModel.updateTaskContent(task)
//                        memoViewModel.handleActions(action, task.id)
//                    },
                onSwipeToUpdate = { index ->
                    memoViewModel.setTaskScreenToEditorMode()
                    memoViewModel.updateIndex(index)
                    toTaskScreen(tasks.itemSnapshotList.items)
                },
                header = memoViewModel.searchAppBarState.value == SearchAppBarState.CLOSE,
                screenMode = memoViewModel.screenMode,
                dateEnabled = memoViewModel.dateEnabled,
                onFavoriteClick = { todo ->
                    memoViewModel.handleActions(
                        action = Action.FAVORITE_UPDATE,
                        todoTask = todo
                    )
                }
            )
        }
    }
}

@Composable
private fun StatusLine(
    prioritySortState: Priority,
    orderEnabled: Boolean,
    dateEnabled: Boolean,
    startDate: Long?,
    endDate: Long?,
    onCloseClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    favoriteOn: Boolean = false,
    onOrderEnabledClick: () -> Unit,
    onDateEnabledClick: () -> Unit,
    onPrioritySelected: (Priority) -> Unit,
) {
    var containerColor = Color.Transparent
    var priorityText = stringResource(id = R.string.priority_none)
    var priorityIcon = painterResource(id = R.drawable.ic_baseline_menu_24)
    when (prioritySortState) {
        Priority.HIGH -> {
            containerColor = HighPriorityColor
            priorityText = stringResource(id = R.string.priority_high)
            priorityIcon = painterResource(id = R.drawable.baseline_priority_high_24)
        }

        Priority.MEDIUM -> {
            containerColor = MediumPriorityColor
            priorityText = stringResource(id = R.string.priority_medium)
            priorityIcon = painterResource(id = R.drawable.ic_baseline_menu_24)
        }

        Priority.LOW -> {
            containerColor = LowPriorityColor
            priorityText = stringResource(id = R.string.priority_low)
            priorityIcon = painterResource(id = R.drawable.ic_baseline_low_priority_24)
        }

        else -> {

        }
    }

    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (startDate != null || endDate != null) 64.dp else 30.dp),
        color = MaterialTheme.colorScheme.topBarContainerColor

    ) {
        Column(
            modifier = Modifier.padding(horizontal = XLARGE_PADDING)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 우선 순위 설정
                Card(
                    modifier = Modifier
                        .clickable { expanded = true }
                        .weight(1F),
                    shape = RoundedCornerShape(4.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = containerColor,
                        contentColor = MaterialTheme.colorScheme.topBarContentColor,
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                PaddingValues(
                                    start = 0.dp,
                                    top = SMALL_PADDING,
                                    end = SMALL_PADDING,
                                    bottom = SMALL_PADDING
                                )
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier.width(12.dp),
                            painter = priorityIcon,
                            contentDescription = "arrow"
                        )
                        Spacer(modifier = Modifier.width(SMALL_PADDING))
                        Text(
                            text = priorityText,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // desc, asc
                Card(
                    modifier = Modifier
                        .weight(1F)
                        .clickable {
                            onOrderEnabledClick()
                        },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (orderEnabled) HighPriorityColor
                        else Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.topBarContentColor,
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                PaddingValues(
                                    start = 0.dp,
                                    top = SMALL_PADDING,
                                    end = SMALL_PADDING,
                                    bottom = SMALL_PADDING
                                )
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier.width(12.dp),
                            painter = if (orderEnabled) painterResource(id = R.drawable.ic_baseline_north_24)
                            else painterResource(id = R.drawable.ic_baseline_south_24),
                            contentDescription = "arrow"
                        )
                        Spacer(modifier = Modifier.width(SMALL_PADDING))
                        Text(
                            text = if (orderEnabled) stringResource(id = R.string.badge_order_asc_label)
                            else stringResource(id = R.string.badge_order_desc_label),
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // updated, created
                Card(
                    modifier = Modifier
                        .weight(1F)
                        .clickable {
                            onDateEnabledClick()
                        },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (dateEnabled) HighPriorityColor
                        else Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.topBarContentColor,
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                PaddingValues(
                                    start = 0.dp,
                                    top = SMALL_PADDING,
                                    end = SMALL_PADDING,
                                    bottom = SMALL_PADDING
                                )
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier.width(12.dp),
                            imageVector = if (dateEnabled) Icons.TwoTone.Edit
                            else Icons.TwoTone.Edit,
                            contentDescription = "star"
                        )
                        Spacer(modifier = Modifier.width(SMALL_PADDING))
                        Text(
                            text = if (dateEnabled) stringResource(id = R.string.badge_date_created_at_label)
                            else stringResource(id = R.string.badge_date_updated_at_label),
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // favorite
                Card(
                    modifier = Modifier
                        .weight(1F)
                        .clickable {
                            onFavoriteClick()
                        },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (favoriteOn) FavoriteYellow
                        else Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.topBarContentColor,
                    ),
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                PaddingValues(
                                    start = 0.dp,
                                    top = SMALL_PADDING,
                                    end = SMALL_PADDING,
                                    bottom = SMALL_PADDING
                                )
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier.width(12.dp),
                            imageVector = Icons.Default.Star,
                            contentDescription = "star"
                        )
                        Spacer(modifier = Modifier.width(SMALL_PADDING))
                        Text(
                            text = stringResource(id = R.string.badge_favorite_label),
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { PriorityItem(priority = Priority.HIGH) },
                    onClick = {
                        expanded = false
                        onPrioritySelected(Priority.HIGH)
                    })
                DropdownMenuItem(
                    text = { PriorityItem(priority = Priority.LOW) },
                    onClick = {
                        expanded = false
                        onPrioritySelected(Priority.LOW)
                    })
                DropdownMenuItem(
                    text = { PriorityItem(priority = Priority.NONE) },
                    onClick = {
                        expanded = false
                        onPrioritySelected(Priority.NONE)
                    })
            }

            // 날짜 검색 부분 표출
            if (startDate != null || endDate != null) {
                Row(
                    modifier = Modifier
                        .padding(
                            PaddingValues(
                                top = SMALL_PADDING
                            )
                        )
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val startDateStr = if (startDate != null)
                        ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(startDate),
                            ZoneId.systemDefault()
                        ).format(
                            DateTimeFormatter.ofPattern("yy/MM/dd")
                        )
                    else stringResource(id = R.string.status_line_date_range_from_the_first_memo_text)

                    val endDateStr = if (endDate != null)
                        ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(endDate),
                            ZoneId.systemDefault()
                        ).format(
                            DateTimeFormatter.ofPattern("yy/MM/dd")
                        )
                    else stringResource(id = R.string.status_line_date_range_up_to_date_text)
                    Surface(
                        color = MaterialTheme.colorScheme.onPrimaryElevation
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(SMALL_PADDING),
                            text = stringResource(
                                id = R.string.status_line_date_range_text,
                                startDateStr, endDateStr
                            ),
                            fontSize = MaterialTheme.typography.titleSmall.fontSize
                        )
                    }

                    Icon(
                        modifier = Modifier
//                            .padding(vertical = SMALL_PADDING)
                            .border(
                                border = BorderStroke(
                                    0.dp,
                                    color = MaterialTheme.colorScheme.topBarContentColor
                                )
                            )
                            .clickable {
                                onCloseClick()
                            },
                        imageVector = Icons.Default.Close,
                        contentDescription = "close button",
                    )
                }
            }
        }
    }
}

// enabled 가 true 일 경우만 팝업이 뜬다
@Composable
private fun DisplaySnackBar(
    snackBarHostState: SnackbarHostState,
    action: Action,
    enabled: ByteArray,
    title: String,
    duration: SnackbarDuration = SnackbarDuration.Short,
    buttonClicked: (Action, SnackbarResult) -> Unit,
    orderEnabled: Boolean,
    dateEnabled: Boolean,
    startDate: Long?,
    endDate: Long?,
    memoViewModel: MemoViewModel
) {

    val message = when (action) {
        Action.ADD ->
            title + " " + stringResource(id = R.string.new_task_added_message)

        Action.UPDATE ->
            title + " " + stringResource(id = R.string.task_updated_message)

        Action.DELETE ->
            title + " " + stringResource(id = R.string.task_deleted_message)

        Action.DELETE_ALL ->
            stringResource(id = R.string.all_tasks_deleted_message)

        Action.UNDO ->
            title + " " + stringResource(id = R.string.all_tasks_restored_message)

        Action.PRIORITY_CHANGE ->
            stringResource(id = R.string.snackbar_message_priority_change)

        Action.SORT_ORDER_CHANGE ->
            if (orderEnabled)
                stringResource(id = R.string.snackbar_message_order_asc_change)
            else
                stringResource(id = R.string.snackbar_message_order_desc_change)

        Action.SORT_DATE_CHANGE ->
            if (dateEnabled)
                stringResource(id = R.string.snackbar_message_date_created_at_change)
            else
                stringResource(id = R.string.snackbar_message_date_updated_at_change)

        Action.SEARCH_WITH_DATE_RANGE ->
            if (startDate == null && endDate == null)
                stringResource(id = R.string.snackbar_message_date_range_cancelled)
            else
                stringResource(id = R.string.snackbar_message_date_range_applied)

        Action.SORT_FAVORITE_CHANGE ->
            stringResource(id = R.string.snackbar_favorite_change_message)

        else -> {
            ""
        }
    }

    val label = if (action == Action.DELETE)
        stringResource(id = R.string.snack_bar_undo_label)
    else "OK"

    // enabled 는 이벤트 가 발생한 경우를 정확 하게 구분 하기 위한 변수
    LaunchedEffect(key1 = enabled) {
        Log.i("PHILIP", "[DisplaySnackBar]snack bar with $action")
        if (action != Action.NO_ACTION) {
            Log.i("PHILIP", "[DisplaySnackBar]snack bar popped up $action")
            memoViewModel.updateAction(Action.NO_ACTION)
            val snackBarResult = snackBarHostState.showSnackbar(
                message = message,
                actionLabel = label,
                duration = duration
            )
            buttonClicked(action, snackBarResult)
        }
    }
}

// Floating Action Button
@Composable
fun AddMemoFab(
    onFabClicked: (taskId: Int) -> Unit
) {
    FloatingActionButton(
        onClick = {
            onFabClicked(-1)
        },
        shape = RoundedCornerShape(4.dp),
        containerColor = MaterialTheme.colorScheme.fabContainerColor,
        contentColor = MaterialTheme.colorScheme.fabContent
    ) {
        Icon(
            imageVector = Icons.Default.Create,
            contentDescription = stringResource(id = R.string.add_button_icon),
        )
    }
}

@Composable
@Preview
private fun ListScreenPreview() {
    TodoComposeTheme {
        ListScreen(
            toTaskScreen = {},
            onClickBottomNavBar = {},
            memoViewModel = viewModel(),
        )
    }
}