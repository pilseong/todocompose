package net.pilseong.todocompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val LightGreenLighter = Color(0xFF5CBCB5)
val LightGreenBackground = Color(0xFF086158)
val DarkGreenBackground = Color(0xFF082B26)
val headlineYellow = Color(0xFFFFC107)
val booksBackground = Color(0xFFDADADA)

val LightGray = Color(0xFFFCFCFC)
val MediumGray = Color(0xFF9C9C9C)
val DarkGray = Color(0xFF141414)
val FavoriteYellow = Color(0xFFFFE500)
val FavoriteYellowForDarkTheme = Color(0xFFC5A31C)

val LowPriorityLightColor = Color(0xFF6F975C)
val LowPriorityDarkColor = Color(0xFF6F975C)
val MediumPriorityLightColor = Color(0xFFFFC114)
val MediumPriorityDarkColor = Color(0xFFFFC114)
val HighPriorityLightColor = Color(0XFFFF6361)
val HighPriorityDarkColor = Color(0XFFFF6361)
val NonePriorityDarkColor = Color(0xFF9C9C9C)

val LowPriorityColor = Color(0xFF6F975C)
val MediumPriorityColor = Color(0xFFFFC114)
val HighPriorityColor = Color(0XFFFF6361)
val NonePriorityColor = Color(0xFF9C9C9C)

val WEEKDAY_COLOR = Color(0xFF5494A8)
val WEEKEND_COLOR = Color(0xFFDF877A)

// 색상 추가
val ColorScheme.topBarContentColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.LightGray
    else MaterialTheme.colorScheme.onPrimary

val ColorScheme.topBarContainerColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.Black
    else MaterialTheme.colorScheme.primary

val ColorScheme.fabContainerColor: Color
    @Composable
    get() = if (isSystemInDarkTheme())
        MaterialTheme.colorScheme.tertiaryContainer
    else
        MaterialTheme.colorScheme.primaryContainer

val ColorScheme.fabContent: Color
    @Composable
    get() = if (isSystemInDarkTheme())
        MaterialTheme.colorScheme.onTertiaryContainer
    else
        MaterialTheme.colorScheme.onPrimaryContainer

val ColorScheme.taskItemContainerColor: Color
    @Composable
    get() = if (isSystemInDarkTheme())
        Color.Black
    else
        MaterialTheme.colorScheme.surface

val ColorScheme.taskItemContentColor: Color
    @Composable
    get() = if (isSystemInDarkTheme())
        Color.LightGray
    else
        MaterialTheme.colorScheme.onSurface

val ColorScheme.onPrimaryElevation: Color
    @Composable
    get() = if (isSystemInDarkTheme())
        MaterialTheme.colorScheme
            .surfaceColorAtElevation(2.dp)
    else
        MaterialTheme.colorScheme
            .surfaceColorAtElevation(100000000.dp)

val ColorScheme.mediumGray: Color
    @Composable
    get() = MediumGray

val ColorScheme.HighPriorityColor: Color
    @Composable
    get() = if (isSystemInDarkTheme())
        HighPriorityLightColor
    else
        HighPriorityDarkColor

val ColorScheme.MediumPriorityColor: Color
    @Composable
    get() = if (isSystemInDarkTheme())
        MediumPriorityLightColor
    else
        MediumPriorityDarkColor

val ColorScheme.LowPriorityColor: Color
    @Composable
    get() = if (isSystemInDarkTheme())
        LowPriorityLightColor
    else
        LowPriorityDarkColor

val ColorScheme.FavoriteYellowColor: Color
    @Composable
    get() = if (isSystemInDarkTheme())
        FavoriteYellowForDarkTheme
    else
        FavoriteYellow