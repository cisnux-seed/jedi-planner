package org.cisnux.jediplanner.domains.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.codec.multipart.FilePart

data class UpdateUserProfile(
    @field:NotBlank(message = "first name cannot be blank")
    @field:Size(
        max = 255,
        message = "first name cannot be more than 255 characters"
    )
    val firstName: String,
    @field:NotBlank(message = "last name cannot be blank")
    @field:Size(
        max = 255,
        message = "last name cannot be more than 255 characters"
    )
    val lastName: String,
    @field:Size(
        max = 255,
        message = "place of birth cannot be more than 255 characters"
    )
    val placeOfBirth: String? = null,
    val dateOfBirth: String? = null,
    val profilePic: FilePart? = null
)