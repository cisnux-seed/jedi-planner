package org.cisnux.jediplanner.applications.resolvers

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.cisnux.jediplanner.applications.filters.ContextPayload
import org.cisnux.jediplanner.commons.logger.Loggable
import org.springframework.core.MethodParameter
import org.springframework.graphql.data.method.HandlerMethodArgumentResolver
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GraphqlSubjectArgumentResolver : HandlerMethodArgumentResolver, Loggable {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(Subject::class.java) && parameter.parameterType.isAssignableFrom(
            ContextPayload::class.java
        )

    override fun resolveArgument(
        parameter: MethodParameter,
        environment: DataFetchingEnvironment
    ): Mono<Any> = mono {
        ReactiveSecurityContextHolder.getContext().awaitFirstOrNull()?.authentication?.principal as ContextPayload
    }
}