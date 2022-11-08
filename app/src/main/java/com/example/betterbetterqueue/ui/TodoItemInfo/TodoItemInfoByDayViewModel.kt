package com.example.betterbetterqueue.ui.TodoItemInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.betterbetterqueue.dateFormatter
import com.example.betterbetterqueue.logic.Entity.TodoCategoryInfo
import com.example.betterbetterqueue.logic.Entity.TodoItemInfo
import com.example.betterbetterqueue.logic.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
// Done
class TodoItemInfoByDayViewModel: ViewModel() {

    /**
     * 左右滑动事件的监听
     */
    var downX: Float = 0.0F
    var downY: Float = 0.0F

    /**
     * 界面上显示的信息
     *      currentLocalDate: 时间跨度所在日期，用于标题栏
     *      时间跨度的上下限
     *      时间跨度内 itemInfo 的总时间
     */
    var currentLocalDate: String = LocalDateTime.now().dateFormatter()
    var minLocalDateLong: Long = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).toInstant(ZoneOffset.of("+8")).toEpochMilli()
    var maxLocalDateLong: Long = minLocalDateLong + (3600 * 24 - 1) * 1000
    var totalSecond = 0
    val todoItemInfoList = ArrayList<TodoItemInfo>()

    private val getTodoItemInfoByTimeScopeObs = MutableLiveData<Pair<Long, Long>>()

    fun getTodoItemInfoByTimeScopeResult() = Transformations.switchMap(getTodoItemInfoByTimeScopeObs) {
        Repository.getTodoItemInfoByTimeScope(it.first, it.second)
    }

    fun getTodoItemInfoByTimeScope(minTime: Long, maxTime: Long) {
        getTodoItemInfoByTimeScopeObs.value = Pair(minTime, maxTime)
    }

}