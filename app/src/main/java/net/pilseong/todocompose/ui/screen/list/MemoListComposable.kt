package net.pilseong.todocompose.ui.screen.list

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.sharedViewModel
import net.pilseong.todocompose.ui.components.InfoAlertDialog
import net.pilseong.todocompose.ui.components.NotebooksPickerDialog
import net.pilseong.todocompose.ui.components.ProgressIndicator
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.ui.viewmodel.toMemoTask
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants
import net.pilseong.todocompose.util.SearchAppBarState
import net.pilseong.todocompose.util.SortOption

fun NavGraphBuilder.memoListComposable(
    navHostController: NavHostController,
    toTaskScreen: () -> Unit,
    toNoteScreen: () -> Unit
) {
    composable(
        route = Screen.MemoList.route,
        arguments = listOf(
            navArgument(Constants.NOTE_ID_ARGUMENT) {
                type = NavType.IntType
                defaultValue = 0
            }
        )
    ) { navBackStackEntry ->

        val memoViewModel = navBackStackEntry.sharedViewModel<MemoViewModel>(navHostController)

        LaunchedEffect(key1 = Unit) {
            Log.d("PHILIP", "[memoListComposable] memoViewModel $memoViewModel")
        }

        // 아래 루틴은 직전이 memo screen 에 속해 있지 않을 경우 action 을 None 으로 초기화 한다.
        // 이유는 마지막 action 에 대한 snack bar 를 화면에 보여 주어야 할지를 판단할 수가 없기 때문 이다.
        // memo detail 에서 온 경우는 어떤 action 에 의해 list 화면 으로 돌아 오는 것이기 때문에
        // action 에 대해 처리를 해야 하지만 다른 곳에서 온 경우는 엑션이 실행 되면 안 된다.
        // action 은 state 가 아니기 때문에 memo list 로 새로 진입한 경우 snack bar 는 그려 주어야 할지 판단할 수 없다.
        val route = navHostController.previousBackStackEntry?.destination?.route

        if (route != null && (route != Screen.MemoDetail.route && route != Screen.MemoList.route)) {
            // 노트북 을 변경 하거나 노트북 을 이동할 경우 MemoNavGraph 가 실행이 된다. 이 경우 이전에 home 에서 온 경우 에도 이전 것이 남아 있어
            // 중복적 으로 NO_ACTION 이 실행 되게 되는데 이것을 막기 위해서 사용 하였다.
            LaunchedEffect(key1 = route) {
                Log.d("PHILIP", "[MemoNavGraph] NO_ACTION called $route")
                memoViewModel.updateAction(Action.NO_ACTION)
            }
        }

        val uiState = memoViewModel.uiState

        val intentResultLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) memoViewModel.handleImport(uri)
            }

        var openDialog by remember { mutableStateOf(false) }
        var dialogMode by remember { mutableStateOf(0) }
        val snackBarHostState = remember { SnackbarHostState() }

