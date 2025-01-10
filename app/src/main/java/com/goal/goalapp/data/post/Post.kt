package com.goal.goalapp.data.post

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goal.goalapp.data.BaseEntity
import com.goal.goalapp.data.group.Group

// Post Entity
@Entity(
    foreignKeys = [ForeignKey(
        entity = Group::class,
        parentColumns = ["id"],
        childColumns = ["groupId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("groupId")]
)
data class Post(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val groupId: Int,
    val goalName: String,
    val goalPeriod: String,
    val likes: Int
): BaseEntity()