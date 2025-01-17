package com.goal.goalapp.data.group

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.goal.goalapp.data.post.Post
import com.goal.goalapp.data.post.PostWithDetails
import com.goal.goalapp.data.user.User
import com.goal.goalapp.data.user.UserGroupCrossRef

// Group with Categories and Members
data class GroupWithDetails(
    @Embedded val group: Group,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = GroupGroupCategoryCrossRef::class,
            parentColumn = "groupId",
            entityColumn = "groupCategoryId"
        )
    )
    val categories: List<GroupCategory>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = User::class,
        associateBy = Junction(
            value = UserGroupCrossRef::class,
            parentColumn = "groupId",
            entityColumn = "userId"
        )
    )
    val members: List<User>,
    @Relation(
        parentColumn = "id",
        entityColumn = "groupId",
        entity = Post::class,
        associateBy = Junction(
            value = Post::class,
            parentColumn = "groupId",
            entityColumn = "id"
        )
    )
    val posts: List<PostWithDetails>
)
