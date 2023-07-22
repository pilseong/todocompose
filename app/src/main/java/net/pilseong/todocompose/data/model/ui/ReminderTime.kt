package net.pilseong.todocompose.data.model.ui

import net.pilseong.todocompose.R

enum class ReminderTime(val label: Int, val timeInMillis: Long) {
    NOT_USED(label = R.string.reminder_no_reminder, timeInMillis = 0L),
    ON_TIME(label = R.string.reminder_on_time, timeInMillis = 0L),
    TEN_MIN(label = R.string.reminder_10minutes, timeInMillis = 10 * 60 * 1000L),
    HALF_AN_HOUR(label = R.string.reminder_30minutes, timeInMillis = 30 * 60 * 1000L),
    AN_HOUR(label = R.string.reminder_an_hour, timeInMillis = 60 * 60 * 1000L),
    SIX_HOURS(label = R.string.reminder_six_hours, timeInMillis = 6 * 60 * 60 * 1000L),
    TWELVE_HOURS(label = R.string.reminder_twelve_hours, timeInMillis = 12 * 60 * 60 * 60 * 1000L),
    A_DAY(label = R.string.reminder_a_day, timeInMillis = 24 * 60 * 60 * 60 * 1000L),
    TWO_DAYS(label = R.string.reminder_two_days, timeInMillis = 48 * 60 * 60 * 60 * 1000L),
}