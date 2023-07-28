package net.pilseong.todocompose.navigation.destination

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.navigation.Screen

@Composable
fun BottomNavBar(
    currentDestination: String,
    onClick: (String) -> Unit
) {
    val screens = listOf(
        BottomBarScreen.Note,
        BottomBarScreen.Memo,
        BottomBarScreen.Settings
    )

    // 현재 페이지 를 찾는다
    Surface(
//        modifier = Modifier
//            .shadow(
//                elevation = 20.dp,
//                spotColor = Color.Red
//            )
//            .background(color = MaterialTheme.colorScheme.surface)
//            .fillMaxWidth(),
        color = Color.Transparent,
        tonalElevation = 4.dp,
        shadowElevation = 15.dp
    ) {
        NavigationBar(
            modifier = Modifier.height(70.dp),
//            .graphicsLayer {
//                clip = true
//                shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp)
//                shadowElevation = 40F
//            }
//            .padding(top = 3.dp)
//            containerColor = Color.Transparent,
        ) {
            screens.forEach { screen ->
                addItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    onClick = onClick
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewBottomNavBar() {
    MaterialTheme {
        BottomNavBar(currentDestination = Screen.Home.route, onClick = {})
    }

}

@Composable
fun RowScope.addItem(
    screen: BottomBarScreen,
    currentDestination: String,
    onClick: (String) -> Unit
) {
    NavigationBarItem(
        selected = currentDestination == screen.route,
        onClick = {
            if (currentDestination != screen.route)
                onClick(screen.route)
        },
        colors = NavigationBarItemDefaults.colors(
//            selectedIconColor = Color.Transparent,
//            indicatorColor = MaterialTheme.colorScheme.onPrimaryElevation

        ),
        icon = {
            Icon(
//                modifier = Modifier.size(20.dp),
                imageVector = screen.icon,
//                tint = MaterialTheme.colorScheme.topBarContentColor,
                contentDescription = "Navigation Icon"
            )
        },
        label = {
            Text(text = stringResource(id = screen.title))
        }
    )
}