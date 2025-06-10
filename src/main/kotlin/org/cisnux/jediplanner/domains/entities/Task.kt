package org.cisnux.jediplanner.domains.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("tasks")
data class Task(
    @Id val id: UUID = UUID.randomUUID(),
    @Column("user_id")
    val userId: Long,
    val title: String,
    val description: String?,
    @Column("is_completed") val isCompleted: Boolean = false,
    @Column("due_date") val dueDate: Instant,
    @Column("created_at") val createdAt: Instant = Instant.now(),
    @Column("updated_at") val updatedAt: Instant = Instant.now()
)
