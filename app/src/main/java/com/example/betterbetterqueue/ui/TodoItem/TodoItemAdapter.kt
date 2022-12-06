package com.example.betterbetterqueue.ui.TodoItem

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betterbetterqueue.R
import com.example.betterbetterqueue.TodoApplication
import com.example.betterbetterqueue.dateFormatter
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Entity.TodoItem
import com.example.betterbetterqueue.logic.Entity.TodoItemStatus
import com.example.betterbetterqueue.toTypedString
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
// Done
class TodoItemAdapter(val todoItemList: List<TodoItem>, val startTodoItemInfo: (todoItemId: Long) -> Unit): RecyclerView.Adapter<TodoItemAdapter.ViewHolder>() {

    var todoItemStatus: TodoItemStatus? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val todoItemLayout: RelativeLayout = view.findViewById(R.id.todoitem_layout)
        val todoItemName: TextView = view.findViewById(R.id.todoitem_card_name)
        val todoItemCreatetime: TextView = view.findViewById(R.id.todoitem_card_createtime)
        val todoItemTotalTime: TextView = view.findViewById(R.id.todoitem_card_totaltime)
        val todoItemIcTop: View = view.findViewById(R.id.view_ic_top)
    }

    /**
     * 点击后进入 TodoItemInfoActivity
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todoitem_card, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val todoItem = todoItemList[position]
            startTodoItemInfo(todoItem.id)
        }
        return holder
    }

    /**
     * 更新 UI
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todoitem = todoItemList[position]
        holder.todoItemName.text = todoitem.name
        holder.todoItemCreatetime.text = todoitem.createTime?.dateFormatter()
        holder.todoItemTotalTime.text = "${(todoitem.totalTime.toFloat() / 3600).toTypedString(1)}"

        if(todoitem.topTime != 0L) {
            holder.todoItemIcTop.visibility = View.VISIBLE
        } else {
            holder.todoItemIcTop.visibility = View.GONE
        }

        if(todoItemStatus != null) {
            if(todoItemStatus!!.running.contains(todoitem.id)) {
                holder.todoItemLayout.setBackgroundColor(TodoApplication.context.getColor(R.color.todo_running))
            }
            else if(todoItemStatus!!.visited.contains(todoitem.id)) {
                holder.todoItemLayout.setBackgroundColor(TodoApplication.context.getColor(R.color.todo_visited))
            } else {
                holder.todoItemLayout.setBackgroundColor(TodoApplication.context.getColor(R.color.white))
            }
        }

    }

    override fun getItemCount() = todoItemList.size

}

