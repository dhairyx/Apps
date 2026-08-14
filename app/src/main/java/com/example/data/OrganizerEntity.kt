package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "organizers")
data class OrganizerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val profilePhotoRes: String,
    val isVerified: Boolean = false,
    val joinedDate: String,
    val tripsOrganized: Int,
    val reviewsCount: Int
)
