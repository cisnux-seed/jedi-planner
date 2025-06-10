package org.cisnux.jediplanner.applications.controllers.dtos

import java.time.LocalDate
import java.util.UUID

data class UserProfileResponse(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val placeOfBirth: String? = null,
    val dateOfBirth: LocalDate? = null,
    val profilePic: String? = null
)