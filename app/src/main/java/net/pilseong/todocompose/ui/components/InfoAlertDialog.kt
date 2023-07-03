package net.pilseong.todocompose.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoAlertDialog(
    enable: Boolean = false,
    title: String = "Title",
    content: String = "Content",
    dismissLabel: String = "Dismiss",
    onDismiss: () -> Unit,
) {
    if (enable) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            icon = { Icon(Icons.Default.Info, contentDescription = "Info icon") },
            title = { Text(title) },
            text = { Text(content) },
            confirmButton = { },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(dismissLabel)
                }
            }
        )
    }
}