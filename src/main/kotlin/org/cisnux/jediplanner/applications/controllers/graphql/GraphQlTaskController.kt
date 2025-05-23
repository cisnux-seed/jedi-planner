package org.cisnux.jediplanner.applications.controllers.graphql

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.domains.services.TaskService
import org.cisnux.jediplanner.domains.services.dtos.TaskRespDTO
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import java.time.Duration

@Controller
class GraphQlTaskController(private val taskService: TaskService) {
    @QueryMapping("tasks")
    suspend fun getAllTasks(): List<TaskRespDTO> = withContext(Dispatchers.Default) {
        taskService.getTasks().flowOn(Dispatchers.IO).toList()
    }

    @SubscriptionMapping("tasks")
    fun getSubscribeAllTasks(): Flow<TaskRespDTO> = Flux.interval(Duration.ofMillis(500L)).flatMap {
            taskService.getTasks().asFlux(Dispatchers.IO)
        }.asFlow()
}