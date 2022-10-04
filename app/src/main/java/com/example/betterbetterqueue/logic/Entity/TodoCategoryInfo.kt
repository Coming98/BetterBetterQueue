package com.example.betterbetterqueue.logic.Entity

import androidx.room.*

@Entity(
    tableName = "todo_category_info",
    foreignKeys = arrayOf(
        ForeignKey(entity = TodoCategory::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("category_id"),
        onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TodoItem::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("item_id"),
        onDelete = ForeignKey.CASCADE),
    ),
    indices = [Index(value = ["item_id"], unique = true)] // 对 item_id 列创建索引, 一个 item 只能对应一个 category
)
data class TodoCategoryInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "category_id", index = true) val categoryId: Long,
    @ColumnInfo(name = "item_id") val itemId:Long
)
