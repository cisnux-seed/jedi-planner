package org.cisnux.jediplanner.applications.controllers.dtos

data class TaskResp(
    val id: String,
    val title: String,
    val description: String?,
    val dueDate: Long,
    val isCompleted: Boolean
)