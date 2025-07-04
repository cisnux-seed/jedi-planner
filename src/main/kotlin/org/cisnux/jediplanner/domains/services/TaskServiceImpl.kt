package org.cisnux.jediplanner.domains.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.commons.exceptions.APIException
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.repositories.TaskRepository
import org.cisnux.jediplanner.domains.entities.Task
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TaskServiceImpl(private val taskRepository: TaskRepository) : TaskService,
    Loggable {
    private val taskFlows = mutableMapOf<Long, MutableStateFlow<List<Task>>>()
    private val mutex = Mutex()

    override suspend fun getRealtimeTasks(owner: Long): SharedFlow<List<Task>> {
        mutex.withLock {
            if (!taskFlows.containsKey(owner)) {
                val initialTasks = taskRepository.findAll(owner).toList()
                taskFlows[owner] = MutableStateFlow(initialTasks)
            }
        }
        return taskFlows[owner]!!.stateIn(
            scope = TaskCoroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = taskFlows[owner]!!.value
        )
    }

    override fun getAllByEmail(owner: Long): Flow<Task> =
        taskRepository.findAll(owner)


    override suspend fun refreshTasks(owner: Long) {
        log.info("Refreshing tasks for user: $owner")
        val updatedTasks = taskRepository.findAll(owner).toList()
        taskFlows[owner]?.emit(updatedTasks)
    }


    override suspend fun getById(owner: Long, id: UUID): Task = withContext(Dispatchers.IO) {
        val task = taskRepository.findById(id) ?: throw APIException.NotFoundResourceException(
            statusCode = HttpStatus.NOT_FOUND.value(),
            message = "Task with id $id not found"
        )

        if (task.userId != owner) {
            throw APIException.ForbiddenException(
                statusCode = HttpStatus.FORBIDDEN.value(),
                message = "you are not authorized to access this task"
            )
        }

        task
    }

    override suspend fun create(newTask: Task): UUID = withContext(Dispatchers.IO) {
        log.info("creating new task: $newTask")
        taskRepository.insert(newTask)?.id?.also {
            refreshTasks(newTask.userId)
        } ?: throw APIException.InternalServerException(
            statusCode = 500,
            message = "Failed to create task"
        )
    }

    override suspend fun update(newTask: Task): UUID = withContext(Dispatchers.IO) {
        log.info("updating task: $newTask")
        val task = taskRepository.findById(newTask.id)
            ?: throw APIException.NotFoundResourceException(
                message = "task with id ${newTask.id} not found"
            )
        if (task.userId != newTask.userId) {
            throw APIException.ForbiddenException(
                statusCode = HttpStatus.FORBIDDEN.value(),
                message = "you are not authorized to update this task"
            )
        }
        val updatedTask = task.copy(
            title = newTask.title,
            description = newTask.description,
            dueDate = newTask.dueDate,
            isCompleted = newTask.isCompleted,
            updatedAt = newTask.updatedAt
        )
        taskRepository.update(updatedTask)?.id?.also {
            refreshTasks(newTask.userId)
        } ?: throw APIException.InternalServerException(
            statusCode = 500,
            message = "Failed to update task"
        )
    }

    override suspend fun delete(owner: Long, id: UUID): UUID = withContext(Dispatchers.IO) {
        val task = taskRepository.findById(id)
            ?: throw APIException.NotFoundResourceException(
                message = "task with id $id not found"
            )
        if (task.userId != owner) {
            throw APIException.ForbiddenException(
                statusCode = HttpStatus.FORBIDDEN.value(),
                message = "you are not authorized to delete this task"
            )
        }
        taskRepository.deleteById(id).also {
            refreshTasks(owner)
        }
        task.id
    }

    companion object {
        private val TaskCoroutineScope = CoroutineScope(Dispatchers.Default)
    }
}