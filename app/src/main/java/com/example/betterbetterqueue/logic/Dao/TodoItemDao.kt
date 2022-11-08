package com.example.betterbetterqueue.logic.Dao

import androidx.room.*
import com.example.betterbetterqueue.logic.Entity.TodoItem
import com.example.betterbetterqueue.logic.Entity.TodoItemInfo

@Dao
interface TodoItemDao {
    // **TodoItem**
    // - 插入一个 Item
    @Insert
    suspend fun insertTodoItem(todoItem: TodoItem): Long

    // - 删除一个 Item
    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItem)

    @Query("delete from todo_item where id=:todoItemId")
    suspend fun deleteTodoItemById(todoItemId: Long)

    //- 更新 item 的名称
    @Query("update todo_item set name=:name where id=:id")
    suspend fun updateTodoItemNameById(name: String, id:Long)

    @Query("update todo_item set top_time=:topTime where id=:id")
    suspend fun updateTodoItemToptimeById(topTime: Long, id: Long)

    //- 更新 item 的 totalTime
    @Query("update todo_item set total_time=total_time+:time where id=:id")
    suspend fun updateTodoItemTotalTimeById(time: Long, id:Long)

    // - 根据 id 查找一个 item
    @Query("select * from todo_item where id=:id")
    suspend fun getTodoItemById(id: Long): TodoItem

    // - 获取所有的 TodoItem
    @Query("select * from todo_item order by top_time desc, create_time desc")
    suspend fun getAllTodoItem(): List<TodoItem>


    // - 根据 CategoryId 查找所有 Item
    @Query("select * from todo_item where id in (select item_id from todo_category_info where category_id=:categoryId) order by top_time desc, create_time desc")
    suspend fun getTodoItemsByCategoryId(categoryId: Long): List<TodoItem>

    // - 删除所有的 TodoItem
    @Query("delete from todo_item")
    suspend fun deleteAllTodoItem()

    // **TodoItemInfo**
    // - 插入一条 ItemInfo
    @Insert
    suspend fun insertTodoItemInfo(todoItemInfo: TodoItemInfo): Long

    //- 更新 ItemInfo 的 description
    @Query("update todo_item_info set description=:description where id=:id")
    suspend fun updateTodoItemInfoDesById(description: String, id: Long)

    // - 根据 itemId 查找所有的 ItemInfo
    @Query("select * from todo_item_info where item_id=:itemId order by begin_time desc")
    suspend fun getTodoItemInfosByItemId(itemId: Long): List<TodoItemInfo>

    // - 根据时间范围筛选 ItemInfo
    @Query("select * from todo_item_info where begin_time between :minTime and :maxTime")
    suspend fun getTodoItemInfoByTimeScope(minTime: Long, maxTime: Long): List<TodoItemInfo>

    // - 获取所有的 TodoItemInfo
    @Query("select * from todo_item_info order by begin_time asc")
    suspend fun getAllTodoItemInfo(): List<TodoItemInfo>

    // - 删除所有的  TodoItemInfo
    @Query("delete from todo_item_info")
    suspend fun deleteAllTodoItemInfo()

}