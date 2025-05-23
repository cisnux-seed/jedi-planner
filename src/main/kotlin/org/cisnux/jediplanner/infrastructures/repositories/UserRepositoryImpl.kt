package org.cisnux.jediplanner.infrastructures.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.domains.repositories.UserRepository
import org.cisnux.jediplanner.domains.repositories.entities.User
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl : UserRepository {
    private val tasks = mutableListOf<User>()

    override suspend fun findByEmail(email: String): User? = withContext(Dispatchers.IO) {
        tasks.find { it.email == email }?.let {
            User(
                id = it.id,
                username = it.username,
                email = it.email,
                password = it.password,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        }
    }

    override suspend fun save(user: User): User = withContext(Dispatchers.IO) {
        tasks.add(user)
        return@withContext user
    }

    override suspend fun update(user: String): User? = withContext(Dispatchers.IO) {
        val userToUpdate = tasks.find { it.id == user }
        userToUpdate?.let {
            tasks.remove(userToUpdate)
            tasks.add(userToUpdate)
            userToUpdate
        }
    }
}