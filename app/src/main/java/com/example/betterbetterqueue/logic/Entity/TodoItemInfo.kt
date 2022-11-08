package com.example.betterbetterqueue.logic.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "todo_item_info",
        foreignKeys = arrayOf(
            ForeignKey(entity = TodoItem::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("item_id"),
                onDelete = ForeignKey.CASCADE),
        )
    )
data class TodoItemInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "item_id", index = true) val itemId: Long,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "resource_type") val resourceType: Int = TYPE_TEXT,
    @ColumnInfo(name = "begin_time") val beginTime: LocalDateTime?,
    @ColumnInfo(name = "end_time") val endTime: LocalDateTime?,
    @ColumnInfo(name = "total_time") val totalTime: Long,
) {
    companion object {
        const val TYPE_LEFT = 0
        const val TYPE_RIGHT = 1
        const val TYPE_TEXT = 2
        const val TYPE_IMAGE = 3
        const val TYPE_AUDIO = 4
    }
}