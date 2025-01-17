package com.goal.goalapp.data.group

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goal.goalapp.data.BaseEntity

// GroupCategory Entity
@Entity
data class GroupCategory(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val name: String
): BaseEntity()