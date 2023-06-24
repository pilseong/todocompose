package net.pilseong.todocompose.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
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
import net.pilseong.todocompose.util.NoteSortingOption
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun VerticalContent(
    onSelectNotebook: (Int) -> Unit,
    onInfoClick: (Int) -> Unit,
    currentNotebook: NotebookWithCount,
    firstRecentNotebook: NotebookWithCount?,
    secondRecentNotebook: NotebookWithCount?,
    onSortMenuClick: () -> Unit,
    noteSortingOption: NoteSortingOption,
    notebooks: List<NotebookWithCount>,
    selectedNotebookIds: SnapshotStateList<Int>,
    onSelectNotebookWithLongClick: (Int) -> Unit,
    onEmptyImageClick: () -> Unit
) {
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
                        fontStyle = MaterialTheme.typography.headlineSmall.fontStyle,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize
                    )
                }

                // 오늘 날짜
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        color = Color(
                            ColorUtils.blendARGB(
                                MaterialTheme.colorScheme.onSurface.toArgb(),
                                Color.White.toArgb(),
                                0.2f
                            )
                        ).copy(0.9f),
                        text = stringResource(id = R.string.note_content_today) + ": ${
                            ZonedDateTime.now().toLocalDate().format(
                                DateTimeFormatter.ofPattern(
                                    stringResource(id = R.string.note_content_dateformat),
                                    Locale.getDefault()
                                )
                            )
                        }",
                        fontStyle = MaterialTheme.typography.titleSmall.fontStyle,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize
                    )
                }
            }
        }

        val recentNotebookSpace =
            ((LocalConfiguration.current.screenWidthDp - (2 * 16) - 115 - (2 * 6))
                    - (3 * 6)) / 2.0F
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
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(SMALL_PADDING)
                ) {
                    CurrentNotebook(
                        notebookWidth = recentNotebookSpace,
                        onSelectNotebook,
                        onInfoClick,
                        currentNotebook
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(
                        start = SMALL_PADDING,
                        top = SMALL_PADDING,
                        bottom = SMALL_PADDING
                    )
                ) {
                    if (firstRecentNotebook != null)
                        RecentNotebook(
                            notebookWidth = recentNotebookSpace,
                            onSelectNotebook,
                            onInfoClick,
                            firstRecentNotebook
                        )
                    else
                        EmptyNotebook(recentNotebookSpace)

                    if (secondRecentNotebook != null)
                        RecentNotebook(
                            notebookWidth = recentNotebookSpace,
                            onSelectNotebook,
                            onInfoClick,
                            secondRecentNotebook
                        )
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
                    .padding(XLARGE_PADDING)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,

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
                    fontStyle = MaterialTheme.typography.headlineSmall.fontStyle,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
                Card(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable {
                            onSortMenuClick()
                        },
                    border =
                    BorderStroke(
                        0.5F.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2F)
                    ),
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(SMALL_PADDING),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier.width(12.dp),
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Check list icon"
                        )
                        Spacer(modifier = Modifier.width(SMALL_PADDING))
                        Text(
                            text = stringResource(id = noteSortingOption.label),
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        val notebookWidth =
            ((LocalConfiguration.current.screenWidthDp) - (2 * XLARGE_PADDING.value) - (4 * SMALL_PADDING.value)) / 3
        val notebookHeight = notebookWidth * 4 / 3
        val bookShelfHeight = notebookHeight * 2 + (2 * LARGE_PADDING.value) + SMALL_PADDING.value
        Surface(
            modifier = Modifier
                .padding(start = XLARGE_PADDING, end = XLARGE_PADDING)
                .height(bookShelfHeight.dp)
//                .height(340.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            if (notebooks.isNotEmpty()) {
                BookShelf(
                    notebooks = notebooks,
                    selectedNotebookIds = selectedNotebookIds,
                    notebookWidth = notebookWidth,
                    noteSortingOption = noteSortingOption,
                    onSelectNotebookWithLongClick = onSelectNotebookWithLongClick,
                    onSelectNotebook = onSelectNotebook,
                    onInfoClick = onInfoClick
                )
            } else {
                Row(
                    modifier = Modifier.padding(SMALL_PADDING)
                ) {
                    Card(
                        modifier = Modifier
                            .width(110.dp)
                            .height(130.dp)
                            .clickable {
                                onEmptyImageClick()
                            },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "add icon"
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun NoteContentVerticalPreview() {
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
                description = "desc3", priority = Priority.NONE
            )
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