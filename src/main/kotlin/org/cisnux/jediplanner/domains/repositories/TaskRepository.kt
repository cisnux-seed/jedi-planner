package org.cisnux.jediplanner.domains.repositories

import kotlinx.coroutines.flow.Flow
import org.cisnux.jediplanner.domains.entities.Task
import java.util.UUID

interface TaskRepository {
    suspend fun findById(id: UUID): Task?
    suspend fun insert(task: Task): Task?
    suspend fun update(task: Task): Task?
    suspend fun deleteById(id: UUID)
    fun findAll(userId: Long): Flow<Task>
}