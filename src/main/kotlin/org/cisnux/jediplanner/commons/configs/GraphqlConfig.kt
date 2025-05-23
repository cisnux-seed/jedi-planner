package org.cisnux.jediplanner.commons.configs

import graphql.scalars.ExtendedScalars
import graphql.schema.idl.RuntimeWiring
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration
class GraphqlConfig {
    @Bean
    fun runtimeWiringConfigurer(): RuntimeWiringConfigurer =
        RuntimeWiringConfigurer { wiringBuilder: RuntimeWiring.Builder? -> wiringBuilder!!.scalar(ExtendedScalars.GraphQLLong) }

}