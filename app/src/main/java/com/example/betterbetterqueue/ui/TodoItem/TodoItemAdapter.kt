package com.example.betterbetterqueue.ui.TodoItem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betterbetterqueue.R
import com.example.betterbetterqueue.dateFormatter
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Entity.TodoItem
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

class TodoItemAdapter(val todoItemList: List<TodoItem>): RecyclerView.Adapter<TodoItemAdapter.ViewHolder>() {

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
        holder.todoItemName.text = todoitem.name
        holder.todoItemCreatetime.text = todoitem.createTime?.dateFormatter()
        holder.todoItemTotalTime.text = todoitem.totalTime.toString()

    }

    override fun getItemCount() = todoItemList.size

}

