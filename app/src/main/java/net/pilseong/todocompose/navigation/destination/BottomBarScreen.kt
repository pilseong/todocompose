package net.pilseong.todocompose.navigation.destination

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import net.pilseong.todocompose.R
import net.pilseong.todocompose.util.Constants.HOME_SCREEN
import net.pilseong.todocompose.util.Constants.MEMO_LIST
import net.pilseong.todocompose.util.Constants.SETTINGS

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home: BottomBarScreen(
        route = HOME_SCREEN,
        title = "Home",
        icon = Icons.Default.Home
    )
    object Memo: BottomBarScreen(
        route = MEMO_LIST,
        title = "Memo",
        icon = Icons.Default.Note
    )
    object Settings: BottomBarScreen(
        route = SETTINGS,
        title = "Settings",
        icon = Icons.Default.Settings
    )
}