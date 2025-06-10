package org.cisnux.jediplanner.domains.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import org.cisnux.jediplanner.domains.entities.Task
import java.util.UUID

interface TaskService {
    fun getAllByEmail(owner: Long): Flow<Task>
    suspend fun getRealtimeTasks(owner: Long): SharedFlow<List<Task>>
    suspend fun refreshTasks(owner: Long)
    suspend fun getById(owner: Long, id: UUID): Task
    suspend fun create(newTask: Task): UUID
    suspend fun update(newTask: Task): UUID
    suspend fun delete(owner: Long, id: UUID): UUID
}