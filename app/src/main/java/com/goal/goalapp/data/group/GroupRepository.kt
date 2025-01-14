package com.goal.goalapp.data.group

import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    /**
     * Insert group in the data source
     */
    suspend fun insertGroup(group: Group): Long

    /**
     * Update group in the data source
     */
    suspend fun update(group: Group)

    /**
     * Delete group from the data source
     */
    suspend fun deleteGroup(group: Group)

    /**
     * Delete group from the data source
     */
    suspend fun deleteGroupById(groupId: Int)

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
     * Retrieve an group with details from the data source that match with the provided id
     */
    fun getGroupWithDetailsByIdStream(groupId: Int): Flow<GroupWithDetails?>

    /**
     * Retrieve all group categories from the data source
     */
    fun getAllGroupCategoriesStream(): Flow<List<GroupCategory>>

    /**
     * Retrieves a group with categories from the data source that match with the provided id
     */
    suspend fun getGroupWithCategoriesById(groupId: Int): GroupWithCategories?

}