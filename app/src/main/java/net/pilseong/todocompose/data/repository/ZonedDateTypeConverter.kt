package net.pilseong.todocompose.data.repository

import androidx.room.TypeConverter

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTypeConverter {
    companion object {

        @TypeConverter
        @JvmStatic
        fun toZonedDateTime(value: Long?): ZonedDateTime? {
            if (value != null) {
                val instant = Instant.ofEpochSecond(value)
                return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
            } else {
                return null
            }
        }

        @TypeConverter
        @JvmStatic
        fun fromZonedDateTime(date: ZonedDateTime?): Long? = date?.toEpochSecond()
    }
}