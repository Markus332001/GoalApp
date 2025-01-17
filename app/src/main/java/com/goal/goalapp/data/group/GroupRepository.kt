package com.goal.goalapp.data.group

import com.goal.goalapp.data.group.request.CreateGroupWithDetailsRequest
import com.goal.goalapp.data.group.request.GroupWithDetailsAndRole
import com.goal.goalapp.data.user.UserGroupCrossRef
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    /**
     * Insert group in the data source
     */
    suspend fun insertGroup(group: Group): Long

    /**
     * Insert group with categories in the data source
     */
    suspend fun insertGroupWithDetailsRequest(groupWithDetailsRequest: CreateGroupWithDetailsRequest): Long

    /**
     * Insert group with user cross ref in the data source
     */
    suspend fun insertUserGroupCrossRef(userGroupCrossRef: UserGroupCrossRef): Long

    /**
     * Insert Group with categories cross ref in the data source
     */
    suspend fun insertGroupWithCategoriesCrossRef(groupWithCategoriesCrossRef: GroupGroupCategoryCrossRef): Long


    /**
     * Update group in the data source
     */
    suspend fun update(group: Group)

    /**
     * Update group with categories in the data source
     */
    suspend fun updateGroupWithCategories(groupWithCategories: GroupWithCategories)


    /**
     * Delete group from the data source
     */
    suspend fun deleteGroup(group: Group)

    /**
     * Delete group from the data source
     */
    suspend fun deleteGroupById(groupId: Int)

    /**
     * Delete user from group
     */
    suspend fun deleteUserGroupCrossRefByIds(userId: Long, groupId: Long)

    /**
     * Delete group category from group
     */
    suspend fun deleteGroupGroupCategoryCrossRefById(groupId: Int, groupCategoryId: Int)

    /**
     * Retrieve an group from the data source that match with the provided id
     */
    fun getGroupByIdStream(groupId: Int): Flow<Group?>

    /**
     * Retrieve all groups from the data source
     */
    fun getAllGroupsStream(): Flow<List<Group>>

    /**
     * Retrieve all groups from the data source that are not associated with the provided user
     */
    fun getGroupsWithCategoriesNotContainingUserStream(userId: Int): Flow<List<GroupWithCategories>>

    /**
     * Retrieve all groups from the data source by User id
     */
    fun getGroupsWithCategoriesByUserIdStream(userId: Int): Flow<List<GroupWithCategories>>

    /**
     * Retrieve all groups from the data source by User id
     */
    suspend fun getGroupsByUserId(userId: Long): List<Group>

    /**
     * Retrieve an group with details from the data source that match with the provided id
     */
    fun getGroupWithDetailsAndRoleByIdStream(groupId: Int): Flow<GroupWithDetailsAndRole?>

    /**
     * Retrieve all group categories from the data source
     */
    fun getAllGroupCategoriesStream(): Flow<List<GroupCategory>>

    /**
     * Retrieves a group with categories from the data source that match with the provided id
     */
    suspend fun getGroupWithCategoriesById(groupId: Int): GroupWithCategories?

    /**
     * Retrieve group categories by group id
     */
    suspend fun getGroupCategoryIdsByGroupId(groupId: Int): List<Int>

}