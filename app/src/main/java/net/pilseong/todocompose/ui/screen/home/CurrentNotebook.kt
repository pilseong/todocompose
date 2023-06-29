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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.NotebookWithCount
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun CurrentNotebook(
    notebookWidth: Float,
    onSelectNotebook: (Int) -> Unit,
    onInfoClick: (Int) -> Unit,
    currentNotebook: NotebookWithCount
) {
    Surface(
        modifier = Modifier
            .width(notebookWidth.dp)
            .height((notebookWidth * 4 / 3).dp)
            .padding(bottom = SMALL_PADDING)
            .combinedClickable(
                onClick = {
                    onSelectNotebook(currentNotebook.id)
                },
                onLongClick = {
                }
            ),
        color = currentNotebook.priority.color,
        tonalElevation = 2.dp,
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
                        .padding(horizontal = SMALL_PADDING)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 2.dp),
                        text = stringResource(id = R.string.current_label),
                        lineHeight = TextUnit(20F, TextUnitType.Sp),
                        color = Color.White,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize
                    )
                    Badge(
                        modifier = Modifier.clickable {
                            onInfoClick(currentNotebook.id)
                        }
                    ) {
                        Text(text = currentNotebook.memoTotalCount.toString())
                    }
                }
            }
            Text(
                modifier = Modifier
                    .padding(horizontal = SMALL_PADDING)
                    .weight(1F)
                    .wrapContentHeight(CenterVertically)
                    .fillMaxWidth(),
                text = currentNotebook.title,
                fontStyle = MaterialTheme.typography.labelMedium.fontStyle,
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                textAlign = TextAlign.Center,
                color = Color(
                    ColorUtils.blendARGB(
                        MaterialTheme.colorScheme.onPrimary.toArgb(),
                        Color.Black.toArgb(),
                        0.8f
                    )
                ).copy(0.9f)
            )
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
                        .padding(horizontal = SMALL_PADDING)
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

@Preview
@Composable
fun PreviewCurrentNotebook() {
    MaterialTheme {
        CurrentNotebook(
            notebookWidth = 110F,
            onSelectNotebook = {},
            onInfoClick = {},
            currentNotebook = NotebookWithCount(
                id = 1,
                title = "My Love Note",
                description = "desc1",
                priority = Priority.NONE
            ),
        )
    }
}