package net.pilseong.todocompose.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.ui.screen.task.CommonAction


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectAppbar(
    scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
    selectedItemsCount: Int,
//    onDeleteSelectedClicked: () -> Unit,
    onBackButtonClick: () -> Unit,
    tonerElevation: Dp = 2.dp,
    actions: @Composable () -> Unit
) {
    Surface(
        tonalElevation = tonerElevation
    ) {
        TopAppBar(
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                CommonAction(
                    onClicked = {
                        onBackButtonClick()
                    },
                    icon = Icons.Default.ArrowBack,
                    description = "Arrow backwards Icon"
                )
            },
            title = {
                Text(
                    text = "$selectedItemsCount",
//                color = MaterialTheme.colorScheme.topBarContentColor
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = MaterialTheme.colorScheme.topBarContainerColor
            ),
            actions = {
                actions()
            }
        )
    }
}

@Composable
fun MultiSelectAppbarActions(
    onDeleteTitle: Int,
    onDeleteDescription: Int,
    onDeleteSelectedClicked: () -> Unit,
    actions: @Composable () -> Unit,
) {
    // 다이얼 로그 박스 에 대한 상태
    var deleteAlertExpanded by remember { mutableStateOf(false) }

    // 모두 삭제 하기의 confirm 용도의 alert dialog 생성
    DisplayAlertDialog(
        title = stringResource(id = onDeleteTitle),
        message = stringResource(id = onDeleteDescription),
        openDialog = deleteAlertExpanded,
        onYesClicked = onDeleteSelectedClicked,
        onCloseDialog = { deleteAlertExpanded = false }
    )

    actions()

    CommonAction(
        icon = Icons.Default.Delete,
        onClicked = { deleteAlertExpanded = true },
        description = "date picker icon"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewMultiSelectAppbar() {
    MultiSelectAppbar(
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
        selectedItemsCount = 1,
        onBackButtonClick = {},
        actions = {}
//        onDeleteSelectedClicked = {}
    )
}