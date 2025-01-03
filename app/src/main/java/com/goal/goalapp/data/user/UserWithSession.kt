package com.goal.goalapp.data.user

import androidx.room.Embedded
import androidx.room.Relation
import com.goal.goalapp.data.user_session.UserSession

// User with Session Relation
data class UserWithSession(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val session: UserSession?
)