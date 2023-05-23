package net.pilseong.todocompose.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.topBarContainerColor
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun NoteContent() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Top Notebooks",
                fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
        }
        Row(
            modifier = Modifier
//                .padding(vertical = SMALL_PADDING)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = ZonedDateTime.now().toLocalDate().format(
                    DateTimeFormatter.ofPattern(stringResource(id = R.string.note_content_dateformat))
                ),
                fontStyle = MaterialTheme.typography.titleSmall.fontStyle,
                fontSize = MaterialTheme.typography.titleSmall.fontSize
            )
        }
        Spacer(modifier = Modifier.height(LARGE_PADDING))
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
//                .padding(horizontal = SMALL_PADDING)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.topBarContainerColor
        ) {
            Row(
                modifier = Modifier.padding(LARGE_PADDING),
                horizontalArrangement = Arrangement.spacedBy(8.dp),

                ) {
                Card(
                    modifier = Modifier
                        .weight(1F)
                        .height(100.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {

                }
                Card(
                    modifier = Modifier
                        .weight(1F)
                        .height(100.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {

                }
                Card(
                    modifier = Modifier
                        .weight(1F)
                        .height(100.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {

                }
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "List of notebooks",
                fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
        }
        Spacer(modifier = Modifier.height(LARGE_PADDING))
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
//                .padding(horizontal = SMALL_PADDING)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.topBarContainerColor
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(LARGE_PADDING),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),

                    ) {
                    Card(
                        modifier = Modifier
                            .weight(1F)
                            .height(100.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {

                    }
                    Card(
                        modifier = Modifier
                            .weight(1F)
                            .height(100.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {

                    }
                    Card(
                        modifier = Modifier
                            .weight(1F)
                            .height(100.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {

                    }
                }
                Row(
                    modifier = Modifier.padding(LARGE_PADDING),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),

                    ) {
                    Card(
                        modifier = Modifier
                            .weight(1F)
                            .height(100.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {

                    }
                    Card(
                        modifier = Modifier
                            .weight(1F)
                            .height(100.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {

                    }
                    Card(
                        modifier = Modifier
                            .weight(1F)
                            .height(100.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {

                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun NoteContentPreview() {
    NoteContent()
}