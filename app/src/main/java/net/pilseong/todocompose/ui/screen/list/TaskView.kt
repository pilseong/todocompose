package net.pilseong.todocompose.ui.screen.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING

@Composable
fun TaskView() {
    TaskStatusLine(

    )

}

@Composable
fun TaskStatusLine() {
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = XLARGE_PADDING)
        ) {
            val lazyState = rememberLazyListState()

            // 처음 기본 화면에 들어 가는 status 갯수를 저장 한다.
            var initialSize by remember { mutableStateOf(0) }
            val stateSize by remember {
                derivedStateOf {
                    if (initialSize == 0 || (lazyState.firstVisibleItemIndex == 0 && !lazyState.canScrollBackward))
                        lazyState.layoutInfo.visibleItemsInfo.size
                    else
                        initialSize
                }
            }
            initialSize = stateSize


            LazyRow(
                modifier = Modifier
                    .padding(vertical = SMALL_PADDING)
                    .fillMaxWidth(),
                state = lazyState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                item {

                }
            }
        }
    }
}