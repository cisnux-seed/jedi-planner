package org.cisnux.jediplanner.infrastructures.repositories.dao

import kotlinx.coroutines.flow.Flow
import org.cisnux.jediplanner.domains.entities.Task
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
interface TaskDao : CoroutineCrudRepository<Task, UUID> {

    @Lock(LockMode.PESSIMISTIC_WRITE)
    override suspend fun findById(id: UUID): Task?

    fun findAllByUserId(userId: Long): Flow<Task>
}