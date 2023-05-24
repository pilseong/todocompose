package net.pilseong.todocompose.data.model

import androidx.compose.ui.graphics.Color
import net.pilseong.todocompose.ui.theme.HighPriorityColor
import net.pilseong.todocompose.ui.theme.LowPriorityColor
import net.pilseong.todocompose.ui.theme.MediumPriorityColor
import net.pilseong.todocompose.ui.theme.NonePriorityColor


enum class Priority(val color: Color, val label: String) {
    HIGH(HighPriorityColor, "High"),
    MEDIUM(MediumPriorityColor, "Medium"),
    LOW(LowPriorityColor, "Low"),
    NONE(NonePriorityColor, "None")
}