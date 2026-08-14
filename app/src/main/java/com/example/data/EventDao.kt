package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Transaction
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<EventWithOrganizer>>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Int): Flow<EventWithOrganizer?>

    @Transaction
    @Query("SELECT * FROM events WHERE category = :category")
    fun getEventsByCategory(category: String): Flow<List<EventWithOrganizer>>
    
    @Transaction
    @Query("SELECT * FROM events WHERE isJoined = 1")
    fun getJoinedEvents(): Flow<List<EventWithOrganizer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganizers(organizers: List<OrganizerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)
    
    @Query("UPDATE events SET isJoined = :isJoined WHERE id = :id")
    suspend fun updateEventJoinedStatus(id: Int, isJoined: Boolean)
}
