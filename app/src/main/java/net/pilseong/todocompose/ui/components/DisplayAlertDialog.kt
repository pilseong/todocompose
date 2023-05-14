package net.pilseong.todocompose.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import net.pilseong.todocompose.R
import net.pilseong.todocompose.ui.theme.TodoComposeTheme

@Composable
fun DisplayAlertDialog(
    title: String,
    message: String,
    openDialog: Boolean,
    onCloseDialog: () -> Unit,
    onYesClicked: () -> Unit
) {
    if (openDialog) {
        AlertDialog(
            title = {
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onYesClicked()
                        onCloseDialog()
                    },
                ) {
                    Text(text = stringResource(id = R.string.label_yes))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { onCloseDialog() },
                ) {
                    Text(text = stringResource(id = R.string.label_no))
                }
            },
            onDismissRequest = { onCloseDialog() }
        )
    }
}

@Preview
@Composable
fun DisplayAlertDialogPreview() {
    TodoComposeTheme {
        DisplayAlertDialog(
            title = "Delete",
            message = "Do you really want to delete selected task?",
            openDialog = true,
            onCloseDialog = {},
            onYesClicked = {}
        )
    }
}