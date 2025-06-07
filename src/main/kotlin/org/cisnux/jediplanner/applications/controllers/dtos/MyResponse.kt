package org.cisnux.jediplanner.applications.controllers.dtos

import java.time.LocalDateTime

data class MyResponse(
    val role: String,
    val expiration: LocalDateTime,
    val issuedAt: LocalDateTime,
    val status: Int,
    val username: String
)