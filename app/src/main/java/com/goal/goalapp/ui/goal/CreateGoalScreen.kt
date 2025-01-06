package com.goal.goalapp.ui.goal

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.data.CompletionType
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.DateInput
import com.goal.goalapp.ui.components.NotesInput
import com.goal.goalapp.ui.components.SelectButton
import androidx.compose.foundation.lazy.items
import com.goal.goalapp.ui.components.AddComponentButton

const val PADDING_PREVIOUS_SECTION = 30
const val PADDING_AFTER_HEADLINE = 10
const val PADDING_BETWEEN_ELEMENTS = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    navigateBack: () -> Unit,
    toCreateRoutineScreen: () -> Unit,
    modifier: Modifier = Modifier,
    createGoalViewModel: CreateGoalViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val createGoal by createGoalViewModel.createGoal.collectAsState()
    var showReachTargetValuePopup by remember { mutableStateOf(false) }

    /**
     * Completion Criteria Reach Target Value Popup
     */
    if(showReachTargetValuePopup){
        CompletionCriteriaReachTargetValuePopUp(
            setShowPopup = { showReachTargetValuePopup = it },
            setCompletionCriteria = { targetValue, unit ->
                createGoalViewModel.updateGoalCompletionCriteriaReachTargetValue(targetValue, unit)
            }
        )
    }

    LazyColumn(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)) {
        /**
         * Back arrow
         */
        item{
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_arrow),
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .clickable{ navigateBack() }
                        .size(30.dp)
                )

        }

        /**
         * Headline
         */
        item{
            Text(
                text = stringResource(R.string.new_goal),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 15.dp)
            )
        }

        /**
         * Title
         */
        item{
            Text(text = stringResource(R.string.title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = PADDING_PREVIOUS_SECTION.dp, bottom = PADDING_AFTER_HEADLINE.dp)
            )
        }
        item{
            OutlinedTextField(
                value = createGoal.title,
                onValueChange = { createGoalViewModel.updateGoalTitle(it) },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        /**
         * Deadline
         */
        item{
            Text(
                text = stringResource(R.string.deadline),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = PADDING_PREVIOUS_SECTION.dp, bottom = PADDING_AFTER_HEADLINE.dp)
            )
        }
        item{
            DateInput(
                date = createGoal.deadline,
                onDateChange = { createGoalViewModel.updateGoalDeadline(it) },
                label = stringResource(R.string.deadline)
            )
        }

        /**
         * Completion Criterion
         */
        item{
            Text(
                text = stringResource(R.string.completion_criteria),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = PADDING_PREVIOUS_SECTION.dp, bottom = PADDING_AFTER_HEADLINE.dp)
            )
        }
        item{
            Text(
                text = stringResource(R.string.completion_criteria_description),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
            )
        }
        item{
            SelectButton(
                title = stringResource(R.string.reach_goal),
                onClick = { createGoalViewModel.updateGoalCompletionCriteriaReachGoal() },
                selected = createGoal.completionCriteria?.completionType == CompletionType.ReachGoal,
                modifier = Modifier.padding(top = PADDING_BETWEEN_ELEMENTS.dp).height(50.dp)
            )
        }
        item {
            SelectButton(
                title = stringResource(R.string.connect_routine),
                onClick = { createGoalViewModel.updateGoalCompletionCriteriaConnectRoutine() },
                selected = createGoal.completionCriteria?.completionType == CompletionType.ConnectRoutine,
                modifier = Modifier.padding(top = PADDING_BETWEEN_ELEMENTS.dp).height(50.dp)
            )
        }
        item {
            SelectButton(
                title = stringResource(R.string.reach_target_value),
                onClick = {
                    showReachTargetValuePopup = true
                },
                selected = createGoal.completionCriteria?.completionType == CompletionType.ReachTargetValue,
                modifier = Modifier.padding(top = PADDING_BETWEEN_ELEMENTS.dp).height(50.dp)
            )
        }

        /**
         * Notes
         */
        item{
            Text(
                text = stringResource(R.string.notes),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = PADDING_PREVIOUS_SECTION.dp, bottom = PADDING_AFTER_HEADLINE.dp)
            )
        }
        item{
            NotesInput(
                noteText = createGoal.notes,
                onNoteChange = { createGoalViewModel.updateGoalNotes(it) },
                label = stringResource(R.string.notes)
            )
        }

        /**
         * Routine
         */
        item{
            Text(
                text = stringResource(R.string.routine),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = PADDING_PREVIOUS_SECTION.dp, bottom = PADDING_AFTER_HEADLINE.dp)
            )
        }
        item{
            AddComponentButton(
                title = stringResource(R.string.add_routine),
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(top = PADDING_BETWEEN_ELEMENTS.dp).height(50.dp)
            )
        }
        items(items = createGoal.routines) { item ->
            /*TODO*/
        }
    }

}

@Composable
fun CompletionCriteria(){


}

@Composable
fun CompletionCriteriaReachTargetValuePopUp(
    setShowPopup: (Boolean) -> Unit,
    setCompletionCriteria: (Int, String) -> Unit,
    modifier: Modifier = Modifier
){
    var targetValue by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { setShowPopup(false) }) {


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
                        text = stringResource(R.string.target_value_input),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                    )
                    OutlinedTextField(
                        value = targetValue,
                        onValueChange = {newValue ->
                            // Filter input to allow only numeric characters
                            if (newValue.all { it.isDigit() }) {
                                targetValue = newValue
                                isValid = (targetValue != "" && unit != "")
                            }
                        },
                        label = { Text(stringResource(R.string.target_value)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {
                            unit = it
                            isValid = (targetValue != "" && unit != "")},
                        label = { Text(stringResource(R.string.unit)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ){
                        Button(
                            onClick = { setShowPopup(false) },
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
                                setShowPopup(false)
                                setCompletionCriteria(targetValue.toInt(), unit)
                            },
                            enabled = isValid,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.primary),
                                contentColor = colorResource(R.color.button_font_light)
                            ),
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

@Preview
@Composable
fun CompletionCriteriaReachTargetValuePopUpPreview(){
    var showPopup by remember { mutableStateOf(true) }
    CompletionCriteriaReachTargetValuePopUp(
        setShowPopup = { showPopup = it },
        setCompletionCriteria = { _, _ -> }
    )
}

