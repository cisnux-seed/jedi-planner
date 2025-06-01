package org.cisnux.jediplanner.domains.services

import kotlinx.coroutines.flow.Flow
import org.cisnux.jediplanner.domains.entities.Task

interface TaskService {
    fun getAllByEmail(owner: String): Flow<Task>
    suspend fun getById(owner: String, id: String): Task
    suspend fun create(newTask: Task): String
    suspend fun update(newTask: Task): String?
    suspend fun delete(owner: String, id: String): String?
}