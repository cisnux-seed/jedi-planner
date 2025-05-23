package org.cisnux.jediplanner.domains.repositories.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("users")
data class User(
    @Id
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    @Column("created_at")
    val createdAt: Instant? = null,
    @Column("updated_at")
    val updatedAt: Instant? = null
)
