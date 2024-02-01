package net.pilseong.todocompose.ui.screen.taskmanager

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.sharedViewModel
import net.pilseong.todocompose.ui.components.NotebooksPickerDialog
import net.pilseong.todocompose.ui.components.NotebooksPickerMode
import net.pilseong.todocompose.ui.screen.calendar.CalendarAction
import net.pilseong.todocompose.ui.screen.list.MemoAction
import net.pilseong.todocompose.ui.viewmodel.MemoTaskManagerViewModel

fun NavGraphBuilder.memoTaskManagerComposable(
    navHostController: NavHostController,
    toScreen: (Screen) -> Unit,
) {
    composable(
        route = Screen.MemoTaskManager.route,
    ) { navBackStackEntry ->
        val memoTaskManagerViewModel =
            navBackStackEntry.sharedViewModel<MemoTaskManagerViewModel>(navHostController)

        val userData = memoTaskManagerViewModel.userData
        val selectedNotebook = memoTaskManagerViewModel.selectedNotebook


        var openDialog by remember { mutableStateOf(false) }
        var dialogMode by remember { mutableStateOf(NotebooksPickerMode.SWITCH_NOTE_MODE) }

        TaskManagerScreen(
            tasks = memoTaskManagerViewModel.tasks.collectAsLazyPagingItems(),
            userData = userData,
            selectedNotebook = selectedNotebook,
            toScreen = toScreen,
            onAppBarTitleClick = {
                memoTaskManagerViewModel.getDefaultNoteCount()
                dialogMode = NotebooksPickerMode.SWITCH_NOTE_MODE
                openDialog = true
            },
            onSearchRangeAllClicked = { state, _ ->
                memoTaskManagerViewModel.handleActions(
                    taskManagerAction = TaskManagerAction.SEARCH_RANGE_CHANGE,
                    boolParam = state
                )
            },
            onFabClicked = {},
            toTaskScreen = { index ->
                memoTaskManagerViewModel.updateIndex(index)
                toScreen(Screen.MemoDetail)
            },
            onSwipeToEdit = { index, memo ->
                memoTaskManagerViewModel.updateIndex(index)
                memoTaskManagerViewModel.setTaskScreenToEditorMode(memo)
                // 화면 전환 시에는 action 을 초기화 해야 뒤로 가기 버튼을 눌렀을 때 오동작 을 예방할 수 있다.
                memoTaskManagerViewModel.updateAction(MemoAction.NO_ACTION)

                toScreen(Screen.MemoDetail)
            },
        )

        // dialogMode 가 0이면 copy to, 1이면 move to가 된다.
        NotebooksPickerDialog(
            dialogMode = dialogMode,
            visible = openDialog,
            onDismissRequest = {
                openDialog = false
            },
            notebooks = memoTaskManagerViewModel.notebooks.collectAsState().value,
            defaultNoteMemoCount = memoTaskManagerViewModel.defaultNoteMemoCount,
            onCloseClick = {
                openDialog = false
            },
            onNotebookClick = { id, action ->
                Log.d("PHILIP", "[MemoNavGraph] onNotebookClick $id, $action")
                memoTaskManagerViewModel.handleActions(
                    taskManagerAction = TaskManagerAction.NOTE_SWITCH,
                    notebookId = id
                )
                openDialog = false
            }
        )
    }
}