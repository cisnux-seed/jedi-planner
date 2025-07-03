package org.cisnux.jediplanner.applications.controllers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.applications.controllers.dtos.MetaResponse
import org.cisnux.jediplanner.applications.controllers.dtos.WebResponse
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.dtos.CreateUserProfile
import org.cisnux.jediplanner.domains.dtos.UpdateUserProfile
import org.cisnux.jediplanner.applications.controllers.dtos.UserProfileResponse
import org.cisnux.jediplanner.applications.filters.ContextPayload
import org.cisnux.jediplanner.applications.resolvers.Subject
import org.cisnux.jediplanner.domains.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserProfileController(private val userService: UserService) : Loggable {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getUserProfile(@Subject subject: ContextPayload): WebResponse<UserProfileResponse> =
        withContext(Dispatchers.Default) {
            WebResponse(
                meta = MetaResponse(
                    code = HttpStatus.OK.value().toString(),
                    message = "successfully retrieved user profile with id ${subject.id}",
                ),
                data = userService.getUserProfileByUserId(
                    owner = subject.id
                ).let {
                    UserProfileResponse(
                        id = it!!.id,
                        firstName = it.firstName,
                        lastName = it.lastName,
                        placeOfBirth = it.placeOfBirth,
                        dateOfBirth = it.dateOfBirth,
                        profilePic = it.profilePic
                    )
                }
            )
        }

    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createProfile(
        @Subject subject: ContextPayload,
        @ModelAttribute
        @Validated
        createUserProfile: CreateUserProfile
    ): WebResponse<UUID> =
        withContext(Dispatchers.Default) {
            log.info("subject: $subject")
            log.info("creating new profile: $createUserProfile")

            WebResponse(
                meta = MetaResponse(
                    code = HttpStatus.CREATED.value().toString(),
                    message = "successfully created user profile",
                ),
                data = userService.createUserProfile(owner = subject.id, createUserProfile)
            )
        }

    @PutMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.OK)
    suspend fun updateProfile(
        @Subject subject: ContextPayload,
        @ModelAttribute
        @Validated
        updateUserProfile: UpdateUserProfile
    ): WebResponse<UUID> =
        withContext(Dispatchers.Default) {
            log.info("updating profile: $updateUserProfile")

            WebResponse(
                meta = MetaResponse(
                    code = HttpStatus.OK.value().toString(),
                    message = "successfully updated a task",
                ),
                data = userService.updateUserProfile(owner = subject.id, updateUserProfile)
            )
        }

    @DeleteMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun deleteProfile(@Subject subject: ContextPayload): WebResponse<UUID> =
        withContext(Dispatchers.Default) {
            WebResponse(
                meta = MetaResponse(
                    code = HttpStatus.OK.value().toString(),
                    message = "successfully deleted profile with id ${subject.id}",
                ),
                data = userService.deleteUserProfileById(
                    owner = subject.id
                )
            )
        }
}