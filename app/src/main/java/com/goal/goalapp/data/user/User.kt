package com.goal.goalapp.data.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goal.goalapp.data.BaseEntity

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val username: String,
    val email: String,
    val passwordHash: String
): BaseEntity()
