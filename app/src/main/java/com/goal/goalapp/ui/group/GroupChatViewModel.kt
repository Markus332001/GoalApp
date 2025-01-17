package com.goal.goalapp.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.group.Comment
import com.goal.goalapp.data.goal.GoalRepository
import com.goal.goalapp.data.goal.GoalWithDetails
import com.goal.goalapp.data.group.CommentWithUser
import com.goal.goalapp.data.group.Group
import com.goal.goalapp.data.group.GroupRepository
import com.goal.goalapp.data.group.GroupWithDetails
import com.goal.goalapp.data.group.request.GroupWithDetailsAndRole
import com.goal.goalapp.data.post.PostRepository
import com.goal.goalapp.data.post.PostWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupChatViewModel(
    private val groupRepository: GroupRepository,
    private val userSessionStorage: UserSessionStorage,
    private val goalRepository: GoalRepository,
    private val postRepository: PostRepository
): ViewModel() {
    private val _group = MutableStateFlow<GroupWithDetailsAndRole?>(null)
    val group: StateFlow<GroupWithDetailsAndRole?> = _group

    val userId:StateFlow<Int> = userSessionStorage.userIdFlow
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )


    fun loadGroup(groupId: Int) {
        viewModelScope.launch {
            groupRepository.getGroupWithDetailsAndRoleByIdStream(groupId)
                .filterNotNull()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = null
                )
                .collect { groupDetails ->
                    _group.value = groupDetails
                }
        }
    }

    suspend fun getAllGoalsFromUser(): List<GoalWithDetails> {
        return goalRepository.getGoalsWithDetailsByUserId(userId.first())
    }

    fun addPostWithDetailsDb(postWithDetails: PostWithDetails, groups: List<Group>){
        if(groups.isEmpty()) return
        viewModelScope.launch {
            val groupId = groups.first().id
            postRepository.insertPostWithDetails(postWithDetails.copy(
                post = postWithDetails.post.copy( groupId = groupId, userId = userId.value)
            ))
        }
    }

    fun toggleLike(postWithDetails: PostWithDetails){
        val hasLiked = postWithDetails.post.likesUserIds.contains(userId.value)
        val newLikes = if(hasLiked) postWithDetails.post.likesUserIds.filter { it != userId.value }
                        else postWithDetails.post.likesUserIds + userId.value
        viewModelScope.launch{
            postRepository.updatePost(postWithDetails.post.copy(likesUserIds = newLikes))
        }
    }

    fun addComment(comment: String, post: PostWithDetails){
        if(comment.isBlank()) return
        viewModelScope.launch {
            val newComment = Comment(
                text = comment,
                writerId = userId.value,
                postId = post.post.id.toLong(),
            )

            postRepository.insertComment(newComment)
        }
    }

    fun getCommentsForPost(postId: Int): Flow<List<CommentWithUser>> {
        return postRepository.getCommentsForPostFlow(postId)
    }
}
