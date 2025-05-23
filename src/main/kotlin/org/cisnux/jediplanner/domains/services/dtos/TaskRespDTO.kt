package org.cisnux.jediplanner.domains.services.dtos

import java.util.UUID

data class TaskRespDTO(
    val id: UUID,
    val title: String,
    val description: String?,
    val dueDate: Long,
    val hasCompleted: Boolean
)