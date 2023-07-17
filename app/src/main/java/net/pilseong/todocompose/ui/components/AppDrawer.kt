package net.pilseong.todocompose.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING

@Composable
fun AppDrawer(
//    currentScreen: Screen,
    onScreenSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppDrawerHeader()
        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f)
        )
        ScreenNavigationButton(
            icon = Icons.Filled.Home,
            label = "Home",
            isSelected = false,
            onClick = {
                onScreenSelected(Screen.Home.route)
            }
        )
        ScreenNavigationButton(
            icon = Icons.Filled.NoteAlt,
            label = "Notes",
            isSelected = false,
            onClick = {
                onScreenSelected(Screen.Notes.route)
            }
        )
        ScreenNavigationButton(
            icon = Icons.Default.StickyNote2,
            label = "Memo List",
            isSelected = false,
            onClick = {
                onScreenSelected(Screen.MemoList.route)
            }
        )
        ScreenNavigationButton(
            icon = Icons.Default.Create,
            label = "Add Memo",
            isSelected = false,
            onClick = {
                onScreenSelected(Screen.MemoDetail.passId(memoId = -1))
            }
        )
        ScreenNavigationButton(
            icon = Icons.Filled.Delete,
            label = "Trash",
            isSelected = false,
            onClick = {
//                onScreenSelected.invoke(Screen.Trash)
            }
        )
        LightDarkThemeItem()
    }
}

@Preview
@Composable
fun AppDrawerPreview() {
    TodoComposeTheme {
        AppDrawer {

        }
    }
}

@Composable
fun AppDrawerHeader() {
    Surface(color = Color.Black.copy(alpha = 0.8f)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier
                    .padding(16.dp)
                    .size(36.dp),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Drawer Header Icon",
//            tint = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Idea Notes",
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }
}

@Preview
@Composable
fun AppDrawerHeaderPreview() {
    MaterialTheme {
        AppDrawerHeader()
    }
}


@Composable
fun LightDarkThemeItem() {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = "Turn on dark theme",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = 8.dp, top = 8.dp, end = 8.dp, bottom =
                    8.dp
                )
                .align(alignment = Alignment.CenterVertically)
        )
        Switch(
            checked = false,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .align(alignment = Alignment.CenterVertically),
            onCheckedChange = {}
        )
    }
}

@Preview
@Composable
fun LightDarkThemeItemPreview() {
    MaterialTheme {
        LightDarkThemeItem()
    }
}

@Composable
fun ScreenNavigationButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val imageAlpha = if (isSelected) 1f else 0.6f

    val textColor = if (isSelected) colors.primary else colors.onSurface.copy(alpha = 0.6f)
    val backgroundColor = if (isSelected) colors.primary.copy(alpha = 0.12f) else colors.surface

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = backgroundColor,
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .padding(XLARGE_PADDING)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Screen Navigation Button",
                tint = textColor.copy(alpha = imageAlpha),
            )
            Spacer(Modifier.width(16.dp)) // 3
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Preview
@Composable
fun ScreenNavigationButtonPreview() {
    MaterialTheme {
        ScreenNavigationButton(
            icon = Icons.Filled.Home,
            label = "Notes",
            isSelected = true,
            onClick = { }
        )
    }
}