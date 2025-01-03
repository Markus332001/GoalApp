package com.goal.goalapp.data.group

import kotlinx.coroutines.flow.Flow

//override the functions defined in the GroupRepository interface and call the corresponding functions from the GroupDao
class OfflineGroupRepository(private val groupDao: GroupDao) : GroupRepository {
    override suspend fun insertGroup(group: Group): Long = groupDao.insertGroup(group)

    override suspend fun update(group: Group) = groupDao.update(group)

    override suspend fun deleteGroup(group: Group) = groupDao.deleteGroup(group)

    override fun getGroupById(groupId: Long): Flow<Group?> = groupDao.getGroupById(groupId)

    override fun getAllGroups(): Flow<List<Group>> = groupDao.getAllGroups()

    override fun getGroupsNotContainingUser(userId: Long): Flow<List<Group>> = groupDao.getGroupsNotContainingUser(userId)

    override fun getGroupWithDetailsById(groupId: Long): Flow<GroupWithDetails?> = groupDao.getGroupWithDetailsById(groupId)
}