

package com.example.models

import java.util.UUID

data class Album(
    val id: UUID,
    val name: String,
    val year: Int,
    val coverUrl: String, // URL firmada
    val artistId: UUID
)