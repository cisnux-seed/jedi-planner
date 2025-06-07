package org.cisnux.jediplanner.domains.securities

import org.cisnux.jediplanner.domains.entities.User
import java.time.Instant

interface TokenService {
    fun generate(
        secretKey: String,
        user: User,
        expirationDate: Instant,
        additionalClaims: Map<String, Any> = emptyMap()
    ): String

    fun isValid(secretKey: String, token: String, user: User): Boolean
    fun extractEmail(secretKey: String, token: String): String?
}