package org.cisnux.jediplanner.domains.repositories

import kotlinx.coroutines.flow.Flow
import org.cisnux.jediplanner.domains.entities.Task

interface TaskRepository {
    suspend fun findById(id: String): Task?
    suspend fun insert(task: Task): Task?
    suspend fun update(task: Task): Task?
    suspend fun deleteById(id: String)
    fun findAll(email: String): Flow<Task>
}