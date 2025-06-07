package org.cisnux.jediplanner.domains.services

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.applications.controllers.dtos.TokenResponse
import org.cisnux.jediplanner.commons.configs.JwtProperties
import org.cisnux.jediplanner.commons.exceptions.APIException
import org.cisnux.jediplanner.applications.controllers.dtos.AuthResponse
import org.cisnux.jediplanner.domains.entities.Authentication
import org.cisnux.jediplanner.domains.entities.User
import org.cisnux.jediplanner.domains.repositories.TokenRepository
import org.cisnux.jediplanner.domains.repositories.UserRepository
import org.cisnux.jediplanner.domains.securities.TokenManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AuthServiceImpl(
    private val encoder: PasswordEncoder,
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager,
    private val tokenRepository: TokenRepository,
    private val jwtProperties: JwtProperties,
) :
    AuthService {
    override suspend fun authenticate(user: User): AuthResponse = withContext(Dispatchers.Default) {
        val existedUser = userService.findByUsername(user.email) ?: throw APIException.UnauthenticatedException(
            message = "invalid email or password"
        )
        if (!encoder.matches(user.password, existedUser.password)) {
            throw APIException.UnauthenticatedException(
                message = "invalid email or password"
            )
        }
        val accessToken = tokenManager.generate(
            secretKey = jwtProperties.accessSecret,
            user = user,
            expirationDate = Instant.now().plusMillis(jwtProperties.accessTokenExpiration)
        )
        val refreshToken = tokenManager.generate(
            secretKey = jwtProperties.refreshSecret,
            user = user,
            expirationDate = Instant.now().plusMillis(jwtProperties.refreshTokenExpiration)
        )

        val authentication = Authentication(
            token = refreshToken,
            email = existedUser.email
        )

        val auth = tokenRepository.insert(authentication) ?: throw APIException.InternalServerException(
            message = "failed to create authentication token"
        )

        AuthResponse(
            accessToken = accessToken,
            refreshToken = auth.token,
        )
    }

    override suspend fun register(user: User): String = withContext(Dispatchers.IO) {
        val user = user.copy(password = encoder.encode(user.password))
        userRepository.insert(user)?.email ?: throw APIException.InternalServerException(
            message = "failed to create user"
        )
    }

    override suspend fun refresh(refreshToken: String): TokenResponse? = try {
        val email = tokenManager.extractEmail(secretKey = jwtProperties.refreshSecret, refreshToken)
        email?.let {
            val currentUser = userService.findByUsername(email)
                ?: throw APIException.UnauthenticatedException(
                    message = "token is invalid or expired"
                )
            val isRefreshTokenExists = tokenRepository.isExists(refreshToken)

            if (isRefreshTokenExists && tokenManager.isValid(
                    secretKey = jwtProperties.refreshSecret,
                    refreshToken,
                    currentUser
                ) && email == currentUser.email
            ) {
                val accessToken = tokenManager.generate(
                    secretKey = jwtProperties.accessSecret,
                    user = currentUser,
                    expirationDate = Instant.now().plusMillis(jwtProperties.accessTokenExpiration)
                )
                TokenResponse(
                    accessToken = accessToken,
                )
            } else {
                throw APIException.UnauthenticatedException(
                    message = "token is invalid or expired"
                )
            }
        } ?: throw APIException.UnauthenticatedException(
            message = "token is invalid or expired"
        )
    } catch (_: ExpiredJwtException) {
        throw APIException.UnauthenticatedException(
            message = "token is expired"
        )
    } catch (_: MalformedJwtException) {
        throw APIException.UnauthenticatedException(
            message = "invalid token"
        )
    } catch (_: SignatureException){
        throw APIException.UnauthenticatedException(
            message = "invalid token signature"
        )
    }


    override suspend fun logout(refreshToken: String): Unit =
        tokenRepository.deleteById(refreshToken)

}