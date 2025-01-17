package com.goal.goalapp.data

import androidx.annotation.Keep
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Base entity for all entities in the database.
 */
@Keep
open class BaseEntity(
    var createdAt: LocalDate = LocalDate.now(),
    var createdBy: Int = 0,
    var updatedAt: LocalDate = LocalDate.now(),
    var updatedBy: Int = 0,
    var isDeleted: Boolean = false
)
