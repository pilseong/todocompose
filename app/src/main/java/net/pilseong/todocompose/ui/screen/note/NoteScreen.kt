package net.pilseong.todocompose.ui.screen.note

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FiberNew
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
import net.pilseong.todocompose.ui.screen.list.AddMemoFab
import net.pilseong.todocompose.ui.screen.task.CommonAction
import net.pilseong.todocompose.util.Constants.HOME_ROOT
import net.pilseong.todocompose.util.Constants.MEMO_ROOT
import net.pilseong.todocompose.util.NoteSortingOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    onClickBottomNavBar: (String) -> Unit,
    onFabClick: () -> Unit,
    onSelectNotebook: (Long) -> Unit,
    onSelectNotebookWithLongClick: (Long) -> Unit,
    onBackButtonClick: () -> Unit,
    notebooks: List<NotebookWithCount>,
    currentNotebook: NotebookWithCount,
    firstRecentNotebook: NotebookWithCount?,
    secondRecentNotebook: NotebookWithCount?,
    defaultNotebook: NotebookWithCount = NotebookWithCount.instance(),
    selectedNotebookIds: SnapshotStateList<Long>,
    noteSortingOption: NoteSortingOption,
    onDeleteSelectedClicked: () -> Unit,
    onEditClick: () -> Unit,
    onInfoClick: (Long) -> Unit,
    onSortMenuClick: (NoteSortingOption) -> Unit,
) {
    val scrollBehavior = exitUntilCollapsedScrollBehavior()

    Scaffold(
//        modifier = Modifier.verticalScroll(rememberScrollState()),
        topBar = {
            NoteAppBar(
                scrollBehavior = scrollBehavior,
                selectedNotebookIds = selectedNotebookIds,
                onBackButtonClick = onBackButtonClick,
                onDeleteSelectedClicked = onDeleteSelectedClicked,
                onEditClick = onEditClick,
            )
        },
        bottomBar = {
            if (LocalConfiguration.current.screenHeightDp > 500)
                BottomNavBar(
                    onClick = onClickBottomNavBar,
                    currentDestination = MEMO_ROOT
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
                    top = paddingValues.calculateTopPadding()
                )
                .fillMaxSize(),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface
            ) {
                NoteContent(
                    notebooks = notebooks,
                    selectedNotebookIds = selectedNotebookIds,
                    defaultNotebook = defaultNotebook,
                    onSelectNotebook = onSelectNotebook,
                    onSelectNotebookWithLongClick = onSelectNotebookWithLongClick,
                    onInfoClick = onInfoClick,
                    currentNotebook = currentNotebook,
                    firstRecentNotebook = firstRecentNotebook,
                    secondRecentNotebook = secondRecentNotebook,
                    onEmptyImageClick = onFabClick,
                    onSortMenuClick = onSortMenuClick,
                    noteSortingOption = noteSortingOption
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NoteAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
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
        NoteScreen(
            onClickBottomNavBar = {},
            onFabClick = { /*TODO*/ },
            onSelectNotebook = {},
            onSelectNotebookWithLongClick = {},
            onBackButtonClick = {},
            listOf(
                NotebookWithCount(
                    id = 1,
                    title = "My Love Note",
                    description = "desc1",
                    priority = Priority.NONE
                ),
                NotebookWithCount(
                    id = 2,
                    title = "first notebooksss",
                    description = "desc2",
                    priority = Priority.NONE
                ),
                NotebookWithCount(
                    id = 3,
                    title = "test3",
                    description = "desc3", priority = Priority.NONE
                )
            ),
            currentNotebook = NotebookWithCount.instance(),
            defaultNotebook = NotebookWithCount.instance(),
            selectedNotebookIds = SnapshotStateList(),
            onDeleteSelectedClicked = {},
            onEditClick = {},
            onInfoClick = {},
            firstRecentNotebook = NotebookWithCount.instance(),
            secondRecentNotebook = NotebookWithCount.instance(),
            onSortMenuClick = {},
            noteSortingOption = NoteSortingOption.ACCESS_AT
        )
    }
}