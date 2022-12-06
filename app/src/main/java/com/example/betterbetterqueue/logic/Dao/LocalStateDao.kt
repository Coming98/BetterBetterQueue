package com.example.betterbetterqueue.logic.Dao

import android.content.Context
import android.util.Log
import com.example.betterbetterqueue.TodoApplication
import com.example.betterbetterqueue.TodoApplication.Companion.cacheGson
import com.example.betterbetterqueue.TodoApplication.Companion.todoGson
import com.example.betterbetterqueue.dateFormatter
import com.example.betterbetterqueue.logic.Entity.TickerInfos
import com.example.betterbetterqueue.logic.Entity.TodoItemStatus
import com.example.betterbetterqueue.open
import java.time.LocalDateTime

object LocalStateDao {

    // 仅有一个实例
    private fun sharedPreferences() = TodoApplication.context.getSharedPreferences("bbq", Context.MODE_PRIVATE)

    fun loadCurrentTodoCategoryId(): Long {
        val todoCategoryId = sharedPreferences().getLong("todoCategoryId", -1L)
        return todoCategoryId
    }

    fun loadTodoItemStatus(): TodoItemStatus {
        val todoItemRunningIds: List<Long> = sharedPreferences().all.keys.filter { it.contains("-tickerInfo") }.map { it.split('-').get(0).toLong() }
        val todoItemVisitedIdsStr: String? = sharedPreferences().getString(LocalDateTime.now().dateFormatter(), "")
        var todoItemVisitedIds: List<Long> = listOf()
        if(todoItemVisitedIdsStr != null && !todoItemVisitedIdsStr.isEmpty()) {
            todoItemVisitedIds = todoItemVisitedIdsStr.split("-").map { it.toLong() }
        }
        val todoItemStatus = TodoItemStatus(running = todoItemRunningIds, visited = todoItemVisitedIds)
        return todoItemStatus
    }

    // fun loadTodoItemIdCache(): Long {
    //     val todoItemId = sharedPreferences().getLong("todoItemId", -1L)
    //     return todoItemId
    // }
    //
    // fun loadTickerInfosCache(): TickerInfos {
    //     val status = sharedPreferences().getBoolean("tickerStatus", false)
    //     val baseTime = sharedPreferences().getInt("tickerBaseTime", 0)
    //     val recentTime = sharedPreferences().getLong("tickerRecentTime", -1L)
    //     val beginTime = sharedPreferences().getLong("tickerBeginTime", -1L)
    //     return TickerInfos(status, baseTime, recentTime, beginTime)
    // }
    //
    // fun loadTodoItemInfoDesCache(): String {
    //     val todoItemInfoDes = sharedPreferences().getString("todoItemInfoDes", "") ?: ""
    //     return todoItemInfoDes
    // }

    fun loadTickerInfosCacheById(todoItemId: Long): TickerInfos? {
        Log.d("TEMP", "Loading ${todoItemId} ...")
        val jsonData = sharedPreferences().getString("${todoItemId}-tickerInfo", null)
        if(jsonData != null) {
            return todoGson.fromJson(jsonData, TickerInfos::class.java)
        } else {
            return jsonData
        }
    }

    fun dumpTickerInfosCacheById(todoItemId: Long, tickerInfos: TickerInfos?) {
        if(tickerInfos == null) {
            sharedPreferences().open {
                remove("${todoItemId}-tickerInfo")
            }
        } else {
            val jsonData = todoGson.toJson(tickerInfos)
            sharedPreferences().open {
                putString("${todoItemId}-tickerInfo", jsonData)
            }
        }
    }

    fun dumpTodoItemStatusCacheOfVisited(todoItemId: Long) {
        val todoItemVisitedIdsStr: String? = sharedPreferences().getString(LocalDateTime.now().dateFormatter(), "")

        var todoItemVisitedIds: List<String> = listOf()
        if(todoItemVisitedIdsStr != null && !todoItemVisitedIdsStr.isEmpty()) {
            todoItemVisitedIds = todoItemVisitedIdsStr.split("-")
        }

        val todoItemIdStr = todoItemId.toString()
        if(!todoItemVisitedIds.contains(todoItemIdStr)) {
            todoItemVisitedIds = todoItemVisitedIds.plus(todoItemIdStr)
            sharedPreferences().open {
                putString(LocalDateTime.now().dateFormatter(), todoItemVisitedIds.joinToString("-"))
            }
        }

        Log.d("TEMP", todoItemVisitedIds.joinToString("-"))
    }

    fun dumpCurrentTodoCategoryId(todoCategoryId: Long) {
        sharedPreferences().open {
            putLong("todoCategoryId", todoCategoryId)
        }
    }


    // fun dumpTodoItemIdCache(todoItemId: Long) {
    //     sharedPreferences().open {
    //         putLong("todoItemId", todoItemId)
    //     }
    // }
    //
    // fun dumpTickerInfosCache(tickerInfos: TickerInfos) {
    //     sharedPreferences().open {
    //         putBoolean("tickerStatus", tickerInfos.status)
    //         putInt("tickerBaseTime", tickerInfos.baseTime)
    //         putLong("tickerRecentTime", tickerInfos.recentTime)
    //         putLong("tickerBeginTime", tickerInfos.beginTime)
    //     }
    // }
    //
    // fun dumpTodoItemInfoDesCache(todoItemInfoDes: String) {
    //     sharedPreferences().open {
    //         putString("todoItemInfoDes", todoItemInfoDes)
    //     }
    // }

}