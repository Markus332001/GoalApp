package com.goal.goalapp.data.comment

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Comment] from a given data source.
 */
interface CommentRepository {
    /**
     * Insert comment in the data source
     */
    suspend fun insertComment(comment: Comment): Long

    /**
     * Update comment in the data source
     */
    suspend fun updateComment(comment: Comment)

    /**
     * Delete comment from the data source
     */
    suspend fun deleteComment(comment: Comment)

    /**
     * Retrieve an comment from the data source that match with the provided id
     */
    fun getCommentByIdStream(commentId: Int): Flow<Comment?>

}