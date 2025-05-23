package org.cisnux.jediplanner.domains.services

import kotlinx.coroutines.flow.Flow
import org.cisnux.jediplanner.domains.services.dtos.NewTaskDTO
import org.cisnux.jediplanner.domains.services.dtos.TaskRespDTO
import org.cisnux.jediplanner.domains.services.dtos.TaskUpdateDTO
import java.util.UUID

interface TaskService {
    fun getTasks(): Flow<TaskRespDTO>
    suspend fun getTaskById(id: String): TaskRespDTO
    suspend fun createTask(newTaskDTO: NewTaskDTO): UUID
    suspend fun updateTask(taskUpdateDTO: TaskUpdateDTO): UUID?
    suspend fun deleteTask(id: String): UUID?
}