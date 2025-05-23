package org.cisnux.jediplanner.infrastructures.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.repositories.TaskRepository
import org.cisnux.jediplanner.domains.repositories.entities.Task
import org.cisnux.jediplanner.infrastructures.repositories.h2.TaskDAO
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

@Repository
open class TaskRepositoryImpl(private val taskDAO: TaskDAO, private val template: R2dbcEntityTemplate) : TaskRepository, Loggable {

    override suspend fun findById(id: String): Task? = withContext(Dispatchers.IO) {
        taskDAO.findById(id).awaitSingleOrNull()
    }

    override suspend fun save(task: Task): Task? = withContext(Dispatchers.IO) {
        template.insert(task).awaitSingleOrNull()
    }

    override suspend fun update(task: Task): Task? = withContext(Dispatchers.IO) {
        template.update(task).awaitSingleOrNull()
    }

    override suspend fun delete(id: String): Unit = withContext(Dispatchers.IO) {
        taskDAO.deleteById(id).awaitSingleOrNull()
    }

    override fun findAll(): Flow<Task> = taskDAO.findAll().asFlow()
}