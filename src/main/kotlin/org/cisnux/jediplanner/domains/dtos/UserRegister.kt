package org.cisnux.jediplanner.domains.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserRegister(
    @field:Email(message = "email is not valid")
    val email: String,
    @field:NotBlank(message = "password cannot be blank")
    val password: String,
)