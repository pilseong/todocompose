package net.pilseong.todocompose.ui.screen.home

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.NotebookWithCount
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.theme.topBarContainerColor
import net.pilseong.todocompose.util.NoteSortingOption
import net.pilseong.todocompose.util.getPriorityColor
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NoteContent(
    notebooks: List<NotebookWithCount>,
    selectedNotebookIds: SnapshotStateList<Int>,
    noteSortingOption: NoteSortingOption,
    onSelectNotebook: (Int) -> Unit,
    onSelectNotebookWithLongClick: (Int) -> Unit,
    onInfoClick: (Int) -> Unit,
    currentNotebook: NotebookWithCount,
    firstRecentNotebook: NotebookWithCount?,
    secondRecentNotebook: NotebookWithCount?,
    onEmptyImageClick: () -> Unit,
    onSortMenuClick: () -> Unit,
) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {

            Row(
                modifier = Modifier
                    .padding(horizontal = XLARGE_PADDING)
                    .fillMaxSize(),
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
            VerticalContent(
                onSelectNotebook,
                onInfoClick,
                currentNotebook,
                firstRecentNotebook,
                secondRecentNotebook,
                onSortMenuClick,
                noteSortingOption,
                notebooks,
                selectedNotebookIds,
                onSelectNotebookWithLongClick,
                onEmptyImageClick
            )
        }
    }
}


@Preview
@Composable
fun NoteContentPreview() {
    NoteContent(
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
                description = "desc3", priority = Priority.NONE)
        ),
        onSelectNotebook = {},
        onSelectNotebookWithLongClick = {},
        selectedNotebookIds = SnapshotStateList(),
        onInfoClick = {},
        currentNotebook = NotebookWithCount.instance(),
        firstRecentNotebook = NotebookWithCount.instance(),
        secondRecentNotebook = NotebookWithCount.instance(),
        onEmptyImageClick = {},
        onSortMenuClick = {},
        noteSortingOption = NoteSortingOption.ACCESS_AT
    )
}