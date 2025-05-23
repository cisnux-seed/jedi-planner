package org.cisnux.jediplanner.infrastructures.repositories.h2

import org.cisnux.jediplanner.domains.repositories.entities.Task
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
interface TaskDAO : R2dbcRepository<Task, String> {

    @Lock(LockMode.PESSIMISTIC_WRITE)
    override fun findById(id: String): Mono<Task?>
}