package com.goal.goalapp.data.post

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.goal.goalapp.data.group.Comment
import com.goal.goalapp.data.group.CommentWithUser
import com.goal.goalapp.data.goal.RoutineSummary
import com.goal.goalapp.data.user.User

// Post with Comments
data class PostWithDetails(
    @Embedded val post: Post,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId",
        entity = Comment::class,
        associateBy = Junction(
            value = Comment::class,
            parentColumn = "postId",
            entityColumn = "id"
        )
    )
    val comments: List<CommentWithUser>,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId",
        entity = RoutineSummary::class
    )
    val routineSummary: List<RoutineSummary>,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id",
        entity = User::class
    )
    val user: User
)