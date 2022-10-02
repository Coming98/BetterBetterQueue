package com.example.betterbetterqueue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.ui.MainViewModel
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button_insert_tc: Button = findViewById(R.id.btn_insert_tc)
        val button_getall_tc: Button = findViewById(R.id.btn_getall_tc)
        val button_delete_tc: Button = findViewById(R.id.btn_delete_tc)
        val button_update_tc: Button = findViewById(R.id.btn_update_tc)
        val button_get_tc_by_id: Button = findViewById(R.id.btn_get_tc_by_id)
        val button_get_tc_by_category: Button = findViewById(R.id.btn_get_tc_by_category)
        val onClickedButtons = arrayOf(button_insert_tc, button_getall_tc, button_delete_tc, button_update_tc, button_get_tc_by_id, button_get_tc_by_category)
        onClickedButtons.forEach { it.setOnClickListener(this) }

        viewModel.insertTodoCategoryResult.observe(this, Observer { result ->
            val resultInfo = result.getOrNull()
            if (resultInfo != null) {
                Toast.makeText(this, resultInfo, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Insert Wrong!", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })

        viewModel.getAllTodoCategoryResult.observe(this, Observer { result ->
            val todocategories = result.getOrNull()
            if(todocategories != null) {
                todocategories.forEach { todocategory -> Log.d("TEMP", "(${todocategory.id}, ${todocategory.category}, ${todocategory.createTime}, ${todocategory.count})")}
            }
        })

        viewModel.deleteTodoCategoryResult.observe(this, Observer { result ->
            val resultInfo = result.getOrNull()
            if (resultInfo != null) {
                Toast.makeText(this, resultInfo, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Delete Wrong!", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })

        viewModel.updateCategoryByIdResult.observe(this, Observer { result ->
            val resultInfo = result.getOrNull()
            if (resultInfo != null) {
                Toast.makeText(this, resultInfo, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Update Wrong!", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })

        viewModel.getTodoCategoryByIdResult.observe(this, Observer { result ->
            val targetTodoCategory = result.getOrNull()
            if(targetTodoCategory != null) {
                Log.d("TEMP", "(${targetTodoCategory.id}, ${targetTodoCategory.category}, ${targetTodoCategory.createTime}, ${targetTodoCategory.count})")
            } else {
                Toast.makeText(this, "GET BY ID Wrong!", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.getTodoCategoryByCategoryResult.observe(this, Observer { result ->
            val targetTodoCategory = result.getOrNull()
            if(targetTodoCategory != null) {
                Log.d("TEMP", "(${targetTodoCategory.id}, ${targetTodoCategory.category}, ${targetTodoCategory.createTime}, ${targetTodoCategory.count})")
            } else {
                Toast.makeText(this, "GET BY CATEGIRY Wrong!", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_insert_tc -> {
                viewModel.insertTodoCategory(TodoCategory(category = (1..100).random().toString(), createTime = Date(), count = (1..100).random()))
            }
            R.id.btn_getall_tc -> {
                viewModel.getAllTodoCategory()
            }
            R.id.btn_delete_tc -> {
                viewModel.deleteTodoCategory(viewModel.getAllTodoCategoryResult.value?.getOrNull()!!.get(0))
            }
            R.id.btn_update_tc -> {
                viewModel.updateCategoryById("COMING", viewModel.getAllTodoCategoryResult.value?.getOrNull()!!.get(0).id)
            }
            R.id.btn_get_tc_by_id -> {
                viewModel.getTodoCategoryById(2)
            }
            R.id.btn_get_tc_by_category -> {
                viewModel.getTodoCategoryByCategory("COMING")
            }
        }
    }
}