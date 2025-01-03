package com.goal.goalapp.data.user

import com.goal.goalapp.data.user_session.UserSession
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    /**
     * Insert user in the data source
     */
    suspend fun insertUser(user: User): Long

    /**
     * Update user in the data source
     */
    suspend fun updateUser(user: User)

    /**
     * Delete user from the data source
     */
    suspend fun deleteUser(user: User)

    /**
     * Retrieve an user from the data source that match with the provided id
     */
    fun getUserByIdStream(userId: Int): Flow<User?>

    /**
     * Retrieve an user from the data source that match with the provided email
     */
    suspend fun getUserByEmail(email: String): User?

    /**
     * Retrieve an user with groups from the data source that match with the provided id
     */
    fun getUserWithGroupsByIdStream(userId: Long): Flow<UserWithGroups?>

    /**
     * Retrieve an user with goals from the data source that match with the provided id
     */
    fun getUserWithGoalsByIdStream(userId: Long): Flow<UserWithGoals?>

    /**
     * Retrieve an userSession from the data source that match with the provided user id
     */
    fun getUserWithSessionByIdStream(sessionId: Int): Flow<UserWithSession?>



}