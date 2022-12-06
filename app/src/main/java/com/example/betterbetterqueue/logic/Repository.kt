package com.example.betterbetterqueue.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.example.betterbetterqueue.TodoApplication
import com.example.betterbetterqueue.logic.Dao.LocalStateDao
import com.example.betterbetterqueue.logic.Dao.TodoDatabase
import com.example.betterbetterqueue.logic.Entity.*
import com.example.betterbetterqueue.ui.Config.DBInJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

object Repository {

    val todoDatabase: TodoDatabase = TodoDatabase.getDatabase(TodoApplication.context)

    /////////////////////////////// Local state

    fun loadCurrentTodoCategoryId() = fire(Dispatchers.IO) {
        val todoCategoryId = LocalStateDao.loadCurrentTodoCategoryId()
        Result.success(todoCategoryId)
    }

    fun loadTodoItemStatus() = fire(Dispatchers.IO) {
        val todoItemStatus = LocalStateDao.loadTodoItemStatus()
        Result.success(todoItemStatus)
    }

    // fun loadTodoItemIdCache() = fire(Dispatchers.IO) {
    //     val todoItemId = LocalStateDao.loadTodoItemIdCache()
    //     Result.success(todoItemId)
    // }
    //
    // fun loadTickerInfosCache() = fire(Dispatchers.IO) {
    //     val tickerInfosCache = LocalStateDao.loadTickerInfosCache()
    //     Result.success(tickerInfosCache)
    // }
    //
    // fun loadTodoItemInfoDesCache() = fire(Dispatchers.IO) {
    //     Result.success(LocalStateDao.loadTodoItemInfoDesCache())
    // }

    fun loadTickerInfosCacheById(todoItemId: Long) = fire(Dispatchers.IO) {
        val tickerInfosCache = LocalStateDao.loadTickerInfosCacheById(todoItemId)
        Result.success(tickerInfosCache)
    }

    fun dumpTickerInfosCacheById(todoItemId: Long, tickerInfos: TickerInfos?) {
        thread {
            LocalStateDao.dumpTickerInfosCacheById(todoItemId, tickerInfos)
        }
    }

    fun dumpTodoItemStatusCacheOfVisited(todoItemId: Long) {
        thread {
            LocalStateDao.dumpTodoItemStatusCacheOfVisited(todoItemId)
        }
    }


    fun dumpCurrentTodoCategoryId(todoCategoryId: Long) {
        thread {
            LocalStateDao.dumpCurrentTodoCategoryId(todoCategoryId)
        }
    }


    // fun dumpTodoItemIdCache(todoItemId: Long) {
    //     thread {
    //         LocalStateDao.dumpTodoItemIdCache(todoItemId)
    //     }
    // }
    //
    // fun dumpTickerInfosCache(tickerInfos: TickerInfos) {
    //     thread {
    //         LocalStateDao.dumpTickerInfosCache(tickerInfos)
    //     }
    // }
    //
    // fun dumpTodoItemInfoDesCache(todoItemInfoDes: String) {
    //     thread {
    //         LocalStateDao.dumpTodoItemInfoDesCache(todoItemInfoDes)
    //     }
    // }

    ////////////////////////////// TodoItem

    fun insertTodoItem(todoItem: TodoItem, todoItemCategory: String, todoItemCategoryId: Long) = fire(Dispatchers.IO) {
        coroutineScope {
            // 插入 todoItem
            val insertedTodoItemId_ = async {
                todoDatabase.todoItemDao().insertTodoItem(todoItem)
            }

            var insertedTodoCategoryInfoId_: Any? = null
            var insertedTodoCategoryId: Long = todoItemCategoryId
            if(todoItemCategoryId == -1L) {
                // 插入 todoCategory
                val insertedTodoCategoryId_ =
                    async {
                        todoDatabase.todoCategoryDao().insertTodoCategory(TodoCategory(name = todoItemCategory, createTime = todoItem.createTime, count = 1))
                    }
                // 插入 todoCategoryInfo itemId, categoryId
                val insertedTodoItemId: Long = insertedTodoItemId_.await()
                insertedTodoCategoryId = insertedTodoCategoryId_.await()
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
            Result.success(insertedTodoCategoryId)
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
                val todoCategoryName: String = todoCategory_.await().name
                val todoItemList = todoItemList_.await()
                Result.success(Pair(todoCategoryName, todoItemList))
            }
        }
    }

    fun getTodoItemById(todoItemId: Long) = fire(Dispatchers.IO) {
        val todoItem: TodoItem = todoDatabase.todoItemDao().getTodoItemById(todoItemId)
        Result.success(todoItem)
    }

    fun updateTodoItemNameById(name: String, id: Long) = fire(Dispatchers.IO) {
        todoDatabase.todoItemDao().updateTodoItemNameById(name, id)
        Result.success(name)
    }

    fun updateTodoItemToptimeById(topTime: Long, id: Long) = fire(Dispatchers.IO) {
        todoDatabase.todoItemDao().updateTodoItemToptimeById(topTime, id)
        Result.success(topTime)
    }

    fun deleteTodoItemById(todoItemId: Long) = fire(Dispatchers.IO) {
        todoDatabase.todoItemDao().deleteTodoItemById(todoItemId)
        Result.success("Delete TodoItem Success")
    }

