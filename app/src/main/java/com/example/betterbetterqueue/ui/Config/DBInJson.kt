package com.example.betterbetterqueue.ui.Config

import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Entity.TodoCategoryInfo
import com.example.betterbetterqueue.logic.Entity.TodoItem
import com.example.betterbetterqueue.logic.Entity.TodoItemInfo

data class DBInJson(
    val todoItemList: List<TodoItem>,
    val todoItemInfoList: List<TodoItemInfo>,
    val todoCategoryList: List<TodoCategory>,
    val todoCategoryInfoList: List<TodoCategoryInfo>
)