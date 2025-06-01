package org.cisnux.jediplanner.domains.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.cisnux.jediplanner.commons.configs.JwtProperties
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date

@Service
class TokenServiceImpl(jwtProperties: JwtProperties) : TokenService {
    private val secretKey = Keys.hmacShaKeyFor(
        jwtProperties.secret.toByteArray()
    )

    override fun generate(
        userDetails: UserDetails?,
        expirationDate: Instant,
        additionalClaims: Map<String, Any>
    ): String = Jwts.builder()
        .claims()
        .subject(userDetails?.username)
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(expirationDate))
        .add(additionalClaims)
        .and()
        .signWith(secretKey)
        .compact()

    override fun isValid(
        token: String?,
        userDetails: UserDetails?
    ): Boolean {
        val email = extractEmail(token)
        return email != null && userDetails != null && email == userDetails.username && !isExpired(token)
    }

    override fun extractEmail(token: String?): String? = getAllClaims(token)
        .subject


    override fun isExpired(token: String?): Boolean = getAllClaims(token)
        .expiration
        .toInstant()
        .isBefore(Instant.now())

    private fun getAllClaims(token: String?): Claims {
        val parser = Jwts.parser()
            .verifyWith(secretKey)
            .build()

        return parser
            .parseSignedClaims(token)
            .payload
    }
}