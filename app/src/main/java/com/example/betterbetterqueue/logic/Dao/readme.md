
# TodoCategoryDao

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

# TodoItemDao

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