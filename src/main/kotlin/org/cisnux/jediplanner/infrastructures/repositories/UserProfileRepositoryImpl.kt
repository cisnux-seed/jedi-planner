package org.cisnux.jediplanner.infrastructures.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.domains.entities.UserProfile
import org.cisnux.jediplanner.domains.repositories.UserProfileRepository
import org.cisnux.jediplanner.infrastructures.repositories.dao.UserProfileDao
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.util.UUID

@Repository
class UserProfileRepositoryImpl(
    private val userProfileDao: UserProfileDao,
    private val operator: TransactionalOperator,
    private val template: R2dbcEntityTemplate,
) : UserProfileRepository {
    override suspend fun findById(id: UUID): UserProfile? = withContext(Dispatchers.IO) {
        userProfileDao.findById(id)
    }

    override suspend fun findByUserId(userId: Long): UserProfile? = withContext(Dispatchers.IO) {
        userProfileDao.findByUserId(userId)
    }

    override suspend fun insert(userProfile: UserProfile): UserProfile? = withContext(Dispatchers.IO) {
        operator.executeAndAwait {
            template.insert(userProfile).log().awaitFirstOrNull()
        }
    }


    override suspend fun update(userProfile: UserProfile): UserProfile? = withContext(Dispatchers.IO) {
        operator.executeAndAwait {
            template.update(userProfile).log().awaitFirstOrNull()
        }
    }

    override suspend fun deleteById(id: UUID) = withContext(Dispatchers.IO) {
        operator.executeAndAwait {
            userProfileDao.deleteById(id)
        }
    }

    override suspend fun deleteByUserId(userId: Long) =
        withContext(Dispatchers.IO) {
            operator.executeAndAwait {
                userProfileDao.deleteByUserId(userId)
            }
        }

    override suspend fun existsByUserId(id: Long): Boolean = withContext(Dispatchers.IO) {
        userProfileDao.existsByUserId(id)
    }
}