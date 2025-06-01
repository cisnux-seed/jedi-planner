package org.cisnux.jediplanner.domains.services

import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant
import java.time.LocalDateTime

interface TokenService {
    fun generate(userDetails: UserDetails?, expirationDate: Instant, additionalClaims: Map<String, Any> = emptyMap()): String
    fun isValid(token: String?, userDetails: UserDetails?): Boolean
    fun extractEmail(token: String?): String?
    fun isExpired(token: String?): Boolean
}