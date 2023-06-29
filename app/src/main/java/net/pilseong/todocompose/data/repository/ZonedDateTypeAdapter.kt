package net.pilseong.todocompose.data.repository

import androidx.room.TypeConverter
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTypeAdapter: JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
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

    override fun serialize(
        src: ZonedDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(fromZonedDateTime(src))
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ZonedDateTime? {
        return toZonedDateTime(json.toString().toLong())
    }
}