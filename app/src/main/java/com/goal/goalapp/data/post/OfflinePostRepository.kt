package com.goal.goalapp.data.post

import kotlinx.coroutines.flow.Flow

//override the functions defined in the PostRepository interface and call the corresponding functions from the PostDao
class OfflinePostRepository(private val postDao: PostDao) : PostRepository {
    override suspend fun insertPost(post: Post): Long = postDao.insertPost(post)

    override suspend fun updatePost(post: Post) = postDao.update(post)

    override suspend fun deletePost(post: Post) = postDao.deletePost(post)

    override fun getPostByIdStream(postId: Int): Flow<Post?> = postDao.getPostById(postId)

}