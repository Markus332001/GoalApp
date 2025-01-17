package com.goal.goalapp.data.group.request

import com.goal.goalapp.data.group.Group
import com.goal.goalapp.data.group.GroupCategory

data class CreateGroupWithDetailsRequest (
    val group: Group,
    val categories: List<GroupCategory>,
    val userId: Long
    )
