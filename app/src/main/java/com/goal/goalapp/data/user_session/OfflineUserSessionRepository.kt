package com.goal.goalapp.data.user_session

import kotlinx.coroutines.flow.Flow
import java.util.UUID

//override the functions defined in the UserRepository interface and call the corresponding functions from the UserDao
class OfflineUserSessionRepository(private val userSessionDao: UserSessionDao) : UserSessionRepository {

    override suspend fun insertSession(session: UserSession): Long = userSessionDao.insertUserSession(session)

    override suspend fun updateUserSession(userSession: UserSession) = userSessionDao.updateUserSession(userSession)

    override suspend fun deleteSessionsByUserId(userId: Int) = userSessionDao.deleteSessionsByUserId(userId)

    override fun getSessionByUserId(userId: Int): Flow<UserSession?> = userSessionDao.getSessionByUserId(userId)

    override suspend fun createSession(userId: Int): UserSession{
        // End old user session
        userSessionDao.deleteSessionsByUserId(userId)

        // create a new user session token
        val token = UUID.randomUUID().toString()

        // decide the end date for the token
        val currentTime = System.currentTimeMillis()
        val expiresAt = currentTime + 7 * 24 * 60 * 60 * 1000 // 7 days in milliseconds

        // create the new user session
        val session = UserSession(
            userId = userId,
            token = token,
            createdAt = currentTime,
            expiresAt = expiresAt
        )

        // save the new user session to the database
        val sessionId = userSessionDao.insertUserSession(session)

        // returns the whole user session including the id
        return session.copy(id = sessionId.toInt())
    }

}