package com.goal.goalapp.data.group

import kotlinx.coroutines.flow.Flow

//override the functions defined in the GroupRepository interface and call the corresponding functions from the GroupDao
class OfflineGroupRepository(private val groupDao: GroupDao) : GroupRepository {
    override suspend fun insertGroup(group: Group): Long = groupDao.insertGroup(group)

    override suspend fun update(group: Group) = groupDao.update(group)

    override suspend fun deleteGroup(group: Group) = groupDao.deleteGroup(group)
    override suspend fun deleteGroupById(groupId: Int) = groupDao.deleteGroupById(groupId)

    override fun getGroupByIdStream(groupId: Int): Flow<Group?> = groupDao.getGroupById(groupId)

    override fun getAllGroupsStream(): Flow<List<Group>> = groupDao.getAllGroupsStream()

    override fun getGroupsWithCategoriesNotContainingUserStream(userId: Int): Flow<List<GroupWithCategories>> = groupDao.getGroupsWithCategoriesNotContainingUserStream(userId)

    override fun getGroupsWithCategoriesByUserIdStream(userId: Int): Flow<List<GroupWithCategories>> = groupDao.getGroupsWithCategoriesByUserIdStream(userId)

    override fun getGroupWithDetailsByIdStream(groupId: Int): Flow<GroupWithDetails?> = groupDao.getGroupWithDetailsByIdStream(groupId)

    override fun getAllGroupCategoriesStream(): Flow<List<GroupCategory>> = groupDao.getAllGroupCategoriesStream()

    override suspend fun getGroupWithCategoriesById(groupId: Int): GroupWithCategories? = groupDao.getGroupWithCategoriesById(groupId)
}