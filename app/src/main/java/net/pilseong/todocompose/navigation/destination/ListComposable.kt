package net.pilseong.todocompose.navigation.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.screen.list.ListScreen
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Constants.LIST_ARGUMENT_ACTION_TYPE
import net.pilseong.todocompose.util.Constants.LIST_SCREEN

fun NavGraphBuilder.listComposable(
    navHostController: NavHostController,
    toTaskScreen: (List<TodoTask>) -> Unit,
    memoViewModel: MemoViewModel
) {
    composable(
        route = LIST_SCREEN,
        arguments = listOf(
            navArgument(LIST_ARGUMENT_ACTION_TYPE) {
                type = NavType.StringType
            }
        )
    ) {
        ListScreen(
            navHostController = navHostController,
            toTaskScreen = toTaskScreen,
            memoViewModel = memoViewModel,
        )
    }
}