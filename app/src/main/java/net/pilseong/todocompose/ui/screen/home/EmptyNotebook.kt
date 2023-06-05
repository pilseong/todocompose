package net.pilseong.todocompose.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import net.pilseong.todocompose.ui.theme.SMALL_PADDING

@Composable
fun EmptyNotebook(
    width: Int
) {
    Surface(
        modifier = Modifier
            .width(width.dp)
            .height(160.dp)
            .padding(end = SMALL_PADDING, bottom = SMALL_PADDING),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 6.dp,
            bottomStart = 0.dp,
            bottomEnd = 6.dp
        ),
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
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentSatisfiedAlt,
                            contentDescription = ""
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = SMALL_PADDING),
                            text = "EMPTY",
                            color = Color(
                                ColorUtils.blendARGB(
                                    MaterialTheme.colorScheme.onPrimary.toArgb(),
                                    Color.Black.toArgb(),
                                    0.8f
                                )
                            ).copy(0.9f)
                        )
                    }
                }
            }
        }
        RecentNoteLayoutImage()
    }
}

@Preview
@Composable
fun PreviewEmptyNotebook() {
    MaterialTheme {
        EmptyNotebook(100)
    }
}