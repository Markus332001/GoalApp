package com.goal.goalapp.data.user_session

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

import com.goal.goalapp.data.user.UserWithSession
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSessionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserSession(session: UserSession): Long

    @Update
    suspend fun updateUserSession(userSession: UserSession)

    @Query("DELETE FROM user_sessions WHERE userId = :userId")
    suspend fun deleteSessionsByUserId(userId: Int)

    @Query("SELECT * FROM user_sessions WHERE userId = :userId")
    fun getSessionByUserId(userId: Int): Flow<UserSession?>
}