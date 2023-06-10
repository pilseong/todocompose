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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.NotebookWithCount
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun CurrentNotebook(
    onSelectNotebook: (Int) -> Unit,
    currentNotebook: NotebookWithCount
) {
    Surface(
        modifier = Modifier
            .width(115.dp)
            .height(160.dp)
            .padding(bottom = SMALL_PADDING)
            .combinedClickable(
                onClick = {
                },
                onLongClick = {
                }
            ),
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
            ) {
                Surface(
                    modifier = Modifier.clickable {
                        onSelectNotebook(currentNotebook.id)
                    },
                    color = currentNotebook.priority.color,
                    tonalElevation = 2.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        Surface(
                            color = Color(
                                ColorUtils.blendARGB(
                                    currentNotebook.priority.color.toArgb(),
                                    Color.Black.toArgb(),
                                    0.5f
                                )
                            ),
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(SMALL_PADDING)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
//                                Badge {
//                                    Icon(
//                                        modifier = Modifier.size(8.dp),
//                                        imageVector = Icons.Default.Check,
//                                        contentDescription = "Checked Icon",
//                                    )
//                                }
                                Text(
                                    modifier = Modifier.padding(horizontal = 2.dp),
                                    text = stringResource(id = R.string.current_label),
                                    color = Color.White,
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                                )
                                Column(
                                    modifier = Modifier.weight(1F),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Badge() {
                                        Text(text = currentNotebook.memoCount.toString())
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
                                color = Color(
                                    ColorUtils.blendARGB(
                                        MaterialTheme.colorScheme.onPrimary.toArgb(),
                                        Color.Black.toArgb(),
                                        0.8f
                                    )
                                ).copy(0.9f)
//                                                                Color.White.copy(alpha = 0.9f)
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
                                    .padding(
                                        horizontal = SMALL_PADDING,
                                        vertical = 4.dp
                                    )
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
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCurrentNotebook() {
    MaterialTheme {
        CurrentNotebook(onSelectNotebook = {}, currentNotebook = NotebookWithCount.instance())
    }
}