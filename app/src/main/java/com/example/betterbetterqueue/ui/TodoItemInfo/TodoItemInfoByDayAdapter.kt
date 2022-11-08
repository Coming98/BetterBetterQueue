package com.example.betterbetterqueue.ui.TodoItemInfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import com.example.betterbetterqueue.R
import com.example.betterbetterqueue.dateFormatter
import com.example.betterbetterqueue.logic.Entity.TodoItemInfo
import com.example.betterbetterqueue.timeFormatter


class TodoItemInfoByDayAdapter(val todoItemInfoList: List<TodoItemInfo>): RecyclerView.Adapter<TodoItemInfoByDayAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val todoItemInfoHeader: TextView = view.findViewById(R.id.todoitem_info_header)
        val todoItemInfoContent: TextView = view.findViewById(R.id.todoitem_info_content)
        val todoItemInfoContentLayout: FrameLayout = view.findViewById(R.id.todoitem_info_content_layout)
    }

    override fun getItemViewType(position: Int) = position % 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResource = if(viewType == TodoItemInfo.TYPE_LEFT) R.layout.todoitem_info_item_left else R.layout.todoitem_info_item_right
        val view = LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todoItemInfo: TodoItemInfo = todoItemInfoList[position]

        val todoItemInfoDate = todoItemInfo.beginTime?.dateFormatter()
        val todoItemInfoBeginTime = todoItemInfo.beginTime?.timeFormatter()
        val todoItemInfoEndTime = todoItemInfo.endTime?.timeFormatter()
        val todoItemInfoTotalMinute = todoItemInfo.totalTime / 60

        holder.todoItemInfoHeader.text = "${todoItemInfoBeginTime} ~ ${todoItemInfoEndTime} | ${todoItemInfoTotalMinute} min"
        holder.todoItemInfoContent.text = todoItemInfo.description
    }

    override fun getItemCount() = todoItemInfoList.size

}