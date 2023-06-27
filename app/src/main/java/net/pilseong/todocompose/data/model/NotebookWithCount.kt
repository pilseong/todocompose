package net.pilseong.todocompose.data.model

import androidx.room.ColumnInfo
import java.time.ZonedDateTime

data class NotebookWithCount(
    val id: Int = 0,
    val title: String,
    val description: String,
    val priority: Priority,
    @ColumnInfo(name = "created_at")
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now(),
    @ColumnInfo(name = "accessed_at", defaultValue = "0")
    val accessedAt: ZonedDateTime = ZonedDateTime.now(),
    val memoTotalCount: Int = 0,
    val completedCount: Int = 0,
    val activeCount: Int = 0,
    val suspendedCount: Int = 0,
    val waitingCount: Int = 0,
    val noneCount: Int = 0,
    val highPriorityCount: Int = 0,
    val mediumPriorityCount: Int = 0,
    val lowPriorityCount: Int = 0,
    val nonePriorityCount: Int = 0,
) {
    companion object {
        fun instance(
            id: Int = 0,
            title: String = "",
            description: String = "",
            priority: Priority = Priority.NONE,
            createdAt: ZonedDateTime = ZonedDateTime.now(),
            updatedAt: ZonedDateTime = ZonedDateTime.now(),
            accessedAt: ZonedDateTime = ZonedDateTime.now(),
            memoTotalCount: Int = 0,
        ): NotebookWithCount {
            return NotebookWithCount(
                id = id,
                title = title, description = description, priority = priority,
                createdAt = createdAt, updatedAt = updatedAt, accessedAt = accessedAt,
                memoTotalCount = memoTotalCount
            )
        }
    }

}
