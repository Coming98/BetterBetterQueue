package com.example.betterbetterqueue.ui.TodoItemInfo

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betterbetterqueue.R
import com.example.betterbetterqueue.dateFormatter
import com.example.betterbetterqueue.logic.Entity.TodoItemInfo
import com.example.betterbetterqueue.timeFormatter

class TodoItemInfoAdapter(val todoItemInfoList: List<TodoItemInfo>, val handleTodoItemInfoContentChangeListener: (todoItemInfoId: Long, todoItemInfoDescription: String) -> Unit): RecyclerView.Adapter<TodoItemInfoAdapter.ViewHolder>() {



    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val todoItemInfoHeader: TextView = view.findViewById(R.id.todoitem_info_header)
        val todoItemInfoContent: TextView = view.findViewById(R.id.todoitem_info_content)
    }

    override fun getItemViewType(position: Int) = position % 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResource = if(viewType == TodoItemInfo.TYPE_LEFT) R.layout.todoitem_info_item_left else R.layout.todoitem_info_item_right
        val view = LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
        val holder = ViewHolder(view)
        holder.todoItemInfoContent.setOnLongClickListener {
            val todoItemInfo = todoItemInfoList[holder.adapterPosition]
            handleTodoItemInfoContentChangeListener(todoItemInfo.id, todoItemInfo.description)
            true
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todoItemInfo: TodoItemInfo = todoItemInfoList[position]
        val todoItemInfoDate = todoItemInfo.beginTime?.dateFormatter()
        val todoItemInfoBeginTime = todoItemInfo.beginTime?.timeFormatter()
        val todoItemInfoEndTime = todoItemInfo.endTime?.timeFormatter()

        holder.todoItemInfoHeader.text = "${todoItemInfoDate} | ${todoItemInfoBeginTime} ~ ${todoItemInfoEndTime}"
        holder.todoItemInfoContent.text = todoItemInfo.description
    }

    override fun getItemCount() = todoItemInfoList.size


}