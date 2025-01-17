package com.goal.goalapp.data.group

import androidx.compose.ui.semantics.role
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.goal.goalapp.data.group.request.GroupMemberDTO
import com.goal.goalapp.data.user.User
import com.goal.goalapp.data.user.UserGroupCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroup(group: Group): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroupCategories(groupCategory: List<GroupCategory>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroupWithCategoryCrossRef(groupWithCategoriesCrossRef: GroupGroupCategoryCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserGroupCrossRef(userGroupCrossRef: UserGroupCrossRef): Long

    @Update
    suspend fun update(group: Group)

    @Delete
    suspend fun deleteGroup(group: Group)

    @Query("DELETE FROM `Group` WHERE id = :groupId")
    suspend fun deleteGroupById(groupId: Int)

    @Query("DELETE FROM UserGroupCrossRef WHERE userId = :userId AND groupId = :groupId")
    suspend fun deleteUserGroupCrossRefByIds(userId: Long, groupId: Long)

    @Query("DELETE FROM GroupGroupCategoryCrossRef WHERE groupCategoryId = :groupCategoryId AND groupId = :groupId")
    suspend fun deleteGroupGroupCategoryCrossRefById(groupId: Int, groupCategoryId: Int)

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
    @Query("SELECT * FROM UserGroupCrossRef WHERE groupId = :groupId")
    suspend fun getGroupCrossRefs(groupId: Int): List<UserGroupCrossRef>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User

    @Transaction
    suspend fun getGroupMembersWithRoles(groupId: Int): List<GroupMemberDTO> {
        val crossRefs = getGroupCrossRefs(groupId)
        return crossRefs.map { crossRef ->
            val user = getUserById(crossRef.userId)
            GroupMemberDTO(
                id = user.id,
                name = user.username,
                email = user.email,
                role = crossRef.role
            )
        }
    }

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

    @Query("""
            SELECT * FROM `Group`
            WHERE id IN (
        SELECT groupId FROM UserGroupCrossRef WHERE userId = :userId
    )
        """)
    suspend fun getGroupsByUserId(userId: Long): List<Group>

    @Query("SELECT * FROM GroupCategory")
    fun getAllGroupCategoriesStream(): Flow<List<GroupCategory>>

    @Transaction
    @Query("SELECT * FROM `Group` WHERE id = :groupId")
    suspend fun getGroupWithCategoriesById(groupId: Int): GroupWithCategories?

    @Query("SELECT groupCategoryId FROM GroupGroupCategoryCrossRef WHERE groupId = :groupId")
    suspend fun getGroupCategoryIdsByGroupId(groupId: Int): List<Int>


}