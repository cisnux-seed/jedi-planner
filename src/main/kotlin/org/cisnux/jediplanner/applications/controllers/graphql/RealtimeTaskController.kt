package org.cisnux.jediplanner.applications.controllers.graphql

import org.cisnux.jediplanner.domains.services.TaskService
import org.springframework.stereotype.Controller

@Controller
class RealtimeTaskController(private val taskService: TaskService) {
//    @QueryMapping("tasks")
//    suspend fun getAllTasks(): List<TaskRespDTO> = withContext(Dispatchers.Default) {
//        taskService.getTasks().flowOn(Dispatchers.IO).toList()
//    }
//
//    @SubscriptionMapping("tasks")
//    fun getSubscribeAllTasks(): Flow<TaskRespDTO> = taskService.getTasks()
}