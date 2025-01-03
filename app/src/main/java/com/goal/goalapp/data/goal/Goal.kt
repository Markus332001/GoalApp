package com.goal.goalapp.data.goal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.goal.goalapp.data.BaseEntity
import com.goal.goalapp.data.user.User
import java.util.Date

@Entity(tableName = "goals",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userId")]
)
data class Goal(
    val title: String,
    val deadline: Date,
    val notes: String,
    val userId: Int
) : BaseEntity()