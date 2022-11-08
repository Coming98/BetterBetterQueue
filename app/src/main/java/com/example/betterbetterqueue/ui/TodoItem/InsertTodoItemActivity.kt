package com.example.betterbetterqueue.ui.TodoItem

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.betterbetterqueue.R
import com.example.betterbetterqueue.loadToolbarFragment
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.google.android.material.floatingactionbutton.FloatingActionButton


class InsertTodoItemActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        fun onActionStart(context: Context, activityResultLauncher: ActivityResultLauncher<Intent>) {
            val intent = Intent(context, InsertTodoItemActivity::class.java)
            activityResultLauncher.launch(intent)
        }
    }

    val viewModel: TodoItemViewModel by lazy { ViewModelProvider(this).get(TodoItemViewModel::class.java) }
    val insert_todoitem_name: EditText by lazy { findViewById(R.id.insert_todoitem_name) }
    val insertTodoitmeCategory: EditText by lazy { findViewById(R.id.insert_todoitem_category) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_todoitem)

        //////////////////////////// 加载标题栏
        loadToolbarFragment(toolbarNmae = getString(R.string.insertTodoItemToolbarName), toolbarLeft = R.drawable.ic_back, toolbarRight = null,
            changeToolbarName = null,
            handleButtonLeft = { finish()},
            handleButtonRight = { }
        )


        /////////////////////////////// 加载数据库数据并监听
        viewModel.getAllTodoCategory()

        viewModel.getAllTodoCategoryResult.observe(this, Observer { result ->
            val todoCategoryList: List<TodoCategory>? = result.getOrNull()
            if (todoCategoryList != null) {
                viewModel.todoCategoryList.clear()
                viewModel.todoCategoryList.addAll(todoCategoryList)
            } else {
                Toast.makeText(this, "Get All TodoCategory Wrong", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })


        /////////////////////////// 设置必要的按钮点击事件

        val button_insert_todoitem: FloatingActionButton = findViewById(R.id.btn_insert_todoitem)
        val buttonCheckTodoCategory: Button = findViewById(R.id.btn_check_category)
        arrayOf(button_insert_todoitem, buttonCheckTodoCategory).forEach { it.setOnClickListener(this) }
    }

    private val COUNTRIES = arrayOf(
        "Belgium", "France", "Italy", "Germany", "Spain"
    )

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_insert_todoitem -> {
                val todoItemName: String = insert_todoitem_name.text.trim().toString()
                val todoItemCategory: String = insertTodoitmeCategory.text.toString()
                if(todoItemName.isEmpty()) {
                    Toast.makeText(this, "时光点滴 内容不能为空哦~", Toast.LENGTH_SHORT).show()
                    return
                }
                // 查询类别信息
                var todoItemCategoryId: String = "null"
                val todoCategoryItems = viewModel.todoCategoryList.map { it.name }
                if(todoCategoryItems.contains(todoItemCategory)) {
                    todoItemCategoryId = viewModel.todoCategoryList.get(todoCategoryItems.indexOf(todoItemCategory)).id.toString()
                }
                // 返回 TodoItem 对象的相关信息
                val intent = Intent().apply {
                    putExtra("status", "OK")
                    putExtra("todoItemName", todoItemName)
                    putExtra("todoItemCategory", todoItemCategory)
                    putExtra("todoItemCategoryId", todoItemCategoryId)
                }
                setResult(RESULT_OK, intent)
                finish()
            }
            // 选择类别

            R.id.btn_check_category -> {
                val todoCategoryItems = viewModel.todoCategoryList.map { it.name }.toTypedArray()
                val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(this).apply {
                    setTitle("时间规划集")
                    .setItems(todoCategoryItems, DialogInterface.OnClickListener { dialog, which ->
                        insertTodoitmeCategory.setText(todoCategoryItems.get(which))
                        dialog.dismiss()
                    })
                    create()
                    show()
                }
            }
        }
    }
}