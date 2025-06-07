package org.cisnux.jediplanner.domains.services

import org.cisnux.jediplanner.domains.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override suspend fun findByUsername(username: String) =
        userRepository.findByEmail(username)
}