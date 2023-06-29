package net.pilseong.todocompose.ui.screen.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.pilseong.todocompose.data.model.NotebookWithCount
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.util.NoteSortingOption

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun BookShelf(
    notebooks: List<NotebookWithCount>,
    selectedNotebookIds: SnapshotStateList<Int>,
    notebookWidth: Float = 110F,
    noteSortingOption: NoteSortingOption,
    onSelectNotebookWithLongClick: (Int) -> Unit,
    onSelectNotebook: (Int) -> Unit,
    onInfoClick: (Int) -> Unit
) {
    Log.d("PHILIP", "noteOrder $noteSortingOption")

    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            top = SMALL_PADDING,
            start = SMALL_PADDING,
            end = SMALL_PADDING
        ),
        content = {
            items(
                items = notebooks,
                key = { note ->
                    note.id
                }
            ) { notebook ->
                val selected by remember(selectedNotebookIds.size) {
                    mutableStateOf(
                        selectedNotebookIds.contains(
                            notebook.id
                        )
                    )
                }

                NotebookCover(
                    modifier = Modifier.animateItemPlacement(),
                    notebookWidth = notebookWidth,
                    isMultiSelectionMode = selectedNotebookIds.size > 0,
                    onSelectNotebookWithLongClick,
                    notebook,
                    onSelectNotebook,
                    selected,
                    noteSortingOption,
                    onInfoClick
                )
            }
        }
    )
}

@Preview(widthDp = 360, heightDp = 720)
@Composable
fun BookShelfPreview() {
    MaterialTheme {
        BookShelf(
            notebooks =
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
                    title = "test3", description = "desc3", priority = Priority.NONE
                )
            ),
            selectedNotebookIds = SnapshotStateList<Int>(),
            onSelectNotebookWithLongClick = {},
            onSelectNotebook = {},
            noteSortingOption = NoteSortingOption.ACCESS_AT,
            onInfoClick = {}
        )
    }
}

