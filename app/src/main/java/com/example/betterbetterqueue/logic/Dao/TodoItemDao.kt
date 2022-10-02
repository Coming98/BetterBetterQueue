package com.example.betterbetterqueue.logic.Dao

import androidx.room.*
import com.example.betterbetterqueue.logic.Entity.TodoItem
import com.example.betterbetterqueue.logic.Entity.TodoItemInfo

@Dao
interface TodoItemDao {
    // **TodoItem**
    // - 插入一个 Item
    @Insert
    suspend fun insertTodoItem(todoItem: TodoItem)

    // - 删除一个 Item
    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItem)

    //- 更新 item 的名称
    @Query("update todo_item set name=:name where id=:id")
    suspend fun updateTodoItemNameById(name: String, id:Long)

    // - 根据 id 查找一个 item
    @Query("select * from todo_item where id=:id")
    suspend fun getTodoItemById(id: Long): TodoItem

    // **TodoItemInfo**
    // - 插入一条 ItemInfo
    @Insert
    suspend fun insertTodoItemInfo(todoItemInfo: TodoItemInfo)

    //- 更新 ItemInfo 的 description
    @Query("update todo_item_info set description=:description where id=:id")
    suspend fun updateTodoItemInfoDesById(description: String, id: Long)

    // - 根据 itemId 查找所有的 ItemInfo
    @Query("select * from todo_item_info where item_id=:itemId")
    suspend fun getTodoItemInfosByItemId(itemId: Long): List<TodoItemInfo>
}