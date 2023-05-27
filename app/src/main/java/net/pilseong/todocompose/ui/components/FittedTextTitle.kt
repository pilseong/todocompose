package net.pilseong.todocompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import net.pilseong.todocompose.ui.theme.topBarContentColor

@Composable
@OptIn(ExperimentalTextApi::class)
fun FittedTextTitle(
    onAppBarTitleClick: () -> Unit,
    clickEnabled: Boolean = false,
    appbarTitle: String) {

    val modifier = if (clickEnabled) Modifier
        .fillMaxWidth()
        .clickable {
            onAppBarTitleClick()
        } else {
        Modifier
    }

    BoxWithConstraints(
        modifier = modifier,
    ) {

        val textMeasurer = rememberTextMeasurer()

        var textSize: IntSize
        var boxWidth = LocalDensity.current.run { maxWidth.toPx() }.toInt()
        val localStyle = LocalTextStyle.current

        // 글자를 하나씩 더해 가면서 좌우 크기를 계산 하여 전체 박스에 들어갈 수 있는지 판단
        // title 과 박스 크기가 변하지 않는 이상 계산 하지 않는다.
        var index = remember(appbarTitle, boxWidth) {
            var i = 0
            while (i < appbarTitle.length) {
                val textLayoutResult =
                    textMeasurer.measure(
                        text = AnnotatedString(appbarTitle.substring(0, i)),
                        style = localStyle
                    )
                textSize = textLayoutResult.size
                if (boxWidth < textSize.width) break
                i++
            }
            i
        }

        Text(
            text = if (appbarTitle.length == index) appbarTitle
            else appbarTitle.substring(startIndex = 0, endIndex = index - 3) + "...",
//            color = MaterialTheme.colorScheme.topBarContentColor
        )
    }
}