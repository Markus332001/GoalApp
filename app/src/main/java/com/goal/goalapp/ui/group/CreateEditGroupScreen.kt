package com.goal.goalapp.ui.group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.BackArrow
import com.goal.goalapp.ui.components.ChipWithRemove
import com.goal.goalapp.ui.components.DeleteDialog
import com.goal.goalapp.ui.components.NotesInput
import com.goal.goalapp.ui.components.SelectCategoriesDialog
import com.goal.goalapp.ui.goal.PADDING_AFTER_HEADLINE
import com.goal.goalapp.ui.goal.PADDING_PREVIOUS_SECTION

@Composable
fun CreateEditGroupScreen(
    groupId: Int?,
    navigateBack: () -> Unit,
    toGroupOverviewScreen: () -> Unit,
    createEditGroupViewModel: CreateEditGroupViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val createEditGroup by createEditGroupViewModel.createGroup.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val createEditGroupState = createEditGroupViewModel.createEditGroupState.collectAsState()
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
            selectedCategories = createEditGroup.categories,
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = {
                createEditGroupViewModel.updateCategory(it)
                showAddCategoryDialog = false
            }
        )
    }
    
    /**
     * If the group was successfully created, navigate back to the group overview screen
     */
    if(createEditGroupState.value is CreateEditGroupState.Success){
        if(groupId != null && groupId != 0){
            navigateBack()
        }else{
            toGroupOverviewScreen()
        }
    }
    
    CreateGroupBody(
        navigateBack = navigateBack,
        createEditGroupViewModel = createEditGroupViewModel,
        createEditGroup = createEditGroup,
        createEditGroupState = createEditGroupState.value,
        showDeleteDialog = { showDeleteDialog = true },
        showAddCategoryDialog = { showAddCategoryDialog = true },
        modifier = Modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateGroupBody(
    navigateBack: () -> Unit,
    createEditGroupViewModel: CreateEditGroupViewModel,
    createEditGroup: CreateGroup,
    createEditGroupState: CreateEditGroupState,
    showDeleteDialog: () -> Unit,
    showAddCategoryDialog: () -> Unit,
    modifier: Modifier = Modifier
){
    var isValid by remember { mutableStateOf(false) }
    isValid = createEditGroupViewModel.checkGroupValidity()
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(state = scrollState)
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
            modifier = Modifier.padding(bottom = 10.dp, top = 20.dp)
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
                            showDeleteDialog()
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
        Text(
            text = stringResource(R.string.categories),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(
                top = PADDING_PREVIOUS_SECTION.dp,
                bottom = PADDING_AFTER_HEADLINE.dp
            )
        )

        Button(
            onClick = { showAddCategoryDialog() },
            modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 10.dp)
        ){
            Text(text = stringResource(R.string.add_category))
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ){
            for(category in createEditGroup.categories){
                ChipWithRemove(
                    text = category.name,
                    onRemove = { createEditGroupViewModel.removeCategory(category) }
                )
            }
        }

        /**
         * Description
         */
        Text(
            text = stringResource(R.string.description),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(
                top = PADDING_PREVIOUS_SECTION.dp,
                bottom = PADDING_AFTER_HEADLINE.dp
            )
        )
        NotesInput(
            noteText = createEditGroup.description,
            onNoteChange = { createEditGroupViewModel.updateGroupDescription(it) },
            label = stringResource(R.string.description)
        )

        /**
         * Is private
         */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = PADDING_PREVIOUS_SECTION.dp, bottom = 10.dp)
        ){
            Text(
                text = stringResource(R.string.is_private),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = modifier.padding(end = 10.dp)
            )
            Switch(
                checked = createEditGroup.isPrivate,
                onCheckedChange = { createEditGroupViewModel.updateGroupIsPrivate(it) }
            )
        }
        if(createEditGroup.isPrivate){
            OutlinedTextField(
                value = createEditGroup.key,
                onValueChange = { createEditGroupViewModel.updateGroupKey(it) },
                label = { Text(stringResource(R.string.input_key)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        /**
         * Confirm Button
         */
        Button(
            onClick = {
               createEditGroupViewModel.createOrEditGroup()

            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.primary),
                contentColor = colorResource(R.color.button_font_light)
            ),
            enabled = isValid,
            modifier = Modifier
                .padding(top = 50.dp, bottom = 20.dp)
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(text = stringResource(R.string.confirm))
        }

        /**
         * Error by Saving Goal
         */
        if(createEditGroupState is CreateEditGroupState.Error){
            Text(
                text = createEditGroupState.message,
                color = Color.Red
            )
        }

    }
}
