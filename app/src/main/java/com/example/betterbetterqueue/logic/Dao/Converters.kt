package com.example.betterbetterqueue.logic.Dao

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it/1000, 0, ZoneOffset.ofHours(8)) }
    }

    @TypeConverter
    fun dateToTimestamp(localDateTime: LocalDateTime?): Long? {
        return localDateTime?.let { it.toInstant(ZoneOffset.of("+8")).toEpochMilli() }
    }
}