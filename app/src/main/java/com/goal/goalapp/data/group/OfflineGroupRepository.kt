package com.goal.goalapp.data.group

import com.goal.goalapp.data.Role
import com.goal.goalapp.data.group.request.CreateGroupWithDetailsRequest
import com.goal.goalapp.data.group.request.GroupWithDetailsAndRole
import com.goal.goalapp.data.user.UserGroupCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//override the functions defined in the GroupRepository interface and call the corresponding functions from the GroupDao
class OfflineGroupRepository(private val groupDao: GroupDao) : GroupRepository {
    override suspend fun insertGroup(group: Group): Long = groupDao.insertGroup(group)

    override suspend fun insertGroupWithDetailsRequest(groupWithDetailsRequest: CreateGroupWithDetailsRequest): Long{
        val groupId = groupDao.insertGroup(groupWithDetailsRequest.group)
        for(category in groupWithDetailsRequest.categories){
            groupDao.insertGroupWithCategoryCrossRef(
                GroupGroupCategoryCrossRef(
                groupId = groupId,
                groupCategoryId = category.id.toLong()
            )
            )
        }
        insertUserGroupCrossRef(
            UserGroupCrossRef(
                userId = groupWithDetailsRequest.userId.toInt(),
                groupId = groupId.toInt(),
                role = Role.OWNER
            )
        )

        return groupId
    }

    override suspend fun insertUserGroupCrossRef(userGroupCrossRef: UserGroupCrossRef): Long = groupDao.insertUserGroupCrossRef(userGroupCrossRef)

    override suspend fun insertGroupWithCategoriesCrossRef(groupWithCategoriesCrossRef: GroupGroupCategoryCrossRef):Long = groupDao.insertGroupWithCategoryCrossRef(groupWithCategoriesCrossRef)

    override suspend fun update(group: Group) = groupDao.update(group)

    override suspend fun updateGroupWithCategories(groupWithCategories: GroupWithCategories) {

        update(groupWithCategories.group)

        val groupCategoryInts = getGroupCategoryIdsByGroupId(groupWithCategories.group.id)

        //deletes all groupCategories from db which arent anymore in Update
        val toDeleteGroupCategories = groupCategoryInts.filter{
            groupWithCategories.categories.none { category ->
                category.id == it
            }
        }
        for(categoryId in toDeleteGroupCategories){
            deleteGroupGroupCategoryCrossRefById(groupWithCategories.group.id, categoryId)
        }
        //inserts all groupCategories which are not in db
        val toInsertGroupCategories = groupWithCategories.categories.filter{
            groupCategoryInts.none { categoryId ->
                categoryId == it.id
            }
        }
        for(category in toInsertGroupCategories){
            insertGroupWithCategoriesCrossRef(
                GroupGroupCategoryCrossRef(
                groupId = groupWithCategories.group.id.toLong(),
                groupCategoryId = category.id.toLong()
            ))
        }

    }


    override suspend fun deleteGroup(group: Group) = groupDao.deleteGroup(group)

    override suspend fun deleteGroupById(groupId: Int) = groupDao.deleteGroupById(groupId)

    override suspend fun deleteUserGroupCrossRefByIds(userId: Long, groupId: Long) = groupDao.deleteUserGroupCrossRefByIds(userId, groupId)

    override suspend fun deleteGroupGroupCategoryCrossRefById(groupId: Int, groupCategoryId: Int) = groupDao.deleteGroupGroupCategoryCrossRefById(groupId = groupId, groupCategoryId = groupCategoryId)

    override fun getGroupByIdStream(groupId: Int): Flow<Group?> = groupDao.getGroupById(groupId)

    override fun getAllGroupsStream(): Flow<List<Group>> = groupDao.getAllGroupsStream()

    override fun getGroupsWithCategoriesNotContainingUserStream(userId: Int): Flow<List<GroupWithCategories>> = groupDao.getGroupsWithCategoriesNotContainingUserStream(userId)

    override fun getGroupsWithCategoriesByUserIdStream(userId: Int): Flow<List<GroupWithCategories>> = groupDao.getGroupsWithCategoriesByUserIdStream(userId)

    override suspend fun getGroupsByUserId(userId: Long): List<Group> = groupDao.getGroupsByUserId(userId)

    override fun getGroupWithDetailsAndRoleByIdStream(groupId: Int): Flow<GroupWithDetailsAndRole?> {
        return groupDao.getGroupWithDetailsByIdStream(groupId).map{ groupWithDetails ->
            GroupWithDetailsAndRole(
                group = groupWithDetails?.group ?: Group(
                    name = "",
                    description = "",
                    id = 0,
                    isPrivate = false,
                    key = ""
                ),
                members = groupDao.getGroupMembersWithRoles(groupId),
                posts = groupWithDetails?.posts ?: emptyList(),
                groupCategories = groupWithDetails?.categories ?: emptyList()
            )
        }
    }

    override fun getAllGroupCategoriesStream(): Flow<List<GroupCategory>> = groupDao.getAllGroupCategoriesStream()

    override suspend fun getGroupWithCategoriesById(groupId: Int): GroupWithCategories? = groupDao.getGroupWithCategoriesById(groupId)

    override suspend fun getGroupCategoryIdsByGroupId(groupId: Int): List<Int> = groupDao.getGroupCategoryIdsByGroupId(groupId)
}