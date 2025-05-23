package org.cisnux.jediplanner.domains.repositories.entities

import java.time.Instant
import org.springframework.data.annotation.Id

import org.springframework.data.relational.core.mapping.Column

import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("tasks")
data class Task(
    @Id
    val id: UUID? = null,
    val title: String,
    val description: String? = null,
    @Column("has_completed")
    val hasCompleted: Boolean = false,
    @Column("due_date")
    val dueDate: Instant,
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),
)
