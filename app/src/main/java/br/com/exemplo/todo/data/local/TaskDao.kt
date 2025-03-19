package br.com.exemplo.todo.data.local

import androidx.room.*
import br.com.exemplo.todo.data.model.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): kotlinx.coroutines.flow.Flow<List<Task>>
}
