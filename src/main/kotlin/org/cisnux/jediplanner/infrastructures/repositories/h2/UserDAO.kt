package org.cisnux.jediplanner.infrastructures.repositories.h2

import org.cisnux.jediplanner.domains.repositories.entities.User
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
interface UserDAO : R2dbcRepository<User, String>{

    fun findUserById(id: String): Mono<User?>
}