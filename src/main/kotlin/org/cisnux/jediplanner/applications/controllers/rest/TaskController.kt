package org.cisnux.jediplanner.applications.controllers.rest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.applications.controllers.dtos.MetaResponse
import org.cisnux.jediplanner.applications.controllers.dtos.WebResponse
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.services.TaskService
import org.cisnux.jediplanner.applications.controllers.dtos.NewTask
import org.cisnux.jediplanner.applications.controllers.dtos.TaskResp
import org.cisnux.jediplanner.applications.controllers.dtos.TaskUpdate
import org.cisnux.jediplanner.commons.resolvers.Subject
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


    @OptIn(ExperimentalCoroutinesApi::class)
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getAllTasks(@Subject subject: String): WebResponse<List<TaskResp>> = withContext(Dispatchers.Default) {
        WebResponse(
            meta = MetaResponse(
                code = HttpStatus.OK.value().toString(),
                message = "successfully retrieved all tasks",
            ),
            data = taskService.getAllByEmail(subject).toList().map {
                TaskResp(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    dueDate = it.dueDate.toInstant(ZoneOffset.UTC).toEpochMilli(),
                    isCompleted = it.isCompleted
                )
            }
        )
    }


    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getTaskById(@Subject subject: String, @PathVariable id: String): WebResponse<TaskResp> =
        withContext(Dispatchers.Default) {
            WebResponse(
                meta = MetaResponse(
                    code = HttpStatus.OK.value().toString(),
                    message = "successfully retrieved task with id $id",
                ),
                data = taskService.getById(owner = subject, id = id).let {
                    TaskResp(
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
        @Subject subject: String,
        @RequestBody
        @Validated
        newTaskDTO: NewTask
    ): WebResponse<String> =
        withContext(Dispatchers.Default) {
            log.info("subject: $subject")
            log.info("creating new task: $newTaskDTO")

            val newTask = Task(
                title = newTaskDTO.title,
                description = newTaskDTO.description,
                dueDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(newTaskDTO.dueDate), ZoneOffset.UTC),
                email = subject
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
        @Subject subject: String,
        @PathVariable id: String,
        @RequestBody
        @Validated
        taskUpdateDTO: TaskUpdate
    ): WebResponse<String> =
        withContext(Dispatchers.Default) {
            log.info("updating new task: $taskUpdateDTO")

            val newTask = Task(
                id = id,
                title = taskUpdateDTO.title,
                description = taskUpdateDTO.description,
                dueDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(taskUpdateDTO.dueDate), ZoneOffset.UTC),
                email = subject,
                isCompleted = taskUpdateDTO.isCompleted,
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
    suspend fun deleteTaskById(@Subject subject: String, @PathVariable id: String): WebResponse<String> =
        withContext(Dispatchers.Default) {
            WebResponse(
                meta = MetaResponse(
                    code = HttpStatus.OK.value().toString(),
                    message = "successfully deleted task with id $id",
                ),
                data = taskService.delete(owner = subject, id = id)
            )
        }
}