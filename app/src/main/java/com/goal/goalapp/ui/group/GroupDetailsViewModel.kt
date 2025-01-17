package com.goal.goalapp.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.group.GroupRepository
import com.goal.goalapp.data.group.GroupWithDetails
import com.goal.goalapp.data.group.request.GroupWithDetailsAndRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupDetailsViewModel(
    private val groupRepository: GroupRepository
): ViewModel() {
    private val _groupWithDetails = MutableStateFlow<GroupWithDetailsAndRole?>(null)
    val groupWithDetails: StateFlow<GroupWithDetailsAndRole?> = _groupWithDetails

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
}