

package com.example.models

import java.util.UUID

data class Track(
    val id: UUID,
    val name: String,
    val duration: Int,
    val audioUrl: String?, // URL firmada para reproducir la canción
    val albumId: UUID?,
    val artistId: UUID
)