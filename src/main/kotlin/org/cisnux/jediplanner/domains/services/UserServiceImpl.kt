package org.cisnux.jediplanner.domains.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.commons.exceptions.APIException
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.dtos.CreateUserProfile
import org.cisnux.jediplanner.domains.dtos.UpdateUserProfile
import org.cisnux.jediplanner.domains.entities.UserProfile
import org.cisnux.jediplanner.domains.repositories.UserProfileRepository
import org.cisnux.jediplanner.domains.repositories.UserRepository
import org.cisnux.jediplanner.domains.storages.FileManager
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
    private val fileManager: FileManager
) : UserService, Loggable {
    override suspend fun getByUsername(username: String) = withContext(Dispatchers.IO) {
        userRepository.findByEmail(username)
    }

    override suspend fun getUserProfileByUserId(owner: Long): UserProfile? = withContext(Dispatchers.IO) {
        val userProfile = userProfileRepository.findByUserId(owner) ?: throw APIException.NotFoundResourceException(
            statusCode = HttpStatus.NOT_FOUND.value(),
            message = "user profile not found"
        )

        userProfile
    }

    override suspend fun createUserProfile(owner: Long, createUserProfile: CreateUserProfile): UUID =
        withContext(Dispatchers.IO) {
            log.info("creating new profile: $createUserProfile")
            val isExist = userProfileRepository.existsByUserId(owner)
            if (isExist) {
                throw APIException.ConflictResourceException(
                    statusCode = HttpStatus.CONFLICT.value(),
                    message = "user profile already exists"
                )
            }

            val userProfile = UserProfile(
                userId = owner,
                firstName = createUserProfile.firstName,
                lastName = createUserProfile.lastName,
                placeOfBirth = createUserProfile.placeOfBirth,
                dateOfBirth = createUserProfile.dateOfBirth?.let(this@UserServiceImpl::convertToLocalDate),
                profilePic = createUserProfile.profilePic?.let { "/api/files/${fileManager.exportFilePath(it)}" }
            )
            userProfileRepository.insert(userProfile)?.id ?: throw APIException.InternalServerException(
                statusCode = 500,
                message = "failed to create profile"
            )
        }

    override suspend fun updateUserProfile(owner: Long, updateUserProfile: UpdateUserProfile): UUID =
        withContext(Dispatchers.IO) {
            log.info("updating profile: $updateUserProfile")
            val userProfile = userProfileRepository.findByUserId(owner)
                ?: throw APIException.NotFoundResourceException(
                    message = "user profile not found"
                )

            val updatedProfile = userProfile.copy(
                firstName = updateUserProfile.firstName,
                lastName = updateUserProfile.lastName,
                placeOfBirth = updateUserProfile.placeOfBirth,
                dateOfBirth = updateUserProfile.dateOfBirth?.let(this@UserServiceImpl::convertToLocalDate),
                profilePic = updateUserProfile.profilePic?.let { "/api/files/${fileManager.exportFilePath(it)}" },
                updatedAt = Instant.now()
            )
            userProfileRepository.update(updatedProfile)?.id ?: throw APIException.InternalServerException(
                statusCode = 500,
                message = "failed to update profile"
            )
        }

    override suspend fun deleteUserProfileById(owner: Long): UUID = withContext(Dispatchers.IO) {
        val userProfile = userProfileRepository.findByUserId(owner)
            ?: throw APIException.NotFoundResourceException(
                message = "user profile not found"
            )
        userProfileRepository.deleteByUserId(owner)
        userProfile.id
    }

    override suspend fun isUsernameAvailable(username: String): Boolean = !userRepository.isUsernameExists(username)

    override suspend fun isEmailAvailable(email: String): Boolean = !userRepository.isEmailExists(email)

    private fun convertToLocalDate(date: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return LocalDate.parse(date, formatter)
    }
}