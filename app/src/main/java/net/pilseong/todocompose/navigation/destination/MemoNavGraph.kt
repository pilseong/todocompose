package net.pilseong.todocompose.navigation.destination

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.screen.list.ListScreen
import net.pilseong.todocompose.ui.screen.task.TaskScreen
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants.MEMO_LIST
import net.pilseong.todocompose.util.Constants.NOTE_ID_ARGUMENT

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.memoNavGraph(
    navHostController: NavHostController,
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
            val parentEntry = remember(backStackEntry) {
                navHostController.getBackStackEntry(MEMO_LIST)
            }
            val memoViewModel = hiltViewModel<MemoViewModel>(
                viewModelStoreOwner = parentEntry
            )

            Log.i(
                "PHILIP",
                "[memoNavGraph] ListScreen called with " +
                        "${backStackEntry.arguments?.getInt(NOTE_ID_ARGUMENT)}"
            )

            LaunchedEffect(key1 = Unit) {
                memoViewModel.observePrioritySortState()
                memoViewModel.observeOrderEnabledState()
                memoViewModel.observeDateEnabledState()
                memoViewModel.observeFavoriteState()
                memoViewModel.observeNotebookIdChange()
            }


            ListScreen(
                toTaskScreen = { snapshot ->
                    Log.i("PHILIP", "Snapshot is $snapshot")
                    memoViewModel.updateSnapshotTasks(snapshot)
                    // 화면 전환 시에는 action 을 초기화 해야 뒤로 가기 버튼을 눌렀을 때 오동작 을 예방할 수 있다.
                    memoViewModel.updateAction(Action.NO_ACTION)
                    toTaskScreen()
                },
                onClickBottomNavBar = onClickBottomNavBar,
                memoViewModel = memoViewModel,
            )
        }


        composable(
            route = Screen.MemoDetail.route,
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navHostController.getBackStackEntry(MEMO_LIST)
            }
            val memoViewModel = hiltViewModel<MemoViewModel>(
                viewModelStoreOwner = parentEntry
            )

            TaskScreen(
                memoViewModel = memoViewModel,
                toListScreen = toListScreen
            )
        }
    }
}