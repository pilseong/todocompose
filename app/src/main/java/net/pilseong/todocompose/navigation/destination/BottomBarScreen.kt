package net.pilseong.todocompose.navigation.destination

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.ui.graphics.vector.ImageVector
import net.pilseong.todocompose.R
import net.pilseong.todocompose.util.Constants.HOME_ROOT
import net.pilseong.todocompose.util.Constants.MEMO_ROOT
import net.pilseong.todocompose.util.Constants.SETTINGS

sealed class BottomBarScreen(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector
) {
    object Home: BottomBarScreen(
        route = HOME_ROOT,
        title = R.string.bottom_nav_label_home,
        icon = Icons.Default.Home
    )
    object Memo: BottomBarScreen(
        route = MEMO_ROOT,
        title = R.string.bottom_nav_label_memo,
        icon = Icons.Default.StickyNote2
    )
    object Settings: BottomBarScreen(
        route = SETTINGS,
        title = R.string.bottom_nav_label_settings,
        icon = Icons.Default.Settings
    )
}