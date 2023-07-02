package net.pilseong.todocompose.navigation.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.screen.list.memoDetailComposable
import net.pilseong.todocompose.ui.screen.list.memoListComposable
import net.pilseong.todocompose.util.Constants.MEMO_LIST

fun NavGraphBuilder.memoNavGraph(
    navHostController: NavHostController,
    toTaskScreen: () -> Unit,
    toListScreen: () -> Unit,
    onClickBottomNavBar: (String) -> Unit
) {
    navigation(
        startDestination = Screen.MemoList.route,
        route = MEMO_LIST,
    ) {
        memoListComposable(navHostController, toTaskScreen, onClickBottomNavBar)


        memoDetailComposable(navHostController, toListScreen)
    }
}