package net.pilseong.todocompose.util

import androidx.compose.ui.graphics.Color
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.ui.theme.HighPriorityColor
import net.pilseong.todocompose.ui.theme.LowPriorityColor
import net.pilseong.todocompose.ui.theme.MediumPriorityColor
import net.pilseong.todocompose.ui.theme.NonePriorityColor

fun getPriorityColor(priority: Priority): Color {
    return when (priority) {
        Priority.HIGH -> HighPriorityColor
        Priority.MEDIUM -> MediumPriorityColor
        Priority.LOW -> LowPriorityColor
        Priority.NONE -> NonePriorityColor
    }
}