//        Log.d(
//            "PHILIP",
//            "[memoNavGraph] ListScreen called with " +
//                    "${navBackStackEntry.arguments?.getInt(Constants.NOTE_ID_ARGUMENT)}"
//        )

        ListScreen(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            searchAppBarState = memoViewModel.searchAppBarState,
            searchText = memoViewModel.searchTextString,
            searchRangeAll = memoViewModel.searchRangeAll,
            tasks = memoViewModel.tasks.collectAsLazyPagingItems(),
            selectedItems = memoViewModel.selectedItems,
            selectedNotebook = memoViewModel.selectedNotebook.value,
            memoViewModel = memoViewModel,
            toTaskScreen = {
                // 화면 전환 시에는 action 을 초기화 해야 뒤로 가기 버튼을 눌렀을 때 오동작 을 예방할 수 있다.
                memoViewModel.updateAction(Action.NO_ACTION)
                toTaskScreen()
            },
            onSwipeToEdit = { index, memo ->
                memoViewModel.updateIndex(index)
                memoViewModel.setTaskScreenToEditorMode(memo)
                // 화면 전환 시에는 action 을 초기화 해야 뒤로 가기 버튼을 눌렀을 때 오동작 을 예방할 수 있다.
                memoViewModel.updateAction(Action.NO_ACTION)

                toTaskScreen()
            },
            toNoteScreen = toNoteScreen,
            onAppBarTitleClick = {
                memoViewModel.getDefaultNoteCount()
                dialogMode = 0
                openDialog = true
            },
            // 초기 로딩 을 위한 데이터 검색
            onSearchIconClicked = { memoViewModel.onOpenSearchBar() },
            onCloseClicked = {
                if (memoViewModel.searchTextString.isNotEmpty() ||
                    memoViewModel.searchRangeAll
                ) {
                    memoViewModel.searchTextString = ""
                    memoViewModel.handleActions(
                        Action.SEARCH_RANGE_CHANGE,
                        searchRangeAll = false
                    )
                } else memoViewModel.onCloseSearchBar()
            },
            onSearchClicked = { memoViewModel.refreshAllTasks() },
            onTextChange = { text ->
                memoViewModel.searchTextString = text
                memoViewModel.refreshAllTasks()
            },
            onDeleteSelectedClicked = { memoViewModel.handleActions(Action.DELETE_SELECTED_ITEMS) },
            onMoveMemoClicked = {
                Log.d("PHILIP", "onMoveMemoClicked")
                memoViewModel.getDefaultNoteCount()
                dialogMode = 1
                openDialog = true
            },
            onStateChange = { task, state ->
                memoViewModel.handleActions(
                    Action.STATE_CHANGE,
                    memo = task.toMemoTask(),
                    state = state
                )
            },
            onImportClick = { intentResultLauncher.launch("text/plain") },
            onFabClicked = {
                memoViewModel.updateIndex(Constants.NEW_ITEM_INDEX)
                memoViewModel.setTaskScreenToEditorMode()
                // 화면 전환 시에는 action 을 초기화 해야 뒤로 가기 버튼을 눌렀을 때 오동작 을 예방할 수 있다.
                memoViewModel.updateAction(Action.NO_ACTION)
                toTaskScreen()
            },
            onDeleteAllClicked = {
                Log.d("PHILIP", "onDeleteAllClicked")
                memoViewModel.handleActions(Action.DELETE_ALL)
            },
            onDateRangePickerConfirmed = { start, end ->
                memoViewModel.handleActions(
                    action = Action.SEARCH_WITH_DATE_RANGE,
                    startDate = start,
                    endDate = end
                )
            },
            onExportClick = { memoViewModel.exportData() },
            onSearchRangeAllClicked = { state, orderUpdate ->
                memoViewModel.handleActions(
                    Action.SEARCH_RANGE_CHANGE,
                    searchRangeAll = state,
                    statusLineOrderUpdate = orderUpdate

                )
            },
            onDateRangeCloseClick = {
                memoViewModel.handleActions(
                    Action.SEARCH_WITH_DATE_RANGE,
                    startDate = null,
                    endDate = null
                )
            },
            onFavoriteSortClick = { orderUpdate ->
                memoViewModel.handleActions(
                    action = Action.SORT_FAVORITE_CHANGE,
                    favorite = !uiState.sortFavorite,
                    statusLineOrderUpdate = orderUpdate
                )
            },
            onOrderEnabledClick = { orderUpdate ->
                memoViewModel.handleActions(
                    action = Action.SORT_ORDER_CHANGE,
                    sortOrderEnabled = !(uiState.dateOrderState == SortOption.CREATED_AT_ASC ||
                            uiState.dateOrderState == SortOption.UPDATED_AT_ASC),
                    statusLineOrderUpdate = orderUpdate

                )
            },
            onDateEnabledClick = { orderUpdate ->
                memoViewModel.handleActions(
                    action = Action.SORT_DATE_CHANGE,
                    sortDateEnabled = !(uiState.dateOrderState == SortOption.CREATED_AT_ASC ||
                            uiState.dateOrderState == SortOption.CREATED_AT_DESC),
                    statusLineOrderUpdate = orderUpdate
                )
            },
            onPrioritySelected = { priorityAction, priority, orderUpdate ->
                Log.d("PHILIP", "$priorityAction, $priority")
                memoViewModel.handleActions(
                    priorityAction,
                    priority = priority,
                    statusLineOrderUpdate = orderUpdate
                )
            },
            onFavoriteClick = { todo ->
                memoViewModel.handleActions(
                    action = Action.FAVORITE_UPDATE,
                    memo = todo.toMemoTask()
                )
            },
            onLongClickReleased = { memoViewModel.removeMultiSelectedItem(it) },
            onLongClickApplied = { memoViewModel.appendMultiSelectedItem(it) },
            onStateSelectedForMultipleItems = { state ->
                memoViewModel.handleActions(
                    action = Action.STATE_CHANGE_MULTIPLE,
                    state = state
                )
            },
            onStateSelected = { state ->
                memoViewModel.handleActions(
                    Action.STATE_FILTER_CHANGE,
                    state = state,
                )
            },
            onToggleClicked = {
                var result = memoViewModel.uiState.stateState
                for (i in 0..5) {
                    result += if ((uiState.stateState and (1 shl (i))) == (1 shl (i))) {
                        -(1 shl (i))
                    } else {
                        (1 shl (i))
                    }
                }
                memoViewModel.handleActions(
                    Action.STATE_MULTIPLE_FILTER_CHANGE,
                    stateInt = result,
                )
            },
            onSetAllOrNothingClicked = { all ->
                memoViewModel.handleActions(
                    Action.STATE_MULTIPLE_FILTER_CHANGE,
                    stateInt = if (all) 63 else 0,
                )
            },
            onSearchNoFilterClicked = {
                memoViewModel.handleActions(
                    Action.SEARCH_NO_FILTER_CHANGE,
                    searchRangeAll = it
                )
            },
            onStatusLineUpdate = {
                memoViewModel.handleActions(
                    Action.STATUS_LINE_UPDATE,
                    stateEntity = it
                )
            }
        )

        // 로딩바 보이기
        if (memoViewModel.progressVisible) {
            ProgressIndicator()
        }

        // dialogMode 가 0이면 copy to, 1이면 move to가 된다.
        NotebooksPickerDialog(
            dialogMode = dialogMode,
            visible = openDialog,
            onDismissRequest = {
                openDialog = false
            },
            notebooks = memoViewModel.notebooks.collectAsState().value,
            defaultNoteMemoCount = memoViewModel.defaultNoteMemoCount,
            onCloseClick = {
                openDialog = false
            },
            onNotebookClick = { id, action ->
                Log.d("PHILIP", "[MemoNavGraph] onNotebookClick $id, $action")
                memoViewModel.handleActions(action, notebookId = id)
                openDialog = false
            }
        )

        // task screen 에서 요청한 처리의 결과를 보여 준다. undo 의 경우는 특별 하게 처리 한다.
        // enabled 는 화면에 표출될 지를 결정 하는 변수 이다.
        DisplaySnackBar(
            snackBarHostState = snackBarHostState,
            action = memoViewModel.action,
            range = memoViewModel.searchRangeAll,
            enabled = memoViewModel.actionPerformed,
            title = memoViewModel.savedLastMemoTask.title,
            buttonClicked = { selectedAction, result ->
                Log.d("PHILIP", "[ListScreen] button clicked ${selectedAction.name}")

                if (result == SnackbarResult.ActionPerformed
                    && selectedAction == Action.DELETE
                ) {
                    Log.d("PHILIP", "[ListScreen] undo inside clicked ${selectedAction.name}")
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

        InfoAlertDialog(
            enable = memoViewModel.openDialog,
            title = stringResource(memoViewModel.infoDialogTitle),
            content = stringResource(memoViewModel.infoDialogContent),
            dismissLabel = stringResource(memoViewModel.infoDialogCDismissLabel),
            onDismiss = {
                memoViewModel.openDialog = false
            }
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
            }

            // 검색어가 있는 있는 경우 검색어 해제 및 전체 필터가 있는 경우 제거
            if (memoViewModel.searchTextString.isNotEmpty() || memoViewModel.searchNoFilterState) {
                memoViewModel.searchTextString = ""
                memoViewModel.handleActions(
                    Action.SEARCH_NO_FILTER_CHANGE,
                    searchRangeAll = false
                )
            } else {
                memoViewModel.onCloseSearchBar()
            }
        }

    }
}
