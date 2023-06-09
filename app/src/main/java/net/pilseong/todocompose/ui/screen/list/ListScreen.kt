package net.pilseong.todocompose.ui.screen.list

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Notes
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
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.theme.fabContainerColor
import net.pilseong.todocompose.ui.theme.fabContent
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.ui.viewmodel.toMemoTask
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.SearchAppBarState
import net.pilseong.todocompose.util.SortOption
import net.pilseong.todocompose.util.StateEntity

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    uiState: UserData,
    memoViewModel: MemoViewModel = hiltViewModel(),
    selectedNotebook: Notebook,
    snackBarHostState: SnackbarHostState,
    tasks: LazyPagingItems<MemoWithNotebook>,
    searchAppBarState: SearchAppBarState = SearchAppBarState.CLOSE,
    searchText: String = "",
    searchRangeAll: Boolean = false,
    selectedItems: SnapshotStateList<Long>,
    toTaskScreen: () -> Unit,
    onSwipeToEdit: (Int, MemoWithNotebook) -> Unit,
    toNoteScreen: () -> Unit,
    onAppBarTitleClick: () -> Unit,
    onSearchIconClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    onTextChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onDeleteSelectedClicked: () -> Unit,
    onMoveMemoClicked: () -> Unit,
    onStateSelected: (State) -> Unit,
    onStateChange: (MemoWithNotebook, State) -> Unit,
    onImportClick: () -> Unit,
    onFabClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    onDateRangePickerConfirmed: (Long?, Long?) -> Unit,
    onExportClick: () -> Unit,
    onSearchRangeAllClicked: (Boolean, Boolean) -> Unit,
    onDateRangeCloseClick: () -> Unit,
    onFavoriteSortClick: (Boolean) -> Unit,
    onOrderEnabledClick: (Boolean) -> Unit,
    onDateEnabledClick: (Boolean) -> Unit,
    onPrioritySelected: (Action, Priority, Boolean) -> Unit,
    onFavoriteClick: (MemoWithNotebook) -> Unit,
    onLongClickReleased: (Long) -> Unit,
    onLongClickApplied: (Long) -> Unit,
    onStateSelectedForMultipleItems: (State) -> Unit,
    onToggleClicked: () -> Unit,
    onSetAllOrNothingClicked: (Boolean) -> Unit,
    onSearchNoFilterClicked: (Boolean) -> Unit,
    onStatusLineUpdate: (StateEntity) -> Unit,
) {
    // multi select 가 된 경우는 헤더를 고정 한다.
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        canScroll = { selectedItems.isEmpty() },
        state = rememberTopAppBarState(
            initialContentOffset = 0F,
            initialHeightOffset = 0F,
            initialHeightOffsetLimit = 0F
        )
    )

    val offsetState by remember { derivedStateOf { selectedItems.isNotEmpty() } }

    if (offsetState) scrollBehavior.state.heightOffset = 0F

