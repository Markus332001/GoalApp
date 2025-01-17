package com.goal.goalapp.data.group

import androidx.room.Embedded
import com.goal.goalapp.data.Role
import com.goal.goalapp.data.user.User

data class GroupMemberWithRole (
    @Embedded val user: User,
    val role: Role
)