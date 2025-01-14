package com.goal.goalapp.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.group.GroupCategory
import com.goal.goalapp.data.group.GroupRepository
import com.goal.goalapp.data.group.GroupWithCategories
import com.goal.goalapp.data.post.PostWithDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CreateGroup(
    val id: Int = 0,
    val name: String = "",
    val isPrivate: Boolean = false,
    val key: String = "",
    val description: String = "",
    val categories: List<GroupCategory> = emptyList()
)

sealed class CreateEditGroupState {
    object Initial : CreateEditGroupState()
    object Loading : CreateEditGroupState()
    object Success : CreateEditGroupState()
    data class Error(val message: String) : CreateEditGroupState()
}

class CreateEditGroupViewModel(
    private val groupRepository: GroupRepository,
    private val userSessionStorage: UserSessionStorage
): ViewModel() {

    private val _createGroup = MutableStateFlow(CreateGroup())
    val createGroup: StateFlow<CreateGroup> = _createGroup.asStateFlow()

    private val _createEditGroupState = MutableStateFlow<CreateEditGroupState>(CreateEditGroupState.Initial)
    val createEditGroupState: StateFlow<CreateEditGroupState> = _createEditGroupState

    val allCategories: StateFlow<List<GroupCategory>> = groupRepository.getAllGroupCategoriesStream()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun getGroupFromDb(groupId: Int){
        viewModelScope.launch {
            val group = groupRepository.getGroupWithCategoriesById(groupId) ?: return@launch
            _createGroup.value = CreateGroup(
                id = group.group.id,
                name = group.group.name,
                isPrivate = group.group.isPrivate,
                key = group.group.key,
                description = group.group.description,
                categories = group.categories
            )
        }
    }

    fun updateGroupName(name: String) {
        _createGroup.value = _createGroup.value.copy(name = name)
    }

    fun updateGroupIsPrivate(isPrivate: Boolean) {
        _createGroup.value = _createGroup.value.copy(isPrivate = isPrivate)
    }

    fun updateGroupKey(key: String) {
        _createGroup.value = _createGroup.value.copy(key = key)
    }

    fun updateGroupDescription(description: String) {
        _createGroup.value = _createGroup.value.copy(description = description)
    }

    fun addCategory(category: GroupCategory) {
        _createGroup.value = _createGroup.value.copy(
            categories = _createGroup.value.categories + category
        )
    }

    fun removeCategory(category: GroupCategory) {
        _createGroup.value = _createGroup.value.copy(
            categories = _createGroup.value.categories - category
        )
    }

    fun updateCategory(newCategories: List<GroupCategory>) {
        _createGroup.value = _createGroup.value.copy(
            categories = newCategories
        )
    }

    fun resetCreateGroup() {
        _createGroup.value = CreateGroup()
    }

    fun deleteGroup(){
        if(_createGroup.value.id != 0){
            viewModelScope.launch {
                groupRepository.deleteGroupById(_createGroup.value.id)
            }
        }
    }

    fun checkGroupValidity(): Boolean {
        return _createGroup.value.name.isNotBlank() && if(_createGroup.value.isPrivate) _createGroup.value.key.isNotBlank() else true
    }
}