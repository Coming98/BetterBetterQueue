package com.example.betterbetterqueue.ui.TodoItemInfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.betterbetterqueue.*
import com.example.betterbetterqueue.TodoApplication.Companion.DAY_IN_MILSECOND
import com.example.betterbetterqueue.TodoApplication.Companion.WORKDAY_IN_SECOND
import com.example.betterbetterqueue.TodoApplication.Companion.SLIDE_THRESHOLD
import com.example.betterbetterqueue.ui.ToolbarFragment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
// Done
class TodoItemInfoByDayActivity : AppCompatActivity() {

    /**
     * 外部启动接口
     */
    companion object {
        fun onActionStart(context: Context) {
            val intent = Intent(context, TodoItemInfoByDayActivity::class.java)
            context.startActivity(intent)
        }
    }

    val viewModel by lazy { ViewModelProvider(this).get(TodoItemInfoByDayViewModel::class.java) }

    lateinit var toolbarFragment: ToolbarFragment

    /**
     * 主体组件：RecyclerView
     */
    val dayInfoRecyler: RecyclerView by lazy { findViewById(R.id.todoitem_info_by_day_recycler) }
    lateinit var dayInfoAdapter: TodoItemInfoByDayAdapter

    /**
     * 底部组件
     *      进度条：根据 TodoApplication 中配置的工作时间，展示今天的进度信息
     *      进度条描述信息
     */
    val dayInfoProgressBar: ProgressBar by lazy { findViewById(R.id.todoitem_info_by_day_progress) }
    val dayInfoProgressBarDes: TextView by lazy { findViewById(R.id.todoitem_info_by_day_progress_text) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_item_info_by_day)

        /**
         * 标题栏
         */
        loadHeaderBar()

        /**
         * todoItemInfoAdapter
         */
        val layoutManager_todocategory = LinearLayoutManager(this)
        dayInfoAdapter = TodoItemInfoByDayAdapter(viewModel.todoItemInfoList)
        dayInfoRecyler.layoutManager = layoutManager_todocategory
        dayInfoRecyler.adapter = dayInfoAdapter

        /**
         * 根据时间跨度刷新 TodoItemInfoList
         *      目前时间跨度仅支持一天
         */
        observeGetTodoItemInfoByTimeScopeResult()

        /**
         * 根据时间跨度获取 TodoItemInfo
         */
        viewModel.getTodoItemInfoByTimeScope(viewModel.minLocalDateLong, viewModel.maxLocalDateLong)
    }

    /**
     * 加载 todoItemInfoList 后的更新
     *      更新 adapter
     *      根据时间跨度刷新标题名称
     *      进度条 UI 更新：
     *          totalSecond: 描述进度条信息
     *
     */
    private fun observeGetTodoItemInfoByTimeScopeResult() {
        viewModel.getTodoItemInfoByTimeScopeResult().observe(this, Observer { result ->
            val todoItemInfoList = result.getOrNull()
            if (todoItemInfoList != null) {
                viewModel.todoItemInfoList.clear()
                viewModel.todoItemInfoList.addAll(todoItemInfoList)
                dayInfoAdapter.notifyDataSetChanged()

                toolbarFragment.refreshToolbarName(viewModel.currentLocalDate, true)

                viewModel.totalSecond = todoItemInfoList.sumOf { it.totalTime }.toInt()
                val progressValue = ((viewModel.totalSecond.toFloat() / WORKDAY_IN_SECOND) * 100).toInt()
                if(progressValue <= 15) {
                    dayInfoProgressBar.progress = 0
                    dayInfoProgressBarDes.text = "星尘之外, 别有精彩~"
                } else {
                    dayInfoProgressBar.progress = progressValue
                    dayInfoProgressBarDes.text = "${viewModel.totalSecond/60} / ${WORKDAY_IN_SECOND / 60} (min)"
                }
            }
        })
    }

    private fun loadHeaderBar() {
        toolbarFragment = loadToolbarFragment(
            toolbarNmae= viewModel.currentLocalDate,
            toolbarLeft=R.drawable.ic_back,
            toolbarRight=R.drawable.ic_home_64,
            changeToolbarName = null,
            handleButtonLeft = {
               finish()
            },
            /**
             * 将时间跨度重置到今日
             *      Todo 未来可以考虑缓存查询到的时间跨度或者更方便的切换时间跨度
             */
            handleButtonRight = {
                if(viewModel.currentLocalDate != LocalDateTime.now().dateFormatter()) {
                    viewModel.currentLocalDate = LocalDateTime.now().dateFormatter()
                    viewModel.minLocalDateLong = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).toLong()
                    viewModel.maxLocalDateLong = viewModel.minLocalDateLong + (3600 * 24 - 1) * 1000
                    viewModel.getTodoItemInfoByTimeScope(viewModel.minLocalDateLong, viewModel.maxLocalDateLong)
                }
            }
        )
    }

    /**
     * 左右滑动事件的监听
     *      右滑日期减，左滑日期加
     *      通过日期更新时间跨度，重新加载 todoItemInfoList
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                viewModel.downX = ev.getRawX()
                viewModel.downY = ev.getRawY()
            }
            MotionEvent.ACTION_UP -> {
                val upX = ev.getRawX()
                val upY = ev.getRawY()
                if(upX - viewModel.downX >= SLIDE_THRESHOLD) {
                    // 右滑 - 日期减
                    viewModel.minLocalDateLong -= DAY_IN_MILSECOND
                    viewModel.maxLocalDateLong -= DAY_IN_MILSECOND
                    viewModel.currentLocalDate = viewModel.minLocalDateLong.toLocalDateTime().dateFormatter()
                    viewModel.getTodoItemInfoByTimeScope(viewModel.minLocalDateLong, viewModel.maxLocalDateLong)
                } else if (viewModel.downX - upX >= SLIDE_THRESHOLD) {
                    // 左滑 - 日期加
                    if(viewModel.minLocalDateLong + DAY_IN_MILSECOND > LocalDateTime.now().toLong()) {
                        Toast.makeText(this, "未来可期, 不要着急哦~", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.minLocalDateLong += DAY_IN_MILSECOND
                        viewModel.maxLocalDateLong += DAY_IN_MILSECOND
                        viewModel.currentLocalDate = viewModel.minLocalDateLong.toLocalDateTime().dateFormatter()
                        viewModel.getTodoItemInfoByTimeScope(viewModel.minLocalDateLong, viewModel.maxLocalDateLong)
                    }
                }

            }
        }
        return super.dispatchTouchEvent(ev)
    }
}