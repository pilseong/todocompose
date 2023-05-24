package net.pilseong.todocompose.ui.screen.home

import android.util.Log
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.navigation.destination.BottomNavBar
import net.pilseong.todocompose.ui.screen.list.AddMemoFab
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.theme.topBarContainerColor
import net.pilseong.todocompose.ui.theme.topBarContentColor
import net.pilseong.todocompose.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onClickBottomNavBar: (String) -> Unit,
    onFabClick: () -> Unit,
    onSelectNotebook: (Int) -> Unit,
    notebooks: List<Notebook>
) {


    Log.i("PHILIP", "notebooks $notebooks")

    val scrollBehavior = exitUntilCollapsedScrollBehavior()

    Scaffold(
//        modifier = Modifier.verticalScroll(rememberScrollState()),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(id = R.string.home_screen_title),
                        color = MaterialTheme.colorScheme.topBarContentColor
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.topBarContainerColor
                ),
            )
        },
        bottomBar = {
            BottomNavBar(
                onClick = onClickBottomNavBar,
                currentDestination = Constants.HOME_SCREEN
            )
        },
        floatingActionButton = {
            AddMemoFab(
                onFabClicked = {
                    onFabClick()
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding() + LARGE_PADDING,
                    start = XLARGE_PADDING,
                    end = XLARGE_PADDING,
                    bottom = SMALL_PADDING
                )
                .fillMaxSize(),
        ) {
            NoteContent(
                notebooks = notebooks,
                onSelectNotebook = onSelectNotebook
            )
        }
    }
}