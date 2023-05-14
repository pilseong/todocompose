package net.pilseong.todocompose.ui.screen.list

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.theme.HighPriorityColor
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.LowPriorityColor
import net.pilseong.todocompose.ui.theme.MediumPriorityColor
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.fabContainerColor
import net.pilseong.todocompose.ui.theme.fabContent
import net.pilseong.todocompose.ui.theme.topBarContainerColor
import net.pilseong.todocompose.ui.viewmodel.SharedViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.SearchAppBarState
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    toTaskScreen: (List<TodoTask>) -> Unit,
    sharedViewModel: SharedViewModel,
) {
    /**
     * view model 을 통제 하는 코드가 여기서 실행 되고 관리 된다.
     */
    // 검색 기능의 상태를 매핑 그냥 view model 을 사용할 수 있지만 편의를 위한
    // composable 의 상태를 가지는 변수들
    val searchAppBarState by sharedViewModel.searchAppBarState
    val searchText: String = sharedViewModel.searchTextString
    val prioritySortState: Priority = sharedViewModel.prioritySortState


    // Flow 에 대한 collection 을 처리 하는 파이프 연결 변수들. 이 변수들 은 외부 데이터 베이스 나 외부 API 에 의존 한다.
    // 모든 task 의 상태를 감시 한다. 리스트 는 nav graph 안에서 변동 될 수 있다.
    val tasks = sharedViewModel.tasks.collectAsLazyPagingItems()

    val snackBarHostState = remember { SnackbarHostState() }

    // task screen 에서 요청한 처리의 결과를 보여 준다. undo 의 경우는 특별 하게 처리 한다.
    // enabled 는 화면에 표출될 지를 결정 하는 변수 이다.
    DisplaySnackBar(
        snackBarHostState = snackBarHostState,
        action = sharedViewModel.action,
        enabled = sharedViewModel.actionPerformed,
        title = sharedViewModel.title,
        duration = SnackbarDuration.Short,
        buttonClicked = { selectedAction, result ->
            Log.i("PHILIP", "[ListScreen] button clicked ${selectedAction.name}")

            if (result == SnackbarResult.ActionPerformed
                && sharedViewModel.action == Action.DELETE
            ) {
                Log.i("PHILIP", "[ListScreen] undo inside clicked ${selectedAction.name}")
                sharedViewModel.handleActions(Action.UNDO)
            } else {
                sharedViewModel.updateAction(Action.NO_ACTION)
            }
        },
        orderEnabled = sharedViewModel.snackBarOrderEnabled,
        dateEnabled = sharedViewModel.snackBarDateEnabled
    )

    // 상태 바의 상태가 검색이 열려 있는 경우 뒤로 가기를 하면 기본 상태로 돌아 가게 된다.
    BackHandler(
        enabled = sharedViewModel.searchAppBarState.value != SearchAppBarState.CLOSE
    ) {
        sharedViewModel.onCloseSearchBar()
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
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding())
                    .fillMaxSize(),
            ) {

                StatusLine(
                    prioritySortState = prioritySortState,
                    orderEnabled = sharedViewModel.orderEnabled,
                    dateEnabled = sharedViewModel.dateEnabled,
                    startDate = sharedViewModel.startDate,
                    endDate = sharedViewModel.endDate
                )

                ListContent(
                    tasks = tasks,
                    toTaskScreen = { index ->
                        sharedViewModel.setTaskScreenToViewerMode()
                        sharedViewModel.updateIndex(index)
                        toTaskScreen(tasks.itemSnapshotList.items)
                    },
//                    onSwipeToDelete = { action, task ->
//                        // undo 처리를 위해서 데이터 동기화 필요
//                        sharedViewModel.updateTaskContent(task)
//                        sharedViewModel.handleActions(action, task.id)
//                    },
                    onSwipeToUpdate = { index ->
                        sharedViewModel.setTaskScreenToEditorMode()
                        sharedViewModel.updateIndex(index)
                        toTaskScreen(tasks.itemSnapshotList.items)
                    },
                    header = sharedViewModel.searchAppBarState.value == SearchAppBarState.CLOSE,
                    screenMode = sharedViewModel.screenMode,
                    dateEnabled = sharedViewModel.dateEnabled
                )
            }
        },
        floatingActionButton = {
            ListFab(onFabClicked = {
                sharedViewModel.setTaskScreenToEditorMode()
                sharedViewModel.updateIndex(-1)
                toTaskScreen(tasks.itemSnapshotList.items)
            })
        },
        topBar = {
            ListAppBar(
                sharedViewModel = sharedViewModel,
                searchAppBarState = searchAppBarState,
                searchText = searchText
            )
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun StatusLine(
    prioritySortState: Priority,
    orderEnabled: Boolean,
    dateEnabled: Boolean,
    startDate: Long?,
    endDate: Long?
) {
    val containerColor = when (prioritySortState) {
        Priority.HIGH -> HighPriorityColor
        Priority.MEDIUM -> MediumPriorityColor
        Priority.LOW -> LowPriorityColor
        Priority.NONE -> MaterialTheme.colorScheme.surface
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (startDate != null || endDate != null) 40.dp else 20.dp),
        color = MaterialTheme.colorScheme.topBarContainerColor
    ) {
        Column {
            Row(
                modifier = Modifier.padding(PaddingValues(start = LARGE_PADDING))
            ) {
                Badge(
                    modifier = Modifier.padding(PaddingValues(end = SMALL_PADDING)),
                    containerColor = containerColor,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Text(
                        text = "${stringResource(id = R.string.badge_priority_label)}: $prioritySortState",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Badge(
                    modifier = Modifier.padding(PaddingValues(end = SMALL_PADDING)),
                    containerColor = if (orderEnabled) HighPriorityColor else MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Text(
                        text = if (orderEnabled) "${stringResource(id = R.string.badge_order_label)}: " +
                                stringResource(id = R.string.badge_order_asc_label)
                        else "${stringResource(id = R.string.badge_order_label)}: " +
                                stringResource(id = R.string.badge_order_desc_label),
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Badge(
                    modifier = Modifier.padding(PaddingValues(end = SMALL_PADDING)),
                    containerColor = if (dateEnabled) HighPriorityColor else MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Text(
                        text = if (dateEnabled) "${stringResource(id = R.string.badge_date_label)}: " +
                                stringResource(id = R.string.badge_date_created_at_label)
                        else "${stringResource(id = R.string.badge_date_label)}: " +
                                stringResource(id = R.string.badge_date_updated_at_label),
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            if (startDate != null || endDate != null) {
                Row(
                    modifier = Modifier.padding(PaddingValues(start = LARGE_PADDING, top = SMALL_PADDING))
                ) {
                    val startDateStr = if (startDate != null)
                        ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDate),  ZoneId.systemDefault()).format(
                            DateTimeFormatter.ofPattern("yy/MM/dd"))
                    else "first meno"

                    val endDateStr = if (endDate != null)
                        ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDate),  ZoneId.systemDefault()).format(
                            DateTimeFormatter.ofPattern("yy/MM/dd"))

                    else "up to date"
                    Text(
                        text = "Date from $startDateStr to $endDateStr",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
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
    dateEnabled: Boolean
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
fun ListFab(
    onFabClicked: (taskId: Int) -> Unit
) {
    FloatingActionButton(
        onClick = {
            onFabClicked(-1)
        },
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
            sharedViewModel = viewModel(),
        )
    }
}