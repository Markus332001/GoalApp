package com.goal.goalapp.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.goal.Goal
import com.goal.goalapp.data.goal.GoalRepository
import com.goal.goalapp.data.group.Group
import com.goal.goalapp.data.group.GroupCategory
import com.goal.goalapp.data.group.GroupRepository
import com.goal.goalapp.data.group.GroupWithCategories
import com.goal.goalapp.data.user_session.UserSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class GroupViewModel(
    private val groupRepository: GroupRepository,
    private val userSessionStorage: UserSessionStorage
): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val myFilteredGroups : StateFlow<List<GroupWithCategories>> = userSessionStorage.userIdFlow
        .filterNotNull()
        .flatMapLatest { userId ->
            groupRepository.getGroupsWithCategoriesByUserIdStream(userId)
        }
        .map{ groups ->
            getFilteredGroups(
                groups,
                activeFilterCategoriesMyGroups.value,
                searchQueryMyGroups.value)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val otherFilteredGroups : StateFlow<List<GroupWithCategories>> = userSessionStorage.userIdFlow
        .filterNotNull()
        .flatMapLatest { userId ->
            groupRepository.getGroupsWithCategoriesByUserIdStream(userId)
        }
        .map{ groups ->
            getFilteredGroups(
                groups,
                activeFilterCategoriesOtherGroups.value,
                searchQueryOtherGroups.value)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    val filter = groupRepository.getAllGroupCategoriesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _activeFilterCategoriesMyGroups = MutableStateFlow<List<GroupCategory>>(emptyList())
    val activeFilterCategoriesMyGroups: StateFlow<List<GroupCategory>> = _activeFilterCategoriesMyGroups

    private val _searchQueryMyGroups = MutableStateFlow("")
    val searchQueryMyGroups: StateFlow<String> = _searchQueryMyGroups

    private val _activeFilterCategoriesOtherGroups = MutableStateFlow<List<GroupCategory>>(emptyList())
    val activeFilterCategoriesOtherGroups: StateFlow<List<GroupCategory>> = _activeFilterCategoriesOtherGroups

    private val _searchQueryOtherGroups = MutableStateFlow("")
    val searchQueryOtherGroups: StateFlow<String> = _searchQueryOtherGroups


    /**
     * Filters the groups based on the active filter categories and the search query
     */
    fun getFilteredGroups(groupsWithCategories: List<GroupWithCategories>, activeFilterCategories: List<GroupCategory>, searchQuery: String): List<GroupWithCategories>{
        return groupsWithCategories.filter{ groupWithCategories -> groupWithCategories.group.name.trim().contains(searchQuery.trim(), ignoreCase = true)
                && groupWithCategories.categories.any { category -> activeFilterCategories.contains(category) }
        }
    }

    fun addActiveFilterCategoryMyGroups(category: GroupCategory){
        _activeFilterCategoriesMyGroups.value += category
    }

    fun removeActiveFilterCategoryMyGroups(category: GroupCategory){
        _activeFilterCategoriesMyGroups.value -= category
    }

    fun addActiveFilterCategoryOtherGroups(category: GroupCategory){
        _activeFilterCategoriesOtherGroups.value += category
    }

    fun removeActiveFilterCategoryOtherGroups(category: GroupCategory){
        _activeFilterCategoriesOtherGroups.value -= category
    }

    fun updateSearchQueryMyGroups(searchQuery: String){
        _searchQueryMyGroups.value = searchQuery
    }

    fun updateSearchQueryOtherGroups(searchQuery: String){
        _searchQueryOtherGroups.value = searchQuery
    }

}