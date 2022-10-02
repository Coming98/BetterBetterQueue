package com.example.betterbetterqueue.logic

import androidx.lifecycle.liveData
import com.example.betterbetterqueue.TodoApplication
import com.example.betterbetterqueue.logic.Dao.TodoDatabase
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object Repository {

    val todoDatabase: TodoDatabase = TodoDatabase.getDatabase(TodoApplication.context)

    fun insertTodoCategory(todoCategory: TodoCategory) = fire(Dispatchers.IO) {
        todoDatabase.todoCategoryDao().insertTodoCategory(todoCategory)
        Result.success("Insert TodoCategory(${todoCategory.category}) Success.")
    }

    fun deleteTodoCategory(todoCategory: TodoCategory) = fire(Dispatchers.IO) {
        todoDatabase.todoCategoryDao().deleteTodoCategory(todoCategory)
        Result.success("Delete TodoCategory(${todoCategory.category}) Success.")
    }

    fun updateCategoryById(category: String, id: Long) = fire(Dispatchers.IO) {
        todoDatabase.todoCategoryDao().updateCategoryById(category, id)
        Result.success("Update TodoCategory's category name(${category}) Success.")
    }

    fun getAllTodoCategory() = fire(Dispatchers.IO) {
        val todoCategories: List<TodoCategory> = todoDatabase.todoCategoryDao().getAllTodoCategory()
        Result.success(todoCategories)
    }

    fun getTodoCategoryById(id: Long) = fire(Dispatchers.IO) {
        val todoCategory = todoDatabase.todoCategoryDao().getTodoCategoryById(id)
        Result.success(todoCategory)
    }

    fun getTodoCategoryByCategory(category: String) = fire(Dispatchers.IO) {
        val todoCategory = todoDatabase.todoCategoryDao().getTodoCategoryByCategory(category)
        Result.success(todoCategory)
    }

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