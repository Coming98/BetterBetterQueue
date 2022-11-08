# Entity

root_dir = logic/Entity

# TodoCategory

TodoCategory 表, 存放着 TodoItem 的类别信息
- columns: id, category, createTime, count(类别对应的 TodoItem 的个数 [TODO 应该可以优化])

```kotlin
@Entity(
    tableName = "todo_category",
    // 对 category 列创建索引, 防止类名重复
    indices = [Index(value = ["category"], unique = true)] 
) 
data class TodoCategory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val category: String,
    @ColumnInfo(name = "create_time") val createTime: Date,
    val count: Int
)
```

# TodoCategoryInfo

TodoCategoryInfo 表, 存放着 Category 与 Item 的映射关系
- columns: id, category_id, item_id
- 设置好外键, 以应用级联操作

```kotlin
@Entity(
    tableName = "TodoCategoryInfo",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = TodoCategory::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("category_id"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TodoItem::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("item_id"),
            onDelete = ForeignKey.CASCADE
        ),
    )
)
data class TodoCategoryInfo(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "category_id", index = true) val categoryId: Long,
    @ColumnInfo(name = "item_id") val itemId:Long
)
```

# TodoItem

TodoItem 表, 存放着 TodoItem 的基本信息
- columns: id, name, createTime, totalTime(单位为 s, [TODO 可能通过数据库相关技术进行优化维护])

```kotlin
@Entity(tableName = "TodoItem")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "create_time") val createTime: Date,
    @ColumnInfo(name = "total_time") val totalTime: Long,
)
```

# TodoItemInfo

TodoItemInfo 表, 存放着 TodoItem 的详细记录信息
- columns: id, item_id, description, begin_time, end_time, total_time(单位 s)

```kotlin
@Entity(
    tableName = "TodoItemInfo",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = TodoItem::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("item_id"),
            onDelete = ForeignKey.CASCADE
        ),
    )
)
data class TodoItemInfo(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "item_id", index = true) val itemId: Long,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "begin_time") val beginTime: Date,
    @ColumnInfo(name = "end_time") val endTime: Date,
    @ColumnInfo(name = "total_time") val TotalTime: Long
)
```