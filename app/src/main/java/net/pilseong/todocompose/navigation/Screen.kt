package net.pilseong.todocompose.navigation

import net.pilseong.todocompose.util.Constants
import net.pilseong.todocompose.util.Constants.HOME_SCREEN
import net.pilseong.todocompose.util.Constants.MEMO_DETAIL
import net.pilseong.todocompose.util.Constants.MEMO_ID_ARGUMENT
import net.pilseong.todocompose.util.Constants.MEMO_LIST
import net.pilseong.todocompose.util.Constants.NOTE_ID_ARGUMENT
import net.pilseong.todocompose.util.Constants.SETTINGS


sealed class Screen(val route: String) {
    object Home: Screen(route = HOME_SCREEN)
    object MemoList: Screen(route = "$MEMO_LIST?$NOTE_ID_ARGUMENT={id}&name={name}") {
        fun passId(note_id: Int = 0, name: String = "pilseong"): String {
            return "$MEMO_LIST?$NOTE_ID_ARGUMENT=$note_id&name=$name"
        }
    }
    object MemoDetail: Screen(route = "$MEMO_DETAIL?$MEMO_ID_ARGUMENT={$MEMO_ID_ARGUMENT}&content={content}") {
        fun passId(memoId: Int = 0, content: String = "pilseong"): String {
            return "$MEMO_DETAIL?$MEMO_ID_ARGUMENT=$memoId&content=$content"
        }
    }

    object Settings: Screen(route = SETTINGS)

}