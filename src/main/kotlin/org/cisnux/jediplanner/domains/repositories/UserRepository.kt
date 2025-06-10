package org.cisnux.jediplanner.domains.repositories

import org.cisnux.jediplanner.domains.entities.User

interface UserRepository {
    suspend fun findByEmail(email: String): User?
    suspend fun insert(user: User): User?
    suspend fun update(user: User): User?
    suspend fun isUsernameExists(username: String): Boolean
    suspend fun isEmailExists(email: String): Boolean
}