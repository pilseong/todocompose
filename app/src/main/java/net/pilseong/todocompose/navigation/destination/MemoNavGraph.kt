package net.pilseong.todocompose.navigation.destination

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.paging.compose.collectAsLazyPagingItems
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.DefaultNoteMemoCount
import net.pilseong.todocompose.data.model.NotebookWithCount
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.components.CustomAlertDialog
import net.pilseong.todocompose.ui.screen.list.DisplaySnackBar
import net.pilseong.todocompose.ui.screen.list.ListScreen
import net.pilseong.todocompose.ui.screen.task.TaskScreen
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants
import net.pilseong.todocompose.util.Constants.MEMO_LIST
import net.pilseong.todocompose.util.Constants.NOTE_ID_ARGUMENT
import net.pilseong.todocompose.util.SearchAppBarState
import net.pilseong.todocompose.util.SortOption

fun NavGraphBuilder.memoNavGraph(
    navHostController: NavHostController,
    viewModelStoreOwner: ViewModelStoreOwner,
    toTaskScreen: () -> Unit,
    toListScreen: (Int?) -> Unit,
    onClickBottomNavBar: (String) -> Unit
) {
    navigation(
        startDestination = Screen.MemoList.route,
        route = MEMO_LIST,
    ) {
        composable(
            route = Screen.MemoList.route,
            arguments = listOf(
                navArgument(NOTE_ID_ARGUMENT) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->

            // 같은 store owner가 소유하는 viewmodel을 사용한다. store owner은 상위 NavGraph이므로
            // activity가 죽지 않는 이상 계속 동일한 view model 객체를 사용하게 된다.
            val memoViewModel = hiltViewModel<MemoViewModel>(
                viewModelStoreOwner = viewModelStoreOwner
            )

            // 아래 루틴은 직전이 memo graph 에 속해 있지 않을 경우 action 을 None 으로 초기화 한다.
            // 이유는 마지막 action 에 대한 snackbar 를 화면에 보여 주어야 할지를 판단할 수가 없기 때문 이다.
            // memo detail 에서 온 경우는 어떤 action 에 의해 list 화면 으로 돌아 오는 것이기 때문에
            // action 에 대해 처리를 해야 하지만 다른 곳에서 온 경우는 엑션이 실행 되면 안된다.
            // action 은 stat e가 아니기 때문에 memolist 로 새로 진입한 경우 snackbar 는 그려 주어야 할지 판단할 수 없다.
            val route = navHostController.previousBackStackEntry?.destination?.route
            if (route != null && route != Screen.MemoDetail.route) {
                // 노트북을 변경하거나 노트북을 이동할 경우 MemoNavGraph가 실행이 된다. 이 경우 이전에 home에서 온 경우에도 이전 것이 남아 있어
                // 중복적으로 NO_ACTION이 실행되게 되는데 이것을 막기 위해서 사용하였다.
                LaunchedEffect(key1 = route) {
                    Log.i("PHILIP", "[MemoNavGraph] NO_ACTION called $route")
                    memoViewModel.updateAction(Action.NO_ACTION)
                }
            }

            val intentResultLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    memoViewModel.handleImport(uri)
                }

            val createNotebookStr =
                stringResource(id = R.string.note_screen_switch_notebook_dialog_title)
            val moveToNotebookStr =
                stringResource(id = R.string.note_screen_move_to_notebook_dialog_title)

            val openDialog = remember { mutableStateOf(false) }
            val dialogTitle = remember { mutableStateOf(createNotebookStr) }
            val action = remember { mutableStateOf(Action.NOTEBOOK_CHANGE) }
            val snackBarHostState = remember { SnackbarHostState() }


            Log.i(
                "PHILIP",
                "[memoNavGraph] ListScreen called with " +
                        "${backStackEntry.arguments?.getInt(NOTE_ID_ARGUMENT)}"
            )

            if (memoViewModel.firstFetch) {
                Log.i("PHILIP", "[MemoNavGraph] memoViewModel value ${memoViewModel.toString()}")
                memoViewModel.observePrioritySortState()
                memoViewModel.observeDateOrderEnabledState()
                memoViewModel.observeFavoriteState()
                memoViewModel.observeNotebookIdChange()
                memoViewModel.observeStateState()
                memoViewModel.observeFirstRecentNotebookIdChange()
                memoViewModel.observeSecondRecentNotebookIdChange()
            }


            ListScreen(
                snackBarHostState = snackBarHostState,
                searchAppBarState = memoViewModel.searchAppBarState,
                searchText = memoViewModel.searchTextString,
                prioritySortState = memoViewModel.prioritySortState,
                tasks = memoViewModel.tasks.collectAsLazyPagingItems(),
                selectedItems = memoViewModel.selectedItems,
                selectedNotebook = memoViewModel.selectedNotebook.value,
                toTaskScreen = { snapshot ->
                    memoViewModel.updateSnapshotTasks(snapshot)
                    // 화면 전환 시에는 action 을 초기화 해야 뒤로 가기 버튼을 눌렀을 때 오동작 을 예방할 수 있다.
                    memoViewModel.updateAction(Action.NO_ACTION)
                    toTaskScreen()
                },
                onSwipeToEdit = { index, todoTask, snapshot ->
                    memoViewModel.updateIndex(index)
                    memoViewModel.setTaskScreenToEditorMode(todoTask)
                    memoViewModel.updateSnapshotTasks(snapshot)
                    // 화면 전환 시에는 action 을 초기화 해야 뒤로 가기 버튼을 눌렀을 때 오동작 을 예방할 수 있다.
                    memoViewModel.updateAction(Action.NO_ACTION)

                    toTaskScreen()
                },
                onClickBottomNavBar = onClickBottomNavBar,
                memoViewModel = memoViewModel,
                stateCompleted = memoViewModel.stateCompleted,
                stateActive = memoViewModel.stateActive,
                stateSuspended = memoViewModel.stateSuspended,
                stateWaiting = memoViewModel.stateWaiting,
                stateNone = memoViewModel.stateNone,
                orderEnabled = (memoViewModel.dateOrderState == SortOption.CREATED_AT_ASC ||
                        memoViewModel.dateOrderState == SortOption.UPDATED_AT_ASC),
                dataEnabled = (memoViewModel.dateOrderState == SortOption.CREATED_AT_ASC ||
                        memoViewModel.dateOrderState == SortOption.CREATED_AT_DESC),
                onAppBarTitleClick = {
                    memoViewModel.getDefaultNoteCount()
                    action.value = Action.NOTEBOOK_CHANGE
                    dialogTitle.value = createNotebookStr
                    openDialog.value = true
                },
                onSearchIconClicked = {
                    // 초기 로딩 을 위한 데이터 검색
                    memoViewModel.onOpenSearchBar()
                },
                onCloseClicked = {
                    if (memoViewModel.searchTextString.isNotEmpty() ||
                            memoViewModel.searchRangeAll) {
                        memoViewModel.searchTextString = ""
                        memoViewModel.handleActions(Action.SEARCH_RANGE_CHANGE, searchRangeAll = false)
                    } else {
                        memoViewModel.onCloseSearchBar()
                    }
                },
                onSearchClicked = {
                    memoViewModel.refreshAllTasks()
                },
                onTextChange = { text ->
                    memoViewModel.searchTextString = text
                    memoViewModel.refreshAllTasks()
                },
                onDeleteSelectedClicked = {
                    memoViewModel.handleActions(Action.DELETE_SELECTED_ITEMS)
                },
                onMoveMemoClicked = {
                    Log.i("PHILIP", "onMoveMemoClicked")
                    memoViewModel.getDefaultNoteCount()
                    action.value = Action.MOVE_TO
                    dialogTitle.value = moveToNotebookStr
                    openDialog.value = true
                },
                onStateSelected = {state ->
                    memoViewModel.handleActions(Action.STATE_FILTER_CHANGE, state = state)
                },
                onStateChange = { task, state ->
                    memoViewModel.handleActions(Action.STATE_CHANGE, todoTask = task, state = state)
                },
                onImportClick = {
                    intentResultLauncher.launch("*/*")
                },
                onFabClicked = { items ->
                    memoViewModel.updateIndex(Constants.NEW_ITEM_ID)
                    memoViewModel.setTaskScreenToEditorMode()
                    memoViewModel.updateSnapshotTasks(items)
                    // 화면 전환 시에는 action 을 초기화 해야 뒤로 가기 버튼을 눌렀을 때 오동작 을 예방할 수 있다.
                    memoViewModel.updateAction(Action.NO_ACTION)
                    toTaskScreen()
                },
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
                },
                onSearchRangeAllClicked = {
                    memoViewModel.handleActions(Action.SEARCH_RANGE_CHANGE, searchRangeAll = it)
                },
                onDateRangeCloseClick = {
                    memoViewModel.handleActions(
                        Action.SEARCH_WITH_DATE_RANGE,
                        startDate = null,
                        endDate = null
                    )
                },
                onFavoriteSortClick = {
                    memoViewModel.handleActions(
                        action = Action.SORT_FAVORITE_CHANGE,
                        favorite = !memoViewModel.sortFavorite
                    )
                },
                onOrderEnabledClick = {
                    memoViewModel.handleActions(
                        action = Action.SORT_ORDER_CHANGE,
                        sortOrderEnabled = !(memoViewModel.dateOrderState == SortOption.CREATED_AT_ASC ||
                                memoViewModel.dateOrderState == SortOption.UPDATED_AT_ASC),
                    )
                },
                onDateEnabledClick = {
                    memoViewModel.handleActions(
                        action = Action.SORT_DATE_CHANGE,
                        sortDateEnabled = !(memoViewModel.dateOrderState == SortOption.CREATED_AT_ASC ||
                                memoViewModel.dateOrderState == SortOption.CREATED_AT_DESC),
                    )
                },
                onPrioritySelected = { priority ->
                    Log.i("PHILIP", "onSortClicked")
                    memoViewModel.handleActions(
                        Action.PRIORITY_CHANGE,
                        priority = priority
                    )
                },
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
            )

            NotebooksPickerDialog(
                dialogTitle = dialogTitle.value,
                visible = openDialog.value,
                onDismissRequest = {
                    openDialog.value = false
                },
                notebooks = memoViewModel.notebooks.collectAsState().value,
                defaultNoteMemoCount = memoViewModel.defaultNoteMemoCount,
                onCloseClick = {
                    openDialog.value = false
                },
                onNotebookClick = { id ->
                    Log.i("PHILIP", "[MemoNavGraph] onNotebookClick $id")
                    memoViewModel.handleActions(action.value, notebookId = id)
                    openDialog.value = false
                }
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
                enabled = memoViewModel.searchAppBarState != SearchAppBarState.CLOSE ||
                        memoViewModel.selectedItems.size != 0
            ) {
                // 선택 해제
                if (memoViewModel.selectedItems.size != 0) {
                    memoViewModel.selectedItems.clear()

                    // 검색바 조절
                } else {
                    if (memoViewModel.searchTextString.isNotEmpty() || memoViewModel.searchRangeAll) {
                        memoViewModel.searchTextString = ""
                        memoViewModel.handleActions(Action.SEARCH_RANGE_CHANGE, searchRangeAll = false)
                    } else {
                        memoViewModel.onCloseSearchBar()
                    }
                }
            }

        }


        composable(
            route = Screen.MemoDetail.route,
        ) {
            val memoViewModel = hiltViewModel<MemoViewModel>(
                viewModelStoreOwner = viewModelStoreOwner
            )

            Log.i(
                "PHILIP",
                "[memoNavGraph] TaskScreen called"
            )

            // activity가 destory 되고 다시 생성된 경우는 List 화면으로 forwarding
            if (memoViewModel.firstFetch) {
                Log.i("PHILIP", "[MemoNavGraph] memoViewModel value ${memoViewModel.toString()}")
                navHostController.navigate(Screen.MemoList.route)
            } else {

                TaskScreen(
                    memoViewModel = memoViewModel,
                    toListScreen = toListScreen
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebooksPickerDialog(
    dialogTitle: String = "",
    visible: Boolean,
    notebooks: List<NotebookWithCount>,
    defaultNoteMemoCount: DefaultNoteMemoCount,
    onDismissRequest: () -> Unit,
    onCloseClick: () -> Unit,
    onNotebookClick: (Int) -> Unit
) {
    if (visible) {
        CustomAlertDialog(onDismissRequest = { onDismissRequest() }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = LARGE_PADDING)
                        .padding(horizontal = XLARGE_PADDING),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ListAlt,
                        contentDescription = "list icon"
                    )
                    Spacer(modifier = Modifier.width(SMALL_PADDING))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = dialogTitle,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,

                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Surface(
                    modifier = Modifier
                        .height(300.dp)
                        .padding(XLARGE_PADDING)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(4.dp),
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                ) {
                    LazyColumn(
                        // contentPadding은 전체를 감싸는 padding
                        contentPadding = PaddingValues(LARGE_PADDING),
                        verticalArrangement = Arrangement.spacedBy(LARGE_PADDING)
                    ) {
                        items(
                            items = notebooks,
                            key = { notebook ->
                                notebook.id
                            }
                        ) {
                            Surface(
                                modifier = Modifier
                                    .height(56.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        onNotebookClick(it.id)
                                    },
                                shape = RoundedCornerShape(4.dp),
                                color = it.priority.color.copy(alpha = 0.4F),
                                tonalElevation = 6.dp,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(start = LARGE_PADDING),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (it.title == "") ""
                                        else if (it.title.length > 20) "${
                                            it.title.substring(
                                                startIndex = 0,
                                                endIndex = 20
                                            )
                                        }..."
                                        else it.title
                                    )
                                }
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color.Transparent
                                ) {
                                    Row(horizontalArrangement = Arrangement.End) {
                                        Badge {
                                            Text(text = it.memoCount.toString())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(XLARGE_PADDING)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(modifier = Modifier.width(IntrinsicSize.Max)) {
                        OutlinedButton(
                            shape = RoundedCornerShape(4.dp),
                            onClick = {
                                onNotebookClick(-1)
                            }) {
                            Text(text = stringResource(id = R.string.note_select_use_default))

                        }
                        Row(horizontalArrangement = Arrangement.End) {
                            Badge {
                                Text(text = defaultNoteMemoCount.total.toString())
                            }
                        }
                    }
                    // Close 버튼
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .fillMaxWidth()
                            .clickable {
                                onCloseClick()
                            }
                            .padding(12.dp),
                        textAlign = TextAlign.End,
                        text = stringResource(id = R.string.close_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun NotebooksPickerDialogPreview() {
    MaterialTheme {
        NotebooksPickerDialog(
            visible = true,
            onCloseClick = {},
            onDismissRequest = {},
            notebooks = listOf(
                NotebookWithCount(
                    id = 1,
                    title = "My Love Note",
                    description = "desc1",
                    priority = Priority.NONE
                ),
                NotebookWithCount(
                    id = 2,
                    title = "first notebooksss",
                    description = "desc2",
                    priority = Priority.NONE
                ),
                NotebookWithCount(
                    id = 3, title = "test3", description = "desc3", priority = Priority.NONE
                )
            ),
            defaultNoteMemoCount = DefaultNoteMemoCount(0, 0, 0, 0, 0),
            onNotebookClick = {

            }
        )
    }
}