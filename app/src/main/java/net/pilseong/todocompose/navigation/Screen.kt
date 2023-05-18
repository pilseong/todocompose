package net.pilseong.todocompose.navigation

import net.pilseong.todocompose.util.Constants.HOME_SCREEN
import net.pilseong.todocompose.util.Constants.MEMO_DETAIL
import net.pilseong.todocompose.util.Constants.MEMO_LIST
import net.pilseong.todocompose.util.Constants.SETTINGS


sealed class Screen(val route: String) {
    object Home: Screen(route = HOME_SCREEN)
    object MemoList: Screen(route = "$MEMO_LIST?id={id}&name={name}") {
        fun passId(id: Int = 0, name: String = "pilseong"): String {
            return "$MEMO_LIST?id=$id&name=$name"
        }
    }
    object MemoDetail: Screen(route = MEMO_DETAIL)
    object Settings: Screen(route = SETTINGS)

}