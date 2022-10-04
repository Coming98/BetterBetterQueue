package com.example.betterbetterqueue.logic.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(
    tableName = "todo_category",
    indices = [Index(value = ["category"], unique = true)]
) // 对 category 列创建索引, 防止类名重复
data class TodoCategory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    @ColumnInfo(name = "create_time") val createTime: LocalDateTime?,
    val count: Int
)
