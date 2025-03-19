package br.com.exemplo.todo.data.repository

import br.com.exemplo.todo.data.local.TaskDao
import br.com.exemplo.todo.data.model.Task

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}
