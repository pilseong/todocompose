package net.pilseong.todocompose.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.pilseong.todocompose.navigation.destination.BottomBarScreen
import net.pilseong.todocompose.navigation.destination.memoNavGraph
import net.pilseong.todocompose.ui.screen.home.HomeScreen
import net.pilseong.todocompose.ui.screen.list.ListScreen
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel

@Composable
fun MainNavGraph(
    navHostController: NavHostController,
    memoViewModel: MemoViewModel
) {

    // screens 객체가 한 번만 생성 되도록 안전망 을 두는 것으로 보인다.
    val router = remember(navHostController) {
        Log.i("PHILIP", "[MainNavGraph]Screens has been created")
        Router(navHostController, memoViewModel)
    }

    NavHost(
        navController = navHostController,
//        startDestination = BottomBarScreen.Home.route
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(
            route = BottomBarScreen.Home.route
        ) {
            HomeScreen(navHostController)
        }

        memoNavGraph(
            navHostController = navHostController,
            memoViewModel = memoViewModel,
            toTaskScreen = router.toTaskScreen,
            toListScreen = router.toListScreen
        )

        composable(
            route = BottomBarScreen.Settings.route
        ) {
            HomeScreen(navHostController)
        }
    }
}