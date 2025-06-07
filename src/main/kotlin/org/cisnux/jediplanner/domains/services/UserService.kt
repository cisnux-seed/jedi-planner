package org.cisnux.jediplanner.domains.services

import org.cisnux.jediplanner.domains.entities.User

interface UserService {
    suspend fun findByUsername(username: String): User?
}