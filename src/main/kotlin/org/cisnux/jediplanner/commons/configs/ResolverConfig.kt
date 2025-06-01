package org.cisnux.jediplanner.commons.configs

import org.cisnux.jediplanner.commons.resolvers.SubjectArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class ResolverConfig(private val subjectArgumentResolver: SubjectArgumentResolver) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(subjectArgumentResolver)
    }
}