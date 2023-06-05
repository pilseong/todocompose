package net.pilseong.todocompose.data.model

import androidx.compose.ui.graphics.Color
import net.pilseong.todocompose.R

enum class State(val color: Color, val label: Int) {
    NONE(Color.LightGray, R.string.status_none),
    OPEN(Color.Black, R.string.status_open),
    SUSPENDED(Color.DarkGray, R.string.status_suspended),
    ONIT(Color.Blue, R.string.status_onit),
    CLOSED(Color.Green, R.string.status_closed),
}