package net.pilseong.todocompose.ui.screen.note

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.util.getPriorityColor
import java.time.format.DateTimeFormatter

@Composable

@OptIn(ExperimentalMaterial3Api::class)
fun RecentNotebook(
    notebookWidth: Float,
    onSelectNotebook: (Long) -> Unit,
    onInfoClick: (Long) -> Unit,
    currentNotebook: NotebookWithCount
) {
    Surface(
        modifier = Modifier
            .width(notebookWidth.dp)
            .height((notebookWidth * 4 / 3).dp)
            .padding(end = SMALL_PADDING, bottom = SMALL_PADDING),
        shadowElevation = 6.dp,
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 6.dp,
            bottomStart = 0.dp,
            bottomEnd = 6.dp
        ),
        border = BorderStroke(
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
                    modifier = Modifier.clickable {
                        onSelectNotebook(currentNotebook.id)
                    },
                    color = getPriorityColor(currentNotebook.priority).copy(
                        alpha = 0.4f
                    ),
                    tonalElevation = 2.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        Surface(
                            color = getPriorityColor(currentNotebook.priority).copy(
                                alpha = 0.4f
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = SMALL_PADDING, vertical = 2.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.note_screen_recent_notebooks_label),
                                    lineHeight = TextUnit(20F, TextUnitType.Sp),
                                    color = Color.Black.copy(alpha = 0.7f),
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                                )
                                Column(
                                    modifier = Modifier
                                        .clickable {
                                            onInfoClick(currentNotebook.id)
                                        }
                                        .weight(1F),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Badge() {
                                        Text(text = currentNotebook.memoTotalCount.toString())
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1F),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = SMALL_PADDING),
                                text = currentNotebook.title,
                                overflow = TextOverflow.Ellipsis,
                                fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                color = Color(
                                    ColorUtils.blendARGB(
                                        MaterialTheme.colorScheme.onPrimary.toArgb(),
                                        Color.Black.toArgb(),
                                        0.9f
                                    )
                                ).copy(0.9f)
                            )
                        }
                        Surface(
                            color = Color(
                                ColorUtils.blendARGB(
                                    currentNotebook.priority.color.toArgb(),
                                    Color.Black.toArgb(),
                                    0.3f
                                )
                            ),
                            tonalElevation = 2.dp,
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = currentNotebook.accessedAt.toLocalDateTime()
                                        .format(
                                            DateTimeFormatter.ofPattern(
                                                stringResource(id = R.string.note_inside_dateformat)
                                            )
                                        ),
                                    lineHeight = TextUnit(16F, TextUnitType.Sp),
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = currentNotebook.accessedAt.toLocalDateTime()
                                        .format(
                                            DateTimeFormatter.ofPattern(
                                                "HH:mm"
                                            )
                                        ),
                                    lineHeight = TextUnit(16F, TextUnitType.Sp),
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
//        RecentNoteLayoutImage()
    }
}


@Preview
@Composable
fun PreviewRecentNotebook() {
    MaterialTheme {
        RecentNotebook(
            100F,
            onSelectNotebook = {},
            currentNotebook = NotebookWithCount.instance(),
            onInfoClick = {}
        )
    }
}