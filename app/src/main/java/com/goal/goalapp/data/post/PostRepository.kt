package com.goal.goalapp.data.post

import com.goal.goalapp.data.group.Comment
import com.goal.goalapp.data.group.CommentWithUser
import com.goal.goalapp.data.group.Group
import kotlinx.coroutines.flow.Flow

interface PostRepository {

    /**
     * Insert post in the data source
     */
    suspend fun insertPost(post: Post): Long

    /**
     * Insert post with details in the data source
     */
    suspend fun insertComment(comment: Comment): Long

    /**
     * Insert post with details in the data source
     */
    suspend fun insertPostWithDetails(postWithDetails: PostWithDetails): Int

    /**
     * Insert post with details to groups in the data source
     */
    suspend fun insertPostWithDetailsToGroupsId(postWithDetails: PostWithDetails, groupsId: List<Int>)

    /**
     * Update post in the data source
     */
    suspend fun updatePost(post: Post)

    /**
     * Delete post from the data source
     */
    suspend fun deletePost(post: Post)

    /**
     * Retrieve an post from the data source that match with the provided id
     */
    fun getPostByIdStream(postId: Int): Flow<Post?>

    /**
     * Retrieve comments of a post from the data source
     */
    fun getCommentsForPostFlow(postId: Int): Flow<List<CommentWithUser>>

}