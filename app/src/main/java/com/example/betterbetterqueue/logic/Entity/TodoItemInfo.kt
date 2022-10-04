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
    @ColumnInfo(name = "begin_time") val beginTime: LocalDateTime?,
    @ColumnInfo(name = "end_time") val endTime: LocalDateTime?,
    @ColumnInfo(name = "total_time") val TotalTime: Long
)