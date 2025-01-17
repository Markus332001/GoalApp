package com.goal.goalapp.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.data.Role
import com.goal.goalapp.data.group.GroupWithDetails
import com.goal.goalapp.data.group.request.GroupMemberDTO
import com.goal.goalapp.data.group.request.GroupWithDetailsAndRole
import com.goal.goalapp.data.user.UserGroupCrossRef
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.BackArrow
import com.goal.goalapp.ui.components.ChipWithRemove
import com.goal.goalapp.ui.components.ScrollableTextField
import com.goal.goalapp.ui.goal.CreateEditState
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
    val userWithRole by groupDetailsViewModel.userWithRole.collectAsState()
    val showJoinGroupDialog = remember { mutableStateOf(false) }
    val joinGroupState by groupDetailsViewModel.joinGroupState.collectAsState()

    if(groupId != null && groupWithDetails?.group?.id != groupId && userWithRole == null){
        groupDetailsViewModel.loadGroup(groupId)
        groupDetailsViewModel.loadUserWithRole(groupId)
    }

    if(showJoinGroupDialog.value){
        JoinGroupDialog(
            onDismiss = { showJoinGroupDialog.value = false },
            onConfirm = { groupDetailsViewModel.joinGroup(it) },
            joinGroupState = joinGroupState,
            modifier = modifier
        )
    }

    if (joinGroupState is JoinGroupState.Success){
        showJoinGroupDialog.value = false
    }

    GroupDetailsBody(
        navigateBack = navigateBack,
        groupWithDetailsAndRole = groupWithDetails,
        toEditGroupScreen = toEditGroupScreen,
        onClickJoinGroup = {
            if(groupWithDetails?.group?.isPrivate == true){
                showJoinGroupDialog.value = true
            }else{
                groupDetailsViewModel.joinGroup()
            }
        },
        userWithRole = userWithRole,
        joinGroupState = joinGroupState,
        groupDetailsViewModel = groupDetailsViewModel,
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GroupDetailsBody(
    navigateBack: () -> Unit,
    groupWithDetailsAndRole: GroupWithDetailsAndRole?,
    toEditGroupScreen: (Int) -> Unit = {},
    onClickJoinGroup: () -> Unit = {},
    userWithRole: UserGroupCrossRef?,
    joinGroupState: JoinGroupState,
    groupDetailsViewModel: GroupDetailsViewModel,
    modifier: Modifier = Modifier
){
    val memberSize = groupWithDetailsAndRole?.members?.size?: 0
    val showMembers = remember { mutableIntStateOf(0) }

    val showEditMemberPopup = remember { mutableStateOf(false) }
    val memberForEditPopup = remember { mutableStateOf<GroupMemberDTO?>(null) }

    val showEditGroupPopup = remember { mutableStateOf(false) }


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

            val isMember = groupWithDetailsAndRole.members.any { it.id == userWithRole?.userId }


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
                            .clickable {
                                //only when the user is in the group and has a role
                                if (userWithRole != null) {
                                    showEditGroupPopup.value = true
                                }
                            }
                    )
                }
            }

            if(showEditGroupPopup.value){
                /**
                 * Edit Group Popup
                 */
                item {
                    EditGroupPopup(
                        onDismissRequest = { showEditGroupPopup.value = false },
                        onEditGroup = {
                            toEditGroupScreen(groupWithDetailsAndRole.group.id)
                            showEditGroupPopup.value = false
                        },
                        onExitGroup = {
                            if (userWithRole != null) {
                                groupDetailsViewModel.removeMember(userWithRole.userId)
                                showEditGroupPopup.value = false
                                navigateBack()
                            }
                        },
                        userWithRole = userWithRole
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
            if(isMember) {
                items(showMembers.intValue) { index ->
                    if (index < groupWithDetailsAndRole.members.size) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(bottom = 10.dp)
                                .background(
                                    color = colorResource(R.color.cardsBackground),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .fillMaxWidth()
                                .clickable {
                                    if (userWithRole != null) {
                                        //checks if the user has the permission to edit the member and if it is an other member
                                        if (userWithRole.userId != groupWithDetailsAndRole.members[index].id && userWithRole.role != Role.MEMBER &&
                                            (groupWithDetailsAndRole.members[index].role.rank < userWithRole.role.rank)
                                        ) {
                                            showEditMemberPopup.value = true
                                            memberForEditPopup.value =
                                                groupWithDetailsAndRole.members[index]
                                        }
                                    }
                                }
                        ) {
                            Text(
                                text = groupWithDetailsAndRole.members[index].name,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(
                                    start = 10.dp,
                                    top = 3.dp,
                                    bottom = 3.dp
                                )
                            )

                            if (groupWithDetailsAndRole.members[index].role != Role.MEMBER) {
                                Text(
                                    text = groupWithDetailsAndRole.members[index].role.toString(),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        color = colorResource(
                                            R.color.today
                                        )
                                    ),
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                            }

                            /**
                             * Edit Member Popup
                             */
                            if (showEditMemberPopup.value && memberForEditPopup.value == groupWithDetailsAndRole.members[index]) {
                                EditMemberPopup(
                                    onDismiss = {
                                        showEditMemberPopup.value = false
                                        memberForEditPopup.value = null
                                    },
                                    onChangeMemberRole = {
                                        groupDetailsViewModel.changeRole(
                                            role = it,
                                            userId = groupWithDetailsAndRole.members[index].id
                                        )
                                    },
                                    onRemoveMember = {
                                        groupDetailsViewModel.removeMember(groupWithDetailsAndRole.members[index].id)
                                        showEditMemberPopup.value = false
                                        memberForEditPopup.value = null
                                    },
                                    member = groupWithDetailsAndRole.members[index],
                                    isOwner = userWithRole?.role == Role.OWNER
                                )
                            }
                        }
                    }
                }
            }
            /**
             * Show more Members text
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
                                showMembers.intValue =
                                    if (moreMembers > memberSize) memberSize else moreMembers
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

            /**
             * Join Group
             */
            if(!isMember){
                item{
                    Button(
                        onClick = onClickJoinGroup,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.primary),
                            contentColor = colorResource(R.color.white)
                        ),
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                    ){
                        Text(
                            text = stringResource(R.string.join_group),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
                if(joinGroupState is JoinGroupState.Error){
                    item{
                        Text(
                            text = joinGroupState.message,
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JoinGroupDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    joinGroupState: JoinGroupState,
    modifier: Modifier
){
    val groupKey = remember { mutableStateOf("") }
    val isValid = remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = modifier
                .background(
                    color = colorResource(R.color.cardsBackground),
                    shape = RoundedCornerShape(16.dp)
                )
        ){
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.private_group),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                OutlinedTextField(
                    value = groupKey.value,
                    onValueChange = {
                        groupKey.value = it
                        isValid.value = it != ""
                    },
                    label = { Text(text = stringResource(R.string.insert_group_key)) },
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.cardsBackground),
                            contentColor = colorResource(R.color.button_font)
                        ),
                        modifier = Modifier
                            .padding(5.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .fillMaxWidth()
                            .weight(1f)
                    ){
                        Text( text = stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            onConfirm(groupKey.value)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.primary),
                            contentColor = colorResource(R.color.button_font_light),
                            disabledContainerColor = colorResource(R.color.disabled_button),
                            disabledContentColor = colorResource(R.color.disabled_button_font)
                        ),
                        enabled = isValid.value,
                        modifier = Modifier
                            .padding(5.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .fillMaxWidth()
                            .weight(1f)
                    ){
                        Text(text = stringResource(R.string.confirm))
                    }
                    if(joinGroupState is JoinGroupState.Error){

                        Text(
                            text = joinGroupState.message,
                            color = Color.Red
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun EditMemberPopup(
    onDismiss: () -> Unit,
    onChangeMemberRole: (Role) -> Unit,
    onRemoveMember: () -> Unit,
    member: GroupMemberDTO,
    isOwner: Boolean,
    modifier: Modifier = Modifier
){
    Popup(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = modifier
                .padding(16.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )

        ){
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                /**
                 * Name of Member
                 */
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth()
                )
                /**
                 * Change Member Role
                 */
                if(member.role != Role.ADMIN){
                    Button(
                        onClick = { onChangeMemberRole(Role.ADMIN) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.promote_member),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }else{
                    Button(
                        onClick = { onChangeMemberRole(Role.MEMBER) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                            ),
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.demote_member),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
                /**
                 * Remove Member
                 */
                if(isOwner || member.role != Role.ADMIN) {
                    Button(
                        onClick = onRemoveMember,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(16.dp)
                            )
                        ){
                            Text(
                                text = stringResource(R.string.remove_member),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }

                }
            }
        }
    }
}

@Composable
fun EditGroupPopup(
    onDismissRequest: () -> Unit,
    onEditGroup: () -> Unit,
    onExitGroup: () -> Unit,
    userWithRole: UserGroupCrossRef?,
    modifier: Modifier = Modifier
){
    Popup(onDismissRequest = onDismissRequest) {
        Box(
            modifier = modifier
                .padding(16.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
        ){
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(userWithRole?.role != Role.MEMBER) {
                    Button(
                        onClick = onEditGroup,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .padding(top = 10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.edit_group),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                Button(
                    onClick = onExitGroup,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Red
                    ),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.exit_group),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}