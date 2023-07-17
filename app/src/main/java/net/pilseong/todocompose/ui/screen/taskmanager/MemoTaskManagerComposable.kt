package net.pilseong.todocompose.ui.screen.taskmanager

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.sharedViewModel
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel

fun NavGraphBuilder.memoTaskManagerComposable(
    navHostController: NavHostController,
    toScreen: (String) -> Unit,
) {
    composable(
        route = Screen.MemoTaskManager.route,
    ) { navBackStackEntry ->
        val memoTaskManagerViewModel =
            navBackStackEntry.sharedViewModel<MemoTaskManagerViewModel>(navHostController)

        val uiState = memoTaskManagerViewModel.uiState
        val selectedNotebook = memoTaskManagerViewModel.selectedNotebook

        TaskManagerScreen(
            selectedNotebook = selectedNotebook,
            toScreen = toScreen,
            onFabClicked = {}
        )
    }
}