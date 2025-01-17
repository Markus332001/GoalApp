package com.goal.goalapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.goal.goalapp.R
import com.goal.goalapp.data.goal.GoalWithDetails
import com.goal.goalapp.data.goal.Routine
import com.goal.goalapp.data.goal.RoutineWithCalendarDays
import com.goal.goalapp.data.group.Group
import com.goal.goalapp.data.group.GroupWithDetails
import com.goal.goalapp.data.post.Post
import com.goal.goalapp.data.post.PostWithDetails
import com.goal.goalapp.data.user.User
import com.goal.goalapp.ui.helper.transformInfosForPost
import kotlinx.coroutines.selects.select
import kotlin.math.abs

@Composable
fun SelectGoalDialog(
    onDismiss: () -> Unit,
    goalWithDetails: List<GoalWithDetails>,
    firstSelectedGoal: GoalWithDetails?,
    onConfirm: (GoalWithDetails?) -> Unit,
    modifier: Modifier = Modifier
){
    //sets the first selected goal, when its null it set the first goal in the list
    var selectedGoal = remember { mutableStateOf(firstSelectedGoal?: if(goalWithDetails.isNotEmpty()) goalWithDetails[0] else null)}
    val isValid = remember { mutableStateOf(selectedGoal.value != null) }

    Dialog(
        onDismissRequest = onDismiss
    ){
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
        ){
            if(goalWithDetails.isEmpty()){
                Text(
                    text = stringResource(R.string.no_goals_found),
                    style = MaterialTheme.typography.headlineLarge.copy(color = Color.Red),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }else{
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ){
                    /**
                     * Headline
                     */
                    Text(
                        text = stringResource(R.string.select_goal),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .padding(bottom = 30.dp)
                            .fillMaxWidth()
                    )
                    /**
                     * Goal Picker
                     */
                    VerticalPicker(
                        items = goalWithDetails,
                        onItemSelected = {
                            selectedGoal.value = it

                            if(selectedGoal.value != null){
                                isValid.value = true
                            }else{
                                isValid.value = false
                            }
                        },
                        initialPage = goalWithDetails.indexOf(selectedGoal.value)
                    )
                    /**
                     * Cancel and Next Buttons
                     */
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
                                onConfirm(selectedGoal.value)
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
                            Text(text = stringResource(R.string.next))
                        }
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectGoalDetailsPostDialog(
    goalWithDetails: GoalWithDetails,
    onDismiss: () -> Unit,
    onBack: (goalWithDetails: GoalWithDetails) -> Unit,
    onConfirm: (withProgress: Boolean, routines: List<RoutineWithCalendarDays>) -> Unit,
    selectedProgressBefore: Boolean = false,
    selectedRoutinesBefore: List<RoutineWithCalendarDays> = emptyList(),
    modifier: Modifier = Modifier
){
    val withProgress = remember { mutableStateOf(selectedProgressBefore) }
    val selectedRoutines = remember { mutableStateOf(selectedRoutinesBefore) }
    val isValid = remember { mutableStateOf(withProgress.value || selectedRoutines.value.isNotEmpty()) }

    Dialog(
        onDismissRequest = onDismiss
    ){
        Box(
            modifier = modifier
                .fillMaxWidth()
                .requiredHeightIn(max = 600.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                /**
                 * Headline
                 */
                Text(
                    text = stringResource(R.string.select_goal_details),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 30.dp)
                )
                LazyColumn {

                    /**
                     * Details Picker
                     */
                    item{
                        Text(
                            text = stringResource(R.string.general),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                    item {
                        SelectButton(
                            title = stringResource(R.string.goal_progress),
                            onClick = {
                                withProgress.value = !withProgress.value
                                isValid.value = withProgress.value || selectedRoutines.value.isNotEmpty()
                                      },
                            selected = withProgress.value,
                            modifier = Modifier
                                .fillMaxWidth(0.45f)
                                .padding(bottom = 20.dp)
                        )
                    }

                    /**
                     * Routines Picker
                     */
                    item {
                        Text(
                            text = stringResource(R.string.general),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 10.dp, start = 20.dp)
                        )
                    }

                    item {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (routine in goalWithDetails.routines) {
                                SelectButton(
                                    title = routine.routine.title,
                                    onClick = {
                                        if (selectedRoutines.value.contains(routine)) {
                                            selectedRoutines.value =
                                                selectedRoutines.value.filter { it != routine }
                                        } else {
                                            selectedRoutines.value += routine
                                        }
                                        isValid.value =
                                            withProgress.value || selectedRoutines.value.isNotEmpty()
                                    },
                                    selected = selectedRoutines.value.contains(routine),
                                    modifier = Modifier.fillMaxWidth(0.45f)
                                )
                            }
                        }
                    }
                }

                /**
                 * Back and Next Buttons
                 */
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Button(
                        onClick = { onBack(goalWithDetails) },
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
                        Text( text = stringResource(R.string.back))
                    }
                    Button(
                        onClick = {
                            onConfirm(withProgress.value, selectedRoutines.value)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.primary),
                            contentColor = colorResource(R.color.button_font_light),
                            disabledContainerColor = colorResource(R.color.disabled_button),
                            disabledContentColor = colorResource(R.color.disabled_button_font)
                        ),
                        enabled = isValid.value,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            disabledElevation = 0.dp
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
                        Text(text = stringResource(R.string.next))
                    }
                }

            }
        }
    }
}

