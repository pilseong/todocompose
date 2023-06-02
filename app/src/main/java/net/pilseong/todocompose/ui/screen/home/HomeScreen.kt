package net.pilseong.todocompose.ui.screen.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.NotebookWithCount
import net.pilseong.todocompose.navigation.destination.BottomNavBar
import net.pilseong.todocompose.ui.components.MultiSelectAppbar
import net.pilseong.todocompose.ui.components.MultiSelectAppbarActions
import net.pilseong.todocompose.ui.screen.list.AddMemoFab
import net.pilseong.todocompose.ui.screen.task.CommonAction
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onClickBottomNavBar: (String) -> Unit,
    onFabClick: () -> Unit,
    onSelectNotebook: (Int) -> Unit,
    onSelectNotebookWithLongClick: (Int) -> Unit,
    onBackButtonClick: () -> Unit,
    notebooks: List<NotebookWithCount>,
    selectedNotebookIds: SnapshotStateList<Int>,
    onDeleteSelectedClicked: () -> Unit,
    onEditClick: () -> Unit,
    onInfoClick: (Int) -> Unit,
) {


    Log.i("PHILIP", "notebooks $notebooks")

    val scrollBehavior = exitUntilCollapsedScrollBehavior()

    Scaffold(
//        modifier = Modifier.verticalScroll(rememberScrollState()),
        topBar = {
            HomeAppBar(
                scrollBehavior = scrollBehavior,
                selectedNotebookIds = selectedNotebookIds,
                onBackButtonClick = onBackButtonClick,
                onDeleteSelectedClicked = onDeleteSelectedClicked,
                onEditClick = onEditClick,
            )
        },
        bottomBar = {
            BottomNavBar(
                onClick = onClickBottomNavBar,
                currentDestination = Constants.HOME_SCREEN
            )
        },
        floatingActionButton = {
            AddMemoFab(
                icon = Icons.Default.FiberNew,
                onFabClicked = {
                    onFabClick()
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding() + LARGE_PADDING,
                    start = XLARGE_PADDING,
                    end = XLARGE_PADDING,
                    bottom = SMALL_PADDING
                )
                .fillMaxSize(),
        ) {
            NoteContent(
                notebooks = notebooks,
                selectedNotebookIds = selectedNotebookIds,
                onSelectNotebook = onSelectNotebook,
                onSelectNotebookWithLongClick = onSelectNotebookWithLongClick,
                onInfoClick = onInfoClick
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    selectedNotebookIds: SnapshotStateList<Int>,
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
                onDeleteDescription = R.string.delete_seleccted_notebooks_dialog_confirmation,
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
//    } else {
//        TopAppBar(
//            scrollBehavior = scrollBehavior,
//            title = {
//                Text(
//                    text = "",
//                    //                        color = MaterialTheme.colorScheme.topBarContentColor
//                )
//            },
//            colors = TopAppBarDefaults.topAppBarColors(
//                //                    containerColor = MaterialTheme.colorScheme.topBarContainerColor
//            ),
//        )
//    }
}


@Preview
@Composable
fun PreviewHomeScreen() {
    MaterialTheme {
        HomeScreen(
            onClickBottomNavBar = {},
            onFabClick = { /*TODO*/ },
            onSelectNotebook = {},
            onSelectNotebookWithLongClick = {},
            onBackButtonClick = {},
            notebooks = listOf(),
            selectedNotebookIds = SnapshotStateList(),
            onDeleteSelectedClicked = {},
            onEditClick = {},
            onInfoClick = {}
        )
    }
}