package org.cisnux.jediplanner.infrastructures.repositories.dao

import org.cisnux.jediplanner.domains.entities.User
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component

@Component
interface UserDao : CoroutineCrudRepository<User, Long> {
    @Lock(LockMode.PESSIMISTIC_WRITE)
    override suspend fun findById(id: Long): User?
    @Lock(LockMode.PESSIMISTIC_WRITE)
    suspend fun findByEmail(email: String): User?
    suspend fun existsByUsername(username: String): Boolean
    suspend fun existsByEmail(email: String): Boolean
}