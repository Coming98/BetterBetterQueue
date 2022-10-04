package com.example.betterbetterqueue.logic.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "todo_item")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "create_time") val createTime: LocalDateTime?,
    @ColumnInfo(name = "total_time") val totalTime: Long,
)
