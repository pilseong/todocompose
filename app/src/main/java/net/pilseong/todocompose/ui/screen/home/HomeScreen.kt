package net.pilseong.todocompose.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "HOME",
                modifier = modifier,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}