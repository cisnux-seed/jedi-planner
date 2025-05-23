package org.cisnux.jediplanner.domains.services.dtos

import java.time.Instant

data class NewTaskDTO(
    @field:jakarta.validation.constraints.NotBlank("title cannot be blank")
    @field:jakarta.validation.constraints.Size(
        max = 100,
        message = "title cannot be more than 100 characters"
    )
    val title: String?,
    @field:jakarta.validation.constraints.Size(
        max = 500,
        message = "description cannot be more than 500 characters"
    )
    val description: String?,
    @field:jakarta.validation.constraints.NotNull(message = "due date cannot be null")
    val dueDate: Instant?
)