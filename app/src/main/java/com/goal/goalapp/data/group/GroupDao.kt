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

    @Query("DELETE FROM `Group` WHERE id = :groupId")
    suspend fun deleteGroupById(groupId: Int)

    @Query("SELECT * FROM `group` WHERE id = :groupId")
    fun getGroupById(groupId: Int): Flow<Group?>

    @Query("SELECT * FROM `group`")
    fun getAllGroupsStream(): Flow<List<Group>>

    @Query("""
        SELECT * FROM `Group`
        WHERE id NOT IN (
            SELECT groupId FROM UserGroupCrossRef WHERE userId = :userId
        )
    """)
    fun getGroupsWithCategoriesNotContainingUserStream(userId: Int): Flow<List<GroupWithCategories>>

    @Transaction
    @Query("SELECT * FROM `group` WHERE id = :groupId")
    fun getGroupWithDetailsByIdStream(groupId: Int): Flow<GroupWithDetails?>

    @Query("""
            SELECT * FROM `Group`
            WHERE id IN (
        SELECT groupId FROM UserGroupCrossRef WHERE userId = :userId
    )
        """)
    fun getGroupsWithCategoriesByUserIdStream(userId: Int): Flow<List<GroupWithCategories>>

    @Query("SELECT * FROM GroupCategory")
    fun getAllGroupCategoriesStream(): Flow<List<GroupCategory>>

    @Transaction
    @Query("SELECT * FROM `Group` WHERE id = :groupId")
    suspend fun getGroupWithCategoriesById(groupId: Int): GroupWithCategories?

}