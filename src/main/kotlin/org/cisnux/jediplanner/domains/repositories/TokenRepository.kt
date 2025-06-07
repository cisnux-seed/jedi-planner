package org.cisnux.jediplanner.domains.repositories

import org.cisnux.jediplanner.domains.entities.Authentication

interface TokenRepository {
    suspend fun insert(authentication: Authentication): Authentication?
    suspend fun deleteById(token: String)
    suspend fun findById(token: String): Authentication?
    suspend fun isExists(token: String): Boolean
}