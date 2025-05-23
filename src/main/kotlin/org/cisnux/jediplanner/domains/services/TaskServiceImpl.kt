package org.cisnux.jediplanner.domains.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.commons.exceptions.APIException
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.repositories.TaskRepository
import org.cisnux.jediplanner.domains.repositories.entities.Task
import org.cisnux.jediplanner.domains.services.dtos.NewTaskDTO
import org.cisnux.jediplanner.domains.services.dtos.TaskRespDTO
import org.cisnux.jediplanner.domains.services.dtos.TaskUpdateDTO
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
 class TaskServiceImpl(val taskRepository: TaskRepository, val validationService: ValidationService) : TaskService,
    Loggable {
    override fun getTasks(): Flow<TaskRespDTO> = taskRepository.findAll().map {
        TaskRespDTO(
            id = it.id!!,
            title = it.title,
            description = it.description,
            dueDate = it.dueDate.toEpochMilli(),
            hasCompleted = it.hasCompleted
        )
    }
        .flowOn(Dispatchers.IO)

    override suspend fun getTaskById(id: String): TaskRespDTO = withContext(Dispatchers.IO) {
        taskRepository.findById(id)?.let {
            TaskRespDTO(
                id = it.id!!,
                title = it.title,
                description = it.description,
                dueDate = it.dueDate.toEpochMilli(),
                hasCompleted = it.hasCompleted
            )
        } ?: throw APIException.NotFoundResourceException(
            statusCode = 404,
            message = "Task with id $id not found"
        )
    }

    override suspend fun createTask(newTaskDTO: NewTaskDTO): UUID = withContext(Dispatchers.IO) {
        log.info("creating new task: $newTaskDTO")
        validationService.validateObject(newTaskDTO)
        val task = Task(
            id = UUID.randomUUID(),
            title = newTaskDTO.title!!,
            description = newTaskDTO.description!!,
            dueDate = newTaskDTO.dueDate!!
        )
        taskRepository.save(task)?.id ?:
            throw APIException.InternalServerException(
                statusCode = 500,
                message = "Failed to create task"
            )
    }

    override suspend fun updateTask(taskUpdateDTO: TaskUpdateDTO): UUID? = withContext(Dispatchers.IO) {
        log.info("updating task: $taskUpdateDTO")
        validationService.validateObject(taskUpdateDTO)
        val task = taskRepository.findById(taskUpdateDTO.id!!)
            ?: throw APIException.NotFoundResourceException(
                statusCode = 404,
                message = "Task with id ${taskUpdateDTO.id} not found"
            )
        val updatedTask = task.copy(
            title = taskUpdateDTO.title!!,
            description = taskUpdateDTO.description!!,
            dueDate = taskUpdateDTO.dueDate!!,
            hasCompleted = taskUpdateDTO.hasCompleted!!,
            updatedAt = Instant.now()
        )
        taskRepository.update(updatedTask)?.id
    }

    override suspend fun deleteTask(id: String): UUID? = withContext(Dispatchers.IO) {
        val task = taskRepository.findById(id)
            ?: throw APIException.NotFoundResourceException(
                statusCode = 404,
                message = "Task with id $id not found"
            )
        taskRepository.delete(id)
        task.id
    }
}