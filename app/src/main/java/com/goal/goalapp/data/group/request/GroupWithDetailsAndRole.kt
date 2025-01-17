package com.goal.goalapp.data.group.request

import com.goal.goalapp.data.group.Group
import com.goal.goalapp.data.group.GroupCategory
import com.goal.goalapp.data.post.PostWithDetails

data class GroupWithDetailsAndRole (
    val group: Group,
    val members: List<GroupMemberDTO>,
    val posts: List<PostWithDetails>,
    val groupCategories: List<GroupCategory>
)