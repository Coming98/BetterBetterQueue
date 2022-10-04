package com.example.betterbetterqueue.logic.Dao

import androidx.room.*
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Entity.TodoCategoryInfo

@Dao
interface TodoCategoryDao {

    // - TodoCategory
    // - 插入类别信息：新建一个 category
    @Insert
    suspend fun insertTodoCategory(todoCategory: TodoCategory): Long

    // - 删除类别：删除一个 category
    @Delete
    suspend fun deleteTodoCategory(todoCategory: TodoCategory)

    // - 更改类别名称：更改某个 category 的 name 属性
    @Query("update todo_category set category=:category where id=:id")
    suspend fun updateCategoryById(category: String, id: Long)

    // - 更改类别数目：更改某个 category 的 count 属性
    @Query("update todo_category set count=count+1 where id=:id")
    suspend fun updateTodoCategoryCountById(id: Long)


    // - 获取所有类别信息：获取所有 category
    @Query("select * from todo_category")
    suspend fun getAllTodoCategory(): List<TodoCategory>

    // - 根据 id 获取类别信息：根据 category_id 获取 category 信息
    @Query("select * from todo_category where id=:id")
    suspend fun getTodoCategoryById(id: Long): TodoCategory

    // - 根据类别信息获取 todoCategory:
    // 根据 category 获取 category_id; 更改 item 对应的 category 时, 需要根据 category 找到 category_id, 让后更新 TodoCategoryInfo
    @Query("select * from todo_category where category=:category")
    suspend fun getTodoCategoryByCategory(category: String): TodoCategory

    // - TodoCategoryInfo
    // - 插入类别条目：插入 todoitem 时，插入 todoitem 与其 category 的映射
    @Insert
    suspend fun insertTodoCategoryInfo(todoCategoryInfo: TodoCategoryInfo): Long

    // - 删除类别条目：级联操作自动处理
    @Delete
    suspend fun deleteTodoCategoryInfo(todoCategoryInfo: TodoCategoryInfo)

    @Query("delete from todo_category_info where id=:todoCategoryInfoId")
    suspend fun deleteTodoCategoryInfoById(todoCategoryInfoId: Long)

    // - 更改类别条目：更改 todoitem 的 category
    @Update
    suspend fun updateCategoryInfo(todoCategoryInfo: TodoCategoryInfo)

    // - 获取某一类别下的所有条目：根据 category_id 获取所有的 todoitem
    @Query("select * from todo_category_info where category_id=:categoryId")
    suspend fun getItemIdsByCategoryId(categoryId: Long): List<TodoCategoryInfo>

    // - 获取某一条目的类别：根据 item_id 获取 category_id
    @Query("select * from todo_category_info where item_id=:itemId")
    suspend fun getCategoryIdByItemId(itemId: Long): TodoCategoryInfo

    // - 获得所有类别对应信息
    @Query("select * from todo_category_info")
    suspend fun getAllTodoCategoryInfo(): List<TodoCategoryInfo>

}