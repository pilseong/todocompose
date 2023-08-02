package net.pilseong.todocompose.util

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MonthViewCalendar(
    tasks: List<MemoWithNotebook>,
    loadedDates: Array<List<LocalDate>>,
    selectedDate: LocalDate,
    currentMonth: YearMonth,
    onSwipeNext: (YearMonth) -> Unit,
    onSwipePrev: (YearMonth) -> Unit,
    loadDatesForMonth: (YearMonth) -> Unit,
    onDayClick: (LocalDate, memos: List<MemoWithNotebook>) -> Unit,
    onDayLongClick: (LocalDate, memos: List<MemoWithNotebook>) -> Unit,
) {
    val itemWidth = LocalConfiguration.current.screenWidthDp / 7
    CalendarPager(
        loadedDates = loadedDates,
        loadNextDates = { onSwipeNext(currentMonth) },
        loadPrevDates = { onSwipePrev(currentMonth.minusMonths(2)) },
    ) { currentPage ->
        Log.d("TTEST", "currentPage is $currentPage")
        Column {
            Surface(
                color = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DayOfWeek.values().forEach { date ->
                        Text(
                            modifier = Modifier.padding(vertical = SMALL_PADDING),
                            text = DayOfWeek.values()[(date.ordinal + 6) % 7].getDisplayName(
                                TextStyle.SHORT,
                                LocalContext.current.resources.configuration.locales[0]
                            ),
                            fontStyle = MaterialTheme.typography.titleMedium.fontStyle,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                }
            }
            BoxWithConstraints {
                val boxHeight = LocalDensity.current.run { maxHeight }
                FlowRow(modifier = Modifier.fillMaxSize()) {
                    loadedDates[currentPage].forEach { date ->
                        val memos = tasks.filter {
                            it.memo.dueDate!!.month == date.month &&
                                    it.memo.dueDate.dayOfMonth == date.dayOfMonth
                        }

                        DayView(
                            modifier = Modifier
                                .width(itemWidth.dp)
                                .border(
                                    width = 0.2.dp,
                                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                                )
                                .height(
                                    (boxHeight / (
                                            if (loadedDates[currentPage].size % 7 == 0)
                                                loadedDates[currentPage].size / 7
                                            else
                                                (loadedDates[currentPage].size % 7) + 1
                                            ))
                                )
                                .dayViewModifier(
                                    date,
                                    currentMonth,
                                ),
                            notes = memos,
                            date = date,
                            isSelected = selectedDate == date,
                            onDayClick = onDayClick,
                            onDayLongClick = onDayLongClick,
                        )
                    }
                }
            }
        }
    }

}


@Preview
@Composable
fun PreviewMonthViewCalendar() {
    MaterialTheme {
        MonthViewCalendar(
            tasks = listOf(),
            loadedDates = calculateExpandedCalendarDays(
                LocalDate.now().minusMonths(1)
                    .yearMonth().atDay(1)
            ),
            selectedDate = LocalDate.now(),
            currentMonth = LocalDate.now().yearMonth(),
            loadDatesForMonth = {},
            onSwipeNext = {},
            onSwipePrev = {},
            onDayClick = { _, _ -> },
            onDayLongClick = { _, _ -> }
        )
    }

}


@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun CalendarPager(
    loadedDates: Array<List<LocalDate>>,
    loadNextDates: () -> Unit,
    loadPrevDates: () -> Unit,
    content: @Composable (currentPage: Int) -> Unit
) {
//    val pagerState = rememberPagerState(initialPage = 1)

    val pagerState = remember {
        PagerState(currentPage = 1)
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 0 || pagerState.currentPage == 2) {
            if (pagerState.currentPage == 0) {
                loadPrevDates()
            } else {
                loadNextDates()
            }
        }
    }

    // 새로운 데이터 로딩이 끝났을 때만 화면 이동을 허용 한다. 깜박임 방지
    LaunchedEffect(loadedDates) {
        pagerState.scrollToPage(1)
    }

    HorizontalPager(
        count = 3,
        state = pagerState,
        verticalAlignment = Alignment.Top
    ) { currentPage ->
        content(currentPage)
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DayView(
    modifier: Modifier = Modifier,
    date: LocalDate,
    notes: List<MemoWithNotebook>,
    isSelected: Boolean = false,
    onDayClick: (LocalDate, memos: List<MemoWithNotebook>) -> Unit,
    onDayLongClick: (LocalDate, memos: List<MemoWithNotebook>) -> Unit
) {
    val isCurrentDay = date == LocalDate.now()
    Column(
        modifier = modifier
            .border(
                width = if (isCurrentDay) 0.4.dp else 0.dp,
                color = MaterialTheme.colorScheme.primary
            )
            .background(
                color =
//                    if (isCurrentDay)
//                        MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                if (isSelected)
                    MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                else MaterialTheme.colorScheme.background
            )
            .verticalScroll(rememberScrollState())
            .combinedClickable(
                onClick = { onDayClick(date, notes) },
                onLongClick = { onDayLongClick(date, notes) }),
    ) {

        Badge(
            containerColor = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer
            else if (isCurrentDay) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = if (isCurrentDay) MaterialTheme.colorScheme.primary
                else if (isSelected)
                    MaterialTheme.colorScheme.tertiary
                else {
                    when (date.dayOfWeek) {
                        DayOfWeek.SATURDAY -> Color.Blue.copy(alpha = 0.5f)
                        DayOfWeek.SATURDAY -> Color.Red.copy(alpha = 0.5f)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                }

            )
        }


        notes.forEach {
            Surface(
                color = if (it.memo.priority == Priority.NONE)
                    MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                else it.memo.priority.color.copy(alpha = 0.3f)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
//                Icon(
//                    modifier = Modifier.size(5.dp),
//                    imageVector = Icons.Default.Circle,
//                    contentDescription = "circle image",
//                    tint = it.memo.priority.color
//                )
                    Text(
                        text = it.memo.title,
                        fontSize = 10.sp,
                        fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color =
//                        if (it.memo.priority == Priority.NONE)
                        MaterialTheme.colorScheme.onSurface
//                        else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

internal fun Modifier.dayViewModifier(
    date: LocalDate,
    currentMonth: YearMonth? = null,
): Modifier = this.then(
    Modifier.alpha(
        if ((currentMonth != null && date.isAfter(currentMonth.atEndOfMonth())) ||
            (currentMonth != null && date.isBefore(currentMonth.atDay(1)))
        )
            0.5f else 1f
    )
)
