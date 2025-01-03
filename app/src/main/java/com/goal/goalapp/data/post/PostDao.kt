package com.goal.goalapp.data.post

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPost(post: Post): Long

    @Update
    suspend fun update(post: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("SELECT * FROM post WHERE id = :postId")
    fun getPostById(postId: Int): Flow<Post?>

}