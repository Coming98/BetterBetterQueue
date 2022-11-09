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

// Done
class InsertTodoItemActivity : AppCompatActivity(), View.OnClickListener {

    /**
     * 外部启动接口
     */
    companion object {
        fun onActionStart(context: Context, activityResultLauncher: ActivityResultLauncher<Intent>) {
            val intent = Intent(context, InsertTodoItemActivity::class.java)
            activityResultLauncher.launch(intent)
        }
    }

    val viewModel: TodoItemViewModel by lazy { ViewModelProvider(this).get(TodoItemViewModel::class.java) }
    /**
     * 界面组件：TodoItemName, TodoItemCategory
     */
    val insert_todoitem_name: EditText by lazy { findViewById(R.id.insert_todoitem_name) }
    val insertTodoitmeCategory: EditText by lazy { findViewById(R.id.insert_todoitem_category) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_todoitem)


        loadToolbarFragment(toolbarNmae = getString(R.string.insertTodoItemToolbarName), toolbarLeft = R.drawable.ic_back, toolbarRight = null,
            changeToolbarName = null,
            handleButtonLeft = { finish()},
            handleButtonRight = { }
        )

        val button_insert_todoitem: FloatingActionButton = findViewById(R.id.btn_insert_todoitem)
        val buttonCheckTodoCategory: Button = findViewById(R.id.btn_check_category)
        arrayOf(button_insert_todoitem, buttonCheckTodoCategory).forEach { it.setOnClickListener(this) }

        /**
         * 提供类别选择
         */
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
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            /**
             * 浮动按钮: 确认插入 TodoItem
             *      TodoItemName 与 TodoCategoryName 判空
             *      TodoCategoryName 是否已存在
             *          存在则记录 TodoCategoryId
             *          不存在则置 TodoCategoryId 为 -1L
             *      返回信息
             */
            R.id.btn_insert_todoitem -> {
                val todoItemName: String = insert_todoitem_name.text.trim().toString()
                val todoCategoryName: String = insertTodoitmeCategory.text.trim().toString()

                var emptyExceptionContent: String? = null
                emptyExceptionContent = if (todoItemName.isEmpty()) "时光点滴 内容不能为空哦~" else null
                emptyExceptionContent = if (todoCategoryName.isEmpty()) "时光点滴 类别不能为空哦~" else null
                if(emptyExceptionContent != null) {
                    Toast.makeText(this, emptyExceptionContent, Toast.LENGTH_SHORT).show()
                    return
                }

                var todoCategoryId: Long = -1L
                val todoCategoryNames = viewModel.todoCategoryList.map { it.name }
                if(todoCategoryNames.contains(todoCategoryName)) {
                    todoCategoryId = viewModel.todoCategoryList.get(todoCategoryNames.indexOf(todoCategoryName)).id
                }

                val intent = Intent().apply {
                    putExtra("status", "OK")
                    putExtra("todoItemName", todoItemName)
                    putExtra("todoCategoryName", todoCategoryName)
                    putExtra("todoCategoryId", todoCategoryId)
                }
                setResult(RESULT_OK, intent)
                finish()
            }

            /**
             * 类别选择框
             */
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