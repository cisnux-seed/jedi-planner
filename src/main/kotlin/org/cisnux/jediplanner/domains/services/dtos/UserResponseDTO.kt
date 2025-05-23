package org.cisnux.jediplanner.domains.services.dtos

data class UserResponseDTO(
    val id: String,
    val username: String,
    val email: String,
    val profilePicture: String? = null,
)