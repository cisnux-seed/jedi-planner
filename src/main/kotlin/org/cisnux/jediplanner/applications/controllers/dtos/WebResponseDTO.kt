package org.cisnux.jediplanner.applications.controllers.dtos

data class WebResponseDTO<out T>(
    val meta: MetaResponseDTO,
    val data: T? = null,
)
