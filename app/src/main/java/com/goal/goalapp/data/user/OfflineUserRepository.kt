package com.goal.goalapp.data.user

import com.goal.goalapp.data.user_session.UserSession
import kotlinx.coroutines.flow.Flow

//override the functions defined in the UserRepository interface and call the corresponding functions from the UserDao
class OfflineUserRepository(private val userDao: UserDao) : UserRepository{

    override suspend fun insertUser(user: User): Long = userDao.insertUser(user)

    override suspend fun updateUser(user: User) = userDao.update(user)

    override suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    override fun getUserByIdStream(userId: Int): Flow<User?> = userDao.getUserById(userId)

    override suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    override fun getUserWithGroupsByIdStream(userId: Long): Flow<UserWithGroups?> = userDao.getUserWithGroupsById(userId)

    override fun getUserWithGoalsByIdStream(userId: Long): Flow<UserWithGoals?> = userDao.getUserWithGoalsById(userId)

    override fun getUserWithSessionByIdStream(sessionId: Int): Flow<UserWithSession?> = userDao.getUserWithSession(sessionId)


}