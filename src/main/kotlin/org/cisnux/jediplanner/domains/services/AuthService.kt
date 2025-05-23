package org.cisnux.jediplanner.domains.services

import org.cisnux.jediplanner.domains.services.dtos.UserLoginDTO
import org.cisnux.jediplanner.domains.services.dtos.UserRegisterDTO

interface AuthService {
    suspend fun signIn(userLoginDTO: UserLoginDTO): String
    suspend fun signUp(userRegisterDTO: UserRegisterDTO): String
//    suspend fun logOut(onLogout: (userResponseDTO: UserResponseDTO) -> Unit): String
}