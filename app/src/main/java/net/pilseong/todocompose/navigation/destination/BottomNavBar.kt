package net.pilseong.todocompose.navigation.destination

import android.util.Log
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.theme.onPrimaryElevation
import net.pilseong.todocompose.ui.theme.topBarContainerColor
import net.pilseong.todocompose.ui.theme.topBarContentColor

@Composable
fun BottomNavBar(
    currentDestination: String,
    onClick: (String) -> Unit
) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Memo,
        BottomBarScreen.Settings
    )

    // 현재 페이지 를 찾는다
    NavigationBar(
        modifier = Modifier.height(40.dp),
        containerColor = MaterialTheme.colorScheme.topBarContainerColor,
    ) {
        screens.forEach { screen ->
            addItem(
                screen = screen,
                currentDestination = currentDestination,
                onClick = onClick
            )
        }
    }
}

@Composable
fun RowScope.addItem(
    screen: BottomBarScreen,
    currentDestination: String,
    onClick: (String) -> Unit
) {
    NavigationBarItem(
        selected = currentDestination == screen.route,
        onClick = {
            onClick(screen.route)
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.Transparent,
            indicatorColor = MaterialTheme.colorScheme.onPrimaryElevation

        ),
        icon = {
            Icon(
                modifier = Modifier.size(15.dp),
                imageVector = screen.icon,
                tint = MaterialTheme.colorScheme.topBarContentColor,
                contentDescription = "Navigation Icon"
            )
        },
    )
}