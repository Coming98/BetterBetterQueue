package com.example.betterbetterqueue.ui.TodoItemInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.betterbetterqueue.logic.Entity.TickerInfos
import com.example.betterbetterqueue.logic.Entity.TodoItemInfo
import com.example.betterbetterqueue.logic.Repository
import java.time.LocalDateTime
// Done
class TodoItemInfoViewModel: ViewModel() {

    /**
     * 页面显示的数据缓存
     */
    var todoItemId: Long = -1L // 当前的 TodoItemId
    var todoItemName: String = "" // 当前的 TodoItemName
    var lastAccessTime = LocalDateTime.now() // TodoItem 最近访问的时间
    var topTime: Long? = null // todoItem 是否置頂

    var tickerInfos: TickerInfos = TickerInfos(false, 0, null, null, "")
    // var tickerStatus: Boolean = false
    // var tickerBaseTime: Int = 0 // 用于时间校准的基础走时信息
    // var tickerRecentTime: LocalDateTime? = null // 用于记录最近一次开始计时的时间
    // var tickerBeginTime: LocalDateTime? = null
    // var todoItemInfoDes: String = "" // 记录当前 todoItemInfo 的描述信息, 防止被修改其它 info 时篡改
    var tickerTime: Int = 0 // 用于界面显示计时信息

    val todoItemInfoList = ArrayList<TodoItemInfo>()
    var currentTodoItemInfoId: Long = -1L // -1 表示当前正在新增的类别

    /**
     * 需要监听的数据信息
     */
    public val tickerStatusObs = MutableLiveData<Boolean>(null)


    /**
     * Local state cache
     * dumpTodoItemIdCache: 保存当前正在计时的 TodoItemId
     * dumpTickerInfosCache: 保存当前的计时器详细信息 TickerInfos
     * dumpTodoItemInfoDesCache: 保存当前对正在进行的 TodoItemInfo 的描述
     * ---
     * loadTodoItemIdCache: 加载缓存的计时信息对应的 TodoItemId
     * loadTickerInfosCache: 加载缓存的计时器详细信息 TickerInfos
     * loadTodoItemInfoDesCache: 加载当前对正在进行的 TodoItemInfo 的描述
     */


    // private val loadTodoItemIdCacheObs = MutableLiveData<Any?>()
    // private val loadTickerInfosCacheObs = MutableLiveData<Any?>()
    // private val loadTodoItemInfoDesCacheObs = MutableLiveData<Any?>()
    //
    // val loadTodoItemIdCacheResult = Transformations.switchMap(loadTodoItemIdCacheObs) {
    //     Repository.loadTodoItemIdCache()
    // }
    // val loadTickerInfosCacheResult = Transformations.switchMap(loadTickerInfosCacheObs) {
    //     Repository.loadTickerInfosCache()
    // }
    // val loadTodoItemInfoDesCacheResult = Transformations.switchMap(loadTodoItemInfoDesCacheObs) {
    //     Repository.loadTodoItemInfoDesCache()
    // }
    //
    // fun loadTodoItemIdCache() {
    //     loadTodoItemIdCacheObs.value = loadTodoItemIdCacheObs.value
    // }
    // fun loadTickerInfosCache() {
    //     loadTickerInfosCacheObs.value = loadTickerInfosCacheObs.value
    // }
    //
    // fun loadTodoItemInfoDesCache() {
    //     loadTodoItemInfoDesCacheObs.value = loadTodoItemInfoDesCacheObs.value
    // }
    //
    // fun dumpTodoItemIdCache(todoItemId: Long) {
    //     Repository.dumpTodoItemIdCache(todoItemId)
    // }
    //
    // fun dumpTickerInfosCache(tickerInfos: TickerInfos) {
    //     Repository.dumpTickerInfosCache(tickerInfos)
    // }
    //
    // fun dumpTodoItemInfoDesCache(todoItemInfoDes: String) {
    //     // this.todoItemInfoDes = todoItemInfoDes
    //     this.tickerInfos.des = todoItemInfoDes
    //     Repository.dumpTodoItemInfoDesCache(todoItemInfoDes)
    // }

    private val loadTickerInfosCacheByIdObs = MutableLiveData<Long>()

    val loadTickerInfosCacheByIdResult = Transformations.switchMap(loadTickerInfosCacheByIdObs) { todoItemId ->
        Repository.loadTickerInfosCacheById(todoItemId)
    }

    fun loadTickerInfosCacheById(todoItemId: Long) {
        loadTickerInfosCacheByIdObs.value = todoItemId
    }

    fun dumpTickerInfosCacheById(todoItemId: Long, tickerInfos: TickerInfos?) {
        Repository.dumpTickerInfosCacheById(todoItemId, tickerInfos)
    }

    fun dumpTodoItemStatusCacheOfVisited(todoItemId: Long) {
        Repository.dumpTodoItemStatusCacheOfVisited(todoItemId)
    }



    private val getTodoItemByIdObs = MutableLiveData<Long>()
    private val getTodoItemInfosByIdObs = MutableLiveData<Long>()
    private val insertTodoItemInfoObs = MutableLiveData<Triple<TodoItemInfo, Long, Long>>()
    private val updateTodoItemNameByIdObs = MutableLiveData<Pair<String, Long>>()
    private val updateTodoItemToptimeByIdObs = MutableLiveData<Pair<Long, Long>>()


    val getTodoItemByIdResult = Transformations.switchMap(getTodoItemByIdObs) { todoItemId ->
        Repository.getTodoItemById(todoItemId)
    }
    val getTodoItemInfosByIdResult = Transformations.switchMap(getTodoItemInfosByIdObs) { todoItemId ->
        Repository.getTodoItemInfosByItemId(todoItemId)
    }
    val insertTodoItemInfoResult = Transformations.switchMap(insertTodoItemInfoObs) { tripleInfo ->
        Repository.insertTodoItemInfo(tripleInfo.first, tripleInfo.second, tripleInfo.third)
    }

    val updateTodoItemNameByIdResult = Transformations.switchMap(updateTodoItemNameByIdObs) {
        Repository.updateTodoItemNameById(it.first, it.second)
    }

    val updateTodoItemToptimeByIdResult = Transformations.switchMap(updateTodoItemToptimeByIdObs) {
        Repository.updateTodoItemToptimeById(it.first, it.second)
    }


    fun getTodoItemById(todoItemId: Long) {
        getTodoItemByIdObs.value = todoItemId
    }

    fun getTodoItemInfosmById(todoItemId: Long) {
        getTodoItemInfosByIdObs.value = todoItemId
    }

    fun insertTodoItemInfo(todoItemInfo: TodoItemInfo, todoItemId: Long, todoItemInfoId: Long) {
        insertTodoItemInfoObs.value = Triple(todoItemInfo, todoItemId, todoItemInfoId)
    }
    fun updateTodoItemNameById(newTodoItemName: String, todoItemId: Long) {
        updateTodoItemNameByIdObs.value = Pair(newTodoItemName, todoItemId)
    }

    fun updateTodoItemToptimeById(topTime: Long, id: Long) {
        updateTodoItemToptimeByIdObs.value = Pair(topTime, id)
    }

}