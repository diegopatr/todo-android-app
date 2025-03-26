package br.com.exemplo.todo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.exemplo.todo.data.model.TaskEntity

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
