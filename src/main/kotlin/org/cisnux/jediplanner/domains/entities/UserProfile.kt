package org.cisnux.jediplanner.domains.entities

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Table("user_profiles")
data class UserProfile(
    val id: UUID = UUID.randomUUID(),
    @Column("user_id")
    val userId: Long,
    @Column("first_name")
    val firstName: String,
    @Column("last_name")
    val lastName: String,
    @Column("place_of_birth")
    val placeOfBirth: String? = null,
    @Column("date_of_birth")
    val dateOfBirth: LocalDate? = null,
    @Column("profile_pic")
    val profilePic: String? = null,
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    @Column("updated_at")
    val updatedAt: Instant = Instant.now()
)
