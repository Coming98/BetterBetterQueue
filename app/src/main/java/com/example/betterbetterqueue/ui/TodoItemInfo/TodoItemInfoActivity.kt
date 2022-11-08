package com.example.betterbetterqueue.ui.TodoItemInfo

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.betterbetterqueue.*
import com.example.betterbetterqueue.TodoApplication.Companion.inputMethodManager
import com.example.betterbetterqueue.logic.Entity.TickerInfos
import com.example.betterbetterqueue.logic.Entity.TodoItemInfo
import com.example.betterbetterqueue.ui.ToolbarFragment
import java.time.LocalDateTime
import java.time.ZoneOffset

class TodoItemInfoActivity : AppCompatActivity(), View.OnClickListener {

    /**
     * 外部启动需要给定 todoItemId
     */
    companion object {
        fun onActionStart(context: Context, todoItemId: Long) {
            val intent = Intent(context, TodoItemInfoActivity::class.java).apply {
                putExtra("todoItemId", todoItemId)
            }
            context.startActivity(intent)
        }
    }
    val viewModel by lazy { ViewModelProvider(this).get(TodoItemInfoViewModel::class.java) }

    lateinit var toolbarFragment: ToolbarFragment

    /**
     * 头部描述的线性布局 + 内部描述信息
     *      开始时间信息
     *      最近访问时间信息
     *      成长时间信息
     *      冷落时间信息
     */
    val todoItemHeader: LinearLayout by lazy { findViewById(R.id.todoitem_header) }
    val todoItemBegin: TextView by lazy { findViewById(R.id.todoitem_begin) }
    val todoItemRecent: TextView by lazy { findViewById(R.id.todoitem_recent) }
    val todoItemTotal: TextView by lazy { findViewById(R.id.todoitem_total) }
    val todoItemIgnore: TextView by lazy { findViewById(R.id.todoitem_ignore) }

    /**
     * 中间的 info 展示区域
     */
    val todoItemInfoRecycler: RecyclerView by lazy { findViewById(R.id.todoitem_info_recycler) }
    lateinit var todoItemInfoAdapter: TodoItemInfoAdapter

    /**
     * 计时与提交区域
     *      全局的时钟计时：Runnable + Handler
     */

