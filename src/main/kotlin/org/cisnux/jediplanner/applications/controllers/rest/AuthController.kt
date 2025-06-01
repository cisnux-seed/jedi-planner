package org.cisnux.jediplanner.applications.controllers.rest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.applications.controllers.dtos.MetaResponse
import org.cisnux.jediplanner.applications.controllers.dtos.WebResponse
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.dtos.AuthResponse
import org.cisnux.jediplanner.domains.dtos.UserAuth
import org.cisnux.jediplanner.domains.services.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService): Loggable {
//    @GetMapping("/get-token/{username}")
//    fun getToken(@PathVariable username: String): String {
//        return jwtUtil.generateToken(username)
//    }
//
//    @PostMapping("/validate-token")
//    fun validateToken(@RequestBody token: String): String {
//        return try {
//            "Token is valid ${jwtUtil.validateToken(token)}"
//
//        } catch (e: Exception) {
//            "Invalid Token ${e.message}"
//        }
//    }

    @PostMapping(
        "/login",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun login(
        @RequestBody
        @Validated
        userAuth: UserAuth
    ): WebResponse<AuthResponse> = withContext(
        Dispatchers.Default
    ) {
        log.info("user logging in: $userAuth")
        WebResponse(
            meta = MetaResponse(
                code = HttpStatus.OK.value().toString(),
                message = "user logged in successfully"
            ),
            data = authService.authenticate(userAuth)
        )
    }
}
