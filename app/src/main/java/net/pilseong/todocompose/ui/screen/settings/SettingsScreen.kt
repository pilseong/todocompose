package net.pilseong.todocompose.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.destination.BottomBarScreen
import net.pilseong.todocompose.navigation.destination.BottomNavBar
import net.pilseong.todocompose.ui.components.BottomActionBarNavigation
import net.pilseong.todocompose.ui.screen.task.CommonAction

@Composable
fun SettingsScreen(
    onClickBottomNavBar: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SettingsAppBar(

            )
        },
        bottomBar = {
            BottomActionBarNavigation(
                currentScreen = Screen.Settings,
                onNavigateClick = onClickBottomNavBar,
            ) { }
        }
    ) { it ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
                .padding(16.dp)
        ) {
            // the switch composable
            SettingsClickableItem(
                name = R.string.default_note_title,
                icon = R.drawable.ic_create_note_icon,
                iconDesc = R.string.badge_order_desc_label,
            ) {
                // call ViewModel to toggle the value
            }

            SettingsSwitchItem(
                name = R.string.badge_order_desc_label,
                icon = R.drawable.ic_baseline_low_priority_24,
                iconDesc = R.string.add_button_icon,
                state = false
            ) {
                // here you can do anything - navigate - open other settings, ...
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppBar(

) {
    TopAppBar(
        navigationIcon = {
            CommonAction(
                onClicked = { },
                icon = Icons.Default.ArrowBack,
                description = stringResource(
                    R.string.default_task_bar_back_arrow_icon
                )
            )
        },
        title = {
            Text(
                text = stringResource(id = BottomBarScreen.Settings.title),
                overflow = TextOverflow.Ellipsis,
//                color = MaterialTheme.colorScheme.topBarContentColor
            )
        },
//        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = MaterialTheme.colorScheme.topBarContainerColor
//        ),
//        actions = {
//        }
    )
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    MaterialTheme {
        SettingsScreen(onClickBottomNavBar = {})
    }
}