package com.goal.goalapp.data

import androidx.annotation.Keep
import androidx.room.PrimaryKey

/**
 * Base entity for all entities in the database.
 */
@Keep
open class BaseEntity(
    var createdAt: Long = System.currentTimeMillis(),
    var createdBy: Int = 0,
    var updatedAt: Long = System.currentTimeMillis(),
    var updatedBy: Int = 0,
    var isDeleted: Boolean = false
)
