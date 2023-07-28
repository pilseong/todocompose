package net.pilseong.todocompose.ui.screen.calendar

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.components.BottomActionBarNavigation
import net.pilseong.todocompose.ui.screen.list.ListAppBarActions
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.fabContainerColor
import net.pilseong.todocompose.ui.theme.fabContent
import net.pilseong.todocompose.util.yearMonth
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CalendarScreen(
    uiState: UserData,
    tasks: List<MemoWithNotebook>,
    selectedNotebook: Notebook,
    toScreen: (Screen) -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    onFabClicked: () -> Unit,
    onAppBarTitleClick: () -> Unit,
    onSearchRangeAllClicked: (Boolean, Boolean) -> Unit,
) {
    // multi select 가 된 경우는 헤더를 고정 한다.
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState(
            initialContentOffset = 0F,
            initialHeightOffset = 0F,
            initialHeightOffsetLimit = 0F
        )
    )

    /**
     * view model 을 통제 코드 종료
     */

    // UI 처리 부분
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        modifier = Modifier
                            .clickable {
                                onAppBarTitleClick()
                            },
                        text = if (uiState.searchRangeAll) stringResource(id = R.string.badge_search_range_all_label) else selectedNotebook.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = selectedNotebook.priority.color.copy(alpha = 0.5F)
                ),
                actions = {
                    Switch(
                        checked = uiState.searchRangeAll,
                        thumbContent = {
                            if (!uiState.searchRangeAll) {
                                Text(text = "All")
                            }
                        },
                        onCheckedChange = {
                            onSearchRangeAllClicked(!uiState.searchRangeAll, false)
                        }
                    )
                }
            )
        },
        bottomBar = {
            BottomActionBarNavigation(
                currentScreen = Screen.MemoCalendar,
                onNavigateClick = toScreen,
                onFabClicked = onFabClicked,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .fillMaxSize(),
        ) {
            var state by remember { mutableStateOf(0) }
            val titles = listOf("Imminent Tasks", "State View", "Calendar View")
            Column {
                CalendarContent(
                    tasks = tasks,
                    onMonthChange = onMonthChange
                )
            }
        }
    }
}

@Preview
@Composable
fun BottomActionBarNavPreview() {
    MaterialTheme {
        BottomActionBarNavigation(
            currentScreen = Screen.MemoCalendar,
            onNavigateClick = {},
            onFabClicked = {},
        )
    }
}


// Floating Action Button
@Composable
fun AddMemoFab(
    paddingEnd: Dp = 0.dp,
    size: Dp = 56.dp,
    onFabClicked: () -> Unit,
    icon: ImageVector
) {
    FloatingActionButton(
        modifier = Modifier.size(size),
        onClick = {
            onFabClicked()
        },
        shape = RoundedCornerShape(paddingEnd),
        containerColor = MaterialTheme.colorScheme.fabContainerColor,
        contentColor = MaterialTheme.colorScheme.fabContent
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = R.string.add_button_icon),
        )
    }
}

@Preview
@Composable
fun PreviewAddMenuFab() {
    MaterialTheme {
        AddMemoFab(onFabClicked = { /*TODO*/ }, icon = Icons.Default.Create)
    }
}

@Composable
@Preview
private fun CalendarcreenPreview() {
    TodoComposeTheme() {
        CalendarScreen(
            uiState = UserData(),
            tasks = listOf(),
            selectedNotebook = Notebook.instance(),
            toScreen = {},
            onMonthChange = {},
            onFabClicked = {},
            onAppBarTitleClick = {},
            onSearchRangeAllClicked = {_, _ -> Unit}
        )
    }
}