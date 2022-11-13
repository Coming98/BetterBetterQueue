package com.example.betterbetterqueue

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
// import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.betterbetterqueue.TodoApplication.Companion.DEFAULT_CATEGORY_NAME
import com.example.betterbetterqueue.TodoApplication.Companion.SLIDE_THRESHOLD
import com.example.betterbetterqueue.TodoApplication.Companion.todoGson
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Entity.TodoItem
import com.example.betterbetterqueue.ui.Config.DBInJson
import com.example.betterbetterqueue.ui.MainViewModel
import com.example.betterbetterqueue.ui.TodoCategory.TodoCategoryAdapter
import com.example.betterbetterqueue.ui.TodoItem.InsertTodoItemActivity
import com.example.betterbetterqueue.ui.TodoItem.TodoItemAdapter
import com.example.betterbetterqueue.ui.TodoItemInfo.TodoItemInfoActivity
import com.example.betterbetterqueue.ui.TodoItemInfo.TodoItemInfoByDayActivity
import com.example.betterbetterqueue.ui.ToolbarFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.*
import java.time.LocalDateTime
import kotlin.math.min

// Done
class MainActivity : AppCompatActivity(), View.OnClickListener {

    val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) } // viewModel

    val recycler_todoitem: RecyclerView by lazy { findViewById(R.id.recycler_todoitem) } // recycler
    val recycler_todocategory: RecyclerView by lazy { findViewById(R.id.recycler_todocategory) }
    lateinit var adapter_todoitem: TodoItemAdapter
    lateinit var adapter_todoCategory: TodoCategoryAdapter

    val drawerLayout: DrawerLayout by lazy { findViewById(R.id.drawer_layout) } // todoCategory - Drawer

    // val todoItemSwipeRefresh: SwipeRefreshLayout by lazy {
    //     val swipeRefresh: SwipeRefreshLayout = findViewById(R.id.todoitem_swipe)
    //     swipeRefresh.setColorSchemeResources(R.color.blue)
    //     swipeRefresh
    // } // todoItemRefresh - SwipeRefresh

    lateinit var toolbarFragment: ToolbarFragment // ToolBar

    /**
     * 加载顺序一定要注意
     *      popupMenu 本应该是点击右上角配置按钮时才被懒加载
     *      但是我因为避免慵懒的代码在 by lazy 中堆叠, 把点击事件的配置放到了 onCreate 中
     *      这样 onCreate 中就会并发触发 toolbarFragment（线程处理的） 与 popupMenu 的加载（主线程处理）
     *      popupMenu 需要用到 onCreateView 之后的 toolbarFragment 这样就可能造成冲突
     */

    val popupMenu by lazy {
        val menu = PopupMenu(this, toolbarFragment.viewShowMenu)
        menu.menuInflater.inflate(R.menu.main_config, menu.menu)
        setHeaderMenuClick(menu)
        menu
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * 检查 intent 是否为外部应用 (微信) 传来的数据库导入的数据...
         */
        receiveActionSend(intent)

        /**
         * 加载标题栏
         */
        loadHeaderBar()


        /**
         * 加载本地缓存数据
         * 本地缓存数据加载完毕后，根据类别：加载 category 列表 与 todoItem 列表
         */
        viewModel.loadTodoCategoryIdCache()


        /**
         * 加载 Recycler 数据
         * todoitem
         * todocategory
         */
        val layoutManager_todoitem = GridLayoutManager(this, 2)
        adapter_todoitem = TodoItemAdapter(viewModel.todoItemList) { todoItemId ->
            TodoItemInfoActivity.onActionStart(this, todoItemId)
        }
        recycler_todoitem.layoutManager = layoutManager_todoitem
        recycler_todoitem.adapter = adapter_todoitem

        val layoutManager_todocategory = LinearLayoutManager(this)
        adapter_todoCategory = TodoCategoryAdapter(viewModel.todoCategoryList) { todoCategoryId ->
            if(viewModel.todoCategoryId != todoCategoryId) {
                viewModel.todoCategoryId = todoCategoryId
                viewModel.dumpTodoCategoryIdCache(todoCategoryId)
                viewModel.refreshTodoItemByCategory(todoCategoryId)
                viewModel.getAllTodoCategory() // 高亮选中的 TodoCategory
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
        }
        recycler_todocategory.layoutManager = layoutManager_todocategory
        recycler_todocategory.adapter = adapter_todoCategory


        /**
         * 必要的按钮点击事件
         * 新增 todoItem 按钮
         */
        val button_insert_todoitem: FloatingActionButton = findViewById(R.id.btn_insert_todoitem)
        val onClickedButtons = arrayOf(button_insert_todoitem)
        onClickedButtons.forEach { it.setOnClickListener(this) }



        /**
         * 下拉刷新事件
         */
        // todoItemSwipeRefresh.setOnRefreshListener {
        //     viewModel.refreshTodoItemByCategory(viewModel.todoCategoryId)
        // }


        /**
         * 数据变化的回调处理
         */

        /**
         * 插入 todoitem 后的回调
         *      自动切换为新增 item 的类别
         *      缓存当前类别
         *      刷新界面
         */
        observeInsertTodoItemResult()

        /**
         * 根据 todoCategoryId 获取 todoItemList 后的回调
         *      更新 adapter_todoitem
         *      更新标题栏名称及其点击事件
         *      如果是下拉刷新则关闭下拉刷新动画
         *      加载完成数据后检测是否是因为手动切换导致的加载，如果是加载完成后再关闭侧边栏，防止闪屏
         */
        observeRefreshTodoItemByCategoryResult()

        /**
         * 获取全部类别后的回调
         *      统计总 item 个数用于默认类别总 item 计数
         *      更新 adapter_todoCategory
         */
        observeGetAllTodoCategoryResult()

        /**
         * 更新 todoCategoryName 后的回调
         *      使用 toolbarFragment 接口更新标题名称
         *      [低效] 重新获取 todoCategory 列表 重新渲染当前选中的对象
         */
        observeUpdateTodoCategoryNameByIdResult()

        /**
         * 加载本地缓存的 todoCategoryId
         *      更新当前所选的 todoCategoryId
         *      更新 todoItemList
         *      更新 todoCategoryList
         */
        observeLoadTodoCategoryIdCacheResult()

        /**
         * 加载所有数据供导出
         *      后续可以添加动画, 导出比较费时
         */
        observeGetExportedDBDataResult()

        /**
         * 导入数据库后的回调
         *      导入后切换到默认类别
         *      重新加载数据
         *      刷星本地化缓存的类别
         */
        observeImportDBDataResult()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        receiveActionSend(intent)
    }

    private fun mainExportDB(dbInJson: DBInJson) {
        val outputPath = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + File.separator + "${packageName}.db.json")
        if(!outputPath.exists()) {
            outputPath.createNewFile()
            Log.d("TEMP", "CREATE FILE....")
        }
        Log.d("TEMP", outputPath.toString())

        val dbJson = todoGson.toJson(dbInJson)
        val fos = FileOutputStream(outputPath)
        fos.write(dbJson.toByteArray())
        fos.close()
        Toast.makeText(this, "数据库导出到: " + outputPath, Toast.LENGTH_SHORT).show()
    }

    private fun mainImportDB(importPath: File) {
        val fis = FileInputStream(importPath)
        val bufr = BufferedReader(InputStreamReader(fis))
        val dbJson = bufr.readText()
        fis.close()
        val dbInJson = todoGson.fromJson(dbJson, DBInJson::class.java)
        Log.d("TEMP", dbInJson.todoItemInfoList.size.toString())
        // 开启加载动画
        viewModel.importDBData(dbInJson)
        //
    }

    fun receiveActionSend(intent: Intent?) {
        Log.d("TEMP", intent.toString())
        when {
            intent?.action == Intent.ACTION_VIEW -> {
                Log.d("TEMP", intent?.type.toString())
                Log.d("TEMP", intent?.data.toString())
                Log.d("TEMP", intent?.data?.path.toString())
                val file = uriToFileApiQ(intent?.data, this)
                Log.d("TEMP", file.toString())
                file.let {
                    mainImportDB(file!!)
                }
            }
        }
    }

    //通过微信URI解析文件地址
    open fun uriToFileApiQ(uri: Uri?, context: Context): File? {
        var file: File? = null
        if (uri == null) return file
        //android10以上转换
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            file = File(uri.path)
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件复制到沙盒目录
            val contentResolver = context.contentResolver
            val displayName: String =
                (
                        (System.currentTimeMillis() + Math.round((Math.random() + 1) * 1000)).toString()
                        + "." + MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(contentResolver.getType(uri)))
            try {
                val `is` = contentResolver.openInputStream(uri)
                val cache = File(context.cacheDir.absolutePath, displayName)
                val fos = FileOutputStream(cache)
                fos.write(`is`!!.readBytes())
                // FileUtils.copy(`is`!!, fos)
                file = cache
                fos.close()
                `is`!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    // 获取新增 todoitem 的详细信息并新增 todoitem
    private val get_inserted_todoitem_activity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            if(result.data != null) {
                val data: Intent = result.data!!
                if(data.hasExtra("status") && data.getStringExtra("status") == "OK") {
                    val todoItemName = data.getStringExtra("todoItemName")!!
                    val todoCategoryName = data.getStringExtra("todoCategoryName")!!
                    val todoCategoryId: Long = data.getLongExtra("todoCategoryId", -1L)
                    val createTime = LocalDateTime.now()
                    val totalTime: Long = 0

                    val todoItem = TodoItem(name = todoItemName, createTime = createTime, totalTime = totalTime)
                    viewModel.insertTodoItem(todoItem, todoCategoryName, todoCategoryId)
                }
            }
        }
    }

    /**
     * 配置 Header 上的点击事件
     *      星迹的入口
     *      导出数据库
     *      导入数据库
     */
    private fun setHeaderMenuClick(menu: PopupMenu) {
        menu.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_info_by_day -> {
                    TodoItemInfoByDayActivity.onActionStart(this)
                    true
                }
                R.id.menu_export_db -> {
                    viewModel.getExportedDBData()
                    true
                }
                R.id.menu_import_db -> {
                    Toast.makeText(this, "请在微信中将数据库文件选择本应用打开~", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    private fun observeRefreshTodoItemByCategoryResult() {
        viewModel.refreshTodoItemByCategoryResult.observe(this, Observer { result ->
            val pairResult = result.getOrNull()
            if(pairResult != null) {
                val todoCategoryName = pairResult.first
                val todoItemList = pairResult.second

                viewModel.todoItemList.clear()
                viewModel.todoItemList.addAll(todoItemList)
                adapter_todoitem.notifyDataSetChanged()

                toolbarFragment.refreshToolbarName(todoCategoryName, viewModel.todoCategoryId == -1L)

                // if(todoItemSwipeRefresh.isRefreshing) {
                //     todoItemSwipeRefresh.isRefreshing = false
                // }

                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
        })
    }

    private fun observeInsertTodoItemResult() {
        viewModel.insertTodoItemResult.observe(this, Observer { result ->
            val todoCategoryId = result.getOrNull()
            if (todoCategoryId != null) {
                viewModel.todoCategoryId = todoCategoryId
                viewModel.dumpTodoCategoryIdCache(todoCategoryId)
                viewModel.refreshTodoItemByCategory(todoCategoryId)
                viewModel.getAllTodoCategory()
            } else {
                Toast.makeText(this, "Insert TodoItem Wrong!", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

    private fun observeGetAllTodoCategoryResult() {
        viewModel.getAllTodoCategoryResult.observe(this, Observer { result ->
            val todocategories = result.getOrNull()
            if(todocategories != null) {
                val totalCount = todocategories.sumOf { it.count }

                viewModel.todoCategoryList.clear()
                viewModel.todoCategoryList.add(TodoCategory(-1L, DEFAULT_CATEGORY_NAME, null, totalCount))
                viewModel.todoCategoryList.addAll(todocategories)
                adapter_todoCategory.currentTodoCategoryId = viewModel.todoCategoryId
                adapter_todoCategory.notifyDataSetChanged()
            }
        })
    }

    private fun observeUpdateTodoCategoryNameByIdResult() {
        viewModel.updateTodoCategoryNameByIdResult.observe(this, Observer { result ->
            val newTodoCategoryName = result.getOrNull()
            if(newTodoCategoryName != null) {
                // 更新了类别的名称
                toolbarFragment.refreshToolbarName(newTodoCategoryName, clearClickEvent = false)
                // 重新加载类别列表
                viewModel.getAllTodoCategory()
            }
        })
    }

    private fun observeLoadTodoCategoryIdCacheResult() {
        viewModel.loadTodoCategoryIdCacheResult.observe(this, Observer { result ->
            val todoCategoryId = result.getOrNull()
            if(todoCategoryId != null) {
                viewModel.todoCategoryId = todoCategoryId
                viewModel.refreshTodoItemByCategory(todoCategoryId)
                viewModel.getAllTodoCategory()
            }
        })
    }

    private fun observeGetExportedDBDataResult() {
        viewModel.getExportedDBDataResult.observe(this, Observer { result ->
            val dbInJson: DBInJson? = result.getOrNull()
            dbInJson?.let {
                mainExportDB(dbInJson)
            }
        })
    }

    private fun observeImportDBDataResult() {
        viewModel.importDBDataResult.observe(this, Observer { result ->
            val resultInfo = result.getOrNull()
            Log.d("TEMP", resultInfo.toString())
            resultInfo?.let {
                viewModel.todoCategoryId = -1
                viewModel.refreshTodoItemByCategory(viewModel.todoCategoryId)
                viewModel.getAllTodoCategory()
                viewModel.dumpTodoCategoryIdCache(viewModel.todoCategoryId)
            }
        })
    }

    // 加载标题栏
    @SuppressLint("RestrictedApi")
    private fun loadHeaderBar() {
        toolbarFragment = loadToolbarFragment(
            toolbarNmae=DEFAULT_CATEGORY_NAME,
            toolbarLeft=R.drawable.ic_category,
            toolbarRight=R.drawable.ic_config_64,
            changeToolbarName = {
                if(viewModel.todoCategoryId != -1L) {
                    viewModel.updateTodoCategoryNameById(it, viewModel.todoCategoryId)
                }
            },
            handleButtonLeft = {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            },
            handleButtonRight = {
                val menu = MenuPopupHelper(this,
                    (popupMenu.getMenu() as MenuBuilder), toolbarFragment.viewShowMenu)
                menu.setForceShowIcon(true)
                menu.show()
            }
        )
    }

    /**
     * 人性化的标题名称修改事件监听
     *      点击标题栏进入编辑状态后
     *      因为时水平输入框，因此水平越界没关系，只关注垂直方向上是否越界
     *      如果越界则表明点击 editText 之外的区域
     *      则自动取消焦点，隐藏软键盘
     * 左滑事件的监听
     *      联和 ACTION_DOWN 与 ACTION_UP 事件记录 down 与 up 的水平差距
     *      实现水平滑动以及滑动方向的监听
     * 根据 Android 的事件传递机制, MainActivity 的 onTouchEvent 会被
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                viewModel.downX = ev.getRawX()
                viewModel.downY = ev.getRawY()

                val view: View? = currentFocus

                if(view is EditText) {
                    var rect = Rect()
                    view.getGlobalVisibleRect(rect)
                    if(! rect.contains(rect.centerX(), ev.getRawY().toInt())) {
                        view.clearFocus()
                        if(TodoApplication.inputMethodManager.isActive) {
                            TodoApplication.inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                val upX = ev.getRawX()
                val upY = ev.getRawY()
                if(upX - viewModel.downX >= SLIDE_THRESHOLD) {
                    if(!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.openDrawer(GravityCompat.START)
                    }
                } else if (viewModel.downX - upX >= SLIDE_THRESHOLD) {
                    if(!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        TodoItemInfoByDayActivity.onActionStart(this)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    /**
     * 重新回到该页面时刷新 todoItemList
     */
    override fun onResume() {
        super.onResume()
        viewModel.refreshTodoItemByCategory(viewModel.todoCategoryId)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_insert_todoitem -> {
                InsertTodoItemActivity.onActionStart(this, get_inserted_todoitem_activity)
            }
        }
    }
}
