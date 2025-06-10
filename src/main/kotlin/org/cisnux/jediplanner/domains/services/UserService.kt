package org.cisnux.jediplanner.domains.services

import org.cisnux.jediplanner.domains.dtos.CreateUserProfile
import org.cisnux.jediplanner.domains.dtos.UpdateUserProfile
import org.cisnux.jediplanner.domains.entities.User
import org.cisnux.jediplanner.domains.entities.UserProfile
import java.util.UUID

interface UserService {
    suspend fun getByUsername(username: String): User?
    suspend fun getUserProfileByUserId(owner: Long): UserProfile?
    suspend fun createUserProfile(owner: Long, createUserProfile: CreateUserProfile): UUID
    suspend fun updateUserProfile(owner: Long, updateUserProfile: UpdateUserProfile): UUID
    suspend fun deleteUserProfileById(owner: Long): UUID
    suspend fun isUsernameAvailable(username: String): Boolean
    suspend fun isEmailAvailable(email: String): Boolean
}