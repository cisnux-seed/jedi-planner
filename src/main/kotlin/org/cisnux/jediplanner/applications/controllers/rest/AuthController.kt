package org.cisnux.jediplanner.applications.controllers.rest

import org.cisnux.jediplanner.commons.utils.JwtUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(private val jwtUtil: JwtUtil) {
    @GetMapping("/get-token/{username}")
    fun getToken(@PathVariable username: String): String {
        return jwtUtil.generateToken(username)
    }

    @PostMapping("/validate-token")
    fun validateToken(@RequestBody token: String): String {
        return try {
            "Token is valid ${jwtUtil.validateToken(token)}"

        } catch (e: Exception) {
            "Invalid Token ${e.message}"
        }
    }
}