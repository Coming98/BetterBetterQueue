package com.example.betterbetterqueue.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Entity.TodoCategoryInfo
import com.example.betterbetterqueue.logic.Entity.TodoItem
import com.example.betterbetterqueue.logic.Entity.TodoItemStatus
import com.example.betterbetterqueue.logic.Repository
import com.example.betterbetterqueue.ui.Config.DBInJson
// Done
class MainViewModel: ViewModel() {

    /**
     * 滑动事件检测
     * 通过 down + up 得到滑动方向
     * 记录 down 的坐标 (x, y)
     */
    var downX: Float = 0.0f
    var downY: Float = 0.0f

    /**
     * Local state cache
     * loadTodoCategoryIdCache: 成功加载用户的缓存数据后需要回调处理
     * loadTodoItemIdsCache: 加载本地缓存的 TodoItemId 列表用于判断哪个任务正在进行
     * ---
     * dumpTodoCategoryIdCache: 开启一个线程保存当前类别即可, 不需要回调
     */
    var todoItemStatus: TodoItemStatus? = null

    private val loadTodoCategoryIdCacheObs = MutableLiveData<Any?>()
    private val loadTodoItemStatusCacheObs = MutableLiveData<Any?>()

    val loadTodoCategoryIdCacheResult = Transformations.switchMap(loadTodoCategoryIdCacheObs) {
        Repository.loadCurrentTodoCategoryId()
    }

    val loadTodoItemStatusCacheResult = Transformations.switchMap(loadTodoItemStatusCacheObs)  {
        Repository.loadTodoItemStatus()
    }

    fun loadTodoItemStatusCache() {
        loadTodoItemStatusCacheObs.value = loadTodoItemStatusCacheObs.value
    }

    fun loadTodoCategoryIdCache() {
        loadTodoCategoryIdCacheObs.value = loadTodoCategoryIdCacheObs.value
    }

    fun dumpTodoCategoryIdCache(todoCategoryId: Long) {
        Repository.dumpCurrentTodoCategoryId(todoCategoryId)
    }

    /**
     * TodoItem 相关的 ViewModel
     * todoItemList: 界面上展示的 TodoItem 列表
     * ---
     * insertTodoItem: 插入新的 TodoItem 后需要回调处理
     * refreshTodoItemByCategory: 类别更换后需要根据类别刷新 TodoItem 并回调处理
     * deleteTodoItemById: 删除 TodoItem 后需要回调处理 TODO
     */

    val todoItemList = ArrayList<TodoItem>() // 对界面上展示的 TodoItemList 进行缓存

    private val insertTodoItemObs = MutableLiveData<Triple<TodoItem, String, Long>>()
    private val refreshTodoItemByCategoryObs = MutableLiveData<Long>()
    private val deleteTodoItemByIdObs = MutableLiveData<Long>()

    val insertTodoItemResult = Transformations.switchMap(insertTodoItemObs) { insertTodoItemTriple ->
        Repository.insertTodoItem(insertTodoItemTriple.first, insertTodoItemTriple.second, insertTodoItemTriple.third)
    }

    val refreshTodoItemByCategoryResult = Transformations.switchMap(refreshTodoItemByCategoryObs) { todoCategoryId ->
        Repository.refreshTodoItemByCategory(todoCategoryId)
    }

    val deleteTodoItemByIdResult = Transformations.switchMap(deleteTodoItemByIdObs) { todoItemId ->
        Repository.deleteTodoItemById(todoItemId)
    }

    fun insertTodoItem(todoItem: TodoItem, todoItemCategory: String, todoItemCategoryId: Long) {
        insertTodoItemObs.value = Triple(todoItem, todoItemCategory, todoItemCategoryId)
    }

    fun refreshTodoItemByCategory(todoCategoryId: Long) {
        refreshTodoItemByCategoryObs.value = todoCategoryId
    }

    fun deleteTodoItemById(todoItemId: Long) {
        deleteTodoItemByIdObs.value = todoItemId
    }

    /**
     * TodoCategory 相关的 ViewModel
     * todoCategoryList: 界面上展示的 TodoCategory 列表
     * todoCategoryId: 当前选中的 TodoCategoryId, -1 表示星海 (所有类别)
     * ---
     * getAllTodoCategory: 获取所有 TodoCategory 后需要回调处理
     * updateTodoCategoryNameById: 更新 TodoCategoryName 后需要回调处理
     * deleteTodoCategoryById: 删除 TodoCategory 后需要回调处理
     */
    val todoCategoryList = ArrayList<TodoCategory>() // 对界面上展示的 TodoCategoryList 进行缓存
    var todoCategoryId: Long = -1

    private val getAllTodoCategoryObs = MutableLiveData<Any?>()
    private val updateTodoCategoryNameByIdObs = MutableLiveData<Pair<String, Long>>()
    private val deleteTodoCategoryByIdObs = MutableLiveData<Long>()

    val getAllTodoCategoryResult = Transformations.switchMap(getAllTodoCategoryObs) {
        Repository.getAllTodoCategory()
    }

    val updateTodoCategoryNameByIdResult = Transformations.switchMap(updateTodoCategoryNameByIdObs) {
        Repository.updateTodoCategoryNameById(it.first, it.second)
    }

    val deleteTodoCategoryByIdResult = Transformations.switchMap(deleteTodoCategoryByIdObs) { todoCategoryId ->
        Repository.deleteTodoCategoryById(todoCategoryId)
    }

    fun getAllTodoCategory() {
        getAllTodoCategoryObs.value = getAllTodoCategoryObs.value
    }

    fun updateTodoCategoryNameById(newTodoCategoryName: String, todoCategoryId: Long) {
        updateTodoCategoryNameByIdObs.value = Pair(newTodoCategoryName, todoCategoryId)
    }

    fun deleteTodoCategoryById(todoCategoryId: Long) {
        deleteTodoCategoryByIdObs.value = todoCategoryId
    }

    /**
     * 数据库的导入与导出
     * ---
     * getExportedDBData: 加载所有数据库数据后需要回调处理
     * importDBData: 根据外部 JSON 数据覆盖当前数据库后需要回调处理
     *
     */
    private val getExportedDBDataObs = MutableLiveData<Any?>()
    private val importDBDataObs = MutableLiveData<DBInJson>()

    val getExportedDBDataResult = Transformations.switchMap(getExportedDBDataObs) {
        Repository.getExportedDBDataObs()
    }

    val importDBDataResult = Transformations.switchMap(importDBDataObs) { dbInJson ->
        Repository.importDBData(dbInJson)
    }

    fun getExportedDBData() {
        getExportedDBDataObs.value = getExportedDBDataObs.value
    }

    fun importDBData(dbInJson: DBInJson) {
        importDBDataObs.value = dbInJson
    }

}