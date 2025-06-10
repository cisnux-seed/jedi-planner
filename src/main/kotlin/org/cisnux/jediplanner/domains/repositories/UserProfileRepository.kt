package org.cisnux.jediplanner.domains.repositories

import org.cisnux.jediplanner.domains.entities.UserProfile
import java.util.UUID

interface UserProfileRepository {
    suspend fun findById(id: UUID): UserProfile?
    suspend fun findByUserId(userId: Long): UserProfile?
    suspend fun insert(userProfile: UserProfile): UserProfile?
    suspend fun update(userProfile: UserProfile): UserProfile?
    suspend fun deleteById(id: UUID)
    suspend fun deleteByUserId(userId: Long)
    suspend fun existsByUserId(id: Long): Boolean
}