package net.pilseong.todocompose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.Task
import androidx.compose.ui.graphics.vector.ImageVector
import net.pilseong.todocompose.util.Constants.HOME_ROOT
import net.pilseong.todocompose.util.Constants.MEMO_CALENDAR
import net.pilseong.todocompose.util.Constants.MEMO_DETAIL
import net.pilseong.todocompose.util.Constants.MEMO_ID_ARGUMENT
import net.pilseong.todocompose.util.Constants.MEMO_LIST
import net.pilseong.todocompose.util.Constants.MEMO_TASK_MANAGER
import net.pilseong.todocompose.util.Constants.NOTE_ID_ARGUMENT
import net.pilseong.todocompose.util.Constants.NOTE_LIST
import net.pilseong.todocompose.util.Constants.SETTINGS

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Home : Screen(route = HOME_ROOT, icon = Icons.Default.Home, label = "Home")

    object Notes : Screen(route = NOTE_LIST, icon = Icons.Default.NoteAlt, label = "Notes")

    object MemoList : Screen(
        route = "$MEMO_LIST?$NOTE_ID_ARGUMENT={id}&name={name}",
        icon = Icons.Default.StickyNote2,
        label = "Memo List"
    )
//    {
//        fun passId(note_id: Int = 0, name: String = "pilseong"): String {
//            return "$MEMO_LIST?$NOTE_ID_ARGUMENT=$note_id&name=$name"
//        }
//    }

    object MemoTaskManager :
        Screen(route = MEMO_TASK_MANAGER, icon = Icons.Default.Task, label = "Task Manager")

    object MemoCalendar :
        Screen(route = MEMO_CALENDAR, icon = Icons.Default.CalendarMonth, label = "Calendar View")

    object MemoDetail : Screen(
        route = "$MEMO_DETAIL?$MEMO_ID_ARGUMENT={$MEMO_ID_ARGUMENT}&content={content}",
        icon = Icons.Default.Details,
        label = "Memo Detail"
    ) {
        fun passId(memoId: Int = 0, content: String = "pilseong"): String {
            return "$MEMO_DETAIL?$MEMO_ID_ARGUMENT=$memoId&content=$content"
        }
    }

    object Settings : Screen(route = SETTINGS, icon = Icons.Default.Settings, label = "Settings")

}