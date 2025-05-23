package org.cisnux.jediplanner.applications.controllers.rest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.applications.controllers.dtos.MetaResponseDTO
import org.cisnux.jediplanner.applications.controllers.dtos.WebResponseDTO
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.services.TaskService
import org.cisnux.jediplanner.domains.services.dtos.NewTaskDTO
import org.cisnux.jediplanner.domains.services.dtos.TaskRespDTO
import org.cisnux.jediplanner.domains.services.dtos.TaskUpdateDTO
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/tasks")
class RestTaskController(val taskService: TaskService) : Loggable {

    @OptIn(ExperimentalCoroutinesApi::class)
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getAllTasks(): WebResponseDTO<List<TaskRespDTO>> = withContext(Dispatchers.IO) {
        WebResponseDTO(
            meta = MetaResponseDTO(
                code = HttpStatus.OK.value().toString(),
                message = "successfully retrieved all tasks",
            ),
            data = taskService.getTasks().flowOn(Dispatchers.IO).toList()
        )
    }


    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getTaskById(@PathVariable id: String): WebResponseDTO<TaskRespDTO> = withContext(Dispatchers.IO) {
        WebResponseDTO(
            meta = MetaResponseDTO(
                code = HttpStatus.OK.value().toString(),
                message = "successfully retrieved task with id $id",
            ),
            data = taskService.getTaskById(id)
        )
    }

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addTask(@RequestBody newTaskDTO: NewTaskDTO): WebResponseDTO<UUID> =
        withContext(Dispatchers.IO) {
            log.info("creating new task: $newTaskDTO")
            WebResponseDTO(
                meta = MetaResponseDTO(
                    code = HttpStatus.CREATED.value().toString(),
                    message = "successfully created new task",
                ),
                data = taskService.createTask(newTaskDTO)
            )
        }

    @PutMapping(
        path = ["/{id}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.OK)
    suspend fun updateTask(@PathVariable id: String, @RequestBody taskUpdateDTO: TaskUpdateDTO): WebResponseDTO<UUID> =
        withContext(Dispatchers.IO) {
            log.info("updating new task: $taskUpdateDTO")
            WebResponseDTO(
                meta = MetaResponseDTO(
                    code = HttpStatus.OK.value().toString(),
                    message = "successfully updated a task",
                ),
                data = taskService.updateTask(taskUpdateDTO.copy(id = id))
            )
        }

    @DeleteMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun deleteTaskById(@PathVariable id: String): WebResponseDTO<UUID> = withContext(Dispatchers.IO) {
        WebResponseDTO(
            meta = MetaResponseDTO(
                code = HttpStatus.OK.value().toString(),
                message = "successfully deleted task with id $id",
            ),
            data = taskService.deleteTask(id)
        )
    }
}