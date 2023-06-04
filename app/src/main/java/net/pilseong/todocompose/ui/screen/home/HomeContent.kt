package net.pilseong.todocompose.ui.screen.home

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import net.pilseong.todocompose.util.getPriorityColor
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NoteContent(
    notebooks: List<NotebookWithCount>,
    selectedNotebookIds: SnapshotStateList<Int>,
    onSelectNotebook: (Int) -> Unit,
    onSelectNotebookWithLongClick: (Int) -> Unit,
    onInfoClick: (Int) -> Unit,
    currentNotebook: NotebookWithCount,
    firstRecentNotebook: NotebookWithCount?,
    secondRecentNotebook: NotebookWithCount?,
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
                modifier = Modifier
                    .padding(top = LARGE_PADDING)
                    .fillMaxSize()
            ) {
                // 타이틀 + 날짜
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = XLARGE_PADDING,
                            vertical = LARGE_PADDING
                        ),
                    ) {

                        // 헤드 타이틀
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(id = R.string.note_screen_recent_notebooks),
                                color = Color(
                                    ColorUtils.blendARGB(
                                        MaterialTheme.colorScheme.onSurface.toArgb(),
                                        Color.White.toArgb(),
                                        0.2f
                                    )
                                ).copy(0.9f),
                                fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                                fontSize = MaterialTheme.typography.headlineMedium.fontSize
                            )
                        }

                        // 오늘 날짜
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 4.dp),
//                                color = headlineYellow,
                                color = Color(
                                    ColorUtils.blendARGB(
                                        MaterialTheme.colorScheme.onSurface.toArgb(),
                                        Color.White.toArgb(),
                                        0.2f
                                    )
                                ).copy(0.9f),
                                text = stringResource(id = R.string.note_content_today) + ": ${
                                    ZonedDateTime.now().toLocalDate().format(
                                        DateTimeFormatter.ofPattern(stringResource(id = R.string.note_content_dateformat))
                                    )
                                }",
                                fontStyle = MaterialTheme.typography.titleSmall.fontStyle,
                                fontSize = MaterialTheme.typography.titleSmall.fontSize
                            )
                        }
                    }
                }

                val recentNotebookSpace = ((LocalConfiguration.current.screenWidthDp - (2*16) - 115 - (2*6))
                - (3 * 6)) / 2
                // 자주 사용하는 노트북 섹션
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = XLARGE_PADDING,
                            end = XLARGE_PADDING,
                            bottom = XLARGE_PADDING
                        ),

                    ) {
                    Surface(
                        modifier = Modifier.padding(end = MEDIUM_PADDING),
                        shape = RoundedCornerShape(4.dp),
                        tonalElevation = 6.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(SMALL_PADDING)
                        ) {
                            CurrentNotebook(onSelectNotebook, currentNotebook)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        tonalElevation = 6.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(SMALL_PADDING)
                        ) {
                            if (firstRecentNotebook != null)
                                RecentNotebook(
                                    width = recentNotebookSpace,
                                    onSelectNotebook,
                                    firstRecentNotebook
                                )
                            else
                                EmptyNotebook(recentNotebookSpace)

                            if (secondRecentNotebook != null)
                                RecentNotebook(
                                    width = recentNotebookSpace,
                                    onSelectNotebook,
                                    secondRecentNotebook)
                            else
                                EmptyNotebook(recentNotebookSpace)
                        }
                    }
                }

                // 중간 타이틀
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .padding(LARGE_PADDING)
                            .fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(id = R.string.note_screen_list_of_notebooks),
                            color = Color(
                                ColorUtils.blendARGB(
                                    MaterialTheme.colorScheme.onSurface.toArgb(),
                                    Color.White.toArgb(),
                                    0.2f
                                )
                            ).copy(0.9f),
                            fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize
                        )
                    }
                }
//                Spacer(modifier = Modifier.height(LARGE_PADDING))
                Surface(
                    modifier = Modifier
//                        .clip(RoundedCornerShape(4.dp))
                        .padding(start = XLARGE_PADDING, end = XLARGE_PADDING)
                        .height(340.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
//                    border = BorderStroke(0.5.dp, DarkGreenBackground),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
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
//                                            Surface(
//                                                modifier = Modifier
//                                                    .fillMaxHeight(),
//                                                color = getPriorityColor(notebooks[index].priority),
//                                                content = {
//                                                    Spacer(modifier = Modifier.width(12.dp))
//                                                }
//                                            )
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
                                                            modifier = Modifier.padding(horizontal = SMALL_PADDING),
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
//                                                            Row {
                                                            Icon(
                                                                modifier = Modifier
                                                                    .width(14.dp)
                                                                    .padding(top = 3.dp),
                                                                painter = painterResource(id = R.drawable.ic_create_note_icon),
                                                                contentDescription = "edit time",
                                                                tint = if (selected.value) MaterialTheme.colorScheme.onSurface.copy(
                                                                    alpha = 0.2f
                                                                )
//                                                                    else MaterialTheme.colorScheme.onSurface
                                                                else Color.White.copy(alpha = 0.7f)
                                                            )
                                                            Text(
                                                                modifier = Modifier.padding(
                                                                    vertical = SMALL_PADDING
                                                                ),
                                                                text = notebooks[index].createdAt.toLocalDate()
                                                                    .format(
                                                                        DateTimeFormatter.ofPattern(
                                                                            stringResource(id = R.string.note_inside_dateformat)
                                                                        )
                                                                    ),
                                                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                                                fontWeight = FontWeight.Light,
                                                                color = if (selected.value) MaterialTheme.colorScheme.onSurface.copy(
                                                                    alpha = 0.2f
                                                                )
//                                                                    else MaterialTheme.colorScheme.onSurface
                                                                else Color.White.copy(alpha = 0.7f)
                                                            )
                                                        }
//                                                        }
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
            }
        }
    }
}

@Preview
@Composable
fun NoteContentPreview() {
    NoteContent(
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
        onSelectNotebook = {},
        onSelectNotebookWithLongClick = {},
        selectedNotebookIds = SnapshotStateList(),
        onInfoClick = {},
        currentNotebook = NotebookWithCount.instance(),
        firstRecentNotebook = NotebookWithCount.instance(),
        secondRecentNotebook = NotebookWithCount.instance(),
    )
}