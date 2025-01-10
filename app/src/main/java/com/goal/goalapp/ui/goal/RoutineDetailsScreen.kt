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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.data.goal.Routine
import com.goal.goalapp.data.goal.RoutineWithCalendarDays
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.BackArrow
import com.goal.goalapp.ui.components.ProgressBar
import com.goal.goalapp.ui.helper.CalendarDaysBackgroundColorType
import com.goal.goalapp.ui.helper.CalendarDisplay
import com.goal.goalapp.ui.helper.convertDateToStringFormat
import com.goal.goalapp.ui.helper.getColorFromCalendarDaysBackgroundColorType
import com.goal.goalapp.ui.helper.getDayShortForm
import com.goal.goalapp.ui.helper.getFrequencyString
import com.goal.goalapp.ui.helper.getMonthString
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun RoutineDetailsScreen(
    routineId: Int?,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    routineDetailsViewModel: RoutineDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val routine by routineDetailsViewModel.routine.collectAsState()
    val calendarStructure = routineDetailsViewModel.calendarStructure
    val calendarYearMonth = routineDetailsViewModel.calendarYearMonth

    if(routineId != null && routine == null){
        routineDetailsViewModel.loadRoutineWithCalendarDays(routineId)
    }

    LaunchedEffect(routine){
        routineDetailsViewModel.setCalendarStructure()
    }

    if(routine != null){
        RoutineDetailsBody(
            routine = routine!!,
            navigateBack = navigateBack,
            calendarStructure = calendarStructure,
            calendarYearMonth = calendarYearMonth,
            addMonthCalendarYearMonth = {routineDetailsViewModel.addMonthCalendarYearMonth()},
            substractMonthCalendarYearMonth = {routineDetailsViewModel.substractMonthCalendarYearMonth()},
            modifier = modifier
        )
    }
}

@Composable
fun RoutineDetailsBody(
    routine: RoutineWithCalendarDays,
    navigateBack: () -> Unit,
    calendarStructure: State<SnapshotStateList<SnapshotStateList<CalendarDisplay>>>,
    calendarYearMonth: State<YearMonth>,
    addMonthCalendarYearMonth: () -> Unit,
    substractMonthCalendarYearMonth: () -> Unit,
    modifier: Modifier = Modifier
){
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ){
        /**
         * Back Arrow
         */
        BackArrow(
            navigateBack = navigateBack
        )

        /**
         * Title
         */
        Text(
            text = routine.routine.title,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding( bottom = 40.dp)
        )

        /**
         * Frequency
         */
        Text(
            text = getFrequencyString(frequency = routine.routine.frequency,
                intervalDays = routine.routine.intervalDays, daysOfWeek = routine.routine.daysOfWeek),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        /**
         * Start and End date
         */
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(bottom = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.start) + ": " + convertDateToStringFormat(routine.routine.startDate)
            )
            Text(
                text = stringResource(R.string.end) + ": " + if(routine.routine.endDate == null) stringResource(R.string.after)
                        + " " + routine.routine.targetValue.toString() + " " + stringResource(R.string.time) else
                        convertDateToStringFormat(routine.routine.endDate)
            )
        }


        /**
         * Progressbar
         */
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(bottom = 30.dp)
                .fillMaxWidth()
        ){
            Text(
                text = if(routine.routine.endDate == null)  routine.routine.currentValue.toString() + " / " + routine.routine.targetValue.toString() else
                   stringResource(R.string.until) + " " + convertDateToStringFormat(routine.routine.endDate)
           )
            ProgressBar(
                progress = routine.routine.progress,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .height(50.dp)
            )
        }

        /**
         * Calendar
         */
        SmallCalendar(
            calendarStructure = calendarStructure,
            calendarYearMonth = calendarYearMonth,
            addMonthCalendarYearMonth = addMonthCalendarYearMonth,
            substractMonthCalendarYearMonth = substractMonthCalendarYearMonth
        )
    }
}

@Composable
fun RoundDayChips(
    day: Int,
    calendarDaysBackgroundColorType: CalendarDaysBackgroundColorType,
    size: Int,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
){
    val colors = getColorFromCalendarDaysBackgroundColorType(calendarDaysBackgroundColorType)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size.dp)
            .background(
                color = colors.backgroundColor,
                shape = CircleShape
            )
    ){
        Text(
            text = day.toString(),
            style = textStyle,
            color = colors.fontColor
        )
    }
}

@Composable
fun SmallCalendar(
    calendarStructure: State<SnapshotStateList<SnapshotStateList<CalendarDisplay>>>,
    calendarYearMonth: State<YearMonth>,
    addMonthCalendarYearMonth: () -> Unit,
    substractMonthCalendarYearMonth: () -> Unit,
    modifier: Modifier = Modifier
){
    val date = LocalDate.now() // today

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.cardsBackground))
            .fillMaxWidth()
            .padding(10.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = Color.Black,
                contentDescription = stringResource(R.string.back_arrow),
                modifier = Modifier.size(30.dp)
                    .clickable {
                        substractMonthCalendarYearMonth()
                    }
            )
            Text(
                text = getMonthString(calendarYearMonth.value.monthValue) + " " + calendarYearMonth.value.year.toString(),
                style = MaterialTheme.typography.headlineMedium
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                tint = Color.Black,
                contentDescription = stringResource(R.string.back_arrow),
                modifier = Modifier.size(30.dp)
                    .clickable {
                        addMonthCalendarYearMonth()
                    }
            )
        }
        SmallCalendarDays(
            calendarStructure = calendarStructure,
            modifier = Modifier.padding(top = 10.dp)
        )

    }
}

@Composable
fun SmallCalendarDays(
    calendarStructure: State<SnapshotStateList<SnapshotStateList<CalendarDisplay>>>,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ){
        /**
         * Days of the week
         */
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp)
        ){
            for(day in DayOfWeek.entries) {
                Text(
                    text = getDayShortForm(day),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        for(week in calendarStructure.value){

            SmallCalendarDaysRow(
                week = week
            )
        }
    }
}

@Composable
fun SmallCalendarDaysRow(
    week: List<CalendarDisplay>,
    modifier: Modifier = Modifier
){

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ){
        for(day in week){
            SmallCalendarDay(
                date = day.date,
                weekDay = day.date.dayOfWeek,
                isVisible = day.isVisible,
                calendarDaysBackgroundColorType = day.colorDaysBackgroundColorType,
                modifier = Modifier
            )
        }
    }

}

@Composable
fun SmallCalendarDay(
    date: LocalDate,
    weekDay: DayOfWeek,
    isVisible: Boolean,
    calendarDaysBackgroundColorType: CalendarDaysBackgroundColorType?,
    modifier: Modifier = Modifier
){
    val size = 30
    if(isVisible){
        RoundDayChips(
            day = date.dayOfMonth,
            calendarDaysBackgroundColorType = calendarDaysBackgroundColorType ?: CalendarDaysBackgroundColorType.NoTasksOnThatDay,
            textStyle = MaterialTheme.typography.bodyMedium,
            size = size,
            modifier = modifier
        )
    }else{
        Spacer(modifier = Modifier.size(size.dp))
    }
}

@Preview
@Composable
fun RoundDayChipsPreview(){
    RoundDayChips(
        day = 10,
        calendarDaysBackgroundColorType = CalendarDaysBackgroundColorType.AllTasksCompleted,
        textStyle = MaterialTheme.typography.bodyMedium,
        size = 50
    )
}