//    Log.d("PHILIP", "[ListScreen] total size of memos ${tasks.itemCount}")

    /**
     * view model 을 통제 코드 종료
     */

    // UI 처리 부분
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    actionColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        },
        topBar = {
            ListAppBar(
                scrollBehavior = scrollBehavior,
                appbarTitle = selectedNotebook.title,
                notebookColor = selectedNotebook.priority.color,
                searchAppBarState = searchAppBarState,
                searchRangeAll = searchRangeAll,
                searchText = searchText,
                searchNoFilterState = memoViewModel.searchNoFilterState,
                onImportClick = onImportClick,
                onAppBarTitleClick = onAppBarTitleClick,
                selectedItemsCount = selectedItems.size,
                onDeleteSelectedClicked = onDeleteSelectedClicked,
                onBackButtonClick = { selectedItems.clear() },
                onSearchIconClicked = onSearchIconClicked,
                onCloseClicked = onCloseClicked,
                onTextChange = onTextChange,
                onSearchClicked = onSearchClicked,
                onMoveMemoClicked = onMoveMemoClicked,
                onDeleteAllClicked = onDeleteAllClicked,
                onDateRangePickerConfirmed = onDateRangePickerConfirmed,
                onExportClick = onExportClick,
                onSearchNoFilterClicked = onSearchNoFilterClicked,
                onStateSelectedForMultipleItems = onStateSelectedForMultipleItems,
            )
        },
        bottomBar = { BottomActionBarNavigation(toNoteScreen = toNoteScreen) { onFabClicked() } }
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
                uiState = uiState,
                prioritySortState = uiState.prioritySortState,
                orderEnabled = (uiState.dateOrderState == SortOption.CREATED_AT_ASC ||
                        uiState.dateOrderState == SortOption.UPDATED_AT_ASC),
                dateEnabled = (uiState.dateOrderState == SortOption.CREATED_AT_ASC ||
                        uiState.dateOrderState == SortOption.CREATED_AT_DESC),
                searchRangeAll = memoViewModel.searchRangeAll,
                startDate = memoViewModel.startDate,
                endDate = memoViewModel.endDate,
                favoriteOn = memoViewModel.uiState.sortFavorite,
                onCloseClick = onDateRangeCloseClick,
                onFavoriteClick = onFavoriteSortClick,
                onOrderEnabledClick = onOrderEnabledClick,
                onDateEnabledClick = onDateEnabledClick,
                onPrioritySelected = onPrioritySelected,
                onStateSelected = onStateSelected,
                onRangeAllEnabledClick = onSearchRangeAllClicked,
                onToggleClicked = onToggleClicked,
                onSetAllOrNothingClicked = onSetAllOrNothingClicked,
                onStatusLineUpdate = onStatusLineUpdate,
            )

            ListContent(
                tasks = tasks,
                toTaskScreen = { index ->
                    memoViewModel.setTaskScreenToViewerMode(tasks[index]!!.toMemoTask())
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
                dateEnabled = (uiState.dateOrderState == SortOption.CREATED_AT_ASC ||
                        uiState.dateOrderState == SortOption.CREATED_AT_DESC),
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
    toNoteScreen: () -> Unit,
    onFabClicked: () -> Unit,
) {
    BottomAppBar(
        modifier = Modifier.height(65.dp),
        actions = {
            Row(modifier = Modifier.fillMaxWidth(0.80F)) {
                Spacer(modifier = Modifier.width(25.dp))
                IconButton(modifier = Modifier.padding(start = XLARGE_PADDING),
                    onClick = {
                        toNoteScreen()
                    }) {
                    Icon(Icons.Default.NoteAlt, contentDescription = "Localized description")
                }
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Localized description",
                    )
                }
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
        BottomActionBarNavigation(toNoteScreen = {}, onFabClicked = {})
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
            PagingData.from<MemoWithNotebook>(
                listOf(
//                    MemoTask(
//                        1,
//                        "필성 힘내!!!",
//                        "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
//                        Priority.HIGH,
//                        notebookId = -1
//                    )
                )
            )
        ).collectAsLazyPagingItems(),
        uiState = UserData(),
        snackBarHostState = SnackbarHostState(),
        toTaskScreen = {},
        toNoteScreen = {},
        onAppBarTitleClick = {},
        onSearchIconClicked = {},
        onCloseClicked = {},
        onTextChange = {},
        onSearchClicked = {},
        onDeleteSelectedClicked = {},
        onMoveMemoClicked = {},
        onStateSelected = { _ -> },
        onStateChange = { _, _ -> },
        onImportClick = {},
        onFabClicked = {},
        onDeleteAllClicked = {},
        onExportClick = {},
        onSearchRangeAllClicked = {_, _ ->},
        onDateRangePickerConfirmed = { _, _ -> },
        onDateRangeCloseClick = {},
        onFavoriteSortClick = {},
        onOrderEnabledClick = {},
        onDateEnabledClick = {},
        onPrioritySelected = { _, _, _ -> },
        onFavoriteClick = {},
        onLongClickReleased = {},
        onLongClickApplied = {},
        onSwipeToEdit = { _, _ -> },
        onStateSelectedForMultipleItems = {},
        onToggleClicked = {},
        onSetAllOrNothingClicked = { _ -> },
        onSearchNoFilterClicked = {},
        onStatusLineUpdate = {}
    )
}