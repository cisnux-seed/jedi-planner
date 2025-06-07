package org.cisnux.jediplanner.infrastructures.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitFirstOrNull
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.domains.entities.Authentication
import org.cisnux.jediplanner.domains.repositories.TokenRepository
import org.cisnux.jediplanner.infrastructures.repositories.dao.AuthenticationDao
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

@Repository
class TokenRepositoryImpl(private val authDao: AuthenticationDao, private val template: R2dbcEntityTemplate) :
    TokenRepository {
    override suspend fun insert(authentication: Authentication): Authentication? = withContext(Dispatchers.IO) {
        template.insert(authentication).log().awaitFirstOrNull()
    }

    override suspend fun deleteById(token: String) = withContext(Dispatchers.IO) {
        authDao.deleteById(token)
    }

    override suspend fun findById(token: String): Authentication? = withContext(Dispatchers.IO) {
        authDao.findById(token)
    }

    override suspend fun isExists(token: String): Boolean = withContext(Dispatchers.IO) {
        authDao.existsById(token)
    }
}