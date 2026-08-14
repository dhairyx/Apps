package com.example.data

import androidx.room.Embedded
import androidx.room.Relation

data class EventWithOrganizer(
    @Embedded val event: EventEntity,
    @Relation(
        parentColumn = "organizerId",
        entityColumn = "id"
    )
    val organizer: OrganizerEntity
)
