package org.cisnux.jediplanner.applications.controllers.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserRegister(
    @field:Email(message = "email is not valid")
    @field:Size(
        max = 255,
        message = "email cannot be more than 255 characters"
    )
    val email: String,
    @field:NotBlank(message = "username cannot be blank")
    @field:Size(
        max = 255,
        message = "username cannot be more than 255 characters"
    )
    val username: String,
    @field:NotBlank(message = "password cannot be blank")
    @field:Size(
        max = 255,
        message = "password cannot be more than 255 characters"
    )
    val password: String,
)