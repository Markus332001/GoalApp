package com.goal.goalapp.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.data.Frequency
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.BackArrow
import com.goal.goalapp.ui.components.DateInput
import com.goal.goalapp.ui.components.DeleteDialog
import com.goal.goalapp.ui.components.SelectButton
import com.goal.goalapp.ui.helper.getDayShortForm
import java.time.DayOfWeek
import java.time.LocalDate


@Composable
fun CreateRoutineScreen(
    routineId: Int?,
    navigateBack: () -> Unit,
    toGoalDetailsScreen: (Int) -> Unit,
    modifier: Modifier = Modifier,
    createGoalViewModel: CreateGoalViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val routine by createGoalViewModel.routine.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showWeeklyDialog by remember { mutableStateOf(false) }
    val createEditState = createGoalViewModel.createEditState.collectAsState()

    //is called when this composable gets initialized
    LaunchedEffect(Unit) {
        if(routineId != null){
            createGoalViewModel.getRoutineDetailsFromDb(routineId)
        }
    }

    /**
     * Delete Dialog
     */
    if(showDeleteDialog){
        DeleteDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                createGoalViewModel.deleteRoutine()
                createGoalViewModel.resetCreateRoutine()

                //goes back when the routine is in create state or it comes from the edit screen of goal
                if(routine.id == 0 ||  createGoalViewModel.createGoal.value.id != 0){
                    navigateBack()
                }else{
                    toGoalDetailsScreen(routine.goalId)
                }
                showDeleteDialog = false
            }
        )
    }

    /**
     * Weekly Dialog
     */
    if(showWeeklyDialog){
        SelectWeeklyDialog(
            setShowDialog = { showWeeklyDialog = it },
            setDaysSelected = { createGoalViewModel.updateRoutineFrequency(Frequency.Weekly, daysOfWeek = it) },
            oldSelectedDays = routine.daysOfWeek ?: emptyList()
        )
    }

    /**
     * If the goal was successfully created, navigate back to the goal overview screen
     */
    if(createEditState.value is CreateEditState.Success){
        createGoalViewModel.resetCreateRoutine()
        navigateBack()
    }

    CreateRoutineScreenBody(
        navigateBack = {
            createGoalViewModel.resetCreateRoutine()
            navigateBack()
        },
        routine = routine,
        createGoalViewModel = createGoalViewModel,
        setShowDialog = { showWeeklyDialog = it },
        deleteRoutine = {
            if(routine.id != 0){
                showDeleteDialog = true
            }else{
                createGoalViewModel.removeRoutineFromGoal()
                createGoalViewModel.resetCreateRoutine()
                navigateBack()
            }
        },
        modifier = modifier
    )

}

