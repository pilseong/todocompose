package net.pilseong.todocompose.ui.screen.list

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.State
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.theme.fabContainerColor
import net.pilseong.todocompose.ui.theme.fabContent
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Constants.HOME_SCREEN
import net.pilseong.todocompose.util.SearchAppBarState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    memoViewModel: MemoViewModel = hiltViewModel(),
    selectedNotebook: Notebook,
    snackBarHostState: SnackbarHostState,
    tasks: LazyPagingItems<TodoTask>,
    searchAppBarState: SearchAppBarState = SearchAppBarState.CLOSE,
    searchText: String = "",
    orderEnabled: Boolean = false,
    dataEnabled: Boolean = false,
    selectedItems: SnapshotStateList<Int>,
    prioritySortState: Priority = Priority.NONE,
    stateCompleted: Boolean = true,
    stateActive: Boolean = true,
    stateSuspended: Boolean = true,
    stateWaiting: Boolean = true,
    stateNone: Boolean = true,
    toTaskScreen: () -> Unit,
    onSwipeToEdit: (Int, TodoTask) -> Unit,
    onClickBottomNavBar: (String) -> Unit,
    onAppBarTitleClick: () -> Unit,
    onSearchIconClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    onTextChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onDeleteSelectedClicked: () -> Unit,
    onMoveMemoClicked: () -> Unit,
    onStateSelected: (State) -> Unit,
    onStateChange: (TodoTask, State) -> Unit,
    onImportClick: () -> Unit,
    onFabClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    onDateRangePickerConfirmed: (Long?, Long?) -> Unit,
    onExportClick: () -> Unit,
    onSearchRangeAllClicked: (Boolean) -> Unit,
    onDateRangeCloseClick: () -> Unit,
    onFavoriteSortClick: () -> Unit,
    onOrderEnabledClick: () -> Unit,
    onDateEnabledClick: () -> Unit,
    onPrioritySelected: (Priority) -> Unit,
    onFavoriteClick: (TodoTask) -> Unit,
    onLongClickReleased: (Int) -> Unit,
    onLongClickApplied: (Int) -> Unit,
) {
    // Flow 에 대한 collection 을 처리 하는 파이프 연결 변수들. 이 변수들 은 외부 데이터 베이스 나 외부 API 에 의존 한다.
    // 모든 task 의 상태를 감시 한다. 리스트 는 nav graph 안에서 변동 될 수 있다.

//    val snackBarHostState = remember { SnackbarHostState() }

    // multi select 가 된 경우는 헤더를 고정 한다.
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        canScroll = {
            selectedItems.isEmpty()
        },
        state = TopAppBarState(
            initialContentOffset = 0F,
            initialHeightOffset = 0F,
            initialHeightOffsetLimit = 0F
        )
    )


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
//        floatingActionButton = {
//            AddMemoFab(
//                icon = Icons.Default.Create,
//                onFabClicked = {
//                    onFabClicked(tasks.itemSnapshotList.items)
//                }
//            )
//        },
        topBar = {
            ListAppBar(
                scrollBehavior = scrollBehavior,
                appbarTitle = selectedNotebook.title,
                notebookColor = selectedNotebook.priority.color,
                searchAppBarState = searchAppBarState,
                searchText = searchText,
                searchRangeAll = memoViewModel.searchRangeAll,
                onImportClick = onImportClick,
                onAppBarTitleClick = onAppBarTitleClick,
                selectedItemsCount = selectedItems.size,
                onDeleteSelectedClicked = onDeleteSelectedClicked,
                onBackButtonClick = {
                    selectedItems.clear()
                },
                onSearchIconClicked = onSearchIconClicked,
                onCloseClicked = onCloseClicked,
                onTextChange = onTextChange,
                onSearchClicked = onSearchClicked,
                onMoveMemoClicked = onMoveMemoClicked,
                onDeleteAllClicked = onDeleteAllClicked,
                onDateRangePickerConfirmed = onDateRangePickerConfirmed,
                onExportClick = onExportClick,
                onSearchRangeAllClicked = onSearchRangeAllClicked,
            )
        },
        bottomBar = {
            BottomActionBarNavigation(onClickBottomNavBar) {
                onFabClicked()
            }
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
                orderEnabled = orderEnabled,
                dateEnabled = dataEnabled,
                startDate = memoViewModel.startDate,
                endDate = memoViewModel.endDate,
                favoriteOn = memoViewModel.uiState.sortFavorite,
                stateCompleted = stateCompleted,
                stateActive = stateActive,
                stateSuspended = stateSuspended,
                stateWaiting = stateWaiting,
                stateNone = stateNone,
                onCloseClick = onDateRangeCloseClick,
                onFavoriteClick = onFavoriteSortClick,
                onOrderEnabledClick = onOrderEnabledClick,
                onDateEnabledClick = onDateEnabledClick,
                onPrioritySelected = onPrioritySelected,
                onStateSelected = onStateSelected
            )

            ListContent(
                tasks = tasks,
                toTaskScreen = { index ->
                    memoViewModel.setTaskScreenToViewerMode(tasks.peek(index)!!)
                    memoViewModel.updateIndex(index)
                    toTaskScreen()
                },
//                    onSwipeToDelete = { action, task ->
//                        // undo 처리를 위해서 데이터 동기화 필요
//                        memoViewModel.updateTaskContent(task)
//                        memoViewModel.handleActions(action, task.id)
//                    },
                onSwipeToEdit = onSwipeToEdit,
                header = true,
                dateEnabled = dataEnabled,
                onFavoriteClick = onFavoriteClick,
                onLongClickReleased = onLongClickReleased,
                onLongClickApplied = onLongClickApplied,
                selectedItemsIds = selectedItems,
                onStateSelected = onStateChange,
            )
        }
    }
}

