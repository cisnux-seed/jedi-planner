package org.cisnux.jediplanner.applications.filters

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionInput
import graphql.ExecutionResultImpl
import graphql.GraphqlErrorBuilder
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.cisnux.jediplanner.applications.controllers.dtos.MetaResponse
import org.cisnux.jediplanner.applications.controllers.dtos.WebResponse
import org.cisnux.jediplanner.commons.configs.JwtProperties
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.securities.TokenManager
import org.cisnux.jediplanner.domains.services.UserServiceImpl
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.graphql.execution.ErrorType
import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.graphql.support.DefaultExecutionGraphQlResponse
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
import org.springframework.web.util.pattern.PathPatternParser
import reactor.core.publisher.Mono


@Component
class AuthenticationFilter(
    private val jwtProperties: JwtProperties,
    private val userServiceImpl: UserServiceImpl,
    private val tokenManager: TokenManager
) : WebGraphQlInterceptor, WebFilter, Loggable {
    private val pathPatternParser = PathPatternParser()

    private fun String?.doesNotContainBearer(): Boolean = this == null || !this.startsWith("Bearer ")

    private fun String?.extractToken(): String? {
        return this?.substringAfter("Bearer ")?.takeIf { it.isNotBlank() }
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> = mono {
        log.info("processing authentication for request: ${exchange.request.method} ${exchange.request.uri}")

        val isWhitelisted = WHITELISTED_ENDPOINTS.any { endpoint ->
            pathPatternParser.parse(endpoint).matches(exchange.request.path)
        }
        if (isWhitelisted) {
            log.info("request is whitelisted, skipping authentication")
            return@mono chain.filter(exchange).awaitFirstOrNull()
        }

        val isGraphql = exchange.request.uri.path.startsWith("/graphql")
        if (isGraphql) {
            log.info("request is GraphQL, forward to graphql auth interceptor")
            val unauthenticated = UsernamePasswordAuthenticationToken(null, null, null)
            val context = SecurityContextImpl(unauthenticated)
            return@mono chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context))).awaitFirstOrNull()
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

        val jwtToken = authHeader.extractToken()
        if (jwtToken == null) {
            val response = exchange.response
            val buffer = onAuthenticationFailure(
                response,
                "authentication is required"
            )

            return@mono response.writeWith(Mono.just(buffer)).awaitFirstOrNull()
        }

        try {
            val email = tokenManager.extractEmail(jwtProperties.accessSecret, jwtToken)
            if (email == null) {
                log.info("token is invalid or expired: $jwtToken")
                val response = exchange.response
                val buffer = onAuthenticationFailure(
                    response,
                    "authentication is required"
                )

                return@mono response.writeWith(Mono.just(buffer)).awaitFirstOrNull()
            }

            val user = userServiceImpl.findByUsername(email)

            if (user != null && tokenManager.isValid(jwtProperties.accessSecret, jwtToken, user)) {
                val ctxPayload = ContextPayload(username = user.email)
                val auth =
                    UsernamePasswordAuthenticationToken(ctxPayload, null, ctxPayload.authorities)
                val context = SecurityContextImpl(auth)

                return@mono chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)))
                    .awaitFirstOrNull()
            }

            log.info("user not found or token is invalid for email: $email")
            val response = exchange.response
            val buffer = onAuthenticationFailure(
                response,
                "authentication is required"
            )

            return@mono response.writeWith(Mono.just(buffer)).awaitFirstOrNull()
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

    override fun intercept(
        request: WebGraphQlRequest,
        chain: WebGraphQlInterceptor.Chain
    ): Mono<WebGraphQlResponse?> {
        log.info("processing GraphQL header: ${request.headers}")
        log.info("processing GraphQL request: $request with variables: ${request.variables}")
        val error = GraphqlErrorBuilder.newError()
            .message("authentication is required")
            .errorType(ErrorType.UNAUTHORIZED)
        val executionResult = ExecutionResultImpl.newExecutionResult()
        val authHeader = request.headers.getFirst("Authorization")

        if (request.operationName == "IntrospectionQuery") {
            log.info("GraphQL request is for GraphiQL, skipping authentication")
            return chain.next(request)
        }

        if (authHeader.doesNotContainBearer()) {
            log.info("GraphQL request does not contain valid Authorization header")
            return Mono.just(onAuthenticationFailure(request.toExecutionInput(), executionResult, error))
        }

        val jwtToken = authHeader.extractToken()
        if (jwtToken == null) {
            log.info("GraphQL request does not contain valid JWT token")
            return Mono.just(onAuthenticationFailure(request.toExecutionInput(), executionResult, error))
        }

        try {
            val email = tokenManager.extractEmail(jwtProperties.accessSecret, jwtToken)
            if (email == null) {
                return Mono.just(onAuthenticationFailure(request.toExecutionInput(), executionResult, error))
            }
            val user = mono { userServiceImpl.findByUsername(email) }

            return user.flatMap { user ->
                if (user != null && tokenManager.isValid(jwtProperties.accessSecret, jwtToken, user)) {
                    val ctxPayload = ContextPayload(username = user.email)
                    val auth =
                        UsernamePasswordAuthenticationToken(ctxPayload, null, ctxPayload.authorities)
                    val context = SecurityContextImpl(auth)

                    chain.next(request)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)))
                } else {
                    log.info("user not found or token is invalid for email: $email from graphql request")
                    Mono.just(
                        onAuthenticationFailure(
                            request.toExecutionInput(),
                            executionResult,
                            error.message("authentication is required")
                        )
                    )
                }
            }
        } catch (_: ExpiredJwtException) {
            log.info("graphql token is expired: $jwtToken")

            return Mono.just(
                onAuthenticationFailure(
                    request.toExecutionInput(),
                    executionResult,
                    error.message("token is expired")
                )
            )
        } catch (_: MalformedJwtException) {
            log.info("graphql token is malformed: $jwtToken")
            return Mono.just(
                onAuthenticationFailure(
                    request.toExecutionInput(),
                    executionResult,
                    error.message("token is malformed")
                )
            )
        } catch (_: SignatureException) {
            log.info("graphql token signature is invalid: $jwtToken")
            return Mono.just(
                onAuthenticationFailure(
                    request.toExecutionInput(),
                    executionResult,
                    error.message("token signature is invalid")
                )
            )
        } catch (e: Exception) {
            log.error("graphql error processing authentication", e)
            return Mono.just(
                onAuthenticationFailure(
                    request.toExecutionInput(),
                    executionResult,
                    error.message("internal server error")
                )
            )
        }
    }

    private fun <T : GraphqlErrorBuilder<T>> onAuthenticationFailure(
        input: ExecutionInput,
        executionResult: ExecutionResultImpl.Builder<out ExecutionResultImpl.Builder<*>>,
        error: GraphqlErrorBuilder<T>
    ): WebGraphQlResponse {
        executionResult.addError(error.build())

        val executionResponse = DefaultExecutionGraphQlResponse(input, executionResult.build())
        return WebGraphQlResponse(executionResponse)
    }

    private companion object {
        val WHITELISTED_ENDPOINTS = listOf("/api/auth/**")
    }
}

