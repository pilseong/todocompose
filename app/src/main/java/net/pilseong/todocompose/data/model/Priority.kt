package net.pilseong.todocompose.data.model

import androidx.compose.ui.graphics.Color
import net.pilseong.todocompose.R
import net.pilseong.todocompose.ui.theme.HighPriorityColor
import net.pilseong.todocompose.ui.theme.LowPriorityColor
import net.pilseong.todocompose.ui.theme.MediumPriorityColor
import net.pilseong.todocompose.ui.theme.NonePriorityColor


enum class Priority(val color: Color, val label: Int) {
    HIGH(HighPriorityColor, R.string.priority_high),
    MEDIUM(MediumPriorityColor, R.string.priority_medium),
    LOW(LowPriorityColor, R.string.priority_low),
    NONE(NonePriorityColor, R.string.priority_none)
}