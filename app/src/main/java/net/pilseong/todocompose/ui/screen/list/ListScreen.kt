package net.pilseong.todocompose.ui.screen.list

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.State
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.navigation.destination.BottomNavBar
import net.pilseong.todocompose.ui.theme.fabContainerColor
import net.pilseong.todocompose.ui.theme.fabContent
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants.MEMO_LIST
import net.pilseong.todocompose.util.Constants.NEW_ITEM_ID
import net.pilseong.todocompose.util.SearchAppBarState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    toTaskScreen: (List<TodoTask>) -> Unit,
    onClickBottomNavBar: (String) -> Unit,
    memoViewModel: MemoViewModel = hiltViewModel(),
    stateClosed: Boolean = true,
    stateOnit: Boolean = true,
    stateSuspended: Boolean = true,
    stateOpen: Boolean = true,
    stateNone: Boolean = true,
    onAppBarTitleClick: () -> Unit,
    onSearchIconClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    onTextChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onDeleteSelectedClicked: () -> Unit,
    onMoveMemoClicked: () -> Unit,
    onStateSelected: (State) -> Unit,
    onStateChange: (TodoTask, State) -> Unit,
) {
    /**
     * view model 을 통제 하는 코드가 여기서 실행 되고 관리 된다.
     */
    // 검색 기능의 상태를 매핑 그냥 view model 을 사용할 수 있지만 편의를 위한
    // composable 의 상태를 가지는 변수들
    val searchAppBarState by memoViewModel.searchAppBarState
    val searchText: String = memoViewModel.searchTextString
    val prioritySortState: Priority = memoViewModel.prioritySortState

    val intentResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            memoViewModel.handleImport(uri)
        }


    // Flow 에 대한 collection 을 처리 하는 파이프 연결 변수들. 이 변수들 은 외부 데이터 베이스 나 외부 API 에 의존 한다.
    // 모든 task 의 상태를 감시 한다. 리스트 는 nav graph 안에서 변동 될 수 있다.
    val tasks = memoViewModel.tasks.collectAsLazyPagingItems()

    val snackBarHostState = remember { SnackbarHostState() }

    // multi select 가 된 경우는 헤더를 고정 한다.
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        canScroll = {
            memoViewModel.selectedItems.isEmpty()
        },
        state = TopAppBarState(
            initialContentOffset = 0F,
            initialHeightOffset = 0F,
            initialHeightOffsetLimit = 0F
        )
    )


    // task screen 에서 요청한 처리의 결과를 보여 준다. undo 의 경우는 특별 하게 처리 한다.
    // enabled 는 화면에 표출될 지를 결정 하는 변수 이다.
    DisplaySnackBar(
        snackBarHostState = snackBarHostState,
        action = memoViewModel.action,
        enabled = memoViewModel.actionPerformed,
        title = memoViewModel.title,
        buttonClicked = { selectedAction, result ->
            Log.i("PHILIP", "[ListScreen] button clicked ${selectedAction.name}")

            if (result == SnackbarResult.ActionPerformed
                && selectedAction == Action.DELETE
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
        actionAfterPopup = { memoViewModel.updateAction(it) }
    )

    // 상태 바의 상태가 검색이 열려 있는 경우 뒤로 가기를 하면 기본 상태로 돌아 가게 된다.
    BackHandler(
        enabled = memoViewModel.searchAppBarState.value != SearchAppBarState.CLOSE ||
                memoViewModel.selectedItems.size != 0
    ) {
        if (memoViewModel.selectedItems.size != 0) {
            memoViewModel.selectedItems.clear()
        } else {
            if (memoViewModel.searchTextString.isNotEmpty()) {
                memoViewModel.searchTextString = ""
                memoViewModel.refreshAllTasks()
            } else {
                memoViewModel.onCloseSearchBar()
            }
        }
    }

    /**
     * view model 을 통제 코드 종료
     */

    // UI 처리 부분
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
            AddMemoFab(
                icon = Icons.Default.Create,
                onFabClicked = {
                    memoViewModel.updateIndex(NEW_ITEM_ID)
                    memoViewModel.setTaskScreenToEditorMode()
                    toTaskScreen(tasks.itemSnapshotList.items)
                }
            )
        },
        topBar = {
            ListAppBar(
                scrollBehavior = scrollBehavior,
                appbarTitle = memoViewModel.selectedNotebook.value.title,
                notebookColor = memoViewModel.selectedNotebook.value.priority.color,
                searchAppBarState = searchAppBarState,
                searchText = searchText,
                onImportClick = {
                    intentResultLauncher.launch("*/*")
                },
                onAppBarTitleClick = onAppBarTitleClick,
                selectedItemsCount = memoViewModel.selectedItems.size,
                onDeleteSelectedClicked = onDeleteSelectedClicked,
                onBackButtonClick = {
                    memoViewModel.selectedItems.clear()
                },
                onSearchIconClicked = onSearchIconClicked,
                onCloseClicked = onCloseClicked,
                onTextChange = onTextChange,
                onSearchClicked = onSearchClicked,
                onMoveMemoClicked = onMoveMemoClicked,
                onDeleteAllClicked = {
                    Log.i("PHILIP", "onDeleteAllClicked")
                    memoViewModel.handleActions(Action.DELETE_ALL)
                },
                onDateRangePickerConfirmed = { start, end ->
                    memoViewModel.handleActions(
                        action = Action.SEARCH_WITH_DATE_RANGE,
                        startDate = start,
                        endDate = end
                    )
                },
                onExportClick = {
                    memoViewModel.exportData()
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                onClick = onClickBottomNavBar,
                currentDestination = MEMO_LIST
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .fillMaxSize(),
        ) {
            StatusLine(
                prioritySortState = prioritySortState,
                orderEnabled = memoViewModel.orderEnabled,
                dateEnabled = memoViewModel.dateEnabled,
                startDate = memoViewModel.startDate,
                endDate = memoViewModel.endDate,
                favoriteOn = memoViewModel.sortFavorite,
                stateClosed = stateClosed,
                stateOnit = stateOnit,
                stateSuspended = stateSuspended,
                stateOpen = stateOpen,
                stateNone = stateNone,
                onCloseClick = {
                    memoViewModel.handleActions(
                        Action.SEARCH_WITH_DATE_RANGE,
                        startDate = null,
                        endDate = null
                    )
                },
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
                onStateSelected = onStateSelected
            )

            ListContent(
                tasks = tasks,
                toTaskScreen = { index ->
                    memoViewModel.setTaskScreenToViewerMode(tasks.peek(index)!!)
                    memoViewModel.updateIndex(index)
                    toTaskScreen(tasks.itemSnapshotList.items)
                },
//                    onSwipeToDelete = { action, task ->
//                        // undo 처리를 위해서 데이터 동기화 필요
//                        memoViewModel.updateTaskContent(task)
//                        memoViewModel.handleActions(action, task.id)
//                    },
                onSwipeToEdit = { index ->
                    memoViewModel.updateIndex(index)
                    memoViewModel.setTaskScreenToEditorMode(tasks.peek(index)!!)
                    toTaskScreen(tasks.itemSnapshotList.items)
                },
                header = true,//memoViewModel.searchAppBarState.value == SearchAppBarState.CLOSE,
                dateEnabled = memoViewModel.dateEnabled,
                onFavoriteClick = { todo ->
                    memoViewModel.handleActions(
                        action = Action.FAVORITE_UPDATE,
                        todoTask = todo
                    )
                },
                onLongClickReleased = {
                    memoViewModel.removeMultiSelectedItem(it)
                },
                onLongClickApplied = {
                    memoViewModel.appendMultiSelectedItem(it)
                },
                selectedItemsIds = memoViewModel.selectedItems,
                onStateSelected = onStateChange,
            )
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
    actionAfterPopup: (Action) -> Unit,
    orderEnabled: Boolean,
    dateEnabled: Boolean,
    startDate: Long?,
    endDate: Long?,
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

        Action.DELETE_SELECTED_ITEMS ->
            stringResource(id = R.string.snackbar_selected_items_deleted_message)

        Action.NOTEBOOK_CHANGE ->
            stringResource(id = R.string.snackbar_changed_notebook_message)

        Action.MOVE_TO ->
            stringResource(id = R.string.snackbar_move_to_message)

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
            actionAfterPopup(Action.NO_ACTION)
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
    onFabClicked: () -> Unit,
    icon: ImageVector
) {
    FloatingActionButton(
        onClick = {
            onFabClicked()
        },
        shape = RoundedCornerShape(4.dp),
        containerColor = MaterialTheme.colorScheme.fabContainerColor,
        contentColor = MaterialTheme.colorScheme.fabContent
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = R.string.add_button_icon),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
private fun ListScreenPreview() {
//    TodoComposeTheme {
    ListScreen(
        toTaskScreen = {},
        onClickBottomNavBar = {},
        onAppBarTitleClick = {},
        onSearchIconClicked = {},
        onCloseClicked = {},
        onTextChange = {},
        onSearchClicked = {},
        onDeleteSelectedClicked = {},
        onMoveMemoClicked = {},
        onStateSelected = {},
        onStateChange = { TodoTask, State ->

        }
    )
//    }
}