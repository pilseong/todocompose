package net.pilseong.todocompose.data.model

import androidx.compose.ui.graphics.Color
import net.pilseong.todocompose.R
import net.pilseong.todocompose.ui.theme.STATE_ACTIVE_COLOR
import net.pilseong.todocompose.ui.theme.STATE_COMPLETED_COLOR
import net.pilseong.todocompose.ui.theme.STATE_NONE_COLOR
import net.pilseong.todocompose.ui.theme.STATE_SUSPENDED_COLOR
import net.pilseong.todocompose.ui.theme.STATE_WAITING_COLOR

enum class State(val color: Color, val label: Int) {
    NONE(STATE_NONE_COLOR, R.string.status_none),
    WAITING(STATE_WAITING_COLOR, R.string.status_waiting),
    SUSPENDED(STATE_SUSPENDED_COLOR, R.string.status_suspended),
    ACTIVE(STATE_ACTIVE_COLOR, R.string.status_active),
    COMPLETED(STATE_COMPLETED_COLOR, R.string.status_completed),
}