package com.example.betterbetterqueue.ui.TodoItem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betterbetterqueue.R
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Entity.TodoItem

class TodoItemAdapter(val todoItemList: List<TodoCategory>): RecyclerView.Adapter<TodoItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val todoItemName: TextView = view.findViewById(R.id.todoitem_card_name)
        val todoItemCreatetime: TextView = view.findViewById(R.id.todoitem_card_createtime)
        val todoItemTotalTime: TextView = view.findViewById(R.id.todoitem_card_totaltime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todoitem_card, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todoitem = todoItemList[position]
        holder.todoItemName.text = todoitem.category
        holder.todoItemCreatetime.text = todoitem.createTime.toString()
        holder.todoItemTotalTime.text = todoitem.count.toString()
    }

    override fun getItemCount() = todoItemList.size

}