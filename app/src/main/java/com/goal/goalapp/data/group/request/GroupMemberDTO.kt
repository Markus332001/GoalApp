package com.goal.goalapp.data.group.request

import com.goal.goalapp.data.Role

data class GroupMemberDTO(
    val id: Int,
    val name: String,
    val email: String,
    val role: Role
)