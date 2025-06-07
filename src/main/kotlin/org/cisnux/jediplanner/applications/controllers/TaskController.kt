package org.cisnux.jediplanner.applications.controllers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.applications.controllers.dtos.MetaResponse
import org.cisnux.jediplanner.applications.controllers.dtos.WebResponse
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.services.TaskService
import org.cisnux.jediplanner.applications.controllers.dtos.CreateTask
import org.cisnux.jediplanner.applications.controllers.dtos.TaskResponse
import org.cisnux.jediplanner.applications.controllers.dtos.UpdateTask
import org.cisnux.jediplanner.applications.filters.ContextPayload
import org.cisnux.jediplanner.applications.resolvers.Subject
import org.cisnux.jediplanner.domains.entities.Task
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) : Loggable {


    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getAllTasks(@Subject subject: ContextPayload): WebResponse<List<TaskResponse>> =
        withContext(Dispatchers.Default) {
            log.info("Fetching all tasks for user: $subject")
            WebResponse(
                meta = MetaResponse(
                    code = HttpStatus.OK.value().toString(),
                    message = "successfully retrieved all tasks",
                ),
                data = taskService.getAllByEmail(
                    subject.username
                ).toList().map { task ->
                    TaskResponse(
                        id = task.id,
                        title = task.title,
                        description = task.description,
                        dueDate = task.dueDate.toInstant(ZoneOffset.UTC).toEpochMilli(),
                        isCompleted = task.isCompleted
                    )
                }
            )
        }


    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getTaskById(@Subject subject: ContextPayload, @PathVariable id: String): WebResponse<TaskResponse> =
        withContext(Dispatchers.Default) {
            WebResponse(
                meta = MetaResponse(
                    code = HttpStatus.OK.value().toString(),
                    message = "successfully retrieved task with id $id",
                ),
                data = taskService.getById(
                    owner = subject.username, id = id
                ).let {
                    TaskResponse(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        dueDate = it.dueDate.toInstant(ZoneOffset.UTC).toEpochMilli(),
                        isCompleted = it.isCompleted
                    )
                }
            )
        }

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addTask(
        @Subject subject: ContextPayload,
        @RequestBody
        @Validated
        createTaskDTO: CreateTask
    ): WebResponse<String> =
        withContext(Dispatchers.Default) {
            log.info("subject: $subject")
            log.info("creating new task: $createTaskDTO")

            val newTask = Task(
                title = createTaskDTO.title,
                description = createTaskDTO.description,
                dueDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(createTaskDTO.dueDate), ZoneOffset.UTC),
                email = subject.username
            )

            WebResponse(
                meta = MetaResponse(
                    code = HttpStatus.CREATED.value().toString(),
                    message = "successfully created new task",
                ),
                data = taskService.create(newTask)
            )
        }

    @PutMapping(
        path = ["/{id}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.OK)
    suspend fun updateTask(
        @Subject subject: ContextPayload,
        @PathVariable id: String,
        @RequestBody
        @Validated
        updateTaskDTO: UpdateTask
    ): WebResponse<String> =
        withContext(Dispatchers.Default) {
            log.info("updating new task: $updateTaskDTO")

            val newTask = Task(
                id = id,
                title = updateTaskDTO.title,
                description = updateTaskDTO.description,
                dueDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(updateTaskDTO.dueDate), ZoneOffset.UTC),
                email = subject.username,
                isCompleted = updateTaskDTO.isCompleted,
            )

            WebResponse(
                meta = MetaResponse(
                    code = HttpStatus.OK.value().toString(),
                    message = "successfully updated a task",
                ),
                data = taskService.update(newTask)
            )
        }

    @DeleteMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun deleteTaskById(@Subject subject: ContextPayload, @PathVariable id: String): WebResponse<String> =
        withContext(Dispatchers.Default) {
            WebResponse(
                meta = MetaResponse(
                    code = HttpStatus.OK.value().toString(),
                    message = "successfully deleted task with id $id",
                ),
                data = taskService.delete(
                    owner = subject.username, id = id
                )
            )
        }
}