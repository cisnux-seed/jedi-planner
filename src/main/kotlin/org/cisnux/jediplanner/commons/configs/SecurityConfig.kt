package org.cisnux.jediplanner.commons.configs

import org.cisnux.jediplanner.commons.filters.AuthenticationFilter
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.services.UserServiceImpl
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
@EnableWebFluxSecurity
class SecurityConfig : Loggable {
    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        authenticationFilter: AuthenticationFilter,
        authenticationManager: ReactiveAuthenticationManager,
    ): SecurityWebFilterChain {
        log.info("Configuring security")

        return http
            .csrf { it.disable() }
            .authorizeExchange {
                it
                    .pathMatchers(HttpMethod.POST, "/api/auth/**", "/api/users/**", "/error").permitAll()
                    .anyExchange().authenticated()
            }
            .authenticationManager(authenticationManager)
            .addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }

    @Bean
    fun encoder(): PasswordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

    @Bean
    fun reactiveAuthenticationManager(
        userServiceImpl: UserServiceImpl,
        encoder: PasswordEncoder
    ): ReactiveAuthenticationManager {
        val authManager = UserDetailsRepositoryReactiveAuthenticationManager(userServiceImpl)
        authManager.setPasswordEncoder(encoder)
        return authManager
    }
}