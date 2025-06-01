package org.cisnux.jediplanner.domains.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.commons.configs.JwtProperties
import org.cisnux.jediplanner.commons.exceptions.APIException
import org.cisnux.jediplanner.domains.dtos.AuthResponse
import org.cisnux.jediplanner.domains.dtos.UserAuth
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant


@Service
class AuthServiceImpl(
    private val encoder: PasswordEncoder,
    private val userDetailsService: ReactiveUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
) :
    AuthService {
    override suspend fun authenticate(userAuth: UserAuth): AuthResponse = withContext(Dispatchers.Default) {
        val user = userDetailsService.findByUsername(userAuth.email)?.awaitSingleOrNull()
            ?: throw APIException.UnauthenticatedException(
                message = "invalid email or password"
            )
        if (!encoder.matches(userAuth.password, user.password ?: "")) {
            throw APIException.UnauthenticatedException(
                message = "invalid email or password"
            )
        }
        val accessToken = tokenService.generate(
            userDetails = user,
            expirationDate = Instant.now().plusMillis(jwtProperties.accessTokenExpiration)
        )

        AuthResponse(
            accessToken = accessToken,
        )
    }
}