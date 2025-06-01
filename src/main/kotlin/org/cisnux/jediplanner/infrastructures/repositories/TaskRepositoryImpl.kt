package org.cisnux.jediplanner.infrastructures.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.repositories.TaskRepository
import org.cisnux.jediplanner.domains.entities.Task
import org.cisnux.jediplanner.infrastructures.repositories.dao.TaskDAO
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class TaskRepositoryImpl(private val taskDAO: TaskDAO, private val template: R2dbcEntityTemplate) : TaskRepository, Loggable {

    override suspend fun findById(id: String): Task? = withContext(Dispatchers.IO) {
        taskDAO.findById(id)
    }

    @Transactional
    override suspend fun insert(task: Task): Task? = withContext(Dispatchers.IO) {
        template.insert(task).log().awaitFirstOrNull()
    }

    @Transactional
    override suspend fun update(task: Task): Task? = withContext(Dispatchers.IO) {
        template.update(task).log().awaitFirstOrNull()
    }

    @Transactional
    override suspend fun deleteById(id: String): Unit = withContext(Dispatchers.IO) {
        taskDAO.deleteById(id)
    }

    override fun findAll(email: String): Flow<Task> = taskDAO.findAllByEmail(email).flowOn(Dispatchers.IO)
}