package com.example.betterbetterqueue.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Entity.TodoCategoryInfo
import com.example.betterbetterqueue.logic.Entity.TodoItem
import com.example.betterbetterqueue.logic.Repository

class MainViewModel: ViewModel() {

    ////////////////////////////////// TodoItem(MainActivity)

    val todoItemList = ArrayList<TodoItem>() // 对界面上展示的 TodoItemList 进行缓存

    data class InsertTodoItemTriple(val todoItem: TodoItem, val todoItemCategory: String, val todoItemCategoryId: Long)

    private val insertTodoItemObs = MutableLiveData<InsertTodoItemTriple>()
    private val getAllTodoItemObs = MutableLiveData<Any?>()
    private val refreshTodoItemByCategoryObs = MutableLiveData<Long>()

    val insertTodoItemResult = Transformations.switchMap(insertTodoItemObs) { insertTodoItemTriple ->
        Repository.insertTodoItem(insertTodoItemTriple.todoItem, insertTodoItemTriple.todoItemCategory, insertTodoItemTriple.todoItemCategoryId)
    }

    val getAllTodoItemResult = Transformations.switchMap(getAllTodoItemObs) {
        Repository.getAllTodoItem()
    }
    val refreshTodoItemByCategoryResult = Transformations.switchMap(refreshTodoItemByCategoryObs) { todoCategoryId ->
        Repository.refreshTodoItemByCategory(todoCategoryId)
    }

    fun insertTodoItem(todoItem: TodoItem, todoItemCategory: String, todoItemCategoryId: Long) {
        insertTodoItemObs.value = InsertTodoItemTriple(todoItem, todoItemCategory, todoItemCategoryId)
    }

    fun getAllTodoItem() {
        getAllTodoItemObs.value = getAllTodoCategoryObs.value
    }

    fun refreshTodoItemByCategory(todoCategoryId: Long) {
        refreshTodoItemByCategoryObs.value = todoCategoryId
    }

    ////////////////////////////////// TodoCategory(侧边栏)
    val todoCategoryList = ArrayList<TodoCategory>() // 对界面上展示的 TodoCategoryList 进行缓存
    val todoCategoryId = MutableLiveData<Long>(-1)

    private val getAllTodoCategoryObs = MutableLiveData<Any?>()

    val getAllTodoCategoryResult = Transformations.switchMap(getAllTodoCategoryObs) {
        Repository.getAllTodoCategory()
    }

    fun getAllTodoCategory() {
        getAllTodoCategoryObs.value = getAllTodoCategoryObs.value
    }

    ////////////////////////////// TodoCategoryInfo
    private val getAllTodoCategoryInfoObs = MutableLiveData<Any?>()
    private val insertTodoCategoryInfoObs = MutableLiveData<TodoCategoryInfo>()
    private val deleteTodoCategoryInfoByIdObs = MutableLiveData<Long>()

    val getAllTodoCategoryInfoResult = Transformations.switchMap(getAllTodoCategoryInfoObs) {
        Repository.getAllTodoCategoryInfo()
    }

    val insertTodoCategoryInfoResult = Transformations.switchMap(insertTodoCategoryInfoObs) { todoCategoryInfo ->
        Repository.insertTodoCategoryInfo(todoCategoryInfo)
    }

    val deleteTodoCategoryInfoByIdResult = Transformations.switchMap(deleteTodoCategoryInfoByIdObs) { todoCategoryInfoId ->
        Repository.deleteTodoCategoryInfoById(todoCategoryInfoId)
    }

    fun getAllTodoCategoryInfo() {
        getAllTodoCategoryInfoObs.value = getAllTodoCategoryInfoObs.value
    }

    fun insertTodoCategoryInfo(todoCategoryInfo: TodoCategoryInfo) {
        insertTodoCategoryInfoObs.value = todoCategoryInfo
    }

    fun deleteTodoCategoryInfoById(todoCategoryInfoId: Long) {
        deleteTodoCategoryInfoByIdObs.value = todoCategoryInfoId
    }

}