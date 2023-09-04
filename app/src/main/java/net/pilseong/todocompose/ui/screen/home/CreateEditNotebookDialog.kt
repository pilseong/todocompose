package net.pilseong.todocompose.ui.screen.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.ui.components.CustomAlertDialog
import net.pilseong.todocompose.ui.components.PriorityDropDown
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.util.Constants

@Composable
fun CreateEditNotebookDialog(
    dialogTitle: Int,
    mode: Boolean = false,
    visible: Boolean,
    notebookInput: NotebookWithCount,
    onInputChange: (NotebookWithCount) -> Unit,
    onDismissRequest: () -> Unit,
    onOKClick: () -> Unit
) {
    if (visible) {
        CustomAlertDialog(
            onDismissRequest = { onDismissRequest() },
            // 아래를 넣어야 동적으로 입력에 따라 자동으로 다이얼로그가 아래로 확장이 된다.
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .width(320.dp)
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
                    Icon(imageVector = Icons.Default.Create, contentDescription = "create notebook")
                    Spacer(modifier = Modifier.width(SMALL_PADDING))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(id = dialogTitle),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = XLARGE_PADDING)
                        .padding(vertical = LARGE_PADDING)
                        .fillMaxWidth()
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding(),
                        value = notebookInput.title,
                        label = {
                            Text(text = stringResource(id = R.string.new_task_title_placeholder))
                        },
                        onValueChange = {
                            if (it.length <= Constants.MAX_TITLE_LENGTH)
                                onInputChange(notebookInput.copy(title = it))
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                1.dp
                            ),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                1.dp
                            )
                        ),
                        singleLine = false,
                        maxLines =  if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) 1 else 3,
                        supportingText = {
                            Text(
                                text = "${notebookInput.title.length} / ${Constants.MAX_TITLE_LENGTH}",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                            )
                        }
                    )
                    Surface(tonalElevation = 1.dp) {
                        PriorityDropDown(
                            isNew = mode,
                            priority = notebookInput.priority,
                            onPrioritySelected = {
                                onInputChange(notebookInput.copy(priority = it))
                            }
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .height(0.7.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    TextField(
                        modifier = Modifier
                            .height(if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) 100.dp else 160.dp)
                            .imePadding()
                            .fillMaxWidth(),
                        value = notebookInput.description,
                        label = {
                            Text(
                                text = stringResource(id = R.string.new_task_description_placeholder)
                            )
                        },
                        onValueChange = {
                            if (it.length <= Constants.MAX_CONTENT_LENGTH)
                                onInputChange(notebookInput.copy(description = it))

                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                1.dp
                            ),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                1.dp
                            )
                        ),
                        supportingText = {
                            Text(
                                text = "${notebookInput.description.length} / ${Constants.MAX_CONTENT_LENGTH}",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                            )
                        }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        // OK 버튼
                        OutlinedButton(onClick = { onDismissRequest() }) {
                            Text(text = stringResource(id = R.string.close_label))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            enabled = notebookInput.title.isNotEmpty(),
                            onClick = { onOKClick() },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContentColor = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
                                disabledContainerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.save_label),
//                                color = MaterialTheme.colorScheme.onPrimary
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
fun CreateNotebookDialogPreview() {
    MaterialTheme {
        CreateEditNotebookDialog(
            dialogTitle = R.string.note_screen_create_notebook_dialog_title,
            visible = true,
            notebookInput = NotebookWithCount(
                title = "",
                priority = Priority.HIGH,
                description = "수엘이",
            ),
            onInputChange = {},
            onOKClick = {},
            onDismissRequest = {}
        )
    }
}