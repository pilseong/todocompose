package net.pilseong.todocompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.screen.list.AddMemoFab
import net.pilseong.todocompose.ui.theme.LARGE_PADDING

@Composable
fun BottomActionBarNavigation(
    currentScreen: Screen,
    onNavigateClick: (Screen) -> Unit,
    expanded: Boolean = true,
    onFabClicked: () -> Unit = {},
) {

    val screens = listOf(
        Screen.Notes,
        Screen.MemoList,
        Screen.MemoTaskManager,
        Screen.MemoCalendar,
        Screen.Settings
    )

    if (expanded) {
        BottomAppBar(
            modifier = Modifier.height(65.dp),
            actions = {
                Row(
//                    modifier = Modifier.fillMaxWidth(0.85F),
                ) {
//                Spacer(modifier = Modifier.width(20.dp))

                    screens.forEach { screen ->
                        Column(
                            modifier = Modifier
                                .weight(1F)
                                .width(IntrinsicSize.Min)
//                                .fillMaxWidth()
                                .padding(start = LARGE_PADDING),
                            horizontalAlignment = CenterHorizontally
                        ) {
//                        IconButton(
//                            enabled = screen != currentScreen,
//                            onClick = {
//                                onNavigateClick(screen)
//                            }) {

                            Icon(
                                modifier = Modifier.clickable {
                                    if (screen != currentScreen)
                                        onNavigateClick(screen)
                                },
                                imageVector = screen.icon,
                                contentDescription = stringResource(id = screen.label),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = if (screen == currentScreen) 1f else 0.5f)
                            )
//                        }
                            Text(
                                text = stringResource(id = screen.label),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                if (currentScreen != Screen.Settings) {
                    AddMemoFab(
                        icon = Icons.Default.Create,
                        size = 50.dp,
                        paddingEnd = 4.dp,
                        onFabClicked = {
                            onFabClicked()
                        }
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "idea note logo"
                    )
                }

            },
            contentPadding = PaddingValues(0.dp)
        )
    }
}


@Preview
@Composable
fun BottomActionBarNavPreview() {
    MaterialTheme {
        BottomActionBarNavigation(
            currentScreen = Screen.MemoList,
            onNavigateClick = {},
            onFabClicked = {}
        )
    }
}
