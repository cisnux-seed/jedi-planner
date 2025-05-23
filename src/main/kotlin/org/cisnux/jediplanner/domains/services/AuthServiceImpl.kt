package org.cisnux.jediplanner.domains.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.domains.repositories.UserRepository
import org.cisnux.jediplanner.domains.repositories.entities.User
import org.cisnux.jediplanner.domains.services.dtos.UserLoginDTO
import org.cisnux.jediplanner.domains.services.dtos.UserRegisterDTO
import org.springframework.stereotype.Service
import java.util.UUID


@Service
open class AuthServiceImpl(val userRepository: UserRepository) : AuthService {
    override suspend fun signIn(userLoginDTO: UserLoginDTO): String = withContext(Dispatchers.IO){
        val user = userRepository.findByEmail(userLoginDTO.email)
        if (user != null && user.password == userLoginDTO.password) {
            return@withContext user.id
        } else {
            throw Exception("Invalid credentials")
        }
    }

    override suspend fun signUp(userRegisterDTO: UserRegisterDTO): String = withContext(Dispatchers.IO){
        val user = User(
            id = UUID.randomUUID().toString(),
            email = userRegisterDTO.email,
            username = userRegisterDTO.username,
            password = userRegisterDTO.password
        )
        return@withContext userRepository.save(user).id
    }
}