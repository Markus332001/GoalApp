package com.goal.goalapp.data.goal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goal.goalapp.data.BaseEntity
import com.goal.goalapp.data.CompletionType

@Entity(
    foreignKeys = [ForeignKey(
        entity = Goal::class,
        parentColumns = ["id"],
        childColumns = ["goalId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("goalId")]
)
data class CompletionCriterion(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val goalId: Int,
    val completionType: CompletionType,
    val targetValue: Int?,
    val currentValue: Int?,
    val unit: String?
): BaseEntity()