package org.cisnux.jediplanner.applications.controllers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.applications.controllers.dtos.MetaResponse
import org.cisnux.jediplanner.applications.controllers.dtos.TokenRefresh
import org.cisnux.jediplanner.applications.controllers.dtos.TokenResponse
import org.cisnux.jediplanner.applications.controllers.dtos.WebResponse
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.applications.controllers.dtos.AuthResponse
import org.cisnux.jediplanner.applications.controllers.dtos.UserAuth
import org.cisnux.jediplanner.applications.controllers.dtos.UserRegister
import org.cisnux.jediplanner.domains.entities.User
import org.cisnux.jediplanner.domains.services.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) : Loggable {
    @PostMapping(
        "/register", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun register(
        @RequestBody @Validated userRegister: UserRegister
    ): WebResponse<String> = withContext(
        Dispatchers.Default
    ) {
        val user = User(
            email = userRegister.email, password = userRegister.password
        )
        val userId = authService.register(user)

        log.info("user registered successfully: $userId")

        WebResponse(
            meta = MetaResponse(
                code = HttpStatus.CREATED.value().toString(), message = "user registered successfully"
            ), data = userId
        )
    }



    @PostMapping(
        "/login", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun login(
        @RequestBody @Validated userAuth: UserAuth
    ): WebResponse<AuthResponse> = withContext(
        Dispatchers.Default
    ) {
        log.info("user logging in: $userAuth")

        val user = User(
            email = userAuth.email, password = userAuth.password
        )
        val authResp = authService.authenticate(user)

        WebResponse(
            meta = MetaResponse(
                code = HttpStatus.OK.value().toString(), message = "user logged in successfully"
            ), data = authResp
        )
    }

    @PutMapping(
        "/refresh", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun refresh(
        @RequestBody @Validated tokenRefresh: TokenRefresh
    ): WebResponse<TokenResponse> = withContext(
        Dispatchers.Default
    ) {
        val tokenResp = authService.refresh(tokenRefresh.refreshToken)

        WebResponse(
            meta = MetaResponse(
                code = HttpStatus.OK.value().toString(), message = "user logged in successfully"
            ), data = tokenResp
        )
    }

    @DeleteMapping(
        "/logout", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun logout(
        @RequestBody @Validated tokenRefresh: TokenRefresh
    ): WebResponse<String> = withContext(Dispatchers.Default) {
        log.info("user logging out: ${tokenRefresh.refreshToken}")

        authService.logout(tokenRefresh.refreshToken)

        WebResponse(
            meta = MetaResponse(
                code = HttpStatus.OK.value().toString(), message = "user logged out successfully"
            ), data = "user logged out successfully"
        )
    }
}
