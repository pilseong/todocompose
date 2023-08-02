package net.pilseong.todocompose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.ReminderType
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.util.Constants.MEMO_TABLE
import net.pilseong.todocompose.util.Constants.NEW_ITEM_ID
import java.time.ZonedDateTime

@Entity(tableName = MEMO_TABLE)
data class MemoTask constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val priority: Priority,
    @ColumnInfo(name = "favorite", defaultValue = "false")
    var favorite: Boolean = false,
    @ColumnInfo(name = "progression", defaultValue = "0")
    var progression: State = State.NONE,
    @ColumnInfo(name = "created_at")
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now(),
    @ColumnInfo(name = "completed_at", defaultValue = "-1")
    var finishedAt: ZonedDateTime? = null,
    @ColumnInfo(name = "due_date", defaultValue = "-1")
    val dueDate: ZonedDateTime? = null,
    @ColumnInfo(name = "notebook_id", defaultValue = "-1")
    val notebookId: Long,
    @ColumnInfo(name = "deleted", defaultValue = "0")
    val deleted: Boolean = false,
    @ColumnInfo(name = "reminder_type", defaultValue = "NOT_USED")
    val reminderType: ReminderType = ReminderType.NOT_USED,
    @ColumnInfo(name = "reminder_offset", defaultValue = "-1")
    val reminderOffset: Long? = null,


    ) {

    companion object {
        @JvmStatic fun instance(notebookId: Long = -1): MemoTask {
            return MemoTask(
                id = NEW_ITEM_ID,
                title = "",
                description = "",
                priority = Priority.NONE,
                favorite = false,
                progression = State.NONE,
                createdAt = ZonedDateTime.now(),
                updatedAt = ZonedDateTime.now(),
                finishedAt = null,
                dueDate = null,
                reminderType = ReminderType.NOT_USED,
                reminderOffset = null,
                notebookId = notebookId
            )
        }
    }
}
