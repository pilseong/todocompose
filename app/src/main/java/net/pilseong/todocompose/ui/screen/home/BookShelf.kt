package net.pilseong.todocompose.ui.screen.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.NotebookWithCount
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.util.NoteSortingOption
import net.pilseong.todocompose.util.getPriorityColor
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun BookShelf(
    notebooks: List<NotebookWithCount>,
    selectedNotebookIds: SnapshotStateList<Int>,
    noteSortingOption: NoteSortingOption,
    onSelectNotebookWithLongClick: (Int) -> Unit,
    onSelectNotebook: (Int) -> Unit,
    onInfoClick: (Int) -> Unit
) {
    Log.i("PHILIP", "noteOrder $noteSortingOption")
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            top = SMALL_PADDING,
            start = SMALL_PADDING,
            end = SMALL_PADDING
        ),
        content = {
            items(notebooks.size) { index ->

                val selected = remember(selectedNotebookIds.size) {
//                    Log.i(
//                        "PHILIP", "[HomeContent] " +
//                                "index = $index, ${selectedNotebookIds.toList()}, " +
//                                "size = ${selectedNotebookIds.size}"
//                    )
                    mutableStateOf(
                        selectedNotebookIds.contains(
                            notebooks[index].id
                        )
                    )
                }

                Surface(
                    modifier = Modifier
                        .width(125.dp)
                        .height(160.dp)
                        .padding(end = SMALL_PADDING, bottom = LARGE_PADDING)
                        .combinedClickable(
                            onClick = {
                                if (selectedNotebookIds.size > 0) {
                                    onSelectNotebookWithLongClick(notebooks[index].id)
                                } else {
                                    onSelectNotebook(notebooks[index].id)
                                }
                            },
                            onLongClick = {
                                onSelectNotebookWithLongClick(notebooks[index].id)
                            }
                        ),
                    shadowElevation = if (selected.value) 0.dp else 6.dp,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 6.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 6.dp
                    ),
                    border = if (selected.value) BorderStroke(
                        0.5.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    else BorderStroke(
                        0.dp,
                        MaterialTheme.colorScheme.surface
                    )
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 6.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 6.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Surface(
                                color = if (selected.value) {
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                                        10000.dp
                                    )
                                } else
                                    getPriorityColor(notebooks[index].priority).copy(
                                        alpha = 0.4f
                                    ),
//                                                    MaterialTheme.colorScheme.surface,
                                tonalElevation = if (selected.value) 12.dp else 2.dp,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                ) {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .weight(1F),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(
                                                horizontal = SMALL_PADDING
                                            ),
                                            text = notebooks[index].title,
                                            color = if (selected.value) MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.2f
                                            )
//                                                            else MaterialTheme.colorScheme.onSurface
                                            else
                                                Color(
                                                    ColorUtils.blendARGB(
                                                        MaterialTheme.colorScheme.onPrimary.toArgb(),
                                                        Color.Black.toArgb(),
                                                        0.9f
                                                    )
                                                ).copy(0.9f)
//                                                                Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                    Surface(
                                        color = if (selected.value) {
                                            MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                10000.dp
                                            )
                                        } else
                                            Color(
                                                ColorUtils.blendARGB(
                                                    getPriorityColor(notebooks[index].priority).toArgb(),
                                                    Color.Black.toArgb(),
                                                    0.5f
                                                )
                                            ),
//                                                    MaterialTheme.colorScheme.surface,
                                        tonalElevation = if (selected.value) 12.dp else 2.dp,
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
//                                                        .weight(1F),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = when (noteSortingOption) {
                                                        NoteSortingOption.ACCESS_AT -> {
                                                            notebooks[index].accessedAt.toLocalDateTime()
                                                                .format(
                                                                    DateTimeFormatter.ofPattern(
                                                                        stringResource(id = R.string.note_inside_dateformat)
                                                                    )
                                                                )
                                                        }

                                                        NoteSortingOption.UPDATED_AT -> {
                                                            notebooks[index].updatedAt.toLocalDateTime()
                                                                .format(
                                                                    DateTimeFormatter.ofPattern(
                                                                        stringResource(id = R.string.note_inside_dateformat)
                                                                    )
                                                                )
                                                        }

                                                        NoteSortingOption.CREATED_AT -> {
                                                            notebooks[index].createdAt.toLocalDateTime()
                                                                .format(
                                                                    DateTimeFormatter.ofPattern(
                                                                        stringResource(id = R.string.note_inside_dateformat)
                                                                    )
                                                                )
                                                        }
                                                    },
                                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                                    color = if (selected.value) MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.2f
                                                    )
                                                    else Color.White.copy(alpha = 0.7f)
