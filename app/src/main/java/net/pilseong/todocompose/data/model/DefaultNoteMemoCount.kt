package net.pilseong.todocompose.data.model

data class DefaultNoteMemoCount(
    val total: Int = 0,
    val high: Int = 0,
    val medium: Int = 0,
    val low: Int = 0,
    val none: Int = 0,
    val completed: Int = 0,
    val cancelled: Int = 0,
    val active: Int = 0,
    val suspended: Int = 0,
    val waiting: Int = 0,
    val not_assigned: Int = 0,
)
