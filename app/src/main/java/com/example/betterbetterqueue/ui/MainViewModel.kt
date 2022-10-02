package com.example.betterbetterqueue.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Repository

class MainViewModel: ViewModel() {

    data class UpdateCategoryByIdTuple(val category: String, val id: Long){}

    private val insertTodoCategoryObs = MutableLiveData<TodoCategory>()
    private val deleteTodoCategoryObs = MutableLiveData<TodoCategory>()
    private val updateCategoryByIdObs = MutableLiveData<UpdateCategoryByIdTuple>()
    private val getAllTodoCategoryObs = MutableLiveData<Any?>()
    private val getTodoCategoryByIdObs = MutableLiveData<Long>()
    private val getTodoCategoryByCategoryObs = MutableLiveData<String>()

    val todoCategoryList = ArrayList<TodoCategory>() // 对界面上展示的 TodoCategory 进行缓存

    val insertTodoCategoryResult = Transformations.switchMap(insertTodoCategoryObs) { todoCategory ->
        Repository.insertTodoCategory(todoCategory)
    }
    val deleteTodoCategoryResult = Transformations.switchMap(deleteTodoCategoryObs) { todoCategory ->
        Repository.deleteTodoCategory(todoCategory)
    }
    val updateCategoryByIdResult = Transformations.switchMap(updateCategoryByIdObs) { updateCategoryByIdTuple ->
        Repository.updateCategoryById(updateCategoryByIdTuple.category, updateCategoryByIdTuple.id)
    }
    val getAllTodoCategoryResult = Transformations.switchMap(getAllTodoCategoryObs) {
        Repository.getAllTodoCategory()
    }
    val getTodoCategoryByIdResult = Transformations.switchMap(getTodoCategoryByIdObs) { id ->
        Repository.getTodoCategoryById(id)
    }
    val getTodoCategoryByCategoryResult = Transformations.switchMap(getTodoCategoryByCategoryObs) { category ->
        Repository.getTodoCategoryByCategory(category)
    }

    fun insertTodoCategory(todoCategory: TodoCategory) {
        insertTodoCategoryObs.value = todoCategory
    }

    fun deleteTodoCategory(todoCategory: TodoCategory) {
        deleteTodoCategoryObs.value = todoCategory
    }

    fun updateCategoryById(category: String, id: Long) {
        updateCategoryByIdObs.value = UpdateCategoryByIdTuple(category, id)
    }

    fun getAllTodoCategory() {
        getAllTodoCategoryObs.value = getAllTodoCategoryObs.value
    }

    fun getTodoCategoryById(id: Long) {
        getTodoCategoryByIdObs.value = id
    }

    fun getTodoCategoryByCategory(category: String) {
        getTodoCategoryByCategoryObs.value = category
    }

}