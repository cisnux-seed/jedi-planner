package org.cisnux.jediplanner.domains.dtos

import jakarta.validation.constraints.NotBlank

data class UserAuth(
    @field:NotBlank(message = "email cannot be blank")
    val email: String,
    @field:NotBlank(message = "password cannot be blank")
    val password: String
)