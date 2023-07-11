package net.pilseong.todocompose.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.util.NoteSortingOption
import net.pilseong.todocompose.util.getPriorityColor
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun NotebookCover(
    modifier: Modifier = Modifier,
    notebookWidth: Float,
    isMultiSelectionMode: Boolean = false,
    onSelectNotebookWithLongClick: (Long) -> Unit,
    notebook: NotebookWithCount,
    onSelectNotebook: (Long) -> Unit,
    selected: Boolean = false,
    noteSortingOption: NoteSortingOption,
    onInfoClick: (Long) -> Unit
) {
    Surface(
        modifier = modifier
            .padding(end = SMALL_PADDING, bottom = LARGE_PADDING)
            .width(notebookWidth.dp)
            .height((notebookWidth * 4 / 3).dp)
            .combinedClickable(
                onClick = {
                    if (isMultiSelectionMode) {
                        onSelectNotebookWithLongClick(notebook.id)
                    } else {
                        onSelectNotebook(notebook.id)
                    }
                },
                onLongClick = {
                    onSelectNotebookWithLongClick(notebook.id)
                }
            ),
        tonalElevation = if (selected) 12.dp else 2.dp,
        shadowElevation = if (!selected) 6.dp else 0.dp,
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 6.dp,
            bottomStart = 0.dp,
            bottomEnd = 6.dp
        ),
        border = if (selected) BorderStroke(
            0.5.dp,
            color = MaterialTheme.colorScheme.primary
        )
        else BorderStroke(
            0.dp,
            MaterialTheme.colorScheme.surface
        ),
    ) {
        Surface(
            color = if (selected) {
                MaterialTheme.colorScheme.surfaceColorAtElevation(
                    10000.dp
                )
            } else
                getPriorityColor(notebook.priority).copy(
                    alpha = 0.4f
                ),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = SMALL_PADDING
                        ),
                        text = notebook.title,
                        fontStyle = MaterialTheme.typography.labelMedium.fontStyle,
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        overflow = TextOverflow.Ellipsis,
                        color = if (selected) MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.2f
                        )
                        else
                            Color(
                                ColorUtils.blendARGB(
                                    MaterialTheme.colorScheme.onPrimary.toArgb(),
                                    Color.Black.toArgb(),
                                    0.9f
                                )
                            ).copy(0.9f)
                    )
                }
                Surface(
                    color = if (selected) {
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            10000.dp
                        )
                    } else
                        Color(
                            ColorUtils.blendARGB(
                                getPriorityColor(notebook.priority).toArgb(),
                                Color.Black.toArgb(),
                                0.5f
                            )
                        ),
                    tonalElevation = if (selected) 12.dp else 2.dp,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(modifier = Modifier.padding(vertical = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = when (noteSortingOption) {
                                    NoteSortingOption.ACCESS_AT -> {
                                        notebook.accessedAt.toLocalDateTime()
                                            .format(
                                                DateTimeFormatter.ofPattern(
                                                    stringResource(id = R.string.note_inside_dateformat)
                                                )
                                            )
                                    }

                                    NoteSortingOption.UPDATED_AT -> {
                                        notebook.updatedAt.toLocalDateTime()
                                            .format(
                                                DateTimeFormatter.ofPattern(
                                                    stringResource(id = R.string.note_inside_dateformat)
                                                )
                                            )
                                    }

                                    NoteSortingOption.CREATED_AT -> {
                                        notebook.createdAt.toLocalDateTime()
                                            .format(
                                                DateTimeFormatter.ofPattern(
                                                    stringResource(id = R.string.note_inside_dateformat)
                                                )
                                            )
                                    }
                                },
                                lineHeight = TextUnit(16F, TextUnitType.Sp),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                color = if (selected) MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.2f
                                )
                                else Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = when (noteSortingOption) {
                                    NoteSortingOption.ACCESS_AT -> {
                                        notebook.accessedAt.toLocalDateTime()
                                            .format(
                                                DateTimeFormatter.ofPattern(
                                                    "HH:mm"
                                                )
                                            )
                                    }

                                    NoteSortingOption.UPDATED_AT -> {
                                        notebook.updatedAt.toLocalDateTime()
                                            .format(
                                                DateTimeFormatter.ofPattern(
                                                    "HH:mm"
                                                )
                                            )
                                    }

                                    NoteSortingOption.CREATED_AT -> {
                                        notebook.createdAt.toLocalDateTime()
                                            .format(
                                                DateTimeFormatter.ofPattern(
                                                    "HH:mm"
                                                )
                                            )
                                    }
                                },
                                lineHeight = TextUnit(16F, TextUnitType.Sp),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                color = if (selected) MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.2f
                                )
                                else Color.White.copy(alpha = 0.7f)
                            )
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
                            onInfoClick(notebook.id)
                        },
                    containerColor = if (selected) BadgeDefaults.containerColor
                        .copy(alpha = 0.2f)
                    else BadgeDefaults.containerColor
                ) {
                    Text(text = notebook.memoTotalCount.toString())
                }
            }
        }
        if (selected) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clickable {
                            onInfoClick(notebook.id)
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


@Preview
@Composable
fun PreviewNotebookCover() {
    MaterialTheme {
        val screenWidth = (LocalConfiguration.current.screenWidthDp)
        val notebookWidth = remember(screenWidth) {
            (screenWidth - (2 * XLARGE_PADDING.value) - (4 * SMALL_PADDING.value)) / 3
        }
        NotebookCover(
            notebook =
            NotebookWithCount(
                id = 1,
                title = "My Love Note",
                description = "desc1",
                priority = Priority.NONE
            ),
            notebookWidth = notebookWidth,
            onSelectNotebookWithLongClick = {},
            onSelectNotebook = {},
            noteSortingOption = NoteSortingOption.ACCESS_AT,
            onInfoClick = {}

        )
    }
}