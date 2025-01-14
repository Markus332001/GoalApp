package com.goal.goalapp.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Tab
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.SearchBar

@Composable
fun GroupScreen(
    groupViewModel: GroupViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
){
    val myFilteredGroups by groupViewModel.myFilteredGroups.collectAsState()
    val otherFilteredGroups by groupViewModel.otherFilteredGroups.collectAsState()
    val filter by groupViewModel.filter.collectAsState()
    val activeFilterCategoriesMyGroups by groupViewModel.activeFilterCategoriesMyGroups.collectAsState()
    val searchQueryMyGroups by groupViewModel.searchQueryMyGroups.collectAsState()
    val activeFilterCategoriesOtherGroups by groupViewModel.activeFilterCategoriesOtherGroups.collectAsState()
    val searchQueryOtherGroups by groupViewModel.searchQueryOtherGroups.collectAsState()

    // State to track the selected tab
    var selectedTabIndex by remember { mutableStateOf(0) }

    // Tab titles
    val tabTitles = listOf(stringResource(R.string.my_groups), stringResource(R.string.join_group))

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
                        textAlign = TextAlign.Center,
                    ) }
                )
            }
        }

        when(selectedTabIndex){
            0 -> MyGroupsScreen(
                modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp),
                searchInput = searchQueryMyGroups,
                onSearchInputChanged = { groupViewModel.updateSearchQueryMyGroups(it) }

            )
            1 -> JoinGroupScreen(
                modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp)
            )
        }
    }

}

@Composable
fun MyGroupsScreen(
    searchInput: String,
    onSearchInputChanged: (String) -> Unit,
    modifier: Modifier = Modifier
){


    Column(
        modifier = modifier.fillMaxWidth()
    ){

        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            SearchBar(
                searchInput = searchInput,
                onSearchInputChanged = { onSearchInputChanged(it) },
                label = stringResource(R.string.search_groups),
                height = 64,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.align(Alignment.CenterVertically)
            ){
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = stringResource(R.string.filter_groups),
                    modifier = Modifier.size(40.dp)
                )
            }
            IconButton(
                onClick = { /*TODO*/ },
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
}

@Composable
fun JoinGroupScreen(
    modifier: Modifier = Modifier
){

}