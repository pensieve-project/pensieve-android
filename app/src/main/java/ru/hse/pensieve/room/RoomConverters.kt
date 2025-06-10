package ru.hse.pensieve.room

import androidx.room.TypeConverter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.hse.pensieve.posts.models.Point
import java.time.Instant
import java.util.*

class RoomConverters {
    private val objectMapper = jacksonObjectMapper()

    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun pointToJson(point: Point?): String? {
        return point?.let { objectMapper.writeValueAsString(it) }
    }

    @TypeConverter
    fun jsonToPoint(json: String?): Point? {
        return json?.let { objectMapper.readValue(it) }
    }

    @TypeConverter
    fun uuidSetToJson(set: Set<UUID>?): String? {
        return set?.let { objectMapper.writeValueAsString(it) }
    }

    @TypeConverter
    fun jsonToUuidSet(json: String?): Set<UUID>? {
        return json?.let {
            objectMapper.readValue<Set<String>>(it).map { UUID.fromString(it) }.toSet()
        }
    }
}