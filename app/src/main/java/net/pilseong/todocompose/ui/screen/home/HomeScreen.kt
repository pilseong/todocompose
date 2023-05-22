package net.pilseong.todocompose.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.destination.BottomNavBar

@Composable
fun HomeScreen(
    onClickBottomNavBar: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentDestination = Screen.Home.route,
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
                    text = "HOME",
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}