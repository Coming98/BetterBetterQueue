package com.example.betterbetterqueue

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Entity.TodoCategoryInfo
import com.example.betterbetterqueue.logic.Entity.TodoItem
import com.example.betterbetterqueue.ui.MainViewModel
import com.example.betterbetterqueue.ui.TodoCategory.TodoCategoryAdapter
import com.example.betterbetterqueue.ui.TodoItem.InsertTodoItemActivity
import com.example.betterbetterqueue.ui.TodoItem.TodoItemAdapter
import com.example.betterbetterqueue.ui.ToolbarFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    val recycler_todoitem: RecyclerView by lazy { findViewById(R.id.recycler_todoitem) }
    val recycler_todocategory: RecyclerView by lazy { findViewById(R.id.recycler_todocategory) }
    val drawerLayout: DrawerLayout by lazy { findViewById(R.id.drawer_layout) }
    lateinit var adapter_todoitem: TodoItemAdapter
    lateinit var adapter_todoCategory: TodoCategoryAdapter
    lateinit var toolbarFragment: ToolbarFragment

    private val get_inserted_todoitem_activity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            if(result.data != null) {
                val data: Intent = result.data!!
                if(data.hasExtra("status") && data.getStringExtra("status") == "OK") {
                    val todoItemName = data.getStringExtra("todoItemName")!!
                    val todoItemCategory = data.getStringExtra("todoItemCategory")!!
                    var todoItemCategoryIdStatus: String = data.getStringExtra("todoItemCategoryId")!!
                    val createTime = LocalDateTime.now()
                    val totalTime: Long = 0

                    val todoItem = TodoItem(name = todoItemName, createTime = createTime, totalTime = totalTime)
                    var todoItemCategoryId: Long = -1
                    if(todoItemCategoryIdStatus != "null") {
                        todoItemCategoryId = todoItemCategoryIdStatus.toLong()
                    }

                    viewModel.insertTodoItem(todoItem, todoItemCategory, todoItemCategoryId)
                }
            }


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ////////////////////////// 加载标题栏
        toolbarFragment = loadToolbarFragment(toolbarNmae="全部类别", toolbarLeft=R.drawable.ic_category, toolbarRight=null,
            handleButtonLeft = {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            },
            handleButtonRight = {}
        )

        ///////////////////////// 加载 Recycler 数据

        val layoutManager_todoitem = GridLayoutManager(this, 2)
        adapter_todoitem = TodoItemAdapter(viewModel.todoItemList)
        recycler_todoitem.layoutManager = layoutManager_todoitem
        recycler_todoitem.adapter = adapter_todoitem

        viewModel.getAllTodoItem()

        val layoutManager_todocategory = LinearLayoutManager(this)
        adapter_todoCategory = TodoCategoryAdapter(viewModel.todoCategoryList) { todoCategoryId ->
            if(viewModel.todoCategoryId.value != todoCategoryId) {
                viewModel.todoCategoryId.value = todoCategoryId
                viewModel.refreshTodoItemByCategory(todoCategoryId)
            }
        }
        recycler_todocategory.layoutManager = layoutManager_todocategory
        recycler_todocategory.adapter = adapter_todoCategory

        viewModel.getAllTodoCategory()

        /////////////////////////// 必要的按钮点击事件

        val button_insert_todoitem: FloatingActionButton = findViewById(R.id.btn_insert_todoitem)
        val onClickedButtons = arrayOf(button_insert_todoitem, )
        onClickedButtons.forEach { it.setOnClickListener(this) }

        ///////////////////////////// 数据库数据加载后的监听

        viewModel.insertTodoItemResult.observe(this, Observer { result ->
            val resultInfo = result.getOrNull()
            if (resultInfo != null) {
                Toast.makeText(this, resultInfo, Toast.LENGTH_SHORT).show()
                viewModel.getAllTodoItem()
                viewModel.getAllTodoCategory()
            } else {
                Toast.makeText(this, "Insert TodoItem Wrong!", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })

        viewModel.getAllTodoItemResult.observe(this, Observer { result ->
            val todoItemList = result.getOrNull()
            if (todoItemList != null) {
                viewModel.todoItemList.clear()
                viewModel.todoItemList.addAll(todoItemList)
                todoItemList.forEach { Log.d("TEMP", "${it.id} ${it.name} ${it.createTime} ${it.totalTime}") }
                adapter_todoitem.notifyDataSetChanged()
            }
        })

        viewModel.refreshTodoItemByCategoryResult.observe(this, Observer { result ->
            val pairResult = result.getOrNull()
            if(pairResult != null) {
                val todoCategoryName = pairResult.first
                val todoItemList = pairResult.second
                toolbarFragment.refreshToolbarName(todoCategoryName)
                viewModel.todoItemList.clear()
                viewModel.todoItemList.addAll(todoItemList)
                adapter_todoitem.notifyDataSetChanged()
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
        })

        ///////////////////////////// TodoCategory

        viewModel.getAllTodoCategoryResult.observe(this, Observer { result ->
            val todocategories = result.getOrNull()
            if(todocategories != null) {
                todocategories.forEach { todocategory -> Log.d("TEMP", "(${todocategory.id}, ${todocategory.category}, ${todocategory.createTime}, ${todocategory.count})")}
                val totalCount = todocategories.sumOf { it.count }
                viewModel.todoCategoryList.clear()
                viewModel.todoCategoryList.add(TodoCategory(-1L, "星海", null, totalCount))
                viewModel.todoCategoryList.addAll(todocategories)
                adapter_todoCategory.notifyDataSetChanged()
            }
        })

        /////////////////////////////// TodoCategoryInfo
        viewModel.getAllTodoCategoryInfoResult.observe(this, Observer { result ->
            val todoCategoryInfoList = result.getOrNull()
            if(todoCategoryInfoList != null) {
                todoCategoryInfoList.forEach { todoCategoryInfo -> Log.d("TEMP", "(${todoCategoryInfo.id}, ${todoCategoryInfo.categoryId}, ${todoCategoryInfo.itemId})")}
            } else {
                Log.d("TEM{", "STH WRONG")
            }
        })

        viewModel.insertTodoCategoryInfoResult.observe(this, Observer { result ->
            val insertedTodoCategoryInfoId = result.getOrNull()
            if(insertedTodoCategoryInfoId != null) {
                Log.d("TEMP", "Inserted Success ${insertedTodoCategoryInfoId}")
            } else {
                Log.d("TEMP", "Inserted INFO WRONG")
            }
        })

        viewModel.deleteTodoCategoryInfoByIdResult.observe(this, Observer {result ->
            val deletedTodoCategoryInfoRes = result.getOrNull()
            if(deletedTodoCategoryInfoRes != null) {
                Log.d("TEMP", deletedTodoCategoryInfoRes)
            } else {
                Log.d("TEMP", "DELETE WRONG")
            }
        })

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_insert_todoitem -> {
                // viewModel.insertTodoItem(TodoItem(name = (1..100).random().toString(), createTime = Date(), totalTime = (1..1000).random().toLong()))
                InsertTodoItemActivity.onActionStart(this, get_inserted_todoitem_activity)
                // viewModel.getAllTodoCategoryInfo()
                // viewModel.insertTodoCategoryInfo(TodoCategoryInfo(categoryId = 3, itemId = 5))
                // viewModel.deleteTodoCategoryInfoById(1)

            }
        }
    }
}

fun AppCompatActivity.loadToolbarFragment(toolbarNmae: String, toolbarLeft: Int?, toolbarRight: Int?, handleButtonLeft: () -> Unit, handleButtonRight: () -> Unit): ToolbarFragment {
    val toolbarFragment = ToolbarFragment(handleButtonLeft, handleButtonRight)

    val bundle = Bundle().apply {
        putString("toolbarName", toolbarNmae)
        toolbarLeft?.let { putInt("toolbarLeft", it) }
        toolbarRight?.let { putInt("toolbarRight", it) }
    }
    toolbarFragment.arguments = bundle

    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()
    transaction.replace(R.id.toolbar_fragment, toolbarFragment)
    transaction.commit()

    return toolbarFragment
}

fun LocalDateTime.dateFormatter(): String {
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return dateFormatter.format(this).toString()
}

fun LocalDateTime.dateTimeFormatter(): String {
    val datetimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return datetimeFormatter.format(this).toString()
}