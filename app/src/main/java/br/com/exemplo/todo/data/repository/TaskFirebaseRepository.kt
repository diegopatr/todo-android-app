package br.com.exemplo.todo.data.repository

import android.util.Log
import br.com.exemplo.todo.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class TaskFirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val taskCollection = firestore.collection(TASK_COLLECTION)
    private val _tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    val allTasks: StateFlow<List<Task>> = _tasksFlow // Expose StateFlow

    companion object {
        private const val TASK_COLLECTION = "tasks"
    }

    init {
        taskCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Error fetching tasks: ${error.message}") // Log the error
                return@addSnapshotListener
            }

            _tasksFlow.value = snapshot?.documents?.mapNotNull { it.toObject<Task>() } ?: emptyList()
        }
    }

    suspend fun insertTask(task: Task) {
        try {
            val documentId = task.id ?: taskCollection.document().id
            val taskWithId = task.copy(id = documentId)
            taskCollection.document(documentId).set(taskWithId).await()
        } catch (e: Exception) {
            Log.e("TaskFirebaseRepository", "Error adding task", e)
            throw e // Re-throw to propagate the error if needed
        }
    }

    suspend fun deleteTask(task: Task) {
        if (task.id == null) {
            throw IllegalArgumentException("Task ID cannot be null for deletion")
        } else {
            try {
                val taskId = task.id ?: throw IllegalArgumentException("Task ID cannot be null for deletion")
                taskCollection.document(taskId).delete().await()
            } catch (e: Exception) {
                Log.e("TaskFirebaseRepository", "Error deleting task", e)
                throw e
            }
        }
    }
}
