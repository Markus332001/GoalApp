package com.goal.goalapp.data.post

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.goal.goalapp.data.group.Comment
import com.goal.goalapp.data.goal.RoutineSummary
import com.goal.goalapp.data.group.CommentWithUser
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPost(post: Post): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRoutineSummary(routineSummary: RoutineSummary)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertComment(comment: Comment): Long

    @Update
    suspend fun update(post: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("SELECT * FROM post WHERE id = :postId")
    fun getPostById(postId: Int): Flow<Post?>

    @Query("SELECT * FROM comment WHERE postId = :postId ORDER BY createdAt DESC")
    fun getCommentsForPostFlow(postId: Int): Flow<List<CommentWithUser>>
}