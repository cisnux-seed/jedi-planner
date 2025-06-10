package org.cisnux.jediplanner.domains.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserAuth(
    @field:NotBlank(message = "email cannot be blank")
    @field:Size(
        max = 50,
        message = "email cannot be more than 50 characters"
    )
    val email: String,
    @field:NotBlank(message = "password cannot be blank")
    @field:Size(
        max = 255,
        message = "password cannot be more than 255 characters"
    )
    val password: String
)