package net.pilseong.todocompose.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.ui.theme.HighPriorityColor
import net.pilseong.todocompose.ui.theme.LowPriorityColor
import net.pilseong.todocompose.ui.theme.MediumPriorityColor
import net.pilseong.todocompose.ui.theme.NonePriorityColor

@Composable
fun getPriorityColor(priority: Priority): Color {
    return when (priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.HighPriorityColor
        Priority.MEDIUM -> MaterialTheme.colorScheme.MediumPriorityColor
        Priority.LOW -> MaterialTheme.colorScheme.LowPriorityColor
        Priority.NONE -> NonePriorityColor
    }
}

fun Int.toBinary(len: Int): String {
    return String.format("%" + len + "s", this.toString(2)).replace(" ".toRegex(), "0")
}