    ////////////////////////////// TodoItemInfo

    fun getTodoItemInfosByItemId(todoItemId: Long) = fire(Dispatchers.IO) {
        val todoItemInfoList: List<TodoItemInfo> = todoDatabase.todoItemDao().getTodoItemInfosByItemId(todoItemId)
        Result.success(todoItemInfoList)
    }

    fun insertTodoItemInfo(todoItemInfo: TodoItemInfo, todoItemId: Long, todoItemInfoId: Long) = fire(Dispatchers.IO) {
        if(todoItemInfoId == -1L) {
            coroutineScope {
                val todoItemInfoId_ = async {
                    todoDatabase.todoItemDao().insertTodoItemInfo(todoItemInfo)
                }
                val updateTodoItemRes_ = async {
                    todoDatabase.todoItemDao().updateTodoItemTotalTimeById(todoItemInfo.totalTime, todoItemId)
                }
                val todoItemInfoId: Long = todoItemInfoId_.await()
                val updateTodoItemRes = updateTodoItemRes_.await()
                Result.success(todoItemInfoId)
            }
        } else {
            todoDatabase.todoItemDao().updateTodoItemInfoDesById(todoItemInfo.description, todoItemInfoId)
            Result.success(todoItemInfo.itemId)
        }
    }

    fun getTodoItemInfoByTimeScope(minTime: Long, maxTime: Long) = fire(Dispatchers.IO) {
        val todoItemInfoList: List<TodoItemInfo> = todoDatabase.todoItemDao().getTodoItemInfoByTimeScope(minTime, maxTime)
        Result.success(todoItemInfoList)
    }

    ////////////////////////////// TodoCategory

    fun getAllTodoCategory() = fire(Dispatchers.IO) {
        val todoCategories: List<TodoCategory> = todoDatabase.todoCategoryDao().getAllTodoCategory()
        Result.success(todoCategories)
    }

    fun updateTodoCategoryNameById(name: String, id: Long) = fire(Dispatchers.IO) {
        todoDatabase.todoCategoryDao().updateTodoCategoryNameById(name, id)
        Result.success(name)
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

    fun deleteTodoCategoryById(todoCategoryId: Long) = fire(Dispatchers.IO) {
        todoDatabase.todoCategoryDao().deleteTodoCategoryById(todoCategoryId)
        Result.success("Delete TodoCategory Success.")
    }

    ////////////////////////////////////// 导出数据库
    fun getExportedDBDataObs() = fire(Dispatchers.IO) {

        coroutineScope {
            val todoItemList_ = async {
                todoDatabase.todoItemDao().getAllTodoItem()
            }
            val todoItemInfoList_ = async {
                todoDatabase.todoItemDao().getAllTodoItemInfo()
            }
            val todoCategoryList_ = async {
                todoDatabase.todoCategoryDao().getAllTodoCategory()
            }
            val todoCategoryInfoList_ = async {
                todoDatabase.todoCategoryDao().getAllTodoCategoryInfo()
            }
            Result.success(DBInJson(todoItemList_.await(), todoItemInfoList_.await(), todoCategoryList_.await(), todoCategoryInfoList_.await()))
        }
    }

    // 导入数据库
    fun importDBData(dbInJson: DBInJson) = fire(Dispatchers.IO) {
        coroutineScope {
            // 清空当前数据库

            val deleteAllTodoItem_ = async {
                todoDatabase.todoItemDao().deleteAllTodoItem()
                true
            }
            val deleteAllTodOCategory_ = async {
                todoDatabase.todoCategoryDao().deleteAllTodoCategory()
                true
            }
            deleteAllTodoItem_.await() && deleteAllTodOCategory_.await()

            val deleteAllTodoCategoryInfo_ = async {
                todoDatabase.todoCategoryDao().deleteAllTodoCategoryInfo()
                true
            }
            val deleteAllTodoItemInfo_ = async {
                todoDatabase.todoItemDao().deleteAllTodoItemInfo()
                true
            }

            deleteAllTodoItemInfo_.await() && deleteAllTodoCategoryInfo_.await()

            // 导入新数据库
            val insertTodoItemList_ = async {
                dbInJson.todoItemList.forEach {
                    todoDatabase.todoItemDao().insertTodoItem(it)
                }
                true
            }

            val insertTodoCategoryList_ = async {
                dbInJson.todoCategoryList.forEach {
                    todoDatabase.todoCategoryDao().insertTodoCategory(it)
                }
                true
            }
            insertTodoItemList_.await()
            insertTodoCategoryList_.await()
            val insertTodoItemInfo_ = async {
                dbInJson.todoItemInfoList.forEach {
                    todoDatabase.todoItemDao().insertTodoItemInfo(it)
                }
                true
            }
            val insertTodoCategoryInfo_ = async {
                dbInJson.todoCategoryInfoList.forEach {
                    todoDatabase.todoCategoryDao().insertTodoCategoryInfo(it)
                }
            }
            insertTodoItemInfo_.await()
            insertTodoCategoryInfo_.await()
            Result.success("旧数据库已备份, 新数据库导入成功!")
        }
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