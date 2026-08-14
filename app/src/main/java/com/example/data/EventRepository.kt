package com.example.data

import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao) {
    val allEvents: Flow<List<EventWithOrganizer>> = eventDao.getAllEvents()
    val joinedEvents: Flow<List<EventWithOrganizer>> = eventDao.getJoinedEvents()

    fun getEventById(id: Int): Flow<EventWithOrganizer?> {
        return eventDao.getEventById(id)
    }

    suspend fun insertInitialData() {
        // Only insert if empty, handled in VM
        val initialOrganizers = listOf(
            OrganizerEntity(id = 1, name = "Marie L.", profilePhotoRes = "", isVerified = true, joinedDate = "2023", tripsOrganized = 5, reviewsCount = 12),
            OrganizerEntity(id = 2, name = "Thomas D.", profilePhotoRes = "", isVerified = false, joinedDate = "2024", tripsOrganized = 2, reviewsCount = 3),
            OrganizerEntity(id = 3, name = "Toulouse Photo Club", profilePhotoRes = "", isVerified = true, joinedDate = "2021", tripsOrganized = 20, reviewsCount = 45)
        )
        val initialEvents = listOf(
            EventEntity(
                title = "Weekend trip to Carcassonne",
                description = "Join us for a weekend exploring the medieval Cité de Carcassonne. We'll have a guided tour of the castle and enjoy local cassoulet.",
                date = "Sat, Aug 15",
                location = "Carcassonne",
                distanceKm = 90,
                category = "History",
                currentParticipants = 8,
                maxParticipants = 12,
                organizerId = 1,
                estimatedCost = "€45",
                difficulty = "Easy",
                imageResName = "img_carcassonne_1785776224600"
            ),
            EventEntity(
                title = "Hiking near Toulouse",
                description = "A beautiful day hike in the Pyrenees. We'll carpool from Toulouse early morning.",
                date = "Sun, Aug 16",
                location = "Pyrenees",
                distanceKm = 120,
                category = "Hiking",
                currentParticipants = 4,
                maxParticipants = 10,
                organizerId = 2,
                estimatedCost = "€15",
                difficulty = "Medium",
                imageResName = "img_pyrenees_1785776212675"
            ),
            EventEntity(
                title = "Photography walk",
                description = "Golden hour photography walk around Capitole de Toulouse and the Garonne river.",
                date = "Fri, Aug 14",
                location = "Toulouse Center",
                distanceKm = 2,
                category = "Photography",
                currentParticipants = 15,
                maxParticipants = 20,
                organizerId = 3,
                estimatedCost = "Free",
                difficulty = "Easy",
                imageResName = "img_toulouse_1785776202675"
            )
        )
        eventDao.insertOrganizers(initialOrganizers)
        eventDao.insertEvents(initialEvents)
    }
    
    suspend fun joinEvent(id: Int) {
        eventDao.updateEventJoinedStatus(id, true)
    }
    
    suspend fun leaveEvent(id: Int) {
        eventDao.updateEventJoinedStatus(id, false)
    }
}
