package com.example.betterbetterqueue.logic.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "todo_item",
    // foreignKeys = arrayOf(
    //     ForeignKey(entity = TodoCategoryInfo::class,
    //         parentColumns = arrayOf("item_id"),
    //         childColumns = arrayOf("id"),
    //         onDelete = ForeignKey.CASCADE),
    // ),
)
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "create_time") val createTime: LocalDateTime,
    @ColumnInfo(name = "total_time") val totalTime: Long,
    @ColumnInfo(name = "top_time") val topTime: Long = 0
)
