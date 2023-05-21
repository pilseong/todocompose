package net.pilseong.todocompose.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import net.pilseong.todocompose.R
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.destination.BottomBarScreen
import net.pilseong.todocompose.navigation.destination.BottomNavBar
import net.pilseong.todocompose.ui.screen.task.CommonAction
import net.pilseong.todocompose.ui.theme.topBarContainerColor
import net.pilseong.todocompose.ui.theme.topBarContentColor

@Composable
fun SettingsScreen(
    onClickBottomNavBar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SettingsAppBar(

            )
        },
        bottomBar = {
            BottomNavBar(
                currentDestination = Screen.Settings.route,
                onClick = onClickBottomNavBar
            )
        }
    ) { it ->
        Surface(
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(bottom = it.calculateBottomPadding())
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Settings",
                    modifier = modifier,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.titleLarge
                )
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
                color = MaterialTheme.colorScheme.topBarContentColor
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.topBarContainerColor
        ),
        actions = {
        }
    )
}