@Composable
fun PreviewSelectGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onBack: (postWithDetails: PostWithDetails) -> Unit,
    postWithDetails: PostWithDetails,
    modifier: Modifier = Modifier
){
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss
    ){
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
        ){
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                /**
                 * Headline
                 */
                Text(
                    text = stringResource(R.string.preview_post),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                /**
                 * Post Card Preview
                 */
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(bottom = 20.dp)
                    ) {
                        PostCard(
                            postWithDetails = postWithDetails,
                            currentUserId = 0,
                            onLikeClick = {},
                            onCommentClick = {},
                            isPreview = true,
                        )
                    }
                }
                /**
                 * Back and Next Buttons
                 */
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = { onBack(postWithDetails) },
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
                    ) {
                        Text(text = stringResource(R.string.back))
                    }
                    Button(
                        onClick = {
                            onConfirm()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.primary),
                            contentColor = colorResource(R.color.button_font_light)
                        ),
                        modifier = Modifier
                            .padding(5.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(text = stringResource(R.string.next))
                    }
                }

            }
        }
    }
}

@Composable
fun SelectGroupsDialog(
    onDismiss: () -> Unit,
    onConfirm: (List<Group>) -> Unit,
    groups: List<Group>,
    beforeSelectedGroups: List<Group>,
    modifier: Modifier = Modifier
){
    val selectedGroups = remember { mutableStateOf(beforeSelectedGroups) }
    val isValid = remember{ mutableStateOf(selectedGroups.value.isNotEmpty()) }
    val searchInput = remember { mutableStateOf("") }
    val shownGroups = remember { mutableStateOf(groups) }

    Dialog(
        onDismissRequest = onDismiss
    ){
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                /**
                 * Headline
                 */
                Text(
                    text = stringResource(R.string.select_groups_to_share_post),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                /**
                 * Search Bar
                 */
                SearchBar(
                    searchInput = searchInput.value,
                    label = stringResource(R.string.search_groups),
                    onSearchInputChanged = {
                        searchInput.value = it
                        shownGroups.value = shownGroups.value.filter { group -> group.name.contains(it, ignoreCase = true) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                HorizontalDivider(thickness = 2.dp)

                /**
                 * Groups
                 */
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(shownGroups.value.size) { index ->
                        val group = shownGroups.value[index]
                        val isSelected = selectedGroups.value.contains(group)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    if (isSelected) {
                                        selectedGroups.value -= group
                                    } else {
                                        selectedGroups.value += group
                                    }
                                    isValid.value = selectedGroups.value.isNotEmpty()
                                },
                                modifier = Modifier.padding(end = 5.dp)
                            )
                            Text(text = group.name)
                        }
                    }
                }

                HorizontalDivider(thickness = 2.dp)

                /**
                 * Back and Next Buttons
                 */
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
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
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            onConfirm(selectedGroups.value)
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
                    ) {
                        Text(text = stringResource(R.string.next))
                    }
                }
            }
        }
    }
}

