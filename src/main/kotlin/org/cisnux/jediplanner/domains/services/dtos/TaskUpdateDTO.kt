package org.cisnux.jediplanner.domains.services.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class TaskUpdateDTO(
    @field:jakarta.validation.constraints.NotBlank(message = "id cannot be blank")
    @field:JsonIgnore
    val id: String? = null,
    @field:jakarta.validation.constraints.NotBlank("title cannot be blank")
    @field:jakarta.validation.constraints.Size(
        max = 100,
        message = "title cannot be more than 100 characters"
    ) val title: String?,
    @field:jakarta.validation.constraints.Size(
        max = 500,
        message = "description cannot be more than 500 characters"
    )
    val description: String?,
    @field:jakarta.validation.constraints.NotNull(message = "dueDate cannot be null")
    val dueDate: Instant?,
    val hasCompleted: Boolean? = false
)
