package br.com.exemplo.todo

import android.app.Application
import androidx.room.Room
import br.com.exemplo.todo.data.local.TaskDatabase

class TodoApplication : Application() {
    companion object {
        lateinit var database: TaskDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java,
            "task_database"
        ).build()
    }
}
