package org.cisnux.jediplanner.domains.repositories

import kotlinx.coroutines.flow.Flow
import org.cisnux.jediplanner.domains.repositories.entities.Task

interface TaskRepository {
    suspend fun findById(id: String): Task?
    suspend fun save(task: Task): Task?
    suspend fun update(task: Task): Task?
    suspend fun delete(id: String)
    fun findAll(): Flow<Task>
}