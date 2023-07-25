package net.pilseong.todocompose.ui.screen.calendar

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.pilseong.todocompose.R
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.util.MonthViewCalendar
import net.pilseong.todocompose.util.calculateExpandedCalendarDays
import net.pilseong.todocompose.util.calculateSwipeNext
import net.pilseong.todocompose.util.calculateSwipePrev
import net.pilseong.todocompose.util.yearMonth
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@Composable
fun CalendarContent(
//    loadedDates: Array<List<LocalDate>>,
//    selectedDate: LocalDate,
//    currentMonth: YearMonth,
    onCalendarIntent: (CalendarIntent) -> Unit,
) {

    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    var loadedDates by remember {
        mutableStateOf(calculateExpandedCalendarDays(LocalDate.now().minusMonths(1).yearMonth().atDay(1)))
    }

    var currentMonth by remember {
        mutableStateOf(loadedDates[1][loadedDates[1].size / 2].yearMonth())
    }

    val selectedDate by remember {
        mutableStateOf(LocalDate.now())
    }

    Surface {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(color = MaterialTheme.colorScheme.primary) {
                Box(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth(),
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(vertical = LARGE_PADDING),
                        text = currentMonth.format(DateTimeFormatter.ofPattern(stringResource(id = R.string.calendar_title))),
                        fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.padding(end = LARGE_PADDING),
                            imageVector = Icons.Default.Menu, contentDescription = "Add icon"
                        )
                    }
                }
            }
            MonthViewCalendar(
                loadedDates,
                selectedDate,
                currentMonth = currentMonth,
                onSwipeNext = { yearMonth ->
                    currentMonth = yearMonth.plusMonths(1)
                    Log.d("TTEST", "onSwipeNext")
                    loadedDates = calculateSwipeNext(yearMonth.atDay(1), loadedDates)
                },
                onSwipePrev = { yearMonth ->
                    currentMonth = yearMonth.plusMonths(1)
                    Log.d("TTEST", "onSwipePrev")
                    loadedDates = calculateSwipePrev(yearMonth.atDay(1), loadedDates)

                },
                loadDatesForMonth = { yearMonth ->
                    Log.d("TTEST", "get yearMonth $yearMonth")
                    loadedDates = calculateExpandedCalendarDays(yearMonth.atDay(1))
                    onCalendarIntent(
                        CalendarIntent.LoadNextDates(
                            yearMonth.atDay(
                                1
                            )
                        )
                    )
                },
                onDayClick = {
//                    onCalendarIntent(CalendarIntent.SelectDate(it))
                    currentDate = it
                }
            )
        }
    }
}

@Preview
@Composable
fun CalendarContentPreview() {
    TodoComposeTheme {
        CalendarContent(
//            loadedDates = calculateExpandedCalendarDays(LocalDate.now().minusMonths(1).yearMonth().atDay(1)),
//            selectedDate = LocalDate.now(),
//            currentMonth = LocalDate.now().yearMonth(),
            onCalendarIntent = {}
        )
    }
}