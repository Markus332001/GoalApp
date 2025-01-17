package com.goal.goalapp.data.goal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goal.goalapp.data.BaseEntity
import com.goal.goalapp.data.post.Post

@Entity(
    foreignKeys = [ForeignKey(
        entity = Post::class,
        parentColumns = ["id"],
        childColumns = ["postId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("postId")]
)
data class RoutineSummary (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val postId: Long,
    val title: String,
    val frequency: String,
    val progress: Float
): BaseEntity()