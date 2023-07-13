package net.pilseong.todocompose.ui.screen.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RecentNoteLayoutImage() {
    Surface(
        color = Color.Transparent,
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .width(5.dp),
                shape = CutCornerShape(topEnd = 500.dp),
                color = Color.Transparent.copy(alpha = 0.6F)
            ) {
            }
        }
    }
    Surface(
        color = Color.Transparent,
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = Color.Transparent.copy(alpha = 0.6F)
            ) {}

        }
    }
    Surface(
        color = Color.Transparent,
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .width(5.dp),
                shape = CutCornerShape(topStart = 500.dp),
                color = Color.Transparent.copy(alpha = 0.6F)
            ) { }
        }
    }
    Surface(
        color = Color.Transparent,
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .fillMaxHeight(0.2f),
                shape = RoundedCornerShape(topEnd = 100.dp),
                color = Color.Black.copy(0.8F),
            ) {
                Row(
                    modifier = Modifier.size(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center

                ) {
                    Icon(
                        modifier = Modifier.size(12.dp),
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Access Icon",
                        tint = Color.White.copy(alpha = 0.8F)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewRecentNotebookLayoutImage() {
    RecentNoteLayoutImage()
}