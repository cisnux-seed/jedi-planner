package org.cisnux.jediplanner.domains.services.dtos

import org.springframework.web.multipart.MultipartFile

data class UserRegisterDTO(
    @field:jakarta.validation.constraints.NotBlank(message = "username cannot be blank")
    val username: String,
    val profilePicture: MultipartFile? = null,
    @field:jakarta.validation.constraints.Email(message = "email is not valid")
    val email: String,
    @field:jakarta.validation.constraints.NotBlank(message = "password cannot be blank")
    val password: String,
)