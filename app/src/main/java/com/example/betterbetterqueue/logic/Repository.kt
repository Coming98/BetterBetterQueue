package com.example.betterbetterqueue.logic

import androidx.lifecycle.liveData
import com.example.betterbetterqueue.TodoApplication
import com.example.betterbetterqueue.logic.Dao.TodoDatabase
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Entity.TodoCategoryInfo
import com.example.betterbetterqueue.logic.Entity.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {

    val todoDatabase: TodoDatabase = TodoDatabase.getDatabase(TodoApplication.context)

    ////////////////////////////// TodoItem

    fun insertTodoItem(todoItem: TodoItem, todoItemCategory: String, todoItemCategoryId: Long) = fire(Dispatchers.IO) {
        coroutineScope {
            // 插入 todoItem
            val insertedTodoItemId_ = async {
                todoDatabase.todoItemDao().insertTodoItem(todoItem)
            }

            var insertedTodoCategoryInfoId_: Any? = null
            if(todoItemCategoryId == -1L) {
                // 插入 todoCategory
                val insertedTodoCategoryId_ =
                    async {
                        todoDatabase.todoCategoryDao().insertTodoCategory(TodoCategory(category = todoItemCategory, createTime = todoItem.createTime, count = 1))
                    }
                // 插入 todoCategoryInfo
                val insertedTodoItemId: Long = insertedTodoItemId_.await()
                val insertedTodoCategoryId: Long = insertedTodoCategoryId_.await()
                insertedTodoCategoryInfoId_ = async {
                    todoDatabase.todoCategoryDao().insertTodoCategoryInfo(TodoCategoryInfo(categoryId = insertedTodoCategoryId, itemId = insertedTodoItemId))
                }
            } else {
                // 更新 todoCategory 的 Count
                val updateTodoCategoryCountById = async {
                    todoDatabase.todoCategoryDao().updateTodoCategoryCountById(todoItemCategoryId)
                }
                // 插入 todoCategoryInfo
                val insertedTodoItemId: Long = insertedTodoItemId_.await()
                insertedTodoCategoryInfoId_ = async {
                    todoDatabase.todoCategoryDao().insertTodoCategoryInfo(TodoCategoryInfo(categoryId = todoItemCategoryId, itemId = insertedTodoItemId))
                }
            }
            val insertedTodoCategoryInfoId = insertedTodoCategoryInfoId_.await()
            Result.success("Insert TodoItem(${todoItem.name} Success.")
        }
    }

    fun getAllTodoItem() = fire(Dispatchers.IO) {
        val todoItemList: List<TodoItem> = todoDatabase.todoItemDao().getAllTodoItem()
        Result.success(todoItemList)
    }

    fun refreshTodoItemByCategory(todoCategoryId: Long) = fire(Dispatchers.IO) {
        if(todoCategoryId == -1L) {
            val todoItemList = todoDatabase.todoItemDao().getAllTodoItem()
            Result.success(Pair("星海", todoItemList))
        } else {
            // 获取类别名词 - 用于标题栏展示
            coroutineScope {
                val todoCategory_ = async {
                    todoDatabase.todoCategoryDao().getTodoCategoryById(todoCategoryId)
                }
                val todoItemList_ = async {
                    todoDatabase.todoItemDao().getTodoItemsByCategoryId(todoCategoryId)
                }
                val todoCategoryName: String = todoCategory_.await().category
                val todoItemList = todoItemList_.await()
                Result.success(Pair(todoCategoryName, todoItemList))
            }
        }
    }

    ////////////////////////////// TodoCategory

    fun getAllTodoCategory() = fire(Dispatchers.IO) {
        val todoCategories: List<TodoCategory> = todoDatabase.todoCategoryDao().getAllTodoCategory()
        Result.success(todoCategories)
    }

    ////////////////////////////// TodoCategoryInfo
    fun getAllTodoCategoryInfo() = fire(Dispatchers.IO) {
        val todoCategoryInfoList: List<TodoCategoryInfo> = todoDatabase.todoCategoryDao().getAllTodoCategoryInfo()
        Result.success(todoCategoryInfoList)
    }

    fun insertTodoCategoryInfo(todoCategoryInfo: TodoCategoryInfo) = fire(Dispatchers.IO) {
        val insertedTodoCategoryInfoId: Long = todoDatabase.todoCategoryDao().insertTodoCategoryInfo(todoCategoryInfo)
        Result.success(insertedTodoCategoryInfoId)
    }

    fun deleteTodoCategoryInfoById(todoCategoryInfoId: Long) = fire(Dispatchers.IO) {
        todoDatabase.todoCategoryDao().deleteTodoCategoryInfoById(todoCategoryInfoId)
        Result.success("Delete Success ${todoCategoryInfoId}")
    }


    // fun insertTodoCategory(todoCategory: TodoCategory) = fire(Dispatchers.IO) {
    //     todoDatabase.todoCategoryDao().insertTodoCategory(todoCategory)
    //     Result.success("Insert TodoCategory(${todoCategory.category}) Success.")
    // }
    //
    // fun deleteTodoCategory(todoCategory: TodoCategory) = fire(Dispatchers.IO) {
    //     todoDatabase.todoCategoryDao().deleteTodoCategory(todoCategory)
    //     Result.success("Delete TodoCategory(${todoCategory.category}) Success.")
    // }
    //
    // fun updateCategoryById(category: String, id: Long) = fire(Dispatchers.IO) {
    //     todoDatabase.todoCategoryDao().updateCategoryById(category, id)
    //     Result.success("Update TodoCategory's category name(${category}) Success.")
    // }
    //
    // fun getTodoCategoryById(id: Long) = fire(Dispatchers.IO) {
    //     val todoCategory = todoDatabase.todoCategoryDao().getTodoCategoryById(id)
    //     Result.success(todoCategory)
    // }
    //
    // fun getTodoCategoryByCategory(category: String) = fire(Dispatchers.IO) {
    //     val todoCategory = todoDatabase.todoCategoryDao().getTodoCategoryByCategory(category)
    //     Result.success(todoCategory)
    // }

    // 统一的入口函数中进行封装，使得只要进行一次 try catch 处理就行了
    private fun <T> fire(
        context: CoroutineContext,
        block: suspend () -> Result<T>
    ) = liveData<Result<T>>(context){
        val result = try {
            block()
        } catch (e: Exception) {
            Result.failure<T>(e)
        }
        emit(result)
    }

}