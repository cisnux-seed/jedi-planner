package org.cisnux.jediplanner.domains.services

import org.cisnux.jediplanner.domains.dtos.AuthResponse
import org.cisnux.jediplanner.domains.dtos.UserAuth

interface AuthService {
    suspend fun authenticate(userAuth: UserAuth): AuthResponse
//    suspend fun logOut(onLogout: (userResponseDTO: UserResponseDTO) -> Unit): String
}