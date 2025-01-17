package com.goal.goalapp.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.Role
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.group.GroupRepository
import com.goal.goalapp.data.group.GroupWithDetails
import com.goal.goalapp.data.group.request.GroupWithDetailsAndRole
import com.goal.goalapp.data.user.UserGroupCrossRef
import com.goal.goalapp.ui.goal.CreateEditState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class JoinGroupState{
    data object Initial : JoinGroupState() // initial state
    data object Loading : JoinGroupState()
    data object Success : JoinGroupState()
    data class Error(val message: String) : JoinGroupState()
}

class GroupDetailsViewModel(
    private val groupRepository: GroupRepository,
    private val userSessionStorage: UserSessionStorage
): ViewModel() {
    private val _groupWithDetails = MutableStateFlow<GroupWithDetailsAndRole?>(null)
    val groupWithDetails: StateFlow<GroupWithDetailsAndRole?> = _groupWithDetails

    private val _joinGroupState = MutableStateFlow<JoinGroupState>(JoinGroupState.Initial)
    val joinGroupState: StateFlow<JoinGroupState> = _joinGroupState


    private val _userWithRole = MutableStateFlow<UserGroupCrossRef?>(null)
    val userWithRole: StateFlow<UserGroupCrossRef?> = _userWithRole

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadUserWithRole(groupId: Int){
        viewModelScope.launch {
            userSessionStorage.userIdFlow
                .filterNotNull()
                .flatMapLatest { userId ->
                    groupRepository.getUserInGroupWithRoleStream(groupId = groupId, userId = userId)
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = null
                )
                .collect { userWithRoleDb ->
                    _userWithRole.value = userWithRoleDb
                }
        }
    }

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
                    _groupWithDetails.value = groupDetails
                }
        }
    }

    fun joinGroup(key: String = ""){
        if(_groupWithDetails.value == null ){
            _joinGroupState.value = JoinGroupState.Error("Group not found")
            return
        }

        viewModelScope.launch {
            val userId = userSessionStorage.userIdFlow.first()
            if(userId == null){
                _joinGroupState.value = JoinGroupState.Error("User not logged in")
                return@launch
            }

            _joinGroupState.value = JoinGroupState.Loading
            val userGroupCrossRefId = groupRepository.insertUserGroupCrossRefJoinGroup(
                userId = userId,
                groupId = _groupWithDetails.value!!.group.id,
                key = key
            )
            if(userGroupCrossRefId == -1L){
                _joinGroupState.value = JoinGroupState.Error("Invalid key")
                return@launch
            }
            _joinGroupState.value = JoinGroupState.Success
        }
    }

    fun changeRole(role: Role, userId: Int){
        viewModelScope.launch {
            try {
                groupRepository.updateUserGroupCrossRefRole(groupId = _groupWithDetails.value!!.group.id, userId = userId, role = role)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun removeMember(userId: Int){
        viewModelScope.launch {
            try{
                groupRepository.deleteUserGroupCrossRefByIds(groupId = _groupWithDetails.value!!.group.id.toLong(), userId = userId.toLong())
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}
