package net.pilseong.todocompose.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.NotebookWithCount
import net.pilseong.todocompose.ui.components.CustomAlertDialog
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.util.MetricsUtil
import java.time.format.DateTimeFormatter

@Composable
fun InfoDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    notebook: NotebookWithCount?,
    onDismissRequest: () -> Unit,
    onEditClick: (Int) -> Unit,
) {
    if (visible) {
        CustomAlertDialog(
            onDismissRequest = { onDismissRequest() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = modifier
                    .wrapContentHeight()
                    .width(320.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(4.dp))
//                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 10.dp)
                            .padding(horizontal = XLARGE_PADDING),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.padding(top = 2.dp),
                            imageVector = Icons.Default.Info,
                            contentDescription = "notebook info icon"
                        )
                        Spacer(modifier = Modifier.width(SMALL_PADDING))
                        Text(
                            text = stringResource(id = R.string.info_label),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Row(
                            modifier = Modifier.weight(1F),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (notebook?.id != -1) {
                                Icon(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable {
                                            onEditClick(notebook?.id ?: Integer.MIN_VALUE)
                                        },
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = "Edit Note Icon"
                                )
                                Spacer(modifier = Modifier.width(LARGE_PADDING))
                            }
                            Icon(
                                modifier = Modifier
                                    .border(
                                        BorderStroke(
                                            1.5.dp,
                                            MaterialTheme.colorScheme.onSurface
                                        ),
                                        shape = RoundedCornerShape(30.dp)
                                    )
                                    .size(20.dp)
                                    .clickable {
                                        onDismissRequest()
                                    },
                                imageVector = Icons.Default.Close,
                                contentDescription = "close button"
                            )
                        }
                    }
                    Spacer(
                        Modifier
                            .height(1.dp)
                            .border(1.dp, color = MaterialTheme.colorScheme.primary)
                            .fillMaxWidth()
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(XLARGE_PADDING)
                            .verticalScroll(rememberScrollState())
                    ) {
                        val context = LocalContext.current
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val width = remember { mutableStateOf(0.dp) }
                            Column {
                                Text(
                                    text = stringResource(id = R.string.info_title),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    onTextLayout = {
                                        width.value = MetricsUtil.convertPixelsToDp(
                                            it.size.width.toFloat(),
                                            context
                                        ).dp
                                    }
                                )
                                Surface(
                                    modifier = Modifier
                                        .padding(start = width.value)
                                        .wrapContentHeight()
                                        .fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            modifier = Modifier
                                                .padding(start = SMALL_PADDING)
                                                .wrapContentHeight(),
                                            text = notebook?.title ?: "",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val width = remember { mutableStateOf(0.dp) }
                                Text(
                                    text = stringResource(id = R.string.info_description),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    onTextLayout = {
                                        width.value = MetricsUtil.convertPixelsToDp(
                                            it.size.width.toFloat(),
                                            context
                                        ).dp
                                    }
                                )
                                Surface(
                                    modifier = Modifier
                                        .padding(start = width.value)
                                        .fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            modifier = Modifier
                                                .padding(start = SMALL_PADDING)
                                                .wrapContentHeight(),
                                            text = notebook?.description ?: "",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .fillMaxWidth()
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .height(1.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.info_total_memos),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = notebook?.memoTotalCount.toString(),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.info_priority),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(
                                    id = notebook?.priority?.label ?: R.string.priority_none
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.info_priority_memos_count),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "" + (notebook?.highPriorityCount.toString()) + "/" +
                                        (notebook?.mediumPriorityCount.toString()) + "/" +
                                        (notebook?.lowPriorityCount.toString()) + "/" +
                                        (notebook?.nonePriorityCount.toString()),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.info_completed_count),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = notebook?.completedCount.toString(),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.info_in_process_count),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "" + (notebook?.activeCount.toString()) + "/" +
                                        (notebook?.waitingCount.toString()),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.info_suspended_count),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = notebook?.suspendedCount.toString(),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.info_not_assigned_count),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = notebook?.noneCount.toString(),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                        if (notebook?.id != -1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.info_created_at),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = notebook?.createdAt?.toOffsetDateTime()
                                        ?.format(DateTimeFormatter.ISO_DATE_TIME) ?: "",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(
                                Modifier
                                    .padding(bottom = MEDIUM_PADDING)
                                    .height(1.dp)
                                    .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                    .fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.info_updated_at),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = notebook?.updatedAt?.toOffsetDateTime()
                                        ?.format(DateTimeFormatter.ISO_DATE_TIME) ?: "",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(
                                Modifier
                                    .padding(bottom = MEDIUM_PADDING)
                                    .height(1.dp)
                                    .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                    .fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.info_accessed_at),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = notebook?.accessedAt?.toOffsetDateTime()
                                        ?.format(DateTimeFormatter.ISO_DATE_TIME) ?: "",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(
                                Modifier
                                    .padding(bottom = MEDIUM_PADDING)
                                    .height(1.dp)
                                    .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                    .fillMaxWidth()
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
fun PreviewInfoDialog() {
    InfoDialog(
        visible = true,
        notebook = NotebookWithCount.instance(title = "askldflkasdjfkja;ls dkjf;alksd jfl;kjsdf;lkjaklddsk jf;laksdjf;lakjsd;lf"),
        onDismissRequest = {},
        onEditClick = {

        }
    )
}