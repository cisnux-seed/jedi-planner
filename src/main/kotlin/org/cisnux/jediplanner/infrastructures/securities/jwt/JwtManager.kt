package org.cisnux.jediplanner.infrastructures.securities.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.cisnux.jediplanner.domains.entities.User
import org.cisnux.jediplanner.domains.securities.TokenManager
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date

@Service
class JwtManager : TokenManager {
    override fun generate(
        secretKey: String,
        user: User,
        expirationDate: Instant,
        additionalClaims: Map<String, Any>
    ): String {
        val secretKeyBytes = secretKey.toByteArray()
        val secret = Keys.hmacShaKeyFor(secretKeyBytes)
        return Jwts.builder()
            .claims()
            .subject(user.email)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(expirationDate))
            .add(additionalClaims)
            .and()
            .signWith(secret)
            .compact()
    }


    override fun isValid(
        secretKey: String,
        token: String,
        user: User
    ): Boolean {
        val email = extractEmail(secretKey, token)
        return email != null && email == user.email && !isExpired(secretKey, token)
    }

    override fun extractEmail(secretKey: String, token: String): String? = getAllClaims(secretKey, token)
        .subject

    private fun isExpired(secretKey: String, token: String): Boolean = getAllClaims(secretKey, token)
        .expiration
        .toInstant()
        .isBefore(Instant.now())

    private fun getAllClaims(secretKey: String, token: String): Claims {
        val secretKeyBytes = secretKey.toByteArray()
        val secret = Keys.hmacShaKeyFor(secretKeyBytes)
        val parser = Jwts.parser()
            .verifyWith(secret)
            .build()

        return parser
            .parseSignedClaims(token)
            .payload
    }
}