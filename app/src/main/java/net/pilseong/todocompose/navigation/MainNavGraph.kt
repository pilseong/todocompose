package net.pilseong.todocompose.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import net.pilseong.todocompose.navigation.destination.noteNavGraph
import net.pilseong.todocompose.ui.components.AppDrawerHeader
import net.pilseong.todocompose.ui.components.LightDarkThemeItem
import net.pilseong.todocompose.ui.components.ScreenNavigationButton
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

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                AppDrawerHeader()
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha =
                        .2f
                    )
                )
                ScreenNavigationButton(
                    icon = Icons.Filled.Home,
                    label = "Notes",
                    isSelected = false,
                    onClick = {
//                onScreenSelected.invoke(Screen.Notes)
                    }
                )
                ScreenNavigationButton(
                    icon = Icons.Filled.Delete,
                    label = "Trash",
                    isSelected = false,
                    onClick = {
//                onScreenSelected.invoke(Screen.Trash)
                    }
                )
                LightDarkThemeItem()
//                Text("Drawer title", modifier = Modifier.padding(16.dp))
//                Divider()
//                NavigationDrawerItem(
//                    label = { Text(text = "Drawer Item") },
//                    selected = false,
//                    onClick = { /*TODO*/ }
//                )
//                // ...other drawer items
            }
        }
    ) {
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

                noteNavGraph(
                    navHostController = navHostController,
                    toTaskScreen = {
                        navHostController.navigate(Screen.MemoDetail.route)
                    },
                    toListScreen = {
                        navHostController.navigate(Screen.MemoList.route)
                    },
                    toNoteScreen = {
//                    navHostController.popBackStack(Screen.Home.route, true)
                        navHostController.navigate(Screen.Note.route)
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
        Log.d("PHILIP", "sharedViewModel set $navGraphRoute $this")
        navHostController.getBackStackEntry(navGraphRoute)
    }

    LaunchedEffect(key1 = this) {
        Log.d("PHILIP", "sharedViewModel after $parentEntry")
    }

    return hiltViewModel(parentEntry)
}