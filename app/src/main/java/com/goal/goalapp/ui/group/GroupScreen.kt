package com.goal.goalapp.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.data.group.GroupWithCategories
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.SearchBar
import com.goal.goalapp.ui.components.SelectCategoriesDialog

@Composable
fun GroupScreen(
    toCreateGroupScreen: () -> Unit,
    groupViewModel: GroupViewModel = viewModel(factory = AppViewModelProvider.Factory),
    toGroupChatScreen: (Int) -> Unit,
    toGroupDetailsScreen: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    val myFilteredGroups by groupViewModel.myFilteredGroups.collectAsState()
    val otherFilteredGroups by groupViewModel.otherFilteredGroups.collectAsState()
    val filter by groupViewModel.filter.collectAsState()
    val activeFilterCategoriesMyGroups by groupViewModel.activeFilterCategoriesMyGroups.collectAsState()
    val searchQueryMyGroups by groupViewModel.searchQueryMyGroups.collectAsState()
    val activeFilterCategoriesOtherGroups by groupViewModel.activeFilterCategoriesOtherGroups.collectAsState()
    val searchQueryOtherGroups by groupViewModel.searchQueryOtherGroups.collectAsState()
    val openMyGroupFilterDialog = remember { mutableStateOf(false) }
    val openOtherGroupFilterDialog = remember { mutableStateOf(false) }

    // State to track the selected tab
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Tab titles
    val tabTitles = listOf(stringResource(R.string.my_groups), stringResource(R.string.join_group))

    if(openMyGroupFilterDialog.value){
        SelectCategoriesDialog(
            searchLabel = stringResource(R.string.search_categories),
            allCategories = filter,
            selectedCategories = activeFilterCategoriesMyGroups,
            onDismiss = { openMyGroupFilterDialog.value = false },
            onConfirm = {
                groupViewModel.updateActiveFilterCategoriesMyGroups(it)
                openMyGroupFilterDialog.value = false
            }
        )
    }

    if(openOtherGroupFilterDialog.value){
        SelectCategoriesDialog(
            searchLabel = stringResource(R.string.search_categories),
            allCategories = filter,
            selectedCategories = activeFilterCategoriesOtherGroups,
            onDismiss = { openOtherGroupFilterDialog.value = false },
            onConfirm = {
                groupViewModel.updateActiveFilterCategoriesOtherGroups(it)
                openOtherGroupFilterDialog.value = false
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize()
    ){
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ){
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    selectedContentColor = colorResource(R.color.primary),
                    unselectedContentColor = Color.Black,
                    text = { Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    ) }
                )
            }
        }

        when(selectedTabIndex){
            0 -> MyGroupsScreen(
                modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp),
                searchInput = searchQueryMyGroups,
                toCreateGroupScreen = toCreateGroupScreen,
                myFilteredGroups = myFilteredGroups,
                onFilterClick = { openMyGroupFilterDialog.value = true },
                onSearchInputChanged = { groupViewModel.updateSearchQueryMyGroups(it) },
                toGroupChatScreen = toGroupChatScreen
            )
            1 -> JoinGroupScreen(
                searchInput = searchQueryOtherGroups,
                onSearchInputChanged = { groupViewModel.updateSearchQueryOtherGroups(it) },
                otherFilteredGroups = otherFilteredGroups,
                onFilterClick = { openOtherGroupFilterDialog.value = true },
                toGroupDetailsScreen = toGroupDetailsScreen,
                modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp),
            )
        }
    }

}

@Composable
fun MyGroupsScreen(
    searchInput: String,
    onSearchInputChanged: (String) -> Unit,
    toCreateGroupScreen: () -> Unit,
    myFilteredGroups: List<GroupWithCategories>,
    onFilterClick: () -> Unit,
    toGroupChatScreen: (Int) -> Unit,
    modifier: Modifier = Modifier
){


    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ){

        item{

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp)
            ){
                SearchBar(
                    searchInput = searchInput,
                    onSearchInputChanged = { onSearchInputChanged(it) },
                    label = stringResource(R.string.search_groups),
                    height = 64,
                    modifier = Modifier.weight(1f)
                )
                /**
                 * Filter Button
                 */
                IconButton(
                    onClick = { onFilterClick() },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ){
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.filter_groups),
                        modifier = Modifier.size(40.dp)
                    )
                }
                /**
                 * Add Button
                 */
                IconButton(
                    onClick = { toCreateGroupScreen() },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_group),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        /**
         * Group Cards
         */
        items(myFilteredGroups.size) { index ->
            GroupCard(
                groupWithCategories = myFilteredGroups[index],
                isMyGroup = true,
                onClick = { toGroupChatScreen(myFilteredGroups[index].group.id) },
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
    }
}


@Composable
fun GroupCard(
    groupWithCategories: GroupWithCategories,
    isMyGroup: Boolean,
    onClick: (GroupWithCategories) -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(R.color.cardsBackground),
                shape = RoundedCornerShape(10.dp)
                )
            .clickable {
                onClick(groupWithCategories)
            }
    ){
        Text(
            text = groupWithCategories.group.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f).padding(vertical = 10.dp, horizontal = 20.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if(!isMyGroup){
            if(groupWithCategories.group.isPrivate){
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = stringResource(R.string.private_group),
                    modifier = Modifier.size(40.dp)
                )
            }else{
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = stringResource(R.string.public_group),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

    }
}

@Composable
fun JoinGroupScreen(
    searchInput: String,
    onSearchInputChanged: (String) -> Unit,
    otherFilteredGroups: List<GroupWithCategories>,
    onFilterClick: () -> Unit,
    toGroupDetailsScreen: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ){

        item{

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp)
            ){
                SearchBar(
                    searchInput = searchInput,
                    onSearchInputChanged = { onSearchInputChanged(it) },
                    label = stringResource(R.string.search_groups),
                    height = 64,
                    modifier = Modifier.weight(1f)
                )
                /**
                 * Filter Button
                 */
                IconButton(
                    onClick = { onFilterClick() },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ){
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.filter_groups),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        /**
         * Group Cards
         */
        items(otherFilteredGroups.size) { index ->
            GroupCard(
                groupWithCategories = otherFilteredGroups[index],
                isMyGroup = false,
                onClick = { toGroupDetailsScreen(otherFilteredGroups[index].group.id) },
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
    }
}