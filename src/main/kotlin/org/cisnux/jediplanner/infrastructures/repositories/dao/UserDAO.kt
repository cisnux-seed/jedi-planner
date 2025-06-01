package org.cisnux.jediplanner.infrastructures.repositories.dao

import org.cisnux.jediplanner.domains.entities.User
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component

@Component
interface UserDAO : CoroutineCrudRepository<User, String> {
    @Lock(LockMode.PESSIMISTIC_WRITE)
    suspend fun findByEmail(email: String): User?
}