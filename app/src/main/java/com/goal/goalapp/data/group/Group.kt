package com.goal.goalapp.data.group

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.goal.goalapp.data.BaseEntity

// Group Entity
@Entity
data class Group(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val name: String,
    val isPrivate: Boolean,
    val key: String,
    val description: String?
): BaseEntity()