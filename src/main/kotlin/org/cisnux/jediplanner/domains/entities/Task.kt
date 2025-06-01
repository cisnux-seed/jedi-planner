package org.cisnux.jediplanner.domains.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Table("tasks")
data class Task(
    @Id val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String?,
    @Column("is_completed") val isCompleted: Boolean = false,
    @Column("due_date") val dueDate: LocalDateTime,
    val email: String,
    @Column("created_at") val createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),
    @Column("updated_at") val updatedAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
)
