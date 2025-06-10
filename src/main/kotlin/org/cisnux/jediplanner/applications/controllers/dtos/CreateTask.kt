package org.cisnux.jediplanner.applications.controllers.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateTask(
    @field:NotBlank("title cannot be blank")
    @field:Size(
        max = 255,
        message = "title cannot be more than 255 characters"
    )
    val title: String,
    @field:Size(
        max = 500,
        message = "description cannot be more than 500 characters"
    )
    val description: String?,
    @field:NotNull(message = "due date cannot be null")
    val dueDate: Long
)