package org.cisnux.jediplanner.applications.controllers.dtos

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
)
