package org.cisnux.jediplanner.commons.resolvers

import org.springframework.core.MethodParameter
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Component
class SubjectArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(Subject::class.java) && parameter.parameterType == String::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter, bindingContext: BindingContext, exchange: ServerWebExchange
    ): Mono<Any> {
        return ReactiveSecurityContextHolder.getContext().map { it.authentication.principal }.cast(
            UserDetails::class.java
        ).map { it.username }
    }
}