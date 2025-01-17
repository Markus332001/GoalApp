package com.goal.goalapp.ui.goal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.data.CompletionType
import com.goal.goalapp.data.goal.GoalWithDetails
import com.goal.goalapp.data.group.Group
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.BackArrow
import com.goal.goalapp.ui.components.ManagePostDialogs
import com.goal.goalapp.ui.components.ProgressBar
import com.goal.goalapp.ui.components.RoutineCard
import com.goal.goalapp.ui.components.ScrollableTextField
import com.goal.goalapp.ui.components.SelectButton
import com.goal.goalapp.ui.helper.convertDateToStringFormatDots

@Composable
fun GoalDetailsScreen(
    goalId: Int?,
    toRoutineDetailsScreen: (Int) -> Unit,
    toEditGoalScreen: (Int) -> Unit,
    toGoalOverviewScreen: () -> Unit,
    modifier: Modifier = Modifier,
    goalDetailsViewModel: GoalDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val goalWithDetails by goalDetailsViewModel.goalWithDetails.collectAsState()
    val progressChanged = remember { mutableStateOf(false) }
    val openPostDialog = remember { mutableStateOf(false) }
    val userGroups = remember { mutableStateOf<List<Group>?>(null) }
    val openCongratulationDialog = remember { mutableStateOf(false) }
    val userId by goalDetailsViewModel.userId.collectAsState()

    if(goalId != null && goalWithDetails.goal.id != goalId){
        goalDetailsViewModel.loadGoal(goalId)
    }

    LaunchedEffect(goalWithDetails){
        if(progressChanged.value){
            //opens the congratulation dialog, if the goal is completed
            if(goalWithDetails.goal.progress == 1f){
                openCongratulationDialog.value = true
            }
        }
        progressChanged.value = false
    }

    if(openCongratulationDialog.value){
        CongratulationDialog(
            onDismiss = { openCongratulationDialog.value = false },
            onShare = {
                openPostDialog.value = true
                openCongratulationDialog.value = false
            }
        )
    }

    if(openPostDialog.value && goalWithDetails.goal.id != 0){

        //loads all goals from the user asynchronous
        LaunchedEffect(Unit) {
            userGroups.value = goalDetailsViewModel.getAllGroupsFromUser()
        }

        //opens the dialogs, when the data from db is loaded
        if (userGroups.value != null) {
            ManagePostDialogs(
                goalWithDetailsList = null,
                goalWithDetails = goalWithDetails,
                onDismiss = {openPostDialog.value = false},
                onConfirm = { postWithDetails, groups ->
                    goalDetailsViewModel.addPostWithDetailsDb(postWithDetails, groups)
                    openPostDialog.value = false
                },
                groups = userGroups.value,
                fromGroup = null
            )
        }
    }

    if(goalWithDetails.goal.id != 0){
        GoalDetailsScreenBody(
            navigateBack = toGoalOverviewScreen,
            goalWithDetails = goalWithDetails,
            goalDetailsViewModel = goalDetailsViewModel,
            toRoutineDetailsScreen = toRoutineDetailsScreen,
            toEditGoalScreen = toEditGoalScreen,
            showPostDialog = { openPostDialog.value = true },
            onProgressChanged = { progressChanged.value = true },
            modifier = modifier
        )
    }

}


