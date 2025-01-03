package com.goal.goalapp.data.post

import kotlinx.coroutines.flow.Flow

interface PostRepository {

    /**
     * Insert post in the data source
     */
    suspend fun insertPost(post: Post): Long

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

}