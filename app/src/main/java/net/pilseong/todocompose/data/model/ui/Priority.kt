package net.pilseong.todocompose.data.model.ui

import androidx.compose.ui.graphics.Color
import net.pilseong.todocompose.R
import net.pilseong.todocompose.ui.theme.HighPriorityLightColor
import net.pilseong.todocompose.ui.theme.LowPriorityLightColor
import net.pilseong.todocompose.ui.theme.MediumPriorityLightColor
import net.pilseong.todocompose.ui.theme.NonePriorityColor


enum class Priority(val color: Color, val label: Int) {
    HIGH(HighPriorityLightColor, R.string.priority_high),
    MEDIUM(MediumPriorityLightColor, R.string.priority_medium),
    LOW(LowPriorityLightColor, R.string.priority_low),
    NONE(NonePriorityColor, R.string.priority_none)
}