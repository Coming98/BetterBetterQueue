package com.example.betterbetterqueue.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.betterbetterqueue.MainActivity
import com.example.betterbetterqueue.R
import com.example.betterbetterqueue.TodoApplication
import com.example.betterbetterqueue.TodoApplication.Companion.inputMethodManager
// Done
class ToolbarFragment(
    val changeToolbarName: ((newName: String) -> Unit)?, // 改名时所需要触发的数据库请求
    val handleButtonLeft: () -> Unit, // 左侧按钮点击事件处理
    val handleButtonRight: (() -> Unit)? // 右侧按钮点击事件处理
): Fragment() {

    lateinit var toolbarName: TextView
    lateinit var editToolbarName: EditText
    lateinit var toolbarLeft: Button
    lateinit var toolbarRight: Button
    lateinit var viewShowMenu: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.toolbar_fragment, container, false)
        toolbarName = view.findViewById(R.id.toolbar_name)
        editToolbarName = view.findViewById(R.id.edit_toolbar_name)
        toolbarLeft = view.findViewById(R.id.toolbar_left)
        toolbarRight = view.findViewById(R.id.toolbar_right)
        viewShowMenu = view.findViewById(R.id.view_show_menu)

        toolbarName.text = arguments?.getString("toolbarName")
        /**
         * 左右两侧的按钮处理
         *      如果指定了按钮的 ResourceId 则
         *      显示按钮 + 添加点击事件
         */
        update_toolbar_button("toolbarLeft", toolbarLeft, handleButtonLeft)
        update_toolbar_button("toolbarRight", toolbarRight, handleButtonRight)

        /**
         * 标题改名事件的处理
         *      实现 TextView 与 EditText 动画切换的处理
         *      回调 Activity 的数据库处理函数
         */
        handleChangeToolbarName()

        return view
    }


    fun update_toolbar_button(button_name: String, button_obj: Button, handleButtonClicked: (() -> Unit)?) {
        if(arguments?.containsKey(button_name) == true) {
            button_obj.visibility = View.VISIBLE
            button_obj.isClickable = true
            handleButtonClicked?.let { button_obj.setOnClickListener {handleButtonClicked()} }
            button_obj.setBackgroundResource(arguments?.getInt(button_name) ?: R.drawable.ic_category)
        } else {
            button_obj.visibility = View.INVISIBLE
            button_obj.isClickable = false
        }
    }

    fun refreshToolbarName(name: String, clearClickEvent: Boolean) {
        toolbarName.text = name
        if(clearClickEvent) {
            toolbarName.setOnClickListener {  }
        } else {
            handleChangeToolbarName()
        }
    }

    /**
     * 标题改名事件的处理
     *      实现 TextView 与 EditText 动画切换的处理
     *      回调 Activity 的数据库处理函数
     */
    fun handleChangeToolbarName() {
        // 数据库请求为 null 时表示标题栏不支持改名请求
        if(changeToolbarName == null) return

        // 实现 TextView 与 EditText 动画切换的处理
        toolbarName.setOnClickListener {
            toolbarName.visibility = View.GONE
            editToolbarName.visibility = View.VISIBLE
            editToolbarName.setText(toolbarName.text.toString())
            editToolbarName.requestFocus()
            editToolbarName.selectAll()
            inputMethodManager.showSoftInput(editToolbarName, 0)
        }

        // 丢失焦点时：回调 Activity 的数据库处理函数
        editToolbarName.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus == false) {
                toolbarName.visibility = View.VISIBLE
                editToolbarName.visibility = View.GONE
                val newName = editToolbarName.text.toString().trim()
                if(!newName.isEmpty() && newName != toolbarName.text) {
                    toolbarName.text = newName
                    changeToolbarName?.let { it(newName) }
                }
            }
        }
    }
}