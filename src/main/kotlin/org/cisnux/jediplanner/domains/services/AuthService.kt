package org.cisnux.jediplanner.domains.services

import org.cisnux.jediplanner.applications.controllers.dtos.TokenResponse
import org.cisnux.jediplanner.applications.controllers.dtos.AuthResponse
import org.cisnux.jediplanner.domains.dtos.UserAuth
import org.cisnux.jediplanner.domains.entities.User

interface AuthService {
    suspend fun authenticate(user: UserAuth): AuthResponse
    suspend fun register(user: User): String
    suspend fun refresh(refreshToken: String): TokenResponse?
    suspend fun logout(refreshToken: String)
}