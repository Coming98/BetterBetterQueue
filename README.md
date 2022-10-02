# BetterBetterQueue

BBQ, BetterBetterQuere. An open-source cultivation software for managing daily affairs and recording growth.

# Functional Requirement & Tools

- Add TodoItem
  - Props: name, beginTime, 


# Description

## logic

存放业务逻辑相关的代码

- Entity: 数据模型
- dao: 数据访问接口
- model: 网络对象模型
- network: 网络数据访问接口
- Repository: UI 与 Logic 的中间层，总控数据访问接口

## ui

- [x] 主界面标题栏: 显示当前类别 + 打开侧拉菜单
- [x] 主界面内容展示：使用 RecyclerView + MaterialCardView 实现卡片式 TodoItem 布局
- 悬浮按钮：添加新的 TodoItem（可以选择或新建类别）
- 滑动菜单：选择类别
- TodoItem 详细内容展示
- [ ] TodoItem 界面标题栏：显示当前 TodoItem.name + 返回主界面

存放界面展示相关的代码

- TodoItem: 
- TodoItemInfo:
- TodoCategory



# Database

## Entity

root_dir = logic/dao/

### TodoCategory

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

### TodoCategoryInfo

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

### TodoItem

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

### TodoItemInfo

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

## Dao

### TodoCategoryDao

对 TodoCategory 相关的操作提供接口

**TodoCategory**
- 插入类别信息：新建一个 category

```kotlin
@Insert
suspend fun insertCategory(todoCategory: TodoCategory)
```

- 删除类别：删除一个 category

```kotlin
@Delete
suspend fun deleteCategory(todoCategory: TodoCategory)
```

- 更改类别名称：更改某个 category 的 name 属性

```kotlin
@Query("update todo_category set category=:category where id=:id")
suspend fun updateCategoryById(category: String, id: Long)
```

- 获取所有类别信息：获取所有 category 

```kotlin
@Query("select * from todo_category")
suspend fun getAll(): List<TodoCategory>
```

- 根据 id 获取类别信息：根据 category_id 获取 category 信息

```kotlin
@Query("select * from todo_category where id = :id")
suspend fun getTodoCategoryById(id: Long): TodoCategory
```

- 根据类别信息获取 id: 根据 category 获取 category_id; 更改 item 对应的 category 时, 需要根据 category 找到 category_id, 让后更新 TodoCategoryInfo

```kotlin
@Query("select * from todo_category where category=:category")
suspend fun getTodoCategoryByCategory(category: String): TodoCategory
```



TodoCategoryInfo
- 插入类别条目：插入 todoitem 时，插入 todoitem 与其 category 的映射

```kotlin
@Insert
suspend fun insertCategoryInfo(todoCategoryInfo: TodoCategoryInfo)
```

- 删除类别条目：级联操作自动处理

```kotlin
@Delete
suspend fun deleteCategoryInfo(todoCategoryInfo: TodoCategoryInfo)
```

- 更改类别条目：更改 todoitem 的 category

```kotlin
@Update
suspend fun updateCategoryInfo(todoCategoryInfo: TodoCategoryInfo)
```

- 获取某一类别下的所有条目：根据 category_id 获取所有的 todoitem

```kotlin
@Query("select * from todo_category_info where category_id=:categoryId")
suspend fun getItemIdsByCategoryId(categoryId: Long): List<TodoCategoryInfo>
```

- 获取某一条目的类别：根据 item_id 获取 category_id

```kotlin
@Query("select * from todo_category_info where item_id=:itemId")
suspend fun getCategoryIdByItemId(itemId: Long): TodoCategoryInfo
```

### TodoItemDao

**TodoItem**
- 插入一个 Item

```kotlin
@Insert
suspend fun insertTodoItem(todoItem: TodoItem)
```

- 删除一个 Item

```kotlin
@Delete
suspend fun deleteTodoItem(todoItem: TodoItem)
```

- 更新 item 的名称

```kotlin
@Query("update todo_item set name=:name where id=:id")
suspend fun updateTodoItemNameById(name: String, id:Long)
```

- 根据 id 查找一个 item

```kotlin
@Query("select * from todo_item where id=:id")
    suspend fun getTodoItemById(id: Long): TodoItem
```


**TodoItemInfo**
- 插入一条 ItemInfo

```kotlin
@Insert
suspend fun insertTodoItemInfo(todoItemInfo: TodoItemInfo)
```

- 更新 ItemInfo 的 description

```kotlin
@Query("update todo_item_info set description=:description where id=:id")
suspend fun updateTodoItemInfoDesById(description: String, id: Long)
```

- 根据 itemId 查找所有的 ItemInfo

```kotlin
@Query("select * from todo_item_info where item_id=:itemId")
suspend fun getTodoItemInfosByItemId(itemId: Long): List<TodoItemInfo>
```