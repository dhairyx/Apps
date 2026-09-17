package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = OrganizerEntity::class,
            parentColumns = ["id"],
            childColumns = ["organizerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["organizerId"])]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val distanceKm: Int,
    val category: String,
    val currentParticipants: Int,
    val maxParticipants: Int,
    val organizerId: Int,
    val estimatedCost: String,
    val difficulty: String,
    val imageResName: String,
    val isJoined: Boolean = false,
    val isSaved: Boolean = false
)

data class Destination(
    val name: String,
    val distance: String,
    val imageResName: String
)
