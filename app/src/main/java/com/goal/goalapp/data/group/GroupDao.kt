package com.goal.goalapp.data.group

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroup(group: Group): Long

    @Update
    suspend fun update(group: Group)

    @Delete
    suspend fun deleteGroup(group: Group)

    @Query("SELECT * FROM `group` WHERE id = :groupId")
    fun getGroupById(groupId: Long): Flow<Group?>

    @Query("SELECT * FROM `group`")
    fun getAllGroups(): Flow<List<Group>>

    @Query("""
        SELECT * FROM `Group`
        WHERE id NOT IN (
            SELECT groupId FROM UserGroupCrossRef WHERE userId = :userId
        )
    """)
    fun getGroupsNotContainingUser(userId: Long): Flow<List<Group>>

    @Transaction
    @Query("SELECT * FROM `group` WHERE id = :groupId")
    fun getGroupWithDetailsById(groupId: Long): Flow<GroupWithDetails?>

}