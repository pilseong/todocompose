package net.pilseong.todocompose.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.navigation.destination.BottomNavBar
import net.pilseong.todocompose.ui.components.MultiSelectAppbar
import net.pilseong.todocompose.ui.components.MultiSelectAppbarActions
import net.pilseong.todocompose.ui.screen.task.CommonAction
import net.pilseong.todocompose.util.Constants.HOME_ROOT
import net.pilseong.todocompose.util.NoteSortingOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onClickBottomNavBar: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            HomeAppBar(
                selectedNotebookIds = SnapshotStateList(),
                onDeleteSelectedClicked = {},
                onBackButtonClick = {},
                onEditClick = {},
            )
        },
        bottomBar = {
            if (LocalConfiguration.current.screenHeightDp > 500)
                BottomNavBar(
                    onClick = onClickBottomNavBar,
                    currentDestination = HOME_ROOT
                )
        },

        ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding()
                )
                .fillMaxSize(),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface
            ) {
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeAppBar(
    selectedNotebookIds: SnapshotStateList<Long>,
    onDeleteSelectedClicked: () -> Unit,
    onBackButtonClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    if (selectedNotebookIds.size > 0) {
        MultiSelectAppbar(
            selectedItemsCount = selectedNotebookIds.size,
            onBackButtonClick = onBackButtonClick
        ) {
            MultiSelectAppbarActions(
                onDeleteTitle = R.string.delete_selected_notebook_dialog_title,
                onDeleteDescription = R.string.delete_selected_notebooks_dialog_confirmation,
                onDeleteSelectedClicked = onDeleteSelectedClicked,
            ) {
                if (selectedNotebookIds.size == 1) {
                    CommonAction(
                        icon = Icons.Default.EditNote,
                        onClicked = onEditClick,
                        description = "Edit notebook information"
                    )
                }
            }
        }
    }
}


@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun PreviewHomeScreen() {
    MaterialTheme {
        HomeScreen(
            onClickBottomNavBar = {},
        )
    }
}