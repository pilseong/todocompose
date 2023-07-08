package net.pilseong.todocompose.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import net.pilseong.todocompose.navigation.destination.BottomBarScreen
import net.pilseong.todocompose.navigation.destination.homeComposable
import net.pilseong.todocompose.navigation.destination.memoNavGraph
import net.pilseong.todocompose.ui.screen.settings.SettingsScreen
import net.pilseong.todocompose.util.Constants.HOME_ROOT
import net.pilseong.todocompose.util.Constants.MAIN_ROOT

@Composable
fun MainNavGraph(
    startDestination: String,
    navHostController: NavHostController,
) {

    // memoViewModel 을 list 와 task 에서 공유 하기 위하여 owner 를 상위 컴포 넌트 에서 지정함
    // 이렇게 하지 않으면 list 에서 새로운 memoViewModel 생성 된다.
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        navigation(
            startDestination = HOME_ROOT,
            route = MAIN_ROOT,
        ) {
            homeComposable(
                navHostController = navHostController,
                viewModelStoreOwner = viewModelStoreOwner,
                route = HOME_ROOT
            )

            memoNavGraph(
                navHostController = navHostController,
                toTaskScreen = {
                    navHostController.navigate(Screen.MemoDetail.route)
                },
                toListScreen = {
                    navHostController.navigate(Screen.MemoList.route)
                },
                toHomeScreen = {
                    navHostController.popBackStack(Screen.Home.route, true)
                    navHostController.navigate(Screen.Home.route)
                }
            )

            composable(
                route = BottomBarScreen.Settings.route
            ) {
                SettingsScreen(onClickBottomNavBar = { route ->
                    navHostController.navigate(route)
                })
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navHostController: NavHostController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        Log.d("PHILIP", "sharedViewModel set $navGraphRoute $this")
        navHostController.getBackStackEntry(navGraphRoute)
    }

    LaunchedEffect(key1 = this) {
        Log.d("PHILIP", "sharedViewModel after $parentEntry")
    }

    return hiltViewModel(parentEntry)
}