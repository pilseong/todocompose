package net.pilseong.todocompose.navigation.destination

import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.pilseong.todocompose.ui.screen.home.HomeScreen

fun NavGraphBuilder.homeComposable(
    navHostController: NavHostController,
    viewModelStoreOwner: ViewModelStoreOwner,
    route: String
) {
    composable(
        route = route,
    ) {
//        ModalNavigationDrawer(
//            drawerContent = {
//                ModalDrawerSheet {
//                    Text("Drawer title", modifier = Modifier.padding(16.dp))
//                    Divider()
//                    NavigationDrawerItem(
//                        label = { Text(text = "Drawer Item") },
//                        selected = false,
//                        onClick = { /*TODO*/ }
//                    )
//                    // ...other drawer items
//                }
//            }
//        ) {
        HomeScreen(
            onClickBottomNavBar = { route ->
                navHostController.navigate(route)
            },
        )
//        }

    }
}

