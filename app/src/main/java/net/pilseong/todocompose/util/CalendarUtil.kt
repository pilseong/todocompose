package net.pilseong.todocompose.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

fun calculateExpandedCalendarDays(startDate: LocalDate): Array<List<LocalDate>> {
    val array = Array(3) { monthIndex ->
        val monthFirstDate = startDate.plusMonths(monthIndex.toLong())
        val monthLastDate = monthFirstDate.plusMonths(1).minusDays(1)
        val weekBeginningDate = monthFirstDate.getWeekStartDate(DayOfWeek.SUNDAY)
        if (weekBeginningDate != monthFirstDate) {
            weekBeginningDate.getRemainingDatesInMonth()
        } else {
            listOf()
        }.plus(
            monthFirstDate.getNextDates(monthFirstDate.month.length(monthFirstDate.isLeapYear)) +
                    monthLastDate.getRemainingDatesInWeek(DayOfWeek.SUNDAY)
        )
    }
    return array
}

fun calculateSwipeNext(startDate: LocalDate, prevData: Array<List<LocalDate>>): Array<List<LocalDate>> {
    val array = Array(3) { monthIndex ->
        when (monthIndex) {
            0 -> prevData[1].toList()
            1 -> prevData[2].toList()
            else -> {
                val monthFirstDate = startDate.plusMonths(monthIndex.toLong())
                val monthLastDate = monthFirstDate.plusMonths(1).minusDays(1)
                val weekBeginningDate = monthFirstDate.getWeekStartDate(DayOfWeek.SUNDAY)
                if (weekBeginningDate != monthFirstDate) {
                    weekBeginningDate.getRemainingDatesInMonth()
                } else {
                    listOf()
                }.plus(
                    monthFirstDate.getNextDates(monthFirstDate.month.length(monthFirstDate.isLeapYear)) +
                            monthLastDate.getRemainingDatesInWeek(DayOfWeek.SUNDAY)
                )
            }
        }
    }
    return array
}

fun calculateSwipePrev(startDate: LocalDate, prevData: Array<List<LocalDate>>): Array<List<LocalDate>> {
    val array = Array(3) { monthIndex ->
        when (monthIndex) {
            1 -> prevData[0].toList()
            2 -> prevData[1].toList()
            else -> {
                val monthFirstDate = startDate.plusMonths(monthIndex.toLong())
                val monthLastDate = monthFirstDate.plusMonths(1).minusDays(1)
                val weekBeginningDate = monthFirstDate.getWeekStartDate(DayOfWeek.SUNDAY)
                if (weekBeginningDate != monthFirstDate) {
                    weekBeginningDate.getRemainingDatesInMonth()
                } else {
                    listOf()
                }.plus(
                    monthFirstDate.getNextDates(monthFirstDate.month.length(monthFirstDate.isLeapYear)) +
                            monthLastDate.getRemainingDatesInWeek(DayOfWeek.SUNDAY)
                )
            }
        }
    }
    return array
}


internal fun LocalDate.getNextDates(count: Int): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    repeat(count) { day ->
        dates.add(this.plusDays((day).toLong()))
    }
    return dates
}

internal fun LocalDate.getWeekStartDate(weekStartDay: DayOfWeek = DayOfWeek.MONDAY): LocalDate {
    var date = this
    while (date.dayOfWeek != weekStartDay) {
        date = date.minusDays(1)
    }
    return date
}

internal fun LocalDate.getMonthStartDate(): LocalDate {
    return LocalDate.of(this.year, this.month, 1)
}

internal fun LocalDate.getRemainingDatesInWeek(weekStartDay: DayOfWeek = DayOfWeek.MONDAY): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    var date = this.plusDays(1)
    while (date.dayOfWeek != weekStartDay) {
        dates.add(date)
        date = date.plusDays(1)
    }
    return dates
}

internal fun LocalDate.getRemainingDatesInMonth(): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    repeat(this.month.length(this.isLeapYear) - this.dayOfMonth + 1) {
        dates.add(this.plusDays(it.toLong()))
    }
    return dates
}


internal fun LocalDate.yearMonth(): YearMonth = YearMonth.of(this.year, this.month)

