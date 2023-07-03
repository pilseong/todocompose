package net.pilseong.todocompose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.pilseong.todocompose.util.Constants.PHOTO_TABLE
import java.time.ZonedDateTime

@Entity(tableName = PHOTO_TABLE)
data class Photo  constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uri: String,
    val filename: String,
    val memoId: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now(),
)
