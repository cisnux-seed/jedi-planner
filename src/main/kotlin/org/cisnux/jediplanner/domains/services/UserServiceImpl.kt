package org.cisnux.jediplanner.domains.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.commons.exceptions.APIException
import org.cisnux.jediplanner.domains.dtos.UserRegister
import org.cisnux.jediplanner.domains.entities.User
import org.cisnux.jediplanner.domains.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

typealias UserDetailsImpl = org.springframework.security.core.userdetails.User

@Service
class UserServiceImpl(private val userRepository: UserRepository, private val encoder: PasswordEncoder) : UserService {
    override suspend fun register(userRegister: UserRegister): String = withContext(Dispatchers.IO) {
        val user = User(
            email = userRegister.email,
            password = encoder.encode(userRegister.password)
        )
        userRepository.insert(user)?.email ?: throw APIException.InternalServerException(
            message = "failed to create user"
        )
    }

    override fun findByUsername(username: String): Mono<UserDetails?>? = mono {
        userRepository.findByEmail(username)?.let {
            UserDetailsImpl.builder()
                .username(it.email)
                .password(it.password)
                .roles("USER")
                .build()
        }
    }
}