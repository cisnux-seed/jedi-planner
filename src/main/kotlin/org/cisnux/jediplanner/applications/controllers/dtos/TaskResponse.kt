package org.cisnux.jediplanner.applications.controllers.dtos

import java.util.UUID

data class TaskResponse(
    val id: UUID,
    val title: String,
    val description: String?,
    val dueDate: Long,
    val isCompleted: Boolean
)