    val itemInfoRunHandler: Handler = Handler(Looper.getMainLooper())
    private val itemInfoRunTicker: Runnable = object : Runnable {

        override fun run() {
            viewModel.tickerTime += 1

            setItemInfoRunTime(viewModel.tickerTime)
            val nowTime = SystemClock.uptimeMillis()
            val nextTime = nowTime + (1000 - nowTime % 1000)

            itemInfoRunHandler.postAtTime(this, nextTime)
        }
    }
    val tickerHandlerLayout: LinearLayout by lazy { findViewById(R.id.ticker_handler_layout) } // 计时器操作栏所在布局
    val tickerInputLayout: LinearLayout by lazy { findViewById(R.id.ticker_input_layout) } // 计时器信息录入栏所在布局
    val btnToggleTime: ImageButton by lazy { findViewById(R.id.btn_toggle_time) } // 开始 / 暂停
    val btnDeleteItemInfo: Button by lazy { findViewById(R.id.btn_delete_iteminfo) } // 清空
    val btnStoreItemInfo: Button by lazy { findViewById(R.id.btn_store_iteminfo) } // 记录信息
    val todoItemRunHour: TextView by lazy { findViewById(R.id.text_todoitem_run_hour) } // 时/分/秒 对应的 TextView
    val todoItemRunMinute: TextView by lazy { findViewById(R.id.text_todoitem_run_minute) }
    val todoItemRunSecond: TextView by lazy { findViewById(R.id.text_todoitem_run_second) }
    val insertTodoItemInfoDescription: EditText by lazy { findViewById(R.id.insert_todoiteminfo_description) } // 信息记录的 EditTest
    val btnSubmitTodoItemInfo: Button by lazy { findViewById(R.id.btn_submit_todoiteminfo) } // 提交记录的信息

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_item_info)

        /**
         * 根据 intent 传入的参数初始化 Activity
         *      根据 todoItemId 请求数据库, 获取 TodoItem
         *      获取 TodoItem 后回调更新
         */
        viewModel.todoItemId = intent.getLongExtra("todoItemId", -1)
        if(viewModel.todoItemId == -1L) {
            finish()
        }
        viewModel.getTodoItemById(viewModel.todoItemId)

        /**
         * 根据 本地缓存 进行初始化
         *      加载本地缓存的 TodoItemIdCache
         *      如果与 TodoItemId 一致, 则加载缓存的 TickerInfos
         *      否则进行默认的初始化操作
         */
        viewModel.loadTodoItemIdCache()


        /**
         * 初始化 TodoItemInfo 对应的 Adapter
         *      提供更改 TodoItemInfo 内容的接口:
         *          adapter 内部完成: 长摁事件的监听
         *          adapter 外部实现: 弹出输入框 - 用户设置内容 - 点击空白区域完成更新
         *              提交按钮是复用的 btnSubmitTodoItemInfo 数据库操作是更新还是插入将根据 TodoItemInfoId 来判别
         */
        val todoItemInfoLayoutManager = LinearLayoutManager(this)
        todoItemInfoAdapter = TodoItemInfoAdapter(viewModel.todoItemInfoList) { id, description ->
            viewModel.currentTodoItemInfoId = id
            showInfoDescriptionEdit(description)
        }
        todoItemInfoRecycler.layoutManager = todoItemInfoLayoutManager
        todoItemInfoRecycler.adapter = todoItemInfoAdapter

        /**
         * 处理必要点击事件
         */
        val onClickedButtons = arrayOf(btnToggleTime, btnDeleteItemInfo, btnStoreItemInfo, btnSubmitTodoItemInfo)
        onClickedButtons.forEach { it.setOnClickListener(this) }

        /**
         * 根据 Id 获取 TodoItem 对象的回调
         */
        observeGetTodoItemByIdResult()

        /**
         * 加载本地缓存的 TodoItemIdCache
         */
        observeLoadTodoItemIdCacheResult()

        /**
         * 加载 TodoItemInfos 后的回调
         */
        observeGetTodoItemInfosByIdResult()

        /**
         * 加载本地缓存的 TickerInfos
         */
        observeLoadTickerInfosCacheResult()

        /**
         * 监听软键盘的弹出与隐藏，防止 TodoItemInfos 被折叠
         *      本质是对 Activity 根布局高度变化的监听
         *      因此在事件处理时需要二次考虑软件盘的状态
         */
        SoftKeyBoardListener.setListener(this, 200, object : SoftKeyBoardListener.Companion.OnSoftKeyBoardChangeListener {
            override fun keyBoardHide(height: Int) {
                todoItemHeader.visibility = View.VISIBLE
            }

            override fun keyBoardShow(height: Int) {
                todoItemHeader.visibility = View.GONE
            }
        })

        /**
         * 插入或更新 TodoItemInfo 后的回调
         */
        observeInsertTodoItemInfoResult()

        /**
         * 更新 TodoItemName 后的回调
         */
        observeUpdateTodoItemNameByIdResult()

        /**
         * 更新置顶状态后的回调
         */
        observeUpdateTodoItemToptimeByIdResult()

        /**
         * 根据 Ticker 状态的变化缓存信息（已经新建了一个 TodoItemInfo 了才会覆盖）
         */
        observeTickerStatusObs()

        viewModel.loadTodoItemInfoDesCacheResult.observe(this, Observer { result ->
            val todoItemInfoDes = result.getOrNull()
            if(todoItemInfoDes != null) {
                viewModel.todoItemInfoDes = todoItemInfoDes
                showInfoDescriptionEdit(viewModel.todoItemInfoDes)
            }
        })
    }

    /**
     * 从后台到前台: 逻辑上模拟后台计时
     *      根据 ViewModel 的信息修正当前计时信息
     */
    override fun onResume() {
        super.onResume()
        fixTickerTime()
    }

    /**
     * 后台或息屏时关闭定时事件
     *      退出当前 Activity 时关闭时钟，利用本地缓存信息逻辑模拟计时
     */
    override fun onStop() {
        super.onStop()
        if(viewModel.tickerStatus == true) {
            itemInfoRunHandler.removeCallbacks(itemInfoRunTicker)
        }
    }

    /**
     * 显示 Ticker 的编辑栏，并初始化其值
     */
    private fun showInfoDescriptionEdit(text: String?) {
        tickerHandlerLayout.visibility = View.GONE
        tickerInputLayout.visibility = View.VISIBLE

        insertTodoItemInfoDescription.requestFocus()
        text?.let {
            insertTodoItemInfoDescription.setText(text)
            insertTodoItemInfoDescription.setSelection(text.length) // 设置光标到末尾
        }
        inputMethodManager.showSoftInput(insertTodoItemInfoDescription, 0)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            /**
             * 切换暂停与开始状态
             *      暂停 -> 开始:
             *          记录第一次开始时间（允许中间暂停/继续）
             *          记录本次计时开始的时间：用于逻辑上（通过时间差）模拟后台计时，恢复计时
             *          启动计时事件
             *          更新 UI
             *          更新计时器状态并缓存
             *      开始 -> 暂停:
             *          更新计时结束时的数值，缓存下来
             *          清除最近开始计时的时间：
             *          关闭计时事件
             *          更新 UI
             *          更新计时器状态并缓存
             */
            R.id.btn_toggle_time -> {

                if(viewModel.tickerBeginTime == null) {
                    viewModel.tickerBeginTime = LocalDateTime.now()
                }

                val targetTickerStatus = !viewModel.tickerStatus
                if(targetTickerStatus == true) {
                    viewModel.tickerRecentTime = LocalDateTime.now() // 更新本次计时的开始时间
                    viewModel.tickerTime -= 1 // 修补时间计数
                    itemInfoRunHandler.post(itemInfoRunTicker) // 开始计时事件
                } else {
                    viewModel.tickerBaseTime = viewModel.tickerTime
                    viewModel.tickerRecentTime = null
                    itemInfoRunHandler.removeCallbacks(itemInfoRunTicker)
                }
                btnToggleTime.setImageResource(if(viewModel.tickerStatus) R.drawable.ic_start_time_64 else R.drawable.ic_pause_time_64)
                toggleButton(btnDeleteItemInfo, true, viewModel.tickerStatus)
                toggleButton(btnStoreItemInfo, true, true)

                viewModel.tickerStatus = targetTickerStatus
                viewModel.tickerStatusObs.value = targetTickerStatus
            }
            /**
             * 删除本次计时记录
             *      初始化界面操作
             *      清空计时缓存：逻辑上清空，只需要将 todoItemId 置为 1 即可
             *      清空描述缓存
             */
            R.id.btn_delete_iteminfo -> {
                initTickerState()
                viewModel.dumpTodoItemIdCache(-1L)
                viewModel.dumpTodoItemInfoDesCache("")
            }
            /**
             * 弹出描述记录框
             *      手动弹出置当前 todoItemInfoId = -1 以区分对已存在 todoItemInfoDes 修改更新操作
             *      加载缓存的描述信息
             */
            R.id.btn_store_iteminfo -> {
                viewModel.currentTodoItemInfoId = -1L
                viewModel.loadTodoItemInfoDesCache()
            }
            /**
             * 插入/更新 TodoItemInfo
             *      插入操作：currentTodoItemInfoId == -1L
             *      更新操作：currentTodoItemInfoId != -1L
             *      获取描述信息，判空
             *      构造 todoItemInfo 对象
             *      执行数据库操作
             */
            R.id.btn_submit_todoiteminfo -> {
                val description = insertTodoItemInfoDescription.text.trim().toString()
                if(description.isEmpty()) {
                    Toast.makeText(this, "时光需留痕~", Toast.LENGTH_SHORT).show()
                    return
                }
                val todoItemInfo = TodoItemInfo(itemId = viewModel.todoItemId, description = description, beginTime = viewModel.tickerBeginTime,
                    endTime = LocalDateTime.now(), totalTime = viewModel.tickerTime.toLong())
                viewModel.insertTodoItemInfo(todoItemInfo, viewModel.todoItemId, viewModel.currentTodoItemInfoId)
            }
        }
    }

    /**
     * 根据 ViewModel 的信息修正当前计时信息
     *      开始计时状态:
     *          根据时间偏移与 BaseTime 计算得到正确的计时数值
     *          更新数值
     *          开始计时
     *      暂停计时状态：
     *          更新计时数值
     *
     */
    private fun fixTickerTime() {
        if(viewModel.tickerStatus == true) {
            val rightSecond = viewModel.tickerBaseTime + LocalDateTime.now().distence(viewModel.tickerRecentTime!!).toInt()
            if(rightSecond - viewModel.tickerTime >= 2) {
                viewModel.tickerTime = rightSecond
            }
            setItemInfoRunTime(viewModel.tickerTime - 1)
            itemInfoRunHandler.post(itemInfoRunTicker)
        } else {
            setItemInfoRunTime(viewModel.tickerTime)
        }
    }

    /**
     * 录入描述信息后：点击空白结束描述信息的编辑事件
     *      更新 UI
     *      如果是更新已存在的 TodoItemInfoDes：
     *          则表示用户取消了更新操作，重置 currentTodoItemInfoId = -1
     *      如果是新的 TodoItemInfoDes 的编辑：
     *          缓存 TodoItemInfoDes 的内容
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                val view: View? = currentFocus
                if(view is EditText) {
                    var rect: Rect = Rect()
                    view.getGlobalVisibleRect(rect)
                    if(! rect.contains(rect.centerX(), ev.getRawY().toInt())) {
                        view.clearFocus()
                        if(inputMethodManager.isActive) {
                            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

                            tickerHandlerLayout.visibility = View.VISIBLE
                            tickerInputLayout.visibility = View.GONE

                            if(viewModel.currentTodoItemInfoId != -1L) {
                                viewModel.currentTodoItemInfoId = -1L
                            } else {
                                viewModel.todoItemInfoDes = insertTodoItemInfoDescription.text.toString()
                                viewModel.dumpTodoItemInfoDesCache(viewModel.todoItemInfoDes)
                            }
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    /**
     * 加载标题栏
     *      左侧按钮为 返回
     *      中间名称为 TodoItemName
     *      右侧按钮为 置顶
     *      支持 TodoItemName 的更名操作：内部完成 UI 交互，外部提供数据库操作
     */
    private fun loadHeaderBar(todoItemName: String, rightNow: Boolean = false) {
        toolbarFragment = loadToolbarFragment(toolbarNmae=todoItemName, toolbarLeft=R.drawable.ic_back, toolbarRight=R.drawable.ic_top_64,
            rightNow = rightNow,
            changeToolbarName = {
                viewModel.updateTodoItemNameById(it, viewModel.todoItemId)
            },
            handleButtonLeft = {
                finish()
            },
            /**
             * 置顶事件
             *      topTime 初始化为 null, 表示 TodoItem 未加载的状态
             *      加载后 0L 表示未置顶, >0L 表示已置顶, 值的大小体现置顶的优先级
             */
            handleButtonRight = {
                if(viewModel.topTime != null) {
                    toolbarFragment.toolbarRight.isClickable = false
                    if(viewModel.topTime == 0L) { // 未置頂 -> 置頂
                        viewModel.updateTodoItemToptimeById(LocalDateTime.now().toLong() / 1000, viewModel.todoItemId)
                    } else {
                        viewModel.updateTodoItemToptimeById(0L, viewModel.todoItemId)
                    }
                }
            }
        )
    }

    /**
     * 根据置顶时间更新 右侧按钮的 UI
     */
    fun refreshToolbarRightByTopTime(topTime: Long) {
        if(topTime == 0L) {
            toolbarFragment.toolbarRight.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.text_in_gray, theme), )
        } else {
            toolbarFragment.toolbarRight.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.orange, theme), )
        }
    }

    /**
     * 默认的 Ticker 初始化
     *      初始化 tickerStatus, tickerTime, tickerBaseTime, tickerRecentTime, tickerBeginTime
     *      更新计时器 UI
     *      删除和记录按钮初始化为不可见不可交互
     */
    private fun initTickerState() {
        viewModel.tickerStatus = false // 计时器状态
        viewModel.tickerTime = 0 // 计时器显示时间
        viewModel.tickerBaseTime = 0 // tickerBaseTime + (LocalDateTime.now().distence(tickerRecentTime)).toSeconds() = tickerTime
        viewModel.tickerRecentTime = null // 最近开始计时的时间
        viewModel.tickerBeginTime = null // 计时器开始的时间

        setItemInfoRunTime(totalRunSeconds = viewModel.tickerTime) // 更新计时器显示
        btnToggleTime.setImageResource(R.drawable.ic_start_time_64 )
        btnDeleteItemInfo.visibility = View.INVISIBLE // 更新界面显示
        btnStoreItemInfo.visibility = View.INVISIBLE
        btnDeleteItemInfo.isEnabled = false
        btnStoreItemInfo.isEnabled = false
    }


    /**
     * 根据 Id 获取 TodoItem 对象的回调
     *      记录 TodoItemName + 加载标题栏：不能并行处理，否则可能出现未初始化的错误
     *      更新 Header 中的 todoItemBegin, todoItemTotal
     *      记录 topTime + 更新标题栏右侧置顶按钮 UI
     *      初始化 lastAccessTime 为 createTime 后续加载 TodoItemInfos 后将被更新
     *      加载 TodoItemInfos
     */
    private fun observeGetTodoItemByIdResult() {
        viewModel.getTodoItemByIdResult.observe(this, Observer { result ->
            val todoItem = result.getOrNull()
            if(todoItem != null) {
                viewModel.todoItemName = todoItem.name
                loadHeaderBar(viewModel.todoItemName, rightNow = true)

                todoItemBegin.text = todoItem.createTime.dateTimeFormatter()
                todoItemTotal.text = (todoItem.totalTime.toFloat() / 3600).toTypedString(1) + "小时"

                viewModel.topTime = todoItem.topTime
                refreshToolbarRightByTopTime(todoItem.topTime)

                viewModel.lastAccessTime = todoItem.createTime
                viewModel.getTodoItemInfosmById(viewModel.todoItemId)
            }
        })
    }

    /**
     * 加载本地缓存的 TodoItemIdCache
     *      如果与 TodoItemId 一致, 则加载缓存的 TickerInfos
     *      否则进行默认的初始化操作
     */
    private fun observeLoadTodoItemIdCacheResult() {
        viewModel.loadTodoItemIdCacheResult.observe(this, Observer { result ->
            val todoItemIdCache = result.getOrNull()
            if(todoItemIdCache != null) {
                if(todoItemIdCache == viewModel.todoItemId) {
                    viewModel.loadTickerInfosCache()
                } else {
                    initTickerState()
                }
            }
        })
    }

    /**
     * 加载本地缓存的 TickerInfos
     *      获取 TickerStatus: False 表示暂停状态, True 表示启动状态
     *      记录 tickerBeginTime, tickerBaseTime, tickerTime
     *      根据 tickerTime 跟新计时器 UI:
     *          如果是暂停状态, 只需更新最近开始时间为 null;
     *          如果是开始状态则记录最近开始时间, 下一秒会自动更正 tickerTime(因此没有打算在这里修正时间)
     *      根据 tickerStatus 更新 UI
     */
    private fun observeLoadTickerInfosCacheResult() {
        viewModel.loadTickerInfosCacheResult.observe(this, Observer { result ->
            val tickerInfos = result.getOrNull()
            if (tickerInfos != null) {
                val status = tickerInfos.status

                viewModel.tickerBeginTime = tickerInfos.beginTime.toLocalDateTime()
                viewModel.tickerBaseTime = tickerInfos.baseTime
                viewModel.tickerTime = tickerInfos.baseTime

                if(status == false) {
                    viewModel.tickerRecentTime = null
                    btnToggleTime.setImageResource(R.drawable.ic_start_time_64 )
                } else {
                    btnToggleTime.setImageResource(R.drawable.ic_pause_time_64 )
                    viewModel.tickerRecentTime = tickerInfos.recentTime.toLocalDateTime()
                }

                toggleButton(btnDeleteItemInfo, true, !status) // 暂停时可点击(false -> clickable)
                toggleButton(btnStoreItemInfo, true, true)

                viewModel.tickerStatus = status
                fixTickerTime()
            }
        })
    }



    /**
     * 加载 TodoItemInfos 后的回调
     *      更新 adapter
     *      更新 Header 中的 最近访问时间: todoItemRecent
     *      更新 Header 中的 冷落时间: todoItemIgnore
     */
    private fun observeGetTodoItemInfosByIdResult() {
        viewModel.getTodoItemInfosByIdResult.observe(this, Observer { result ->
            val todoItemInfoList = result.getOrNull()
            if(todoItemInfoList != null) {
                viewModel.todoItemInfoList.clear()
                viewModel.todoItemInfoList.addAll(todoItemInfoList)
                todoItemInfoAdapter.notifyDataSetChanged()

                if(viewModel.todoItemInfoList.size == 0) {
                    todoItemRecent.text = "还没有开始哦~"
                } else {
                    todoItemRecent.text = viewModel.todoItemInfoList[0].beginTime?.dateTimeFormatter()
                    viewModel.lastAccessTime = viewModel.todoItemInfoList[0].endTime
                }

                val disHours = LocalDateTime.now().distence(viewModel.lastAccessTime) / 3600
                if(disHours > 24){
                    val disDay = disHours / 24
                    val disHour = disHours % 24
                    todoItemIgnore.text = "${disDay} 天 ${disHour} 小时"
                } else {
                    todoItemIgnore.text = "日常活跃中..."
                }

            }
        })
    }


    /**
     * 根据给定时间（以秒为单位）更新计时器 UI
     */
    private fun setItemInfoRunTime(totalRunSeconds: Int) {
        val second = totalRunSeconds % 60
        val totalRunMinute = totalRunSeconds / 60
        val minute = totalRunMinute % 60
        val hour = totalRunMinute / 60

        todoItemRunHour.text = String.format("%02d", hour)
        todoItemRunMinute.text = String.format("%02d", minute)
        todoItemRunSecond.text = String.format("%02d", second)
    }

    /**
     * 操作 Button 是否可见 是否可点击
     */
    fun toggleButton(button: Button, visible: Boolean, clickable: Boolean) {
        button.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        button.isEnabled = clickable
        button.backgroundTintList = if(clickable) null else  ColorStateList.valueOf(
            resources.getColor(
                R.color.text_in_gray,
                theme
            )
        )
    }

    /**
     * 根据 Ticker 状态的变化缓存信息（已经新建了一个 TodoItemInfo 了才会覆盖）
     *      缓存 todoItemId
     *      缓存 tickerInfos
     */
    private fun observeTickerStatusObs() {
        viewModel.tickerStatusObs.observe(this) { status ->
            if(status == null) return@observe

            val temp = TickerInfos(
                viewModel.tickerStatus,
                viewModel.tickerBaseTime,
                viewModel.tickerRecentTime?.toLong() ?: -1L,
                viewModel.tickerBeginTime?.toLong() ?: -1L
            )
            Log.d("TEMP", temp.toString())
            viewModel.dumpTodoItemIdCache(viewModel.todoItemId)
            viewModel.dumpTickerInfosCache(
                TickerInfos(
                    viewModel.tickerStatus,
                    viewModel.tickerBaseTime,
                    viewModel.tickerRecentTime?.toLong() ?: -1L,
                    viewModel.tickerBeginTime?.toLong() ?: -1L
                )
            )
        }
    }


    /**
     * 更新置顶状态后的回调
     *      记录置顶时间
     *      更新 Toolbar 的 UI
     *      恢复 Toolbar 右侧按钮的点击功能 （防止连续点击造成更新混乱）
     */
    private fun observeUpdateTodoItemToptimeByIdResult() {
        viewModel.updateTodoItemToptimeByIdResult.observe(this, Observer { result ->
            val newTopTime = result.getOrNull()
            if(newTopTime != null) {
                viewModel.topTime = newTopTime
                refreshToolbarRightByTopTime(newTopTime)
            }
            toolbarFragment.toolbarRight.isClickable = true
        })
    }

    /**
     * 更新 TodoItemName 后的回调
     *      刷新 Toolbar
     */
    private fun observeUpdateTodoItemNameByIdResult() {
        viewModel.updateTodoItemNameByIdResult.observe(this, Observer { result ->
            val newTodoItemName = result.getOrNull()
            if(newTodoItemName != null) {
                toolbarFragment.refreshToolbarName(newTodoItemName, false)
            }
        })
    }

    /**
     * 插入或更新 TodoItemInfo 后的回调
     *      重新加载 TodoItemInfo
     *      更新 UI 到计时界面
     *      隐藏软键盘
     *      如果是插入操作则初始化计时界面: 逻辑上清空缓存
     *      如果是更新操作则重置 currentTodoItemInfoId 为 -1L (表示插入状态)
     */
    private fun observeInsertTodoItemInfoResult() {
        viewModel.insertTodoItemInfoResult.observe(this, Observer { result ->
            val todoItemInfoId = result.getOrNull()
            if(todoItemInfoId != null) {
                viewModel.getTodoItemInfosmById(viewModel.todoItemId)

                tickerHandlerLayout.visibility = View.VISIBLE
                tickerInputLayout.visibility = View.GONE

                inputMethodManager.hideSoftInputFromWindow(insertTodoItemInfoDescription.windowToken, 0)

                if(viewModel.currentTodoItemInfoId == -1L) {
                    initTickerState()
                    viewModel.dumpTodoItemInfoDesCache("")
                    viewModel.dumpTodoItemIdCache(-1L)
                    itemInfoRunHandler.removeCallbacks(itemInfoRunTicker)
                } else {
                    viewModel.currentTodoItemInfoId = -1L
                }
            }
        })
    }
}