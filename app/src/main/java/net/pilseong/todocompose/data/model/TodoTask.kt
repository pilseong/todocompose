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
data class TodoTask @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val priority: Priority,
    @ColumnInfo(name = "favorite", defaultValue = "false")
    val favorite: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now()
) {

    companion object {
        @JvmStatic fun instance(): TodoTask {
            return TodoTask(
                id = NEW_ITEM_ID,
                title = "",
                description = "",
                priority = Priority.NONE,
                favorite = false,
                createdAt = ZonedDateTime.now(),
                updatedAt = ZonedDateTime.now()
            )
        }
    }
}
