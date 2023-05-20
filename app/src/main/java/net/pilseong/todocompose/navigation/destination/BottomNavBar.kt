package net.pilseong.todocompose.navigation.destination

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.theme.onPrimaryElevation
import net.pilseong.todocompose.ui.theme.topBarContainerColor
import net.pilseong.todocompose.ui.theme.topBarContentColor

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

    when (navBackStackEntry?.destination?.route) {
        Screen.MemoDetail.route -> {
            // do nothing
        }
        else -> {
            NavigationBar(
                modifier = Modifier.height(40.dp),
                containerColor = MaterialTheme.colorScheme.topBarContainerColor,
            ) {
                screens.forEach { screen ->
                    addItem(screen, currentDestination, navHostController)
                }
            }
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
//        interactionSource = remember { MutableInteractionSource() },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.Transparent,
//            selectedTextColor = Color.Transparent,
            indicatorColor = MaterialTheme.colorScheme.onPrimaryElevation
//            unselectedIconColor = Color.Transparent,
//            unselectedTextColor = Color.Transparent,
//            disabledIconColor = Color.Transparent,
//            disabledTextColor = Color.Transparent
        ),
        icon = {
            Icon(
                modifier = Modifier.size(15.dp),
                imageVector = screen.icon,
                tint = MaterialTheme.colorScheme.topBarContentColor,
                contentDescription = "Navigation Icon"
            )
        },
//        label = {
//            Text(
//                text = screen.title,
//                color = MaterialTheme.colorScheme.topBarContentColor,
//                fontSize = MaterialTheme.typography.labelSmall.fontSize
//            )
//        }
    )
}