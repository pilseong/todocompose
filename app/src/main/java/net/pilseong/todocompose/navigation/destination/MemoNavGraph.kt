package net.pilseong.todocompose.navigation.destination

import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.screen.list.ListScreen
import net.pilseong.todocompose.ui.screen.task.TaskScreen
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants.MEMO_LIST
import net.pilseong.todocompose.util.Constants.NOTE_ID_ARGUMENT

fun NavGraphBuilder.memoNavGraph(
    navHostController: NavHostController,
    memoViewModel: MemoViewModel,
    toTaskScreen: (List<TodoTask>) -> Unit,
    toListScreen: (Int?) -> Unit,
) {

    navigation(
        startDestination = Screen.MemoList.route,
        route = MEMO_LIST
    ) {
        composable(
            route = Screen.MemoList.route,
            arguments = listOf(
                navArgument(NOTE_ID_ARGUMENT) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) {
            Log.i("PHILIP", "[memoNavGraph] ListScreen called with ${it.arguments?.getInt(NOTE_ID_ARGUMENT)}")
            ListScreen(
                navHostController = navHostController,
                toTaskScreen = toTaskScreen,
                memoViewModel = memoViewModel
            )
        }


        composable(
            route = Screen.MemoDetail.route,
        ) {
            TaskScreen(
                memoViewModel = memoViewModel,
                toListScreen = toListScreen
            )
        }


    }
}