@Composable
fun GoalDetailsScreenBody(
    navigateBack: () -> Unit,
    goalWithDetails: GoalWithDetails,
    goalDetailsViewModel: GoalDetailsViewModel,
    onProgressChanged: () -> Unit,
    toRoutineDetailsScreen: (Int) -> Unit,
    toEditGoalScreen: (Int) -> Unit,
    showPostDialog: () -> Unit,
    modifier: Modifier = Modifier,
){
    var showChangeValueDialog by remember { mutableStateOf(false) }

    if(showChangeValueDialog){

        if(goalWithDetails.completionCriteria.targetValue != null && goalWithDetails.completionCriteria.currentValue != null){
            CompletionCriteriaReachTargetValueDialog(
                onDismiss = { showChangeValueDialog = false },
                onConfirm = {
                    goalDetailsViewModel.updateTargetValue(it)
                    showChangeValueDialog = false
                    onProgressChanged()
                            },
                targetValue = goalWithDetails.completionCriteria.targetValue,
                currentValue = goalWithDetails.completionCriteria.currentValue,
                unit = goalWithDetails.completionCriteria.unit?: ""
            )
        }
    }

    LazyColumn(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)) {
        /**
         * Back arrow
         */
        item {
            BackArrow(navigateBack = navigateBack)
        }

        /**
         * Headline with send and settings Icon
         */
        item{
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = PADDING_PREVIOUS_SECTION.dp, top = 20.dp)
            ){
                Text(
                    text = goalWithDetails.goal.title,
                    style = MaterialTheme.typography.headlineLarge,
                    )
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share),
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(30.dp)
                        .clickable { showPostDialog()}
                )
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = stringResource(R.string.settings),
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(30.dp)
                        .clickable { toEditGoalScreen(goalWithDetails.goal.id) }
                )
            }
        }

        /**
         * Deadline
         */
        item{
            Text(
                text = stringResource(R.string.until) + " " + convertDateToStringFormatDots(goalWithDetails.goal.deadline),
                style = MaterialTheme.typography.headlineMedium
            )
        }


        /**
         * progress
         */
        item{
            ProgressDisplay(
                completionType = goalWithDetails.completionCriteria.completionType,
                progress = goalWithDetails.goal.progress,
                targetValue = goalWithDetails.completionCriteria.targetValue,
                onClickReachGoal = {
                    goalDetailsViewModel.toggleProgressReachGoal()
                    onProgressChanged()
                                   },
                onClickReachTargetAdd = {
                    goalDetailsViewModel.addOrSubtractCurrentValue(true)
                    onProgressChanged()
                                        },
                onClickReachTargetSubtract = { goalDetailsViewModel.addOrSubtractCurrentValue(false) },
                unit = goalWithDetails.completionCriteria.unit,
                currentValue = goalWithDetails.completionCriteria.currentValue,
                onClickOpenChangeValueDialog = { showChangeValueDialog = true },
                modifier = Modifier
                    .padding(top = 20.dp)
            )

        }

        /**
         * Notes
         */
        item{
            Text(
                text = stringResource(R.string.notes),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = PADDING_PREVIOUS_SECTION.dp, bottom = 10.dp)
            )
        }
        item{
            ScrollableTextField(
                text = goalWithDetails.goal.notes,
                height = 200
            )
        }

        /**
         * Routines
         */
        item{
            Text(
                text = stringResource(R.string.routine),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = PADDING_PREVIOUS_SECTION.dp, bottom = 10.dp)
            )
        }

        items(items = goalWithDetails.routines) { routine ->
            RoutineCard(
                title = routine.routine.title,
                frequency = routine.routine.frequency,
                progressConnected = goalWithDetails.completionCriteria.completionType == CompletionType.ConnectRoutine,
                progress = routine.routine.progress,
                withProgressBar = true,
                startDate = routine.routine.startDate,
                daysOfWeek = routine.routine.daysOfWeek,
                intervalDays = routine.routine.intervalDays,
                endDate = routine.routine.endDate,
                targetValue = routine.routine.targetValue,
                onClick = { toRoutineDetailsScreen(routine.routine.id) },
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
    }
}

