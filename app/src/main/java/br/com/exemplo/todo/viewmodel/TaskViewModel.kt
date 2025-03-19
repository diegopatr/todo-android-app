package br.com.exemplo.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.exemplo.todo.data.model.Task
import br.com.exemplo.todo.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    // val allTasks = repository.allTasks

    val allTasks = repository.allTasks
        .distinctUntilChanged() // Emit only unique updates
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}
