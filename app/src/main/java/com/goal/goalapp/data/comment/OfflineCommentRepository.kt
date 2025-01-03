package com.goal.goalapp.data.comment

import kotlinx.coroutines.flow.Flow

//overrides the functions defined in the CommentRepository interface and call the corresponding functions from the ItemDao
class OfflineCommentRepository(private val commentDao: CommentDao) : CommentRepository {
    override suspend fun insertComment(comment: Comment): Long = commentDao.insertComment(comment)

    override suspend fun updateComment(comment: Comment) = commentDao.update(comment)

    override suspend fun deleteComment(comment: Comment) = commentDao.deleteComment(comment)

    override fun getCommentByIdStream(commentId: Int): Flow<Comment?> = commentDao.getCommentById(commentId)

}