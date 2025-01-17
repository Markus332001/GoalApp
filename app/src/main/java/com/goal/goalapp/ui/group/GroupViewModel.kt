package com.goal.goalapp.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.group.GroupCategory
import com.goal.goalapp.data.group.GroupRepository
import com.goal.goalapp.data.group.GroupWithCategories
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class GroupViewModel(
    private val groupRepository: GroupRepository,
    private val userSessionStorage: UserSessionStorage
): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val myGroups : StateFlow<List<GroupWithCategories>> = userSessionStorage.userIdFlow
        .filterNotNull()
        .flatMapLatest { userId ->
            groupRepository.getGroupsWithCategoriesByUserIdStream(userId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _activeFilterCategoriesMyGroups = MutableStateFlow<List<GroupCategory>>(emptyList())
    val activeFilterCategoriesMyGroups: StateFlow<List<GroupCategory>> = _activeFilterCategoriesMyGroups

    private val _searchQueryMyGroups = MutableStateFlow("")
    val searchQueryMyGroups: StateFlow<String> = _searchQueryMyGroups

    val myFilteredGroups: StateFlow<List<GroupWithCategories>> = combine(
        myGroups,
        _activeFilterCategoriesMyGroups,
        _searchQueryMyGroups
    ) { groups, activeFilters, searchQuery ->
        getFilteredGroups(groups, activeFilters, searchQuery)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    @OptIn(ExperimentalCoroutinesApi::class)
    val otherGroups : StateFlow<List<GroupWithCategories>> = userSessionStorage.userIdFlow
        .filterNotNull()
        .flatMapLatest { userId ->
            groupRepository.getGroupsWithCategoriesNotContainingUserStream(userId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    private val _activeFilterCategoriesOtherGroups = MutableStateFlow<List<GroupCategory>>(emptyList())
    val activeFilterCategoriesOtherGroups: StateFlow<List<GroupCategory>> = _activeFilterCategoriesOtherGroups

    private val _searchQueryOtherGroups = MutableStateFlow("")
    val searchQueryOtherGroups: StateFlow<String> = _searchQueryOtherGroups

    // if one of them changes it will trigger the combine function and filters the groups
    val otherFilteredGroups: StateFlow<List<GroupWithCategories>> = combine(
        otherGroups,
        _activeFilterCategoriesOtherGroups,
        _searchQueryOtherGroups
    ) { groups, activeFilters, searchQuery ->
        getFilteredGroups(groups, activeFilters, searchQuery)
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





    /**
     * Filters the groups based on the active filter categories and the search query
     */
    fun getFilteredGroups(groupsWithCategories: List<GroupWithCategories>, activeFilterCategories: List<GroupCategory>, searchQuery: String): List<GroupWithCategories>{
        return groupsWithCategories.filter{ groupWithCategories -> groupWithCategories.group.name.trim().contains(searchQuery.trim(), ignoreCase = true)
                && if(groupWithCategories.categories.isEmpty() && activeFilterCategories.isEmpty()) true else groupWithCategories.categories.any { category -> if(activeFilterCategories.isEmpty()) true else activeFilterCategories.contains(category) }
        }
    }

    fun updateActiveFilterCategoriesMyGroups(activeFilterCategories: List<GroupCategory>){
        _activeFilterCategoriesMyGroups.value = activeFilterCategories
    }

    fun updateActiveFilterCategoriesOtherGroups(activeFilterCategories: List<GroupCategory>){
        _activeFilterCategoriesOtherGroups.value = activeFilterCategories
    }

    fun updateSearchQueryMyGroups(searchQuery: String){
        _searchQueryMyGroups.value = searchQuery
    }

    fun updateSearchQueryOtherGroups(searchQuery: String){
        _searchQueryOtherGroups.value = searchQuery
    }

}