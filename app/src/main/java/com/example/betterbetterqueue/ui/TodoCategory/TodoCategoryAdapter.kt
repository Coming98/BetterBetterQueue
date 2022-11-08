package com.example.betterbetterqueue.ui.TodoCategory

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betterbetterqueue.R
import com.example.betterbetterqueue.TodoApplication
import com.example.betterbetterqueue.logic.Entity.TodoCategory


class TodoCategoryAdapter(val todoCategoryList: List<TodoCategory>, val refreshTodoItemByCategory: (id: Long) -> Unit): RecyclerView.Adapter<TodoCategoryAdapter.ViewHolder>() {

    var currentTodoCategoryId: Long = -1L

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val todoCategoryName: TextView = view.findViewById(R.id.todocategory_name)
        val todoCategoryCount: TextView = view.findViewById(R.id.todocategory_count)
        val todoCategoryLayout: LinearLayout = view.findViewById(R.id.todocategory_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoCategoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todocategory_card, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val todoCategory = todoCategoryList[position]
            currentTodoCategoryId = todoCategory.id
            refreshTodoItemByCategory(todoCategory.id)
        }
        return holder
    }

    override fun onBindViewHolder(holder: TodoCategoryAdapter.ViewHolder, position: Int) {
        val todoCategory = todoCategoryList[position]
        holder.todoCategoryName.text = todoCategory.name
        holder.todoCategoryCount.text = todoCategory.count.toString()
        if(todoCategory.id == currentTodoCategoryId){
            holder.todoCategoryLayout.setBackgroundColor(TodoApplication.context.getColor(R.color.bg_orange))
            holder.todoCategoryCount.setTextColor(TodoApplication.context.getColor(R.color.white))
            holder.todoCategoryName.setTextColor(TodoApplication.context.getColor(R.color.white))
            holder.todoCategoryName.setTypeface(Typeface.DEFAULT_BOLD)
        } else {
            holder.todoCategoryLayout.setBackgroundColor(TodoApplication.context.getColor(R.color.white))
            holder.todoCategoryCount.setTextColor(TodoApplication.context.getColor(R.color.text_in_gray))
            holder.todoCategoryName.setTextColor(TodoApplication.context.getColor(R.color.black))
            holder.todoCategoryName.setTypeface(Typeface.DEFAULT)
        }
    }

    override fun getItemCount() = todoCategoryList.size
}