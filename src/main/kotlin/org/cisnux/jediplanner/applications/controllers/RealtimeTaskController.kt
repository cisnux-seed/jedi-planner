package org.cisnux.jediplanner.applications.controllers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cisnux.jediplanner.applications.controllers.dtos.TaskResponse
import org.cisnux.jediplanner.applications.filters.ContextPayload
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.applications.resolvers.Subject
import org.cisnux.jediplanner.domains.services.TaskService
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.stereotype.Controller
import java.time.ZoneOffset

@Controller
class RealtimeTaskController(
    private val taskService: TaskService
) : Loggable {
    @SubscriptionMapping("tasks")
    suspend fun getAllRealtimeTasks(@Subject subject: ContextPayload): Flow<List<TaskResponse>> {
        log.info("Fetching tasks for user: $subject")
        return taskService.getRealtimeTasks(subject.username)
            .map { tasks ->
                tasks.map { task ->
                    TaskResponse(
                        id = task.id,
                        title = task.title,
                        description = task.description,
                        dueDate = task.dueDate.toInstant(ZoneOffset.UTC).toEpochMilli(),
                        isCompleted = task.isCompleted
                    )
                }
            }
    }
}
