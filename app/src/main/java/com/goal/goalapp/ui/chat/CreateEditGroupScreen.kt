package com.goal.goalapp.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.BackArrow
import com.goal.goalapp.ui.components.DeleteDialog
import com.goal.goalapp.ui.components.SearchBar
import com.goal.goalapp.ui.components.SelectCategoriesDialog
import com.goal.goalapp.ui.goal.CreateEditState
import com.goal.goalapp.ui.goal.PADDING_AFTER_HEADLINE
import com.goal.goalapp.ui.goal.PADDING_PREVIOUS_SECTION

@Composable
fun CreateEditGroupScreen(
    groupId: Int?,
    navigateBack: () -> Unit,
    toCreateGroupScreen: () -> Unit,
    toGroupOverviewScreen: () -> Unit,
    createEditGroupViewModel: CreateEditGroupViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val createGroup by createEditGroupViewModel.createGroup.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val createEditState = createEditGroupViewModel.createEditGroupState.collectAsState()
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    val allCategories = createEditGroupViewModel.allCategories.collectAsState()
    
    //gets called when this composable gets initialized
    LaunchedEffect(Unit) {
        if(groupId != null && groupId != 0 && createEditGroupViewModel.createGroup.value.id == 0){
            createEditGroupViewModel.getGroupFromDb(groupId)
        }
    }

    /**
     * Delete Dialog
     */
    if(showDeleteDialog){
        DeleteDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                createEditGroupViewModel.deleteGroup()
                createEditGroupViewModel.resetCreateGroup()
                toGroupOverviewScreen()
                showDeleteDialog = false
            }
        )
    }

    /**
     * Add Category Dialog
     */
    if(showAddCategoryDialog){
        SelectCategoriesDialog(
            searchLabel = stringResource(R.string.search_categories),
            allCategories = allCategories.value,
            selectedCategories = createGroup.categories,
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = {
                createEditGroupViewModel.updateCategory(it)
                showAddCategoryDialog = false
            },
            modifier = Modifier
        )
    }
    
    /**
     * If the group was successfully created, navigate back to the group overview screen
     */
    if(createEditState.value is CreateEditState.Success){
        createEditGroupViewModel.resetCreateGroup()
        navigateBack()
    }
    
    CreateGroupBody(
        navigateBack = TODO(),
        createEditGroupViewModel = TODO(),
        createEditGroup = TODO(),
        deleteGroup = TODO(),
        modifier = TODO()
    )
}

@Composable
fun CreateGroupBody(
    navigateBack: () -> Unit,
    createEditGroupViewModel: CreateEditGroupViewModel,
    createEditGroup: CreateGroup,
    deleteGroup: () -> Unit,
    modifier: Modifier = Modifier
){
    var isValid by remember { mutableStateOf(false) }
    isValid = createEditGroupViewModel.checkGroupValidity()
    val scrollState = rememberScrollState()
    var searchText by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .scrollable(state = scrollState, orientation = Orientation.Vertical)
    ){
        /**
         * Back arrow
         */
        BackArrow(navigateBack = navigateBack)
        
        /**
         * Headline
         */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp)
        ){
            Text(
                text = if(createEditGroup.id == 0 ) stringResource(R.string.new_group)
                else stringResource(R.string.edit_group),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )
            
            if(createEditGroup.id != 0){
                //Delete Icon
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(30.dp)
                        .clickable { 
                            deleteGroup()
                        }
                )
            }
        }
        
        /**
         * Title
         */
        Text(
            text = stringResource(R.string.title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(
                top = PADDING_PREVIOUS_SECTION.dp,
                bottom = PADDING_AFTER_HEADLINE.dp
            )
        )
        OutlinedTextField(
            value = createEditGroup.name,
            onValueChange = { createEditGroupViewModel.updateGroupName(it) },
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth(),
            )

        /**
         * Categories
         */

    }

}