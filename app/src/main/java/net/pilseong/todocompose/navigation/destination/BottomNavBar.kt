package net.pilseong.todocompose.navigation.destination

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(
    navHostController: NavHostController,
) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Memo,
        BottomBarScreen.Settings
    )

    // 현재 페이지 를 찾는다
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            addItem(screen, currentDestination, navHostController)
        }
    }
}

@Composable
fun RowScope.addItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navHostController: NavHostController
) {
    NavigationBarItem(
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        onClick = {
            navHostController.navigate(screen.route)
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation Icon"
            )
        },
        label = {
            Text(text = screen.title)
        }
    )
}