package net.pilseong.todocompose.navigation.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.pilseong.todocompose.ui.screen.task.TaskScreen
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants

// task screen 을 그려 주기 전에 실행 된다.
fun NavGraphBuilder.taskComposable(
    navHostController: NavHostController,
    toListScreen: (Int?) -> Unit,
    memoViewModel: MemoViewModel
) {
    composable(route = Constants.TASK_SCREEN) {
        // task 화면 표출
        TaskScreen(
            toListScreen = toListScreen,
            memoViewModel = memoViewModel
        )
    }
}