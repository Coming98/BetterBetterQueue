package com.example.betterbetterqueue.logic.Entity

data class TodoItemStatus(
    val running: List<Long>,
    val visited: List<Long>,
)