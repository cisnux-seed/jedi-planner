package org.cisnux.jediplanner.applications.controllers.rest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.applications.controllers.dtos.MetaResponse
import org.cisnux.jediplanner.applications.controllers.dtos.WebResponse
import org.cisnux.jediplanner.domains.dtos.UserRegister
import org.cisnux.jediplanner.domains.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    @PostMapping(
        "/register",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun register(
        @RequestBody
        @Validated
        userRegister: UserRegister
    ): WebResponse<String> = withContext(
        Dispatchers.Default
    ) {
        val userId = userService.register(userRegister)
        WebResponse(
            meta = MetaResponse(
                code = HttpStatus.CREATED.value().toString(),
                message = "user registered successfully"
            ),
            data = userId
        )
    }
}