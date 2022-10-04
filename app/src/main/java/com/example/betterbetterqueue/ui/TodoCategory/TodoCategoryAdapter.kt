package com.example.betterbetterqueue.ui.TodoCategory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betterbetterqueue.R
import com.example.betterbetterqueue.logic.Entity.TodoCategory


class TodoCategoryAdapter(val todoCategoryList: List<TodoCategory>, val refreshTodoItemByCategory: (id: Long) -> Unit): RecyclerView.Adapter<TodoCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val todoCategoryName: TextView = view.findViewById(R.id.todocategory_name)
        val todoCategoryCount: TextView = view.findViewById(R.id.todocategory_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoCategoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todocategory_card, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val todoCategory = todoCategoryList[position]
            refreshTodoItemByCategory(todoCategory.id)
        }
        return holder
    }

    override fun onBindViewHolder(holder: TodoCategoryAdapter.ViewHolder, position: Int) {
        val todoCategory = todoCategoryList[position]
        holder.todoCategoryName.text = todoCategory.category
        holder.todoCategoryCount.text = todoCategory.count.toString()
    }

    override fun getItemCount() = todoCategoryList.size

}