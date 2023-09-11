package net.pilseong.todocompose.data.model.ui

import net.pilseong.todocompose.R

enum class DateRangeFilterOption(val label: Int) {
    ALL(R.string.date_range_filter_all),
    TODAY(R.string.date_range_filter_today),
    THREE_DAY(R.string.date_range_filter_three_days),
    WEEK(R.string.date_range_filter_week),
    TWO_WEEK(R.string.date_range_filter_two_weeks),
    MONTH(R.string.date_range_filter_month),
    THIS_MONTH(R.string.date_range_filter_this_month),
    THREE_MONTH(R.string.date_range_filter_three_months),
    CUSTOM(R.string.date_range_filter_custom)
}