package com.goal.goalapp.data.user_session

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [UserSession] from a given data source.
 */
interface UserSessionRepository {

    /**
     * Insert userSession in the data source
     */
    suspend fun insertSession(session: UserSession): Long

    /**
     * Update userSession in the data source
     */
    suspend fun updateUserSession(userSession: UserSession)

    /**
     * Delete userSession from the data source
     */
    suspend fun deleteSessionsByUserId(userId: Int)

    /**
     * Retrieve an userSession from the data source that match with the provided user id
     */
    fun getSessionByUserId(userId: Int): Flow<UserSession?>

    /**
     * Create new user session
     */
    suspend fun createSession(userId: Int): UserSession

}