@Composable
fun ProgressDisplay(
    completionType: CompletionType,
    progress: Float,
    targetValue: Int?,
    onClickReachGoal: () -> Unit,
    onClickReachTargetAdd: () -> Unit,
    onClickReachTargetSubtract: () -> Unit,
    onClickOpenChangeValueDialog: () -> Unit,
    unit: String?,
    currentValue: Int?,
    modifier: Modifier = Modifier
){
    when(completionType){
        CompletionType.ReachGoal -> {
            val goalCompleted = progress >= 1;
            SelectButton(
                title = if(goalCompleted) stringResource(R.string.goal_completed) else stringResource(R.string.click_to_complete_goal),
                selected = goalCompleted,
                onClick = {onClickReachGoal()},
                modifier = modifier
                    .height(50.dp)
            )
        }
        CompletionType.ConnectRoutine -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
            ){
                Text(
                    text = stringResource(R.string.progress_with_routines_connected),
                    style = MaterialTheme.typography.bodyMedium
                )
                ProgressBar(
                    progress = progress,
                    modifier = Modifier
                        .padding(start = 5.dp, top = 3.dp)
                        .fillMaxWidth()
                        .size(60.dp)
                        .height(50.dp)
                )
            }
        }
        CompletionType.ReachTargetValue -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxWidth().wrapContentHeight()
            ){
                Text(
                    text = (currentValue?.toString() ?: "") + " / " + targetValue + " " + unit,
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 3.dp)
                ) {
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .clickable { onClickReachTargetSubtract() }
                            .padding(start = 5.dp, end = 10.dp)
                    )
                    ProgressBar(
                        progress = progress,
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .fillMaxWidth(0.9f)
                            .height(50.dp)
                    )
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .clickable { onClickReachTargetAdd() }

                            .padding(start = 10.dp, end = 5.dp)
                    )
                }
                Button(
                    onClick = onClickOpenChangeValueDialog,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.primary),
                        contentColor = colorResource(R.color.button_font_light)
                    ),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(0.5f)
                        .height(40.dp)
                ){
                    Text(text= stringResource(R.string.change_value))
                }
            }
        }
    }
}

@Composable
fun CompletionCriteriaReachTargetValueDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    targetValue: Int,
    currentValue: Int,
    unit: String,
    modifier: Modifier = Modifier
){
    var isValid by remember { mutableStateOf(false)}
    var newCurrentValue by remember { mutableIntStateOf(currentValue) }

    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Box(
            modifier = modifier
                .shadow(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .wrapContentSize()
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                Text(
                    text = stringResource(R.string.enter_new_value),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    OutlinedTextField(
                        value = newCurrentValue.toString(),
                        onValueChange = {
                            val input = if(it == "") 0 else it.toIntOrNull()
                            if(input != null){
                                newCurrentValue = input
                                if(input in 0..targetValue){
                                    isValid = true
                                }else{
                                    isValid = false
                                }
                            }
                                        },
                        isError = !isValid,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
                /**
                 * Cancel and Confirm button
                 */
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ){
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.cardsBackground),
                            contentColor = colorResource(R.color.button_font)
                        ),
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .weight(1f)
                    ){
                        Text( text = stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            onConfirm(newCurrentValue)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.primary),
                            contentColor = colorResource(R.color.button_font_light)
                        ),
                        enabled = isValid,
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .weight(1f)
                    ){
                        Text(text = stringResource(R.string.confirm))
                    }
                }
            }
        }

    }
}

@Composable
fun CongratulationDialog(
    onShare: () -> Unit = {},
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
){
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = modifier
                .shadow(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(94, 165, 191))
                .height(500.dp)
        ){
            Image(
                painter = painterResource(R.drawable.congratulations),
                contentDescription = stringResource(R.string.congratulations),
                modifier = Modifier
                    .fillMaxSize()
            )

            Text(
                text = stringResource(R.string.congratulations_text),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .align(Alignment.BottomCenter)
                    .background(Color(194, 224, 207))
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ){
                    Text(
                        text = stringResource(R.string.share_your_accomplishment),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onShare() },
                        modifier = Modifier
                            .padding(end = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share),
                            tint = colorResource(R.color.primary),
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }


                }
            }
        }
    }
}

@Preview
@Composable
fun ProgressDisplayPreview(){
    ProgressDisplay(
        completionType = CompletionType.ReachTargetValue,
        progress = 0.5f,
        targetValue = 100,
        onClickReachGoal = {},
        onClickReachTargetAdd = {},
        onClickReachTargetSubtract = {},
        unit = "Steps",
        currentValue = 50,
        onClickOpenChangeValueDialog = {}
    )
}

@Preview
@Composable
fun CongratulationDialogPreview(){
    CongratulationDialog(onDismiss = {})
}