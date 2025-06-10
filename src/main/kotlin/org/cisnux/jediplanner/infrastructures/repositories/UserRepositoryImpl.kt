package org.cisnux.jediplanner.infrastructures.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.domains.repositories.UserRepository
import org.cisnux.jediplanner.domains.entities.User
import org.cisnux.jediplanner.infrastructures.repositories.dao.UserDao
import org.springframework.stereotype.Repository
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Repository
class UserRepositoryImpl(
    private val userDAO: UserDao,
    private val operator: TransactionalOperator
) : UserRepository {

    override suspend fun findByEmail(email: String): User? = withContext(Dispatchers.IO) {
        userDAO.findByEmail(email)
    }

    override suspend fun insert(user: User): User? = withContext(Dispatchers.IO) {
        operator.executeAndAwait {
            userDAO.save(user)
        }
    }

    override suspend fun update(user: User): User? = withContext(Dispatchers.IO) {
        operator.executeAndAwait {
            userDAO.save(user)
        }
    }

    override suspend fun isUsernameExists(username: String): Boolean = withContext(Dispatchers.IO) {
            userDAO.existsByUsername(username)
        }


    override suspend fun isEmailExists(email: String): Boolean = withContext(Dispatchers.IO){
            userDAO.existsByEmail(email)
        }
}