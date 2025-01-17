package com.goal.goalapp.data.post

import com.goal.goalapp.data.group.Comment
import com.goal.goalapp.data.group.CommentWithUser
import com.goal.goalapp.data.group.Group
import kotlinx.coroutines.flow.Flow

//override the functions defined in the PostRepository interface and call the corresponding functions from the PostDao
class OfflinePostRepository(private val postDao: PostDao) : PostRepository {
    override suspend fun insertPost(post: Post): Long = postDao.insertPost(post)

    override suspend fun insertComment(comment: Comment): Long = postDao.insertComment(comment)

    override suspend fun insertPostWithDetails(postWithDetails: PostWithDetails): Int {
        val postId = postDao.insertPost(postWithDetails.post)
        val routineSummaries = postWithDetails.routineSummary.map { routineS ->
            routineS.copy(postId = postId)
        }
        for (routineSummary in routineSummaries) {
            postDao.insertRoutineSummary(routineSummary)
        }
        return postId.toInt()
    }

    override suspend fun insertPostWithDetailsToGroupsId(
        postWithDetails: PostWithDetails,
        groupsId: List<Int>
    ) {
        for (groupId in groupsId) {
            insertPostWithDetails(postWithDetails.copy(post = postWithDetails.post.copy( groupId = groupId)))
        }
    }

    override suspend fun updatePost(post: Post) = postDao.update(post)

    override suspend fun deletePost(post: Post) = postDao.deletePost(post)

    override fun getPostByIdStream(postId: Int): Flow<Post?> = postDao.getPostById(postId)

    override fun getCommentsForPostFlow(postId: Int): Flow<List<CommentWithUser>> = postDao.getCommentsForPostFlow(postId)

}