@Composable
private fun BottomActionBarNavigation(
    onClickBottomNavBar: (String) -> Unit,
    onFabClicked: () -> Unit,
) {
    BottomAppBar(
        modifier = Modifier.height(65.dp),
        actions = {
            Row(modifier = Modifier.fillMaxWidth(0.80F)) {
//                Row(modifier = Modifier.weight(1F)) {
                    IconButton(modifier = Modifier.padding(start = XLARGE_PADDING),
                        onClick = {
                            onClickBottomNavBar(HOME_SCREEN)
                        }) {
                        Icon(Icons.Filled.Home, contentDescription = "Localized description")
//                    }
                }
//                Row(modifier = Modifier.weight(1F)) {
//                    Spacer(modifier = Modifier.width(18.dp))
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Localized description",
                        )
                    }
//                }
            }
        },
        floatingActionButton = {
            AddMemoFab(
                icon = Icons.Default.Create,
                size = 50.dp,
                paddingEnd = 4.dp,
                onFabClicked = {
                    onFabClicked()
                }
            )
        },
        contentPadding = PaddingValues(0.dp)
    )
}

@Preview
@Composable
fun BottomActionBarNavPreview() {
    MaterialTheme {
        BottomActionBarNavigation(onClickBottomNavBar = {}, onFabClicked = {})
    }

}


// Floating Action Button
@Composable
fun AddMemoFab(
    paddingEnd: Dp = 0.dp,
    size: Dp = 56.dp,
    onFabClicked: () -> Unit,
    icon: ImageVector
) {
    FloatingActionButton(
        modifier = Modifier.size(size),
        onClick = {
            onFabClicked()
        },
        shape = RoundedCornerShape(paddingEnd),
        containerColor = MaterialTheme.colorScheme.fabContainerColor,
        contentColor = MaterialTheme.colorScheme.fabContent
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = R.string.add_button_icon),
        )
    }
}

@Preview
@Composable
fun PreviewAddMenuFab() {
    MaterialTheme {
        AddMemoFab(onFabClicked = { /*TODO*/ }, icon = Icons.Default.Create)
    }
}

@Composable
@Preview
private fun ListScreenPreview() {
    ListScreen(
        selectedItems = SnapshotStateList(),
        selectedNotebook = Notebook.instance(),
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
        snackBarHostState = SnackbarHostState(),
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
        onStateChange = { TodoTask, State -> },
        onImportClick = {},
        onFabClicked = {},
        onDeleteAllClicked = {},
        onExportClick = {},
        onSearchRangeAllClicked = {},
        onDateRangePickerConfirmed = { a, b -> },
        onDateRangeCloseClick = {},
        onFavoriteSortClick = {},
        onOrderEnabledClick = {},
        onDateEnabledClick = {},
        onPrioritySelected = {},
        onFavoriteClick = {},
        onLongClickReleased = {},
        onLongClickApplied = {},
        onSwipeToEdit = { a, todo -> }

    )
}