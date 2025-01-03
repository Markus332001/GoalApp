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
     * Retrieve an group from the data source that match with the provided id
     */
    fun getGroupById(groupId: Long): Flow<Group?>

    /**
     * Retrieve all groups from the data source
     */
    fun getAllGroups(): Flow<List<Group>>

    /**
     * Retrieve all groups from the data source that are not associated with the provided user
     */
    fun getGroupsNotContainingUser(userId: Long): Flow<List<Group>>

    /**
     * Retrieve an group with details from the data source that match with the provided id
     */
    fun getGroupWithDetailsById(groupId: Long): Flow<GroupWithDetails?>

}