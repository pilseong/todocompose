package net.pilseong.todocompose.data.repository

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTypeConverter {
    companion object {

        @TypeConverter
        @JvmStatic
        fun toZonedDateTime(value: Long): ZonedDateTime {
            val instant = Instant.ofEpochSecond(value)
            return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
        }

        @TypeConverter
        @JvmStatic
        fun fromZonedDateTime(date: ZonedDateTime): Long = date.toEpochSecond()
    }
}