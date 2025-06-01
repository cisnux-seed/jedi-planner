package org.cisnux.jediplanner.infrastructures.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.domains.repositories.UserRepository
import org.cisnux.jediplanner.domains.entities.User
import org.cisnux.jediplanner.infrastructures.repositories.dao.UserDAO
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class UserRepositoryImpl(private val userDAO: UserDAO, private val template: R2dbcEntityTemplate) : UserRepository {

    override suspend fun findByEmail(email: String): User? = withContext(Dispatchers.IO) {
        userDAO.findByEmail(email)
    }

    @Transactional
    override suspend fun insert(user: User): User? = withContext(Dispatchers.IO) {
        template.insert(user).log().awaitFirstOrNull()
    }

    @Transactional
    override suspend fun update(user: User): User? = withContext(Dispatchers.IO) {
        template.update(user).log().awaitFirstOrNull()
    }
}