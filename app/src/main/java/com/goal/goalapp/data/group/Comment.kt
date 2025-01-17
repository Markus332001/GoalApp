package com.goal.goalapp.data.group

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goal.goalapp.data.BaseEntity
import com.goal.goalapp.data.post.Post
import com.goal.goalapp.data.user.User

// Comment Entity
@Entity(
    foreignKeys = [ForeignKey(
        entity = Post::class,
        parentColumns = ["id"],
        childColumns = ["postId"],
        onDelete = ForeignKey.CASCADE
    ),ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["writerId"],
        onDelete = ForeignKey.NO_ACTION
    )],
    indices = [Index("postId"), Index("writerId")]
)
data class Comment(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val postId: Long, // Post ID
    val text: String,
    val writerId: Int // User ID
): BaseEntity()