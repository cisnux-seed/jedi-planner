package org.cisnux.jediplanner.domains.services

import org.cisnux.jediplanner.domains.dtos.UserRegister
import org.springframework.security.core.userdetails.ReactiveUserDetailsService

interface UserService : ReactiveUserDetailsService {
    suspend fun register(userRegister: UserRegister): String
}