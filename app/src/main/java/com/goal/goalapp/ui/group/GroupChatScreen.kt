package com.goal.goalapp.ui.group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.data.group.CommentWithUser
import com.goal.goalapp.data.goal.GoalWithDetails
import com.goal.goalapp.data.post.PostWithDetails
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.BackArrow
import com.goal.goalapp.ui.components.ManagePostDialogs
import com.goal.goalapp.ui.components.PostCard

@Composable
fun GroupChatScreen(
    groupId: Int?,
    navigateBack: () -> Unit,
    toGroupDetailsScreen: (Int) -> Unit,
    groupChatViewModel: GroupChatViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
){
    val groupWithDetails by groupChatViewModel.group.collectAsState()
    val openAddPostDialog = remember { mutableStateOf(false) }
    val goalWithDetailsList = remember { mutableStateOf<List<GoalWithDetails>?>(null) }
    val userId by groupChatViewModel.userId.collectAsState()
    val openCommentsDialog = remember { mutableStateOf(false) }
    val openedCommentSectionPost = remember { mutableStateOf<PostWithDetails?>(null) }

    LaunchedEffect(Unit){
        if(groupId != null) {
            groupChatViewModel.loadGroup(groupId)
        }
    }

    /**
     * Opens the Add Post Dialog
     */
    if(openAddPostDialog.value && groupWithDetails != null){

        //loads all goals from the user asynchronous
        LaunchedEffect(Unit) {
            goalWithDetailsList.value = groupChatViewModel.getAllGoalsFromUser()
        }

        //opens the dialogs, when the data from db is loaded
        if (goalWithDetailsList.value != null) {
            ManagePostDialogs(
                goalWithDetailsList = goalWithDetailsList.value!!,
                goalWithDetails = null,
                onDismiss = {openAddPostDialog.value = false},
                onConfirm = { postWithDetails, groups ->
                    groupChatViewModel.addPostWithDetailsDb(postWithDetails, groups)
                    openAddPostDialog.value = false
            },
                groups = null,
                fromGroup = groupWithDetails?.group!!
            )
        }
    }

    /**
     * Opens the Comments Screen
     */
    if(openCommentsDialog.value && openedCommentSectionPost.value != null){
        CommentsScreen(
            postId = openedCommentSectionPost.value!!.post.id,
            groupChatViewModel = groupChatViewModel,
            onCommentAdded = {
                groupChatViewModel.addComment(it, openedCommentSectionPost.value!! )
            },
            onClose = {
                openCommentsDialog.value = false
                openedCommentSectionPost.value = null
            }
        )
    }

    /**
     * Main Column
     */
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        /**
         * Header Row with navigate Back, group name and settings and add
         */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            /**
             * Back Arrow
             */
            BackArrow(navigateBack = navigateBack, modifier = Modifier.padding(start = 16.dp, end = 10.dp))

            /**
             * Group name
             */
            if(groupWithDetails != null) {
                Text(
                    text = groupWithDetails!!.group.name,
                    style = MaterialTheme.typography.headlineLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            if(groupWithDetails != null) {
                /**
                 * Settings and add
                 */
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.settings),
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(40.dp)
                        .clickable {
                            if (groupId != null) {
                                toGroupDetailsScreen(groupId)
                            }
                        }
                )
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_post),
                    modifier = Modifier
                        .padding(start = 10.dp, end = 16.dp)
                        .size(40.dp)
                        .clickable {
                            openAddPostDialog.value = true
                        }
                )

            }
        }

        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp).fillMaxWidth()
        )
        /**
         * Chat
         */
        if(groupWithDetails != null) {
            Chat(
                postsWithDetails = groupWithDetails!!.posts,
                userId = userId,
                onLikeClick = {
                    groupChatViewModel.toggleLike(it)
                },
                onCommentClick = {
                    openedCommentSectionPost.value = it
                    openCommentsDialog.value = true
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }

}

@Composable
fun Chat(
    postsWithDetails: List<PostWithDetails>,
    onLikeClick: (PostWithDetails) -> Unit,
    onCommentClick: (PostWithDetails) -> Unit,
    userId: Int,
    modifier: Modifier = Modifier
){

    LazyColumn {
        items(postsWithDetails.size){ index ->
            PostCard(
                postWithDetails = postsWithDetails[index],
                currentUserId = userId,
                onLikeClick = {
                    onLikeClick(postsWithDetails[index])
                },
                onCommentClick = {
                    onCommentClick(postsWithDetails[index])
                },
                isPreview = false,
                modifier.fillMaxWidth().padding(vertical = 10.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    groupChatViewModel: GroupChatViewModel,
    postId: Int,
    onCommentAdded: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true //only allow full sheet to be expanded
    )
    val comments by groupChatViewModel.getCommentsForPost(postId).collectAsState(initial = emptyList())
    var newComment by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        modifier = modifier
    ){
        Column(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .padding(16.dp)
        ) {
            //Comments
            LazyColumn (
                modifier = Modifier.weight(1f)
            ) {
                items(comments) { comment ->
                    Comment(comment = comment, modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            //Add Comment
            OutlinedTextField(
                value = newComment,
                onValueChange = { newComment = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.write_comment)) },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (newComment.isNotBlank()) {
                                onCommentAdded(newComment)
                                newComment = ""
                            }
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(R.string.send_comment)
                        )
                    }
                }
            )


        }
    }

}

@Composable
fun Comment(
    comment: CommentWithUser,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.fillMaxWidth()
    ){
        Text(
            text = comment.user.username,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = comment.comment.text,
            style = MaterialTheme.typography.bodyMedium
        )
    }

}

@Preview
@Composable
fun CommentsScreenPreview() {
    val comments = listOf("Kommentar 1", "Kommentar 2", "Kommentar 3")
   // CommentsScreen(comments = comments, onCommentAdded = {}, onClose = {})
}