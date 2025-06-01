package org.cisnux.jediplanner.commons.filters

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.cisnux.jediplanner.applications.controllers.dtos.MetaResponse
import org.cisnux.jediplanner.applications.controllers.dtos.WebResponse
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.services.TokenService
import org.cisnux.jediplanner.domains.services.UserServiceImpl
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class AuthenticationFilter(
    private val userServiceImpl: UserServiceImpl,
    private val tokenService: TokenService
) : WebFilter, Loggable {

    private fun String?.doesNotContainBearer(): Boolean = this == null || !this.startsWith("Bearer ")

    private fun String?.extractToken(): String? {
        return this?.substringAfter("Bearer ")?.takeIf { it.isNotBlank() }
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> = mono {
        log.info("processing authentication for request: ${exchange.request.method} ${exchange.request.uri}")

        if (exchange.request.path.value() in WHITELISTED_ENDPOINTS) {
            log.info("request is whitelisted, skipping authentication")
            return@mono chain.filter(exchange).awaitFirstOrNull()
        }

        val authHeader = exchange.request.headers.getFirst("Authorization")
        if (authHeader.doesNotContainBearer()) {
            val response = exchange.response
            val buffer = onAuthenticationFailure(
                response,
                "authentication is required"
            )

            return@mono response.writeWith(Mono.just(buffer)).awaitFirstOrNull()
        }

        val jwtToken = authHeader.extractToken() ?: return@mono chain.filter(exchange).awaitFirstOrNull()

        try {
            val email = tokenService.extractEmail(jwtToken) ?: return@mono chain.filter(exchange).awaitFirstOrNull()

            val user = userServiceImpl.findByUsername(email)?.awaitFirstOrNull()
            if (user != null && tokenService.isValid(jwtToken, user)) {
                val auth = UsernamePasswordAuthenticationToken(user, null, user.authorities)
                val context = SecurityContextImpl(auth)
                return@mono chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)))
                    .awaitFirstOrNull()
            }
        } catch (_: ExpiredJwtException) {
            log.info("token is expired: $jwtToken")
            val response = exchange.response
            val buffer = onAuthenticationFailure(
                response,
                "token is expired"
            )

            return@mono response.writeWith(Mono.just(buffer)).awaitFirstOrNull()
        } catch (_: MalformedJwtException) {
            log.info("token is malformed: $jwtToken")
            val response = exchange.response
            val buffer = onAuthenticationFailure(
                response,
                "token is malformed"
            )

            return@mono response.writeWith(Mono.just(buffer)).awaitFirstOrNull()
        } catch (e: Exception) {
            log.error("error processing authentication", e)
            val response = exchange.response
            val buffer = onAuthenticationFailure(
                response,
                "internal server error"
            )

            return@mono response.writeWith(Mono.just(buffer)).awaitFirstOrNull()
        }


        return@mono chain.filter(exchange).awaitFirstOrNull()
    }

    private fun onAuthenticationFailure(response: ServerHttpResponse, message: String): DataBuffer {
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON
        val body = WebResponse<String?>(
            meta = MetaResponse(
                code = response.statusCode?.value().toString(),
                message = message
            )
        )
        val json = ObjectMapper().writeValueAsBytes(body)
        val buffer = response.bufferFactory().wrap(json)
        return buffer
    }

    private companion object {
        val WHITELISTED_ENDPOINTS = setOf("/api/auth/refresh", "/api/users/register", "/api/auth/login")
    }
}