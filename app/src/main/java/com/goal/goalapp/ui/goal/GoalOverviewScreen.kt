package com.goal.goalapp.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goal.goalapp.R
import com.goal.goalapp.ui.components.ProgressBar
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.ui.AppViewModelProvider

@Composable
fun GoalOverviewScreen(
    toGoalDetailsScreen: (Int) -> Unit,
    toCreateGoalScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoalOverviewViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val goals = viewModel.goals.collectAsState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 15.dp, end = 15.dp, top = 10.dp)
    ){
        Column (
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
        ){
            Text(
                text = stringResource(R.string.goal_overview),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            LazyColumn{
                items(items = goals.value) { item ->
                    GoalCard(
                        onClick = { toGoalDetailsScreen(item.id) },
                        goalName = item.title,
                        goalProgress = item.progress
                    )
                }
            }
        }
        AddButton(
            onClick = {toCreateGoalScreen() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp)
        )

    }
}

@Composable
fun GoalCard(
    onClick: () -> Unit,
    goalName: String,
    goalProgress: Float,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .height(100.dp)
            .background(
                color = colorResource(R.color.cardsBackground),
                shape = RoundedCornerShape(16.dp)
            )){
        Text(
            text = goalName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 12.dp)
        )
        ProgressBar(
            progress = goalProgress,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth(0.75f)
                .fillMaxHeight(0.5f)
                .padding(end = 20.dp, bottom = 12.dp)
        )

    }
}

@Composable
fun AddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier){
    Box(
        modifier = modifier
            .size(80.dp)
            .shadow(
                elevation = 8.dp, // height
                shape = RoundedCornerShape(16.dp), // form
                clip = false // if out of Box
            )
            .clickable { onClick() }
            .background(
                color = colorResource(R.color.cardsBackground),
                shape = RoundedCornerShape(16.dp)
            )
    ){
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.add_goal),
            tint = Color.Black,
            modifier = Modifier
                .align(Alignment.Center)
                .size(50.dp)
        )
    }
}

@Preview
@Composable
fun GoalCardPreview(){
    Box(modifier = Modifier.fillMaxSize()){
        GoalCard(onClick = {}, goalName = "Goal 1", goalProgress = 0.5f)
    }

}

@Preview
@Composable
fun GoalOverviewScreenPreview() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)) {
        GoalOverviewScreen(toGoalDetailsScreen = {}, toCreateGoalScreen = {})
    }
}

@Preview
@Composable
fun AddButtonPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        AddButton(onClick = {})
    }
}