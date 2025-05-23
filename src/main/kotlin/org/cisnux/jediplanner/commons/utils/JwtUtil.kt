package org.cisnux.jediplanner.commons.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class JwtUtil {
    companion object {
        private const val SECRET = "lorempicsumdolorsitametconsecteturadipiscingelitloremipsumdolorsitametconsecteturadipiscingelitlorem"
        private val SIGNING_KEY: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET))
    }

    fun generateToken(id: String): String {
        return Jwts.builder()
            .subject(id)
            .signWith(SIGNING_KEY)
            .compact()
    }

    fun validateToken(token: String): Claims {
        val claims = Jwts.parser()
            .verifyWith(SIGNING_KEY)
            .build()
            .parseSignedClaims(token)
            .payload
        return claims
    }
}