@Composable
fun VerticalPicker(
    items: List<GoalWithDetails>,
    initialPage: Int,
    onItemSelected: (GoalWithDetails) -> Unit
) {

    val maxPage: Int = items.size
    val pagerState = rememberPagerState(initialPage = initialPage) { maxPage }

    LaunchedEffect (pagerState.currentPage){
        onItemSelected(items[pagerState.currentPage])
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        contentAlignment = Alignment.Center
    ) {
        VerticalPager(
            state = pagerState,
            contentPadding = PaddingValues(vertical = 50.dp),
        ) { page ->
            val item = items[page]
            val distanceFromCenter = abs(page - pagerState.currentPage)
            // Calculate the alpha value based on the current page
            val alpha = when {
                distanceFromCenter == 0 -> 1f
                else -> 0.5f
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        alpha = alpha,
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.goal.title,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ManagePostDialogs(
    goalWithDetailsList: List<GoalWithDetails>?,
    goalWithDetails: GoalWithDetails?,
    onDismiss: () -> Unit,
    onConfirm: (postWithDetails: PostWithDetails, groups: List<Group>) -> Unit,
    groups: List<Group>?,
    fromGroup: Group?,
){
    val selectedGoal = remember { mutableStateOf(goalWithDetails) }
    val selectedGroups = remember { mutableStateOf( if(fromGroup != null) listOf(fromGroup) else emptyList() ) }
    val showSelectGroupDialog = remember { mutableStateOf(true) }
    val showSelectGoalDialog = remember { mutableStateOf(true) }
    val showSelectGoalDetailsDialog = remember { mutableStateOf(false) }
    val showPreviewPostDialog = remember { mutableStateOf(false) }
    val withProgress = remember { mutableStateOf(false) }
    val selectedRoutines = remember { mutableStateOf(emptyList<RoutineWithCalendarDays>()) }
    val postWithDetails = remember { mutableStateOf(PostWithDetails(
        post = Post(
            userId = 0,
            groupId = 0,
            goalName = "",
            progress = null,
            completionType = null,
            targetValue = null,
            currentValue = null,
            unit = null,
            likesUserIds = emptyList()
        ),
        comments = emptyList(),
        routineSummary = emptyList(),
        user = User(
            username = "",
            email = "",
            passwordHash = ""
        )
    ))  }

    /**
     * Select Group Dialog. StartPoint from Group chat
     * It needs beforeSelectedGroup, goalWithDetailsList and the lambdas
     * So it doesnt need groups, because it is already selected, goalWithDetails, because it will get selected here
     */
    if(goalWithDetailsList != null && fromGroup != null && showSelectGoalDialog.value){
        SelectGoalDialog(
            onDismiss = { onDismiss() },
            goalWithDetails = goalWithDetailsList,
            firstSelectedGoal = selectedGoal.value,
            onConfirm = {
                selectedGoal.value = it
                showSelectGoalDialog.value = false
                showSelectGoalDetailsDialog.value = true
            }
        )
    }

    /**
     * Select Groups Dialog. StartPoint from Goal
     * It needs groups, goalWithDetails and the lambdas
     * So it doesnt need fromGroup, because it will be selected here and goalWithDetailsList, because the goal is already selected
     */
    if(groups != null && showSelectGroupDialog.value && goalWithDetails != null){
        SelectGroupsDialog(
            onDismiss = { onDismiss() },
            onConfirm = {
                selectedGroups.value = it
                showSelectGroupDialog.value = false
                showSelectGoalDetailsDialog.value = true
            },
            groups = groups,
            beforeSelectedGroups = selectedGroups.value?: emptyList()
        )
    }

    /**
     * Select Goal Details Dialog. After Select Group or Select Goal Dialog
     */
    if(showSelectGoalDetailsDialog.value && selectedGoal.value != null){
        SelectGoalDetailsPostDialog(
            goalWithDetails = selectedGoal.value!!,
            onDismiss = { onDismiss() },
            onBack = {
                showSelectGoalDetailsDialog.value = false
                //both true, because it depends on the input which one it starts
                showSelectGoalDialog.value = true
                showSelectGroupDialog.value = true
            },
            onConfirm = { withProgressDialog, routinesDialog ->
                showSelectGoalDetailsDialog.value = false
                showPreviewPostDialog.value = true
                withProgress.value = withProgressDialog
                selectedRoutines.value = routinesDialog
            },
        )
    }

    /**
     * Preview Post Dialog. After Select Goal Details Dialog
     */
    if(showPreviewPostDialog.value && selectedGoal.value != null && selectedGroups.value.isNotEmpty()){
        postWithDetails.value = transformInfosForPost(selectedGoal.value!!,
            selectedRoutines.value.map{r -> r.routine}, withProgress.value)
        PreviewSelectGoalDialog(
            onDismiss = { onDismiss() },
            onConfirm = {
                onConfirm(postWithDetails.value, selectedGroups.value)
            },
            onBack = {
                showPreviewPostDialog.value = false
                showSelectGoalDetailsDialog.value = true
            },
            postWithDetails = postWithDetails.value
        )
    }
}
