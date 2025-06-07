package org.cisnux.jediplanner.commons.configs

import org.cisnux.jediplanner.applications.resolvers.GraphqlSubjectArgumentResolver
import org.cisnux.jediplanner.applications.resolvers.RestSubjectArgumentResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.data.method.annotation.support.AnnotatedControllerConfigurer
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class ResolverConfig(private val restSubjectArgumentResolver: RestSubjectArgumentResolver) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(restSubjectArgumentResolver)
    }

    @Bean
    fun annotatedControllerConfigurer(graphqlSubjectArgumentResolver: GraphqlSubjectArgumentResolver): AnnotatedControllerConfigurer {
        val configurer = AnnotatedControllerConfigurer()
        configurer.addCustomArgumentResolver(graphqlSubjectArgumentResolver)
        return configurer
    }
}