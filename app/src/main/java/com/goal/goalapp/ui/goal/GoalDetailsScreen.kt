package com.goal.goalapp.ui.goal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.data.CompletionType
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.ProgressBar
import com.goal.goalapp.ui.components.SelectButton
import com.goal.goalapp.ui.helper.convertDateToStringFormat
import com.goal.goalapp.ui.helper.convertDateToStringFormatDots

@Composable
fun GoalDetailsScreen(
    goalId: Int?,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    goalDetailsViewModel: GoalDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val goalDetailsUiState by goalDetailsViewModel.goalDetailsUiState.collectAsState()

    if(goalId != null && goalDetailsUiState.goalId != goalId){
        goalDetailsViewModel.loadGoal(goalId)
    }

    if(goalDetailsUiState.goalId != 0){
        Text(text = "Goal Details")
        GoalDetailsScreenBody(
            navigateBack = navigateBack,
            goalDetailsUiState = goalDetailsUiState,
            modifier = modifier
        )
    }

}


@Composable
fun GoalDetailsScreenBody(
    navigateBack: () -> Unit,
    goalDetailsUiState: GoalDetailsUiState,
    modifier: Modifier = Modifier,
){
    LazyColumn(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)) {
        /**
         * Back arrow
         */
        item {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_arrow),
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .clickable {
                        /*TODO*/
                        navigateBack()
                    }
                    .size(30.dp)
            )
        }

        /**
         * Headline with send and settings Icon
         */
        item{
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = goalDetailsUiState.title,
                    style = MaterialTheme.typography.headlineLarge,
                    )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.share),
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(30.dp)
                )
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = stringResource(R.string.settings),
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(30.dp)
                )
            }
        }

        /**
         * Deadline
         */
        item{
            Text(
                text = stringResource(R.string.until) + " " + convertDateToStringFormatDots(goalDetailsUiState.deadline),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        /**
         * progress
         */
        item{
            ProgressDisplay(
                completionType = goalDetailsUiState.completionCriteria.completionType,
                progress = goalDetailsUiState.progress,
                targetValue = goalDetailsUiState.completionCriteria.targetValue,
                onClickReachGoal = { /*TODO*/ },
                onClickReachTargetAdd = { /*TODO*/ },
                onClickReachTargetSubtract = { /*TODO*/ },
                unit = goalDetailsUiState.completionCriteria.unit,
                currentValue = goalDetailsUiState.completionCriteria.currentValue,
                modifier = Modifier.padding(top = 10.dp)
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
    unit: String?,
    currentValue: Int?,
    modifier: Modifier = Modifier
){
    when(completionType){
        CompletionType.ReachGoal -> {
            SelectButton(
                title = stringResource(R.string.click_to_complete_goal),
                selected = progress >= 1,
                onClick = {onClickReachGoal()},
                modifier = modifier
            )
        }
        CompletionType.ConnectRoutine -> {
            Row(
                horizontalArrangement = Arrangement.Center
            ){
                Text(
                    text = stringResource(R.string.progress_connected),
                    style = MaterialTheme.typography.bodySmall
                )
                ProgressBar(
                    progress = progress,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .fillMaxWidth()
                        .size(60.dp)
                )
            }
        }
        CompletionType.ReachTargetValue -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = (currentValue?.toString() ?: "") + " / " + targetValue + " " + unit,
                    style = MaterialTheme.typography.bodySmall
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
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
                            .size(60.dp)
                    )
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .clickable { onClickReachTargetAdd() }

                            .padding(start = 10.dp, end = 5.dp)
                    )
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
        currentValue = 50
    )
}