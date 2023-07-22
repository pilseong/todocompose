package net.pilseong.todocompose.ui.screen.list

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.Task
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
import net.pilseong.todocompose.data.model.ui.MemoDateSortingOption
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.theme.fabContainerColor
import net.pilseong.todocompose.ui.theme.fabContent
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.SearchAppBarState
import net.pilseong.todocompose.util.StateEntity

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    uiState: UserData,
    memoViewModel: MemoViewModel = hiltViewModel(),
    searchNoFilterState: Boolean = false,
    selectedNotebook: Notebook,
    snackBarHostState: SnackbarHostState,
    tasks: LazyPagingItems<MemoWithNotebook>,
    searchAppBarState: SearchAppBarState = SearchAppBarState.CLOSE,
    searchText: String = "",
    selectedItems: SnapshotStateList<Long>,
    toTaskScreen: () -> Unit,
    toTaskManagementScreen: () -> Unit,
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
    onDateSortingChangeClick: (Action, MemoDateSortingOption, Boolean) -> Unit,
    onPrioritySelected: (Action, Priority, Boolean) -> Unit,
    onFavoriteClick: (MemoWithNotebook) -> Unit,
    onLongClickApplied: (Long) -> Unit,
    onStateSelectedForMultipleItems: (State) -> Unit,
    onToggleClicked: () -> Unit,
    onSetAllOrNothingClicked: (Boolean) -> Unit,
    onSearchNoFilterClicked: (Boolean) -> Unit,
    onStatusLineUpdate: (StateEntity) -> Unit,
) {
    // multi select 가 된 경우는 헤더를 고정 한다.
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
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
                searchRangeAll = uiState.searchRangeAll,
                searchText = searchText,
                searchNoFilterState = searchNoFilterState,
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
        bottomBar = {
            BottomActionBarNavigation(
                toTaskManagementScreen = toTaskManagementScreen,
                toNoteScreen = toNoteScreen,
            ) { onFabClicked() }
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
            ListView(
                uiState,
                memoViewModel,
                onDateRangeCloseClick,
                onFavoriteSortClick,
                onOrderEnabledClick,
                onDateSortingChangeClick,
                onPrioritySelected,
                onStateSelected,
                onSearchRangeAllClicked,
                onToggleClicked,
                onSetAllOrNothingClicked,
                onStatusLineUpdate,
                tasks,
                toTaskScreen,
                onSwipeToEdit,
                onFavoriteClick,
                onLongClickApplied,
                selectedItems,
                onStateChange
            )
        }
    }
}


@Composable
private fun BottomActionBarNavigation(
    toNoteScreen: () -> Unit,
    toTaskManagementScreen: () -> Unit,
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
                    Icon(
                        Icons.Default.NoteAlt, contentDescription = "Localized description",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                IconButton(modifier = Modifier.padding(start = XLARGE_PADDING),
                    onClick = {
                    }) {
                    Icon(
                        Icons.Default.StickyNote2, contentDescription = "Memo list",
                    )
                }
                IconButton(
                    enabled = true,
                    onClick = {
                        toTaskManagementScreen()
                    }) {
                    Icon(
                        Icons.Default.Task, contentDescription = "Task Manager",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = "Scheduling",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
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
        BottomActionBarNavigation(
            toNoteScreen = {},
            toTaskManagementScreen = {},
            onFabClicked = {})
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
        onStateSelected = { },
        onStateChange = { _, _ -> },
        onImportClick = {},
        onFabClicked = {},
        onDeleteAllClicked = {},
        onExportClick = {},
        onSearchRangeAllClicked = { _, _ -> },
        onDateRangePickerConfirmed = { _, _ -> },
        onDateRangeCloseClick = {},
        onFavoriteSortClick = {},
        onOrderEnabledClick = {},
        onDateSortingChangeClick = { _, _, _ ->},
        onPrioritySelected = { _, _, _ -> },
        onFavoriteClick = {},
        onLongClickApplied = {},
        onSwipeToEdit = { _, _ -> },
        onStateSelectedForMultipleItems = {},
        onToggleClicked = {},
        onSetAllOrNothingClicked = {},
        onSearchNoFilterClicked = {},
        onStatusLineUpdate = {},
        toTaskManagementScreen = {}
    )
}