package org.cisnux.jediplanner.domains.services.dtos


data class UserLoginDTO(
    @field:jakarta.validation.constraints.NotBlank(message = "email cannot be blank")
    val email: String,
    @field:jakarta.validation.constraints.NotBlank(message = "password cannot be blank")
    val password: String
)