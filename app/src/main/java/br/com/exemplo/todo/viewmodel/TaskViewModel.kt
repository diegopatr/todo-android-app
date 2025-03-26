package br.com.exemplo.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.exemplo.todo.data.model.Task
import br.com.exemplo.todo.data.repository.TaskFirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskFirebaseRepository) : ViewModel() {

    // Loading and error state to handle UI feedback
    private val _uiState = MutableStateFlow<TaskUiState>(TaskUiState.Idle)
    val uiState: StateFlow<TaskUiState> = _uiState

    // Observe all tasks from the repository
    val allTasks = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Add a task to the repository
    fun addTask(task: Task) {
        viewModelScope.launch {
            _uiState.value = TaskUiState.Loading
            try {
                repository.insertTask(task)
                _uiState.value = TaskUiState.Success("Task added successfully!")
            } catch (e: Exception) {
                _uiState.value = TaskUiState.Error("Failed to add task: ${e.localizedMessage}")
            }
        }
    }

    // Remove a task from the repository
    fun removeTask(task: Task) {
        viewModelScope.launch {
            _uiState.value = TaskUiState.Loading
            try {
                repository.deleteTask(task)
                _uiState.value = TaskUiState.Success("Task removed successfully!")
            } catch (e: Exception) {
                _uiState.value = TaskUiState.Error("Failed to remove task: ${e.localizedMessage}")
            }
        }
    }

    // Clear UI feedback after it's processed
    fun clearUiState() {
        _uiState.value = TaskUiState.Idle
    }
}

// Define a sealed class to represent the UI states
sealed class TaskUiState {
    object Idle : TaskUiState() // No action is taking place
    object Loading : TaskUiState() // Loading state during a task's operation
    data class Success(val message: String) : TaskUiState() // Success state with a message
    data class Error(val error: String) : TaskUiState() // Error state with a message
}