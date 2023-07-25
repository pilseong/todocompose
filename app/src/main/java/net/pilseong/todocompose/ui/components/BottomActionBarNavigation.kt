package net.pilseong.todocompose.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.screen.list.AddMemoFab
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING

@Composable
fun BottomActionBarNavigation(
    currentScreen: Screen,
    onNavigateClick: (Screen) -> Unit,
    onFabClicked: () -> Unit,
) {

    val screens = listOf(
        Screen.Notes,
        Screen.MemoList,
        Screen.MemoTaskManager,
        Screen.MemoCalendar,
        Screen.Settings
    )


    BottomAppBar(
        modifier = Modifier.height(65.dp),
        actions = {
            Row(modifier = Modifier.fillMaxWidth(0.80F)) {
                Spacer(modifier = Modifier.width(25.dp))

                screens.forEach { screen ->
                    IconButton(modifier = Modifier.padding(start = XLARGE_PADDING),
                        enabled = screen != currentScreen,
                        onClick = {
                            onNavigateClick(screen)
                        }) {

                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.label,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = if (screen == currentScreen) 1f else 0.5f)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            AddMemoFab(
                icon = Icons.Default.Create,
                size = 50.dp,
                paddingEnd = 4.dp,
                onFabClicked = {
                    onFabClicked()
                }
            )
        },
        contentPadding = PaddingValues(0.dp)
    )
}
