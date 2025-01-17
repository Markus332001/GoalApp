package com.goal.goalapp.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.data.Role
import com.goal.goalapp.data.group.GroupWithDetails
import com.goal.goalapp.data.group.request.GroupWithDetailsAndRole
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.BackArrow
import com.goal.goalapp.ui.components.ChipWithRemove
import com.goal.goalapp.ui.components.ScrollableTextField
import com.goal.goalapp.ui.goal.PADDING_PREVIOUS_SECTION

@Composable
fun GroupDetailsScreen(
    navigateBack: () -> Unit,
    groupId: Int?,
    toEditGroupScreen: (Int) -> Unit,
    groupDetailsViewModel: GroupDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
){
    val groupWithDetails by groupDetailsViewModel.groupWithDetails.collectAsState()

    if(groupId != null && groupWithDetails?.group?.id != groupId){
        groupDetailsViewModel.loadGroup(groupId)
    }

    GroupDetailsBody(
        navigateBack = navigateBack,
        groupWithDetailsAndRole = groupWithDetails,
        toEditGroupScreen = toEditGroupScreen,
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GroupDetailsBody(
    navigateBack: () -> Unit,
    groupWithDetailsAndRole: GroupWithDetailsAndRole?,
    toEditGroupScreen: (Int) -> Unit = {},
    modifier: Modifier = Modifier
){
    val memberSize = groupWithDetailsAndRole?.members?.size?: 0
    val showMembers = remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        //Back Arrow
        item {
            BackArrow(navigateBack = navigateBack)
        }
        if (groupWithDetailsAndRole != null){

            if(showMembers.intValue == 0){
                showMembers.intValue = if(memberSize > 5) 5 else memberSize
            }

            /**
             * Headline with privacy and settings Icon
             */
            item{
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = PADDING_PREVIOUS_SECTION.dp, top = 20.dp)
                ) {
                    Text(
                        text = groupWithDetailsAndRole.group.name,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Icon(
                        imageVector = if (groupWithDetailsAndRole.group.isPrivate) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = if (groupWithDetailsAndRole.group.isPrivate) stringResource(R.string.private_group) else stringResource(
                            R.string.public_group
                        ),
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(30.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings),
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(30.dp)
                            .clickable { toEditGroupScreen(groupWithDetailsAndRole.group.id) }
                    )
                }
            }
            /**
             * Categories
             */
            item{
                Text(
                    text = stringResource(R.string.categories),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
            item{
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    for (category in groupWithDetailsAndRole.groupCategories) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.background(
                                color = colorResource(R.color.cardsBackground),
                                shape = RoundedCornerShape(16.dp)
                            )
                        ) {
                            Text(
                                text = category.name,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                }
            }

            /**
             * Members
             */
            item{
                Text(
                    text = groupWithDetailsAndRole.members.size.toString() + " " + if(groupWithDetailsAndRole.members.size == 1) stringResource(R.string.member) else stringResource(R.string.members),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
            items(showMembers.intValue){ index ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(bottom = 10.dp).background(
                        color = colorResource(R.color.cardsBackground),
                        shape = RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .clickable {

                        }
                ){
                    Text(
                        text = groupWithDetailsAndRole.members[index].name,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(start = 10.dp, top = 3.dp, bottom = 3.dp)
                    )

                    if(groupWithDetailsAndRole.members[index].role != Role.MEMBER){
                        Text(
                            text = groupWithDetailsAndRole.members[index].role.toString(),
                            style = MaterialTheme.typography.headlineSmall.copy(color = colorResource(R.color.today)),
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                }
            }
            /**
             * More Members
             */
            item{
                if(memberSize > showMembers.intValue){
                    Text(
                        text = stringResource(R.string.more),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 10.dp)
                            .fillMaxWidth()
                            .clickable {
                                val moreMembers = showMembers.intValue + 5
                                showMembers.intValue = if(moreMembers > memberSize) memberSize else moreMembers
                            }
                    )
                }
            }

            /**
             * Description
             */
            item{
                Text(
                    text = stringResource(R.string.description),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
            item{
                ScrollableTextField(
                    text = groupWithDetailsAndRole.group.description,
                    height = 200
                )
            }
        }
    }
}