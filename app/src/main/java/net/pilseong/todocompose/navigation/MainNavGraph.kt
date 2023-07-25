package net.pilseong.todocompose.navigation

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.coroutines.launch
import net.pilseong.todocompose.navigation.destination.BottomBarScreen
import net.pilseong.todocompose.navigation.destination.homeComposable
import net.pilseong.todocompose.navigation.destination.noteNavGraph
import net.pilseong.todocompose.ui.components.AppDrawer
import net.pilseong.todocompose.ui.components.AppDrawerHeader
import net.pilseong.todocompose.ui.components.LightDarkThemeItem
import net.pilseong.todocompose.ui.components.ScreenNavigationButton
import net.pilseong.todocompose.ui.screen.settings.SettingsScreen
import net.pilseong.todocompose.util.Constants.HOME_ROOT
import net.pilseong.todocompose.util.Constants.MAIN_ROOT
import net.pilseong.todocompose.util.Constants.MEMO_ROOT

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
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AppDrawer(onScreenSelected = { to ->
                    navHostController.popBackStack(to, true)
                    navHostController.navigate(to)

                    scope.launch {
                        drawerState.close()
                    }
                })
            }
        }
    ) {
        NavHost(
            navController = navHostController,
            startDestination = startDestination
        ) {
            navigation(
                startDestination = MEMO_ROOT,
                route = MAIN_ROOT,
            ) {
                homeComposable(
                    navHostController = navHostController,
                    viewModelStoreOwner = viewModelStoreOwner,
                    route = HOME_ROOT
                )

                noteNavGraph(
                    navHostController = navHostController,
//                    toTaskScreen = {
//                        navHostController.navigate(Screen.MemoDetail.route)
//                    },
//                    toListScreen = {
//                        navHostController.navigate(Screen.MemoList.route)
//                    },
//                    toNoteScreen = {
//                        navHostController.navigate(Screen.Notes.route)
//                    },
//                    toTaskManagementScreen = {
//                        navHostController.navigate(Screen.MemoTaskManager.route)
//                    },
                    toScreen = {
                        navHostController.navigate(it.route)
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
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navHostController: NavHostController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
//        Log.d("PHILIP", "sharedViewModel set $navGraphRoute $this")
        navHostController.getBackStackEntry(navGraphRoute)
    }

//    LaunchedEffect(key1 = this) {
//        Log.d("PHILIP", "sharedViewModel after $parentEntry")
//    }

    return hiltViewModel(parentEntry)
}