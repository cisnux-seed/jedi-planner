package org.cisnux.jediplanner.infrastructures.repositories.dao

import org.cisnux.jediplanner.domains.entities.UserProfile
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
interface UserProfileDao : CoroutineCrudRepository<UserProfile, UUID> {
    @Lock(LockMode.PESSIMISTIC_WRITE)
    override suspend fun findById(id: UUID): UserProfile?
    suspend fun existsByUserId(userId: Long): Boolean
    @Lock(LockMode.PESSIMISTIC_WRITE)
    suspend fun findByUserId(userId: Long): UserProfile?
    suspend fun deleteByUserId(userId: Long)
}