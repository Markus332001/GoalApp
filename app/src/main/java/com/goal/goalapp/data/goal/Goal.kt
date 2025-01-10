package com.goal.goalapp.data.goal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goal.goalapp.data.BaseEntity
import com.goal.goalapp.data.user.User
import java.time.LocalDate

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
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,
    val progress: Float,
    val deadline: LocalDate,
    val notes: String,
    var userId: Int
) : BaseEntity()