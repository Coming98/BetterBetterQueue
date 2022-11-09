package com.example.betterbetterqueue.ui.TodoItem

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Repository

// Done
class TodoItemViewModel: ViewModel() {

    val todoCategoryList = ArrayList<TodoCategory>() // 界面上展示的 TodoCategoryList

    private val getAllTodoCategoryObs = MutableLiveData<Any?>()

    val getAllTodoCategoryResult = Transformations.switchMap(getAllTodoCategoryObs) {
        Repository.getAllTodoCategory()
    }

    fun getAllTodoCategory() {
        getAllTodoCategoryObs.value = getAllTodoCategoryObs.value
    }
}