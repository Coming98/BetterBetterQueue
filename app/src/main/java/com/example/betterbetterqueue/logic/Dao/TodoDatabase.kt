package com.example.betterbetterqueue.logic.Dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.betterbetterqueue.logic.Entity.TodoCategory
import com.example.betterbetterqueue.logic.Entity.TodoCategoryInfo
import com.example.betterbetterqueue.logic.Entity.TodoItem
import com.example.betterbetterqueue.logic.Entity.TodoItemInfo

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = arrayOf(
        TodoCategory::class,
        TodoCategoryInfo::class,
        TodoItem::class,
        TodoItemInfo::class
    ),
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoCategoryDao(): TodoCategoryDao
    abstract fun todoItemDao(): TodoItemDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var instance: TodoDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): TodoDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                TodoDatabase::class.java,
                "todo_database"
            ).fallbackToDestructiveMigration()
                .build().apply {
                instance = this
            }

        }
    }
}