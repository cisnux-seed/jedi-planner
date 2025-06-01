package org.cisnux.jediplanner.infrastructures.repositories.dao

import kotlinx.coroutines.flow.Flow
import org.cisnux.jediplanner.domains.entities.Task
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component

@Component
interface TaskDAO : CoroutineCrudRepository<Task, String> {

    @Lock(LockMode.PESSIMISTIC_WRITE)
    override suspend fun findById(id: String): Task?

    fun findAllByEmail(email: String): Flow<Task>
}