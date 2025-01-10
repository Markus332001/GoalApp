package com.goal.goalapp.data.comment

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goal.goalapp.data.BaseEntity
import com.goal.goalapp.data.post.Post

// Comment Entity
@Entity(
    foreignKeys = [ForeignKey(
        entity = Post::class,
        parentColumns = ["id"],
        childColumns = ["postId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("postId")]
)
data class Comment(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val postId: Long, // Post ID
    val text: String,
    val parentCommentId: Long? // For nested comments
): BaseEntity()