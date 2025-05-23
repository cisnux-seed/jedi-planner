package org.cisnux.jediplanner.domains.repositories

import org.cisnux.jediplanner.domains.repositories.entities.User

interface UserRepository {
    suspend fun findByEmail(email: String): User?
    suspend fun save(user: User): User
    suspend fun update(user: String): User?
}