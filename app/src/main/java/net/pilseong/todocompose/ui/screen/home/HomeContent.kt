package net.pilseong.todocompose.ui.screen.home

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.topBarContainerColor
import net.pilseong.todocompose.util.getPriorityColor
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteContent(
    notebooks: List<Notebook>,
    selectedNotebookIds: SnapshotStateList<Int>,
    onSelectNotebook: (Int) -> Unit,
    onSelectNotebookWithLongClick: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(LARGE_PADDING)
            ) {
                Column(
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Top Notebooks",
                            fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize
                        )
                    }
                    Spacer(modifier = Modifier.height(LARGE_PADDING))
                    Row {
                        Text(
                            text = ZonedDateTime.now().toLocalDate().format(
                                DateTimeFormatter.ofPattern(stringResource(id = R.string.note_content_dateformat))
                            ),
                            fontStyle = MaterialTheme.typography.titleSmall.fontStyle,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp)),
//                .padding(horizontal = SMALL_PADDING)
//                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.topBarContainerColor
                    ) {
                        Row(
                            modifier = Modifier.padding(LARGE_PADDING),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),

                            ) {
                            Card(
                                modifier = Modifier
                                    .width(100.dp)
//                                    .weight(1F)
                                    .height(100.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(text = "Fourth")
                            }
                            Card(
                                modifier = Modifier
                                    .width(100.dp)
//                                    .weight(1F)
                                    .height(100.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(text = "Fourth")
                            }
                            Card(
                                modifier = Modifier
                                    .width(100.dp)
//                                    .weight(1F)
                                    .height(100.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(text = "Fourth")
                            }
                        }
                    }
                }
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "List of notebooks",
                            fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize
                        )
                    }
                    Spacer(modifier = Modifier.height(SMALL_PADDING))
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
//                .padding(horizontal = SMALL_PADDING)
                            .height(200.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.topBarContainerColor
                    ) {
                        LazyHorizontalGrid(
//                modifier = Modifier.padding(LARGE_PADDING),
//                rows = GridCells.Adaptive(120.dp),
                            rows = GridCells.Fixed(2),
                            contentPadding = PaddingValues(
                                start = SMALL_PADDING,
                                end = SMALL_PADDING,
                                top = SMALL_PADDING
                            ),
                            content = {
                                items(notebooks.size) { index ->
                                    Card(
                                        modifier = Modifier
                                            .width(80.dp)
                                            .height(100.dp)
                                            .padding(
                                                end = SMALL_PADDING,
                                                bottom = SMALL_PADDING
                                            ),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Surface(
                                                modifier = Modifier.fillMaxHeight(),
                                                color = getPriorityColor(notebooks[index].priority),
                                                content = {
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                }
                                            )
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .weight(1F),
                                                    horizontalArrangement = Arrangement.Center,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(

                                                        text = notebooks[index].title
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .weight(1F),
                                                    verticalAlignment = Alignment.Bottom,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Row {
                                                        Icon(
                                                            modifier = Modifier.width(18.dp),
                                                            painter = painterResource(id = R.drawable.ic_create_note_icon),
                                                            contentDescription = "edit time",
                                                        )
                                                        Text(
                                                            text = notebooks[index].createdAt.toLocalDate()
                                                                .format(
                                                                    DateTimeFormatter.ofPattern(
                                                                        "LLL, dd, yy"
                                                                    )
                                                                ),
                                                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        else -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
//                        .padding(top = 24.dp)
                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.note_screen_recent_notebooks),
                        fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize
                    )
                }
                Row(
                    modifier = Modifier
//                .padding(vertical = SMALL_PADDING)
                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = stringResource(id = R.string.note_content_today) + ": ${
                            ZonedDateTime.now().toLocalDate().format(
                                DateTimeFormatter.ofPattern(stringResource(id = R.string.note_content_dateformat))
                            )
                        }",
                        fontStyle = MaterialTheme.typography.titleSmall.fontStyle,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize
                    )
                }
                Spacer(modifier = Modifier.height(LARGE_PADDING))
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))

                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
                        modifier = Modifier.padding(LARGE_PADDING)
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(120.dp)
                                .height(140.dp)
                                .padding(end = LARGE_PADDING),
                            shadowElevation = 6.dp
                        ) {
                            Card(
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(text = "First")
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .width(120.dp)
                                .height(140.dp)
                                .padding(end = LARGE_PADDING),
                            shadowElevation = 6.dp
                        ) {
                            Card(
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(text = "Second")
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .width(120.dp)
                                .height(140.dp)
                                .padding(end = LARGE_PADDING),
                            shadowElevation = 6.dp
                        ) {
                            Card(
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(text = "Third")
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.note_screen_list_of_notebooks),
                        fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize
                    )
                }
                Spacer(modifier = Modifier.height(LARGE_PADDING))
                Surface(
                    modifier = Modifier
//                        .clip(RoundedCornerShape(4.dp))
//                .padding(horizontal = SMALL_PADDING)
                        .height(300.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    LazyHorizontalGrid(
                        rows = GridCells.Fixed(2),
                        content = {
                            items(notebooks.size) { index ->

                                val selected = remember(selectedNotebookIds.size) {
                                    Log.i(
                                        "PHILIP", "[HomeContent] " +
                                                "index = $index, ${selectedNotebookIds.toList()}, " +
                                                "size = ${selectedNotebookIds.size}"
                                    )
                                    mutableStateOf(
                                        selectedNotebookIds.contains(
                                            notebooks[index].id
                                        )
                                    )
                                }

                                Log.i("PHILIP", "[HomeContent] selected : ${selected.value}")

                                Surface(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .height(140.dp)
                                        .padding(end = LARGE_PADDING, bottom = LARGE_PADDING)
                                        .combinedClickable(
                                            onClick = {
                                                onSelectNotebook(notebooks[index].id)
                                            },
                                            onLongClick = {
                                                onSelectNotebookWithLongClick(notebooks[index].id)
                                            }
                                        ),
                                    shadowElevation = 6.dp,
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(
                                            topStart = 0.dp,
                                            topEnd = 4.dp,
                                            bottomStart = 0.dp,
                                            bottomEnd = 4.dp
                                        )
//                                        shape = RoundedCornerShape(4.dp),
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxHeight(),
                                                color = getPriorityColor(notebooks[index].priority),
                                                content = {
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                }
                                            )
                                            Surface(
                                                color = if (selected.value) MaterialTheme.colorScheme.primaryContainer
                                                else MaterialTheme.colorScheme.surface,
                                                tonalElevation = 2.dp
                                            ) {
                                                if (selected.value) {
                                                    Row(
                                                        modifier = Modifier.fillMaxSize(),
                                                        verticalAlignment = Alignment.Top,
                                                        horizontalArrangement = Arrangement.End
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.CheckBox,
                                                            contentDescription = "selected",
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                ) {

                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .weight(1F),
                                                        horizontalArrangement = Arrangement.Center,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            modifier = Modifier.padding(horizontal = SMALL_PADDING),
                                                            text = notebooks[index].title,
                                                            color = if (selected.value) MaterialTheme.colorScheme.onSurface.copy(
                                                                alpha = 0.2f
                                                            )
                                                            else MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth(),
//                                                        .weight(1F),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Row {
                                                            Icon(
                                                                modifier = Modifier.width(18.dp),
                                                                painter = painterResource(id = R.drawable.ic_create_note_icon),
                                                                contentDescription = "edit time",
                                                                tint = if (selected.value) MaterialTheme.colorScheme.onSurface.copy(
                                                                    alpha = 0.2f
                                                                )
                                                                else MaterialTheme.colorScheme.onSurface
                                                            )
                                                            Text(
                                                                modifier = Modifier.padding(bottom = SMALL_PADDING),
                                                                text = notebooks[index].createdAt.toLocalDate()
                                                                    .format(
                                                                        DateTimeFormatter.ofPattern(
                                                                            stringResource(id = R.string.note_inside_dateformat)
                                                                        )
                                                                    ),
                                                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                                                color = if (selected.value) MaterialTheme.colorScheme.onSurface.copy(
                                                                    alpha = 0.2f
                                                                )
                                                                else MaterialTheme.colorScheme.onSurface
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun NoteContentPreview() {
    NoteContent(
        listOf(
            Notebook(title = "My Love Note", description = "desc1", priority = Priority.NONE),
            Notebook(title = "first notebooksss", description = "desc2", priority = Priority.NONE),
            Notebook(title = "test3", description = "desc3", priority = Priority.NONE)
        ),
        onSelectNotebook = {},
        onSelectNotebookWithLongClick = {},
        selectedNotebookIds = SnapshotStateList()
    )
}