@Composable
fun CreateRoutineScreenBody(
    navigateBack: () -> Unit,
    routine: CreateRoutine,
    createGoalViewModel: CreateGoalViewModel,
    setShowDialog: (Boolean) -> Unit,
    deleteRoutine: () -> Unit,
    modifier: Modifier = Modifier
){
    val scrollState = rememberScrollState()
    var isValid by remember { mutableStateOf(false) }
    val isRoutineInGoal = createGoalViewModel.isRoutineInGoal()
    isValid = checkRoutineValidity(routine)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        BackArrow(

            navigateBack = navigateBack
        )

        /**
         * Headline
         */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = PADDING_PREVIOUS_SECTION.dp)
        ) {
            Text(
                text = if (routine.id == 0) stringResource(R.string.create_routine) else stringResource(
                    R.string.edit_routine
                ),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )

            if(isRoutineInGoal || routine.id != 0){
                //Delete Icon
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(30.dp)
                        .clickable { deleteRoutine()}
                )
            }
        }

        /**
         * Title
         */
        Text(text = stringResource(R.string.title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = PADDING_PREVIOUS_SECTION.dp, bottom = PADDING_AFTER_HEADLINE.dp)
        )

        OutlinedTextField(
            value = routine.title,
            onValueChange = { createGoalViewModel.updateRoutineTitle(it) },
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth()
        )

        /**
         * Frequency
         */
        Text(text = stringResource(R.string.frequency),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = PADDING_PREVIOUS_SECTION.dp, bottom = PADDING_AFTER_HEADLINE.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ){
            SelectButton(
                title = stringResource(R.string.daily),
                onClick = { createGoalViewModel.updateRoutineFrequency(Frequency.Daily) },
                selected = routine.frequency == Frequency.Daily,
                modifier = Modifier
                    .weight(0.5f)
                    .height(125.dp)
                    .padding(end  = 10.dp)
                    .shadow(8.dp)
            )
            SelectButtonWeekly(
                title = stringResource(R.string.weekly),
                onClick = { setShowDialog(true) },
                daysOfWeek = routine.daysOfWeek?: emptyList(),
                selected = routine.frequency == Frequency.Weekly,
                modifier = Modifier
                    .weight(0.5f)
                    .height(125.dp)
                    .shadow(8.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            SelectButtonWithIntInput(
                firstPart = stringResource(R.string.every),
                secondPart = stringResource(R.string.day),
                selected = routine.frequency == Frequency.IntervalDays,
                onChange = {
                    if((it?: -1) > 1){
                        createGoalViewModel.updateRoutineFrequency(
                            Frequency.IntervalDays,
                            intervalDays = it
                        )
                    }
                },
                input = routine.intervalDays ?: 0,
                modifier = Modifier
                    .weight(0.50f)
                    .height(125.dp)
                    .padding(end  = 10.dp)
                    .shadow(8.dp)
            )
            Spacer(modifier = Modifier.weight(0.50f))

        }

        /**
         * Start Date
         */
        Text(text = stringResource(R.string.start),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = PADDING_PREVIOUS_SECTION.dp, bottom = PADDING_AFTER_HEADLINE.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            StartEndDateInput(
                title = stringResource(R.string.date),
                date = routine.startDate,
                onDateChange = { createGoalViewModel.updateRoutineStartDate(it) },
                isStart = true,
                modifier = Modifier
                    .weight(0.50f)
                    .height(125.dp)
                    .padding(end  = 10.dp)
                    .shadow(8.dp)
            )
            Spacer(modifier = Modifier.weight(0.50f))

        }

        /**
         * End Date
         */
        Text(text = stringResource(R.string.end),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = PADDING_PREVIOUS_SECTION.dp, bottom = PADDING_AFTER_HEADLINE.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ){
            SelectButtonWithIntInput(
                firstPart = stringResource(R.string.after),
                secondPart = stringResource(R.string.time),
                selected = routine.targetValue != null,
                onChange = {
                    if((it?: -1) > 1) {
                        createGoalViewModel.updateRoutineEndDate(null, it)
                    }
                },
                input = routine.targetValue,
                modifier = Modifier
                    .weight(0.5f)
                    .height(125.dp)
                    .padding(end  = 10.dp)
                    .shadow(8.dp)
            )
            StartEndDateInput(
                title = stringResource(R.string.date),
                date = routine.endDate,
                onDateChange = { createGoalViewModel.updateRoutineEndDate(it, null) },
                isStart = false,
                modifier = Modifier
                    .weight(0.50f)
                    .height(125.dp)
                    .shadow(8.dp)
            )
        }

        /**
         * Save Button
         */
        Button(
            onClick = {
                createGoalViewModel.addOrEditRoutine()
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
        ){
            Text(text = stringResource(R.string.confirm))
        }

    }
}

fun checkRoutineValidity(routine: CreateRoutine): Boolean {
    return routine.title != "" &&
            (routine.frequency != null && if(routine.frequency == Frequency.IntervalDays) (routine.intervalDays?: -1 ) >= 1 else true) &&
            (routine.startDate != null ) &&
            ((if(routine.targetValue != null) routine.targetValue > 0 else false) || if(routine.endDate != null) routine.endDate > routine.startDate else false)
}

@Composable
fun SelectButtonWeekly(
    title: String,
    onClick: () -> Unit,
    daysOfWeek: List<DayOfWeek>,
    selected: Boolean,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onClick,
        colors = ButtonColors(
            containerColor = if (selected) colorResource(R.color.primary) else colorResource(R.color.cardsBackground),
            contentColor = if (selected) colorResource(R.color.button_font_light) else Color.Black,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        ){
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 5.dp)
            )
            /**
             * Select Days of the week
             */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    for(i in 0..3){
                        ClickableCircle(
                            day = DayOfWeek.entries[i],
                            dayAsShortForm = getDayShortForm(DayOfWeek.entries[i]),
                            size = 30,
                            fontSize = 10,
                            onClick = {
                                onClick()
                            },
                            isClicked = DayOfWeek.entries[i] in daysOfWeek
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    for(i in 4..6){
                        ClickableCircle(
                            day = DayOfWeek.entries[i],
                            dayAsShortForm = getDayShortForm(DayOfWeek.entries[i]),
                            size = 30,
                            fontSize = 10,
                            onClick = {
                                onClick()
                            },
                            isClicked = DayOfWeek.entries[i] in daysOfWeek
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun SelectButtonWithIntInput(
    firstPart: String,
    secondPart: String,
    selected: Boolean,
    onChange: (Int?) -> Unit,
    input: Int?,
    modifier: Modifier = Modifier
){
    Button(
        onClick = { onChange(input) },
        colors = ButtonColors(
            containerColor = if (selected) colorResource(R.color.primary) else colorResource(R.color.cardsBackground),
            contentColor = if (selected) colorResource(R.color.button_font_light) else Color.Black,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()

    ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Text(text = firstPart, style = MaterialTheme.typography.bodyLarge)
            TextField(
                value = (input?: 0).toString(),
                onValueChange = { onChange(it.toIntOrNull()) },
                modifier = Modifier.width(50.dp),
                isError = (selected && (input?: 0) < 1)
            )
            Text(text = secondPart, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun SelectWeeklyDialog(
    setShowDialog: (Boolean) -> Unit,
    setDaysSelected: (List<DayOfWeek>) -> Unit,
    oldSelectedDays: List<DayOfWeek>,
    modifier: Modifier = Modifier
){
    var isValid by remember { mutableStateOf(false) }
    var selectedDays by remember { mutableStateOf(oldSelectedDays) }
    isValid = selectedDays.isNotEmpty()

    Dialog(onDismissRequest = { setShowDialog(false) }) {
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
                    text = stringResource(R.string.weekly),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                )

                /**
                 * Select Days of the week
                 */
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ){
                        for(i in 0..3){
                            ClickableCircle(
                                day = DayOfWeek.entries[i],
                                dayAsShortForm = getDayShortForm(DayOfWeek.entries[i]),
                                size = 50,
                                fontSize = 20,
                                onClick = {
                                    selectedDays = addOrRemoveDay(it, selectedDays)
                                },
                                isClicked = DayOfWeek.entries[i] in selectedDays
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ){
                        for(i in 4..6){
                            ClickableCircle(
                                day = DayOfWeek.entries[i],
                                dayAsShortForm = getDayShortForm(DayOfWeek.entries[i]),
                                size = 50,
                                fontSize = 20,
                                onClick = {
                                    selectedDays = addOrRemoveDay(it, selectedDays)
                                },
                                isClicked = DayOfWeek.entries[i] in selectedDays
                            )
                        }
                    }
                }

                /**
                 * Cancel and confirm button
                 */
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ){
                    Button(
                        onClick = { setShowDialog(false) },
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
                            setShowDialog(false)
                            setDaysSelected(selectedDays)
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartEndDateInput(
    title: String,
    date: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    isStart: Boolean,
    modifier: Modifier = Modifier

){
    /**
     * Color when selected and not selected
     */
    val selectedColor = OutlinedTextFieldDefaults.colors(
        focusedTextColor = colorResource(R.color.button_font_light),
        unfocusedTextColor = colorResource(R.color.button_font_light),
        focusedBorderColor = colorResource(R.color.button_font_light),
        unfocusedBorderColor = colorResource(R.color.button_font_light),
        cursorColor = colorResource(R.color.button_font_light),
        focusedLabelColor = colorResource(R.color.button_font_light),
        unfocusedLabelColor = colorResource(R.color.button_font_light),
    )
    val selected = date != null

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) colorResource(R.color.primary) else colorResource(R.color.cardsBackground))
    ){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ){
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) colorResource(R.color.button_font_light) else Color.Black
            )
            DateInput(
                date = date,
                onDateChange = { onDateChange(it) },
                label = if(isStart) stringResource(R.string.start) else stringResource(R.string.end),
                color = if (selected) selectedColor else OutlinedTextFieldDefaults.colors(),
                iconColor = if (selected) colorResource(R.color.button_font_light) else LocalContentColor.current
            )
        }
    }
}




fun addOrRemoveDay(day: DayOfWeek, selectedDays: List<DayOfWeek>): List<DayOfWeek>{
    if(day in selectedDays){
        return selectedDays.filter { it != day }
    }else{
        return selectedDays + day
    }
}



@Composable
fun ClickableCircle(day: DayOfWeek, dayAsShortForm: String, size: Int, fontSize: Int, onClick: (DayOfWeek) -> Unit, isClicked: Boolean = false) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .shadow(5.dp, shape = CircleShape)
            .background(
                color = if (isClicked) colorResource(R.color.primary) else colorResource(R.color.cardsBackground),
                shape = CircleShape
            )
            .clickable { onClick(day) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayAsShortForm,
            color = if (isClicked) colorResource(R.color.button_font_light) else Color.Black,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun ClickableCirclePreview(){
   ClickableCircle(day = DayOfWeek.FRIDAY, size = 50, fontSize = 20, dayAsShortForm = "Fr", onClick = {})
}