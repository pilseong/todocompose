package net.pilseong.todocompose.navigation.destination

import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.screen.list.ListScreen
import net.pilseong.todocompose.ui.viewmodel.SharedViewModel
import net.pilseong.todocompose.util.Constants.LIST_ARGUMENT_ACTION_TYPE
import net.pilseong.todocompose.util.Constants.LIST_SCREEN

fun NavGraphBuilder.listComposable(
    toTaskScreen: (List<TodoTask>) -> Unit,
    sharedViewModel: SharedViewModel
) {
    composable(
        route = LIST_SCREEN,
        arguments = listOf(
            navArgument(LIST_ARGUMENT_ACTION_TYPE) {
                type = NavType.StringType
            }
        )
    ) { navBackStackEntry ->
        ListScreen(
            toTaskScreen = toTaskScreen,
            sharedViewModel = sharedViewModel,
        )
    }
}