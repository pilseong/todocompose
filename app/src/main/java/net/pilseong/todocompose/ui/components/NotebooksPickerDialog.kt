package net.pilseong.todocompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Output
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.ui.DefaultNoteMemoCount
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.util.Action

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebooksPickerDialog(
    dialogMode: Int = 0,
    visible: Boolean,
    notebooks: List<NotebookWithCount>,
    defaultNoteMemoCount: DefaultNoteMemoCount,
    onDismissRequest: () -> Unit,
    onCloseClick: () -> Unit,
    onNotebookClick: (Long, Action) -> Unit
) {
    if (visible) {
        CustomAlertDialog(onDismissRequest = { onDismissRequest() }) {
            var switchState by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = LARGE_PADDING)
                        .padding(horizontal = XLARGE_PADDING),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector =
                        if (dialogMode == 0) Icons.Default.ListAlt
                        else {
                            if (switchState) Icons.Default.ContentCopy else Icons.Default.Output
                        },
                        contentDescription = "list icon"
                    )
                    Spacer(modifier = Modifier.width(SMALL_PADDING))
                    Text(
                        modifier = Modifier
                            .weight(1F),
                        text = if (dialogMode == 0) stringResource(id = R.string.note_screen_create_notebook_dialog_title)
                        else {
                            if (switchState)
                                stringResource(id = R.string.note_screen_copy_to_notebook_dialog_title)
                            else
                                stringResource(id = R.string.note_screen_move_to_notebook_dialog_title)
                        },
                        color = MaterialTheme.colorScheme.onPrimaryContainer,

                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (dialogMode != 0)
                        Switch(
                            checked = switchState,
                            onCheckedChange = {
                                switchState = !switchState
                            }
                        )
                }

                Surface(
                    modifier = Modifier
                        .height(300.dp)
                        .padding(XLARGE_PADDING)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(4.dp),
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                ) {
                    LazyColumn(
                        // contentPadding 은 전체를 감싸는 padding
                        contentPadding = PaddingValues(LARGE_PADDING),
                        verticalArrangement = Arrangement.spacedBy(LARGE_PADDING)
                    ) {
                        items(
                            items = notebooks,
                            key = { notebook ->
                                notebook.id
                            }
                        ) { item ->
                            Surface(
                                modifier = Modifier
                                    .height(56.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        onNotebookClick(
                                            item.id,
                                            if (dialogMode == 0) Action.NOTEBOOK_CHANGE
                                            else
                                                if (switchState) Action.COPY_TO else Action.MOVE_TO
                                        )
                                    },
                                shape = RoundedCornerShape(4.dp),
                                color = item.priority.color.copy(alpha = 0.4F),
                                tonalElevation = 6.dp,
                            ) {
                                Text(
                                    modifier = Modifier
                                        .wrapContentHeight(align = Alignment.CenterVertically)
                                        .padding(start = LARGE_PADDING),
                                    text = item.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color.Transparent
                                ) {
                                    Row(horizontalArrangement = Arrangement.End) {
                                        Badge {
                                            Text(text = item.memoTotalCount.toString())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(
                            start = XLARGE_PADDING,
                            end = XLARGE_PADDING,
                            bottom = XLARGE_PADDING
                        )
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(modifier = Modifier.width(IntrinsicSize.Max)) {
                        OutlinedButton(
                            shape = RoundedCornerShape(4.dp),
                            onClick = {
                                onNotebookClick(
                                    -1,
                                    if (dialogMode == 0) Action.NOTEBOOK_CHANGE
                                    else
                                        if (switchState) Action.COPY_TO else Action.MOVE_TO)
                            }) {
                            Text(text = stringResource(id = R.string.note_select_use_default))

                        }
                        Row(horizontalArrangement = Arrangement.End) {
                            Badge {
                                Text(text = defaultNoteMemoCount.total.toString())
                            }
                        }
                    }
                    // Close 버튼
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .fillMaxWidth()
                            .clickable {
                                onCloseClick()
                            }
                            .padding(12.dp),
                        textAlign = TextAlign.End,
                        text = stringResource(id = R.string.close_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun NotebooksPickerDialogPreview() {
    MaterialTheme {
        NotebooksPickerDialog(
            visible = true,
            onCloseClick = {},
            onDismissRequest = {},
            notebooks = listOf(
                NotebookWithCount(
                    id = 1,
                    title = "My Love Note",
                    description = "desc1",
                    priority = Priority.NONE
                ),
                NotebookWithCount(
                    id = 2,
                    title = "first notebook",
                    description = "desc2",
                    priority = Priority.NONE
                ),
                NotebookWithCount(
                    id = 3, title = "test3", description = "desc3", priority = Priority.NONE
                )
            ),
            defaultNoteMemoCount = DefaultNoteMemoCount(0, 0, 0, 0, 0),
            onNotebookClick = { _, _ -> }
        )
    }
}