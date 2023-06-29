package net.pilseong.todocompose.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.pilseong.todocompose.util.Constants.MEMO_TABLE
import net.pilseong.todocompose.util.Constants.NEW_ITEM_ID
import java.time.ZonedDateTime

@Entity(tableName = MEMO_TABLE)
data class MemoTask @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
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
    val finishedAt: ZonedDateTime? = null,
    @ColumnInfo(name = "due_date", defaultValue = "-1")
    val dueDate: ZonedDateTime? = null,
    @ColumnInfo(name = "notebook_id", defaultValue = "-1")
    val notebookId: Int,
    @ColumnInfo(name = "deleted", defaultValue = "0")
    val deleted: Boolean = false,
) {

    companion object {
        @JvmStatic fun instance(notebookId: Int = -1): MemoTask {
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
                notebookId = notebookId
            )
        }
    }
}