//                                                color = Color(
//                                                    ColorUtils.blendARGB(
//                                                        MaterialTheme.colorScheme.onPrimary.toArgb(),
//                                                        Color.Black.toArgb(),
//                                                        0.1f
//                                                    )
//                                                ),
                                                )
                                                Text(
                                                    text = when (noteSortingOption) {
                                                        NoteSortingOption.ACCESS_AT -> {
                                                            notebooks[index].accessedAt.toLocalDateTime()
                                                                .format(
                                                                    DateTimeFormatter.ofPattern(
                                                                        "HH:mm"
                                                                    )
                                                                )
                                                        }

                                                        NoteSortingOption.UPDATED_AT -> {
                                                            notebooks[index].updatedAt.toLocalDateTime()
                                                                .format(
                                                                    DateTimeFormatter.ofPattern(
                                                                        "HH:mm"
                                                                    )
                                                                )
                                                        }

                                                        NoteSortingOption.CREATED_AT -> {
                                                            notebooks[index].createdAt.toLocalDateTime()
                                                                .format(
                                                                    DateTimeFormatter.ofPattern(
                                                                        "HH:mm"
                                                                    )
                                                                )
                                                        }
                                                    },
                                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                                    color = if (selected.value) MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.2f
                                                    )
                                                    else Color.White.copy(alpha = 0.7f)
                                                )
                                            }

//                                            Icon(
//                                                modifier = Modifier
//                                                    .width(14.dp),
////                                                                    .padding(top = 3.dp),
////                                                                painter = painterResource(id = R.drawable.ic_create_note_icon),
//                                                imageVector = Icons.Default.Update,
//                                                contentDescription = "edit time",
//                                                tint = if (selected.value) MaterialTheme.colorScheme.onSurface.copy(
//                                                    alpha = 0.2f
//                                                )
////                                                                    else MaterialTheme.colorScheme.onSurface
//                                                else Color.White.copy(alpha = 0.7f)
//                                            )
//                                            Text(
//                                                modifier = Modifier.padding(
//                                                    vertical = SMALL_PADDING
//                                                ),
//                                                text = notebooks[index].updatedAt.toLocalDate()
//                                                    .format(
//                                                        DateTimeFormatter.ofPattern(
//                                                            stringResource(id = R.string.note_inside_dateformat)
//                                                        )
//                                                    ),
//                                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
//                                                fontWeight = FontWeight.Light,
//                                                color = if (selected.value) MaterialTheme.colorScheme.onSurface.copy(
//                                                    alpha = 0.2f
//                                                )
////                                                                    else MaterialTheme.colorScheme.onSurface
//                                                else Color.White.copy(alpha = 0.7f)
//                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Transparent
                    ) {
                        Row(horizontalArrangement = Arrangement.End) {
                            Badge(
                                modifier = Modifier
                                    .clickable {
                                        onInfoClick(notebooks[index].id)
                                    },
                                containerColor = if (selected.value) BadgeDefaults.containerColor
                                    .copy(alpha = 0.2f)
                                else BadgeDefaults.containerColor
                            ) {
                                Text(text = notebooks[index].memoCount.toString())
                            }
                        }
                    }
                    if (selected.value) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
//                                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(top = 20.dp)
                                    .clickable {
                                        onInfoClick(notebooks[index].id)
                                    }
                                    .size(30.dp),
                                imageVector = Icons.Default.Info,
                                contentDescription = "selected",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun BookShelfPreview() {
    MaterialTheme {
        BookShelf(
            notebooks =
            listOf(
                NotebookWithCount(
                    title = "My Love Note",
                    description = "desc1",
                    priority = Priority.NONE
                ),
                NotebookWithCount(
                    title = "first notebooksss",
                    description = "desc2",
                    priority = Priority.NONE
                ),
                NotebookWithCount(title = "test3", description = "desc3", priority = Priority.NONE)
            ),
            selectedNotebookIds = SnapshotStateList<Int>(),
            onSelectNotebookWithLongClick = {},
            onSelectNotebook = {},
            noteSortingOption = NoteSortingOption.ACCESS_AT,
            onInfoClick = {}
        )
    }
}
