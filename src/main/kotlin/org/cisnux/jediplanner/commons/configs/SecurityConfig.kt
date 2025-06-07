package org.cisnux.jediplanner.commons.configs

import org.cisnux.jediplanner.applications.filters.AuthenticationFilter
import org.cisnux.jediplanner.applications.resolvers.GraphqlSubjectArgumentResolver
import org.cisnux.jediplanner.applications.resolvers.RestSubjectArgumentResolver
import org.cisnux.jediplanner.commons.logger.Loggable
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.data.method.annotation.support.AnnotatedControllerConfigurer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
@EnableWebFluxSecurity
class SecurityConfig(
    private val restSubjectArgumentResolver: RestSubjectArgumentResolver,
    private val authenticationFilter: AuthenticationFilter,
    private val graphqlSubjectArgumentResolver: GraphqlSubjectArgumentResolver
) : WebFluxConfigurer, Loggable {
    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
    ): SecurityWebFilterChain {
        log.info("configuring security")

        return http
            .csrf { it.disable() }
            .authorizeExchange {
                it
                    .pathMatchers("/api/auth/**", "/error", "/graphiql").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }

    @Bean
    fun encoder(): PasswordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(restSubjectArgumentResolver)
    }

    @Bean
    fun annotatedControllerConfigurer(): AnnotatedControllerConfigurer {
        val configurer = AnnotatedControllerConfigurer()
        configurer.addCustomArgumentResolver(graphqlSubjectArgumentResolver)
        return configurer
    }
}