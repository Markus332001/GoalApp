package com.goal.goalapp.data.group

import androidx.room.Entity
import com.goal.goalapp.data.BaseEntity

// Group Entity
@Entity
data class Group(
    val name: String,
    val isPrivate: Boolean,
    val key: String,
    val description: String?
): BaseEntity()