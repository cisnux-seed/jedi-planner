package org.cisnux.jediplanner.infrastructures.repositories.dao

import org.cisnux.jediplanner.domains.entities.Authentication
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component

@Component
interface AuthenticationDao : CoroutineCrudRepository<Authentication, String>

