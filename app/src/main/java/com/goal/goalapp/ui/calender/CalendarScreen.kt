package com.goal.goalapp.ui.calender

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.data.goal.RoutineCalendarDays
import com.goal.goalapp.data.goal.RoutineWithCalendarDays
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.components.ProgressBar
import com.goal.goalapp.ui.helper.CalendarDaysBackgroundColorType
import com.goal.goalapp.ui.helper.CalendarDisplay
import com.goal.goalapp.ui.helper.ColorScheme
import com.goal.goalapp.ui.helper.RoutineCalendarDayWithTitle
import com.goal.goalapp.ui.helper.getColorFromCalendarDaysBackgroundColorType
import com.goal.goalapp.ui.helper.getDayShortForm
import com.goal.goalapp.ui.helper.getFirstDayOfWeek
import com.goal.goalapp.ui.helper.getMonthString
import java.time.LocalDate

@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
){
    val initalDay = remember { mutableStateOf(LocalDate.now()) }
    val dayWithCalendarDays by calendarViewModel.dayWithRoutineCalendarDays.collectAsState()
    val changeNowToSpecificDay = remember { mutableStateOf(false) }
    val currentVisibleDay = remember { mutableStateOf(LocalDate.now()) }
    val currentVisibleDayTasks = remember { mutableStateOf<CalendarDisplay?>(null) }
    val weekView = remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    LaunchedEffect(dayWithCalendarDays){
        if(dayWithCalendarDays.isNotEmpty()){
            currentVisibleDayTasks.value = calendarViewModel.getCurrentVisibleDay(currentVisibleDayTasks.value?.date)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(top = 16.dp)
            .scrollable(
                state = scrollState,
                orientation = Orientation.Vertical
            )
    )
    {

        /**
         * Headline
         */
        Text(
            text = getMonthString(getFirstDayOfWeek(currentVisibleDay.value).monthValue) + " " +
                    currentVisibleDay.value.year,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
        ){
            /**
             * Change Week Month View Button
             */
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .background(Color.White)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        weekView.value = !weekView.value
                        changeNowToSpecificDay.value = true
                    }
            ){
                Text(
                    text = if(weekView.value) "Mo" else "Wo",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            /**
             * Today Button
             */
            val colorSchemeToday = getColorFromCalendarDaysBackgroundColorType(CalendarDaysBackgroundColorType.Today)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(40.dp)
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .background(colorSchemeToday.backgroundColor)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        changeNowToSpecificDay.value = true
                        currentVisibleDay.value = LocalDate.now()
                    }
            ){
                Text(
                    text = LocalDate.now().dayOfMonth.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorSchemeToday.fontColor
                )
            }
        }

        if(weekView.value){
            /**
             * The Week View
             */
            WeekPager(
                generateWeekDays = { calendarViewModel.generateWeekDays(it, dayWithCalendarDays) },
                onClickDay = {
                    currentVisibleDayTasks.value = calendarViewModel.getCurrentVisibleDay(it.date)
                },
                dayWithCalendarDays = dayWithCalendarDays,
                initalDay = initalDay.value,
                changeToSpecificDay = currentVisibleDay.value,
                calculateWeekPage = { date, startPage -> calendarViewModel.calculateWeekPage(date, startPage) },
                onPageChanged = {
                    currentVisibleDay.value = it
                },
                changeNowToSpecificDay = changeNowToSpecificDay.value,
                resetChangeNowToSpecificDay = { changeNowToSpecificDay.value = false },
                currentVisibleDayTask = currentVisibleDayTasks.value,
                modifier = Modifier
            )
        }else{
            /**
             * Month View
             */
            MonthPager(
                generateMonthStructure = { calendarViewModel.generateMonthDaysStructure(it, dayWithCalendarDays) },
                onClickDay = {
                    currentVisibleDayTasks.value = calendarViewModel.getCurrentVisibleDay(it.date)
                },
                dayWithCalendarDays = dayWithCalendarDays,
                initalDay = initalDay.value,
                resetChangeNowToSpecificDay = { changeNowToSpecificDay.value = false },
                changeNowToSpecificDay = changeNowToSpecificDay.value,
                changeToSpecificDay = currentVisibleDay.value,
                calculateMonthPage = { date, startPage -> calendarViewModel.calculateMonthPage(date, startPage) },
                modifier = Modifier,
                currentVisibleDayTask = currentVisibleDayTasks.value,
                onPageChanged = {
                    currentVisibleDay.value = it
                },
            )
        }


        if(currentVisibleDayTasks.value != null){
            /**
             * Progressbar with completed tasks count
             */
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 30.dp, bottom = 10.dp)
            ){
                val completedTasks = currentVisibleDayTasks.value!!.routineCalendarDays.count{it.routineCalendarDays.isCompleted}
                val totalTasks = currentVisibleDayTasks.value!!.routineCalendarDays.size

                ProgressBar(
                    progress = if(totalTasks == 0) 0f else completedTasks.toFloat() / totalTasks.toFloat(),
                    modifier = Modifier.fillMaxWidth().padding(end = 10.dp).height(40.dp).weight(1f)
                )

                Text(
                    text = "$completedTasks/$totalTasks",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(0.2f)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 10.dp)
                    .fillMaxWidth()
            ){
                for(routineCalendarDay in currentVisibleDayTasks.value!!.routineCalendarDays){
                    Task(
                        routineCalendarDay = routineCalendarDay,
                        onClick = {
                            calendarViewModel.checkRoutineCalendarDay(it.routineCalendarDays)
                        }
                    )
                }
            }
        }

    }

}


@Composable
fun WeekPager(
    generateWeekDays: (LocalDate) -> List<CalendarDisplay>,
    onClickDay: (CalendarDisplay) -> Unit,
    dayWithCalendarDays: List<CalendarDisplay>,
    initalDay: LocalDate,
    onPageChanged: (LocalDate) -> Unit,
    changeToSpecificDay: LocalDate,
    changeNowToSpecificDay: Boolean,
    resetChangeNowToSpecificDay: () -> Unit,
    calculateWeekPage: (LocalDate, Int) -> Int,
    currentVisibleDayTask: CalendarDisplay?,
    modifier: Modifier = Modifier
) {
    val initialPage: Int = Int.MAX_VALUE / 2
    val maxPage: Int = Int.MAX_VALUE
    val pagerState = rememberPagerState(initialPage = initialPage) { maxPage }

    //change to specific view if the button is clicked
    LaunchedEffect(changeNowToSpecificDay) {
        if(changeNowToSpecificDay){
            val newPage = calculateWeekPage(changeToSpecificDay, initialPage)
            if(changeToSpecificDay == LocalDate.now()){
                pagerState.animateScrollToPage(newPage)
            }else{
                pagerState.scrollToPage(newPage)
            }
            resetChangeNowToSpecificDay()
        }


    }

    //observes if the current page changes and sends the current visible week to the parent composable
    LaunchedEffect(pagerState.currentPage) {
        // current visible page
        val currentPage = pagerState.currentPage
        val weekOffsetCurrent = currentPage - initialPage
        val week = LocalDate.now().plusWeeks(weekOffsetCurrent.toLong())

        onPageChanged(week)
    }
    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1,
        modifier = modifier
    ) { page ->
        val weekOffset = page - initialPage
        val week = initalDay.plusWeeks(weekOffset.toLong())

        if(dayWithCalendarDays.isEmpty()){
            // loading circle while the data is loading
            CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
        }else{
            val weekStructure = generateWeekDays(week)
            DayRow(
                week = weekStructure,
                isRowWithDayText = true,
                unvisiblePossible = false,
                onClickDay= onClickDay,
                currentVisibleDayTask = currentVisibleDayTask,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
        }
    }
}

@Composable
fun MonthPager(
    generateMonthStructure: (LocalDate) -> List<List<CalendarDisplay>>,
    onClickDay: (CalendarDisplay) -> Unit,
    dayWithCalendarDays: List<CalendarDisplay>,
    initalDay: LocalDate,
    onPageChanged: (LocalDate) -> Unit,
    changeToSpecificDay: LocalDate,
    changeNowToSpecificDay: Boolean,
    resetChangeNowToSpecificDay: () -> Unit,
    calculateMonthPage: (LocalDate, Int) -> Int,
    currentVisibleDayTask: CalendarDisplay?,
    modifier: Modifier = Modifier
) {
    val initialPage: Int = Int.MAX_VALUE / 2
    val maxPage: Int = Int.MAX_VALUE
    val pagerState = rememberPagerState(initialPage = initialPage) { maxPage }

    //change to today view if the button is clicked
    LaunchedEffect(changeNowToSpecificDay) {
        if(changeNowToSpecificDay){
            val newPage = calculateMonthPage(changeToSpecificDay, initialPage)
            if(changeToSpecificDay == LocalDate.now()){
                pagerState.animateScrollToPage(newPage)
            }else{
                pagerState.scrollToPage(newPage)
            }
            resetChangeNowToSpecificDay()
        }
    }

    //observes if the current page changes and sends the current visible month to the parent composable
    LaunchedEffect(pagerState.currentPage) {
        // current visible page
        val currentPage = pagerState.currentPage
        val monthOffsetCurrent = currentPage - initialPage
        val month = LocalDate.now().plusMonths(monthOffsetCurrent.toLong())

        onPageChanged(month)
    }


    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1,
        modifier = modifier
    ) { page ->
        val monthOffset = page - initialPage
        val month = initalDay.plusMonths(monthOffset.toLong())

        if(dayWithCalendarDays.isEmpty()){
            // loading circle while the data is loading
            CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
        }else {
            val monthStructure = generateMonthStructure(month)
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ){
                for ((index, weekStructure) in monthStructure.withIndex()) {
                    DayRow(
                        week = weekStructure,
                        isRowWithDayText = index == 0,
                        unvisiblePossible = true,
                        onClickDay = onClickDay,
                        currentVisibleDayTask = currentVisibleDayTask,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Task(
    onClick: (RoutineCalendarDayWithTitle) -> Unit,
    routineCalendarDay: RoutineCalendarDayWithTitle,
    modifier: Modifier = Modifier
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick(routineCalendarDay)
            }
            .background(
                color = colorResource(R.color.cardsBackground),
                shape = RoundedCornerShape(10.dp)
            )
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(start = 20.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
                .size(40.dp)
        ){

            Canvas(modifier = Modifier.fillMaxSize()){
                // draw circle
                drawCircle(
                    color = Color.Black,
                    radius = size.minDimension / 2, // size of circle
                    style = Stroke(width = 2.dp.toPx())
                )
                if(routineCalendarDay.routineCalendarDays.isCompleted){
                    //draw check
                    drawLine(
                        color = Color.Black,
                        start = Offset(size.width * 0.8f, size.height * 0.8f),
                        end = Offset(size.width * 0.2f, size.height * 0.2f),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = Color.Black,
                        start = Offset(size.width * 0.8f, size.height * 0.2f),
                        end = Offset(size.width * 0.2f, size.height * 0.8f),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }
        Text(
            text = routineCalendarDay.title,
            style = MaterialTheme.typography.headlineSmall.copy(
                textDecoration = if(routineCalendarDay.routineCalendarDays.isCompleted)
                    TextDecoration.LineThrough else
                    TextDecoration.None
            ),
            maxLines = 1,
            modifier = Modifier.padding(start = 10.dp)
        )

    }
}

@Composable
fun DayRow(
    week: List<CalendarDisplay>,
    isRowWithDayText: Boolean,
    unvisiblePossible: Boolean,
    onClickDay: (CalendarDisplay) -> Unit,
    currentVisibleDayTask: CalendarDisplay?,
    modifier: Modifier = Modifier
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ){
        if(isRowWithDayText){
            for(day in week){
                DayWithWeekdayText(
                    day = day,
                    onClick = onClickDay,
                    calendarDaysBackgroundColorType = day.colorDaysBackgroundColorType ?: CalendarDaysBackgroundColorType.NoTasksOnThatDay,
                    tasksCompletedPercentage = day.greenPercentage?: 0f,
                    currentVisibleDayTask = currentVisibleDayTask,
                    isVisible = if(unvisiblePossible) day.isVisible else true,
                    size = 40
                )
            }
        }else{
            for(day in week){

                Day(
                    day = day,
                    calendarDaysBackgroundColorType = day.colorDaysBackgroundColorType ?: CalendarDaysBackgroundColorType.NoTasksOnThatDay,
                    tasksCompletedPercentage = day.greenPercentage?: 0f,
                    currentVisibleDayTask = currentVisibleDayTask,
                    size = 40,
                    isVisible = if(unvisiblePossible) day.isVisible else true,
                    onClickDay = onClickDay
                )
            }

        }
    }
}

@Composable
fun DayWithWeekdayText(
    day: CalendarDisplay,
    onClick:(CalendarDisplay) -> Unit,
    calendarDaysBackgroundColorType: CalendarDaysBackgroundColorType,
    tasksCompletedPercentage: Float,
    isVisible: Boolean,
    currentVisibleDayTask: CalendarDisplay?,
    size: Int,
    modifier: Modifier = Modifier
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        Text(
            text = getDayShortForm(day.date.dayOfWeek),
            style = MaterialTheme.typography.bodyMedium
        )
        Day(
            day = day,
            calendarDaysBackgroundColorType = calendarDaysBackgroundColorType,
            tasksCompletedPercentage = tasksCompletedPercentage,
            size = size,
            currentVisibleDayTask = currentVisibleDayTask,
            isVisible = isVisible,
            onClickDay = onClick,
            modifier = Modifier
                .padding(top = 5.dp)
        )
    }
}


@Composable
fun Day(
    day: CalendarDisplay,
    calendarDaysBackgroundColorType: CalendarDaysBackgroundColorType,
    size: Int,
    tasksCompletedPercentage: Float,
    isVisible: Boolean,
    onClickDay: (CalendarDisplay) -> Unit,
    currentVisibleDayTask: CalendarDisplay?,
    modifier: Modifier = Modifier
){
    if(isVisible){
        val colorScheme1 = getColorFromCalendarDaysBackgroundColorType(calendarDaysBackgroundColorType)
        var colorScheme2: ColorScheme = colorScheme1;
        //has to get a second for the not all tasks completed color
        if(calendarDaysBackgroundColorType == CalendarDaysBackgroundColorType.NotAllTasksCompleted){
            colorScheme2 = getColorFromCalendarDaysBackgroundColorType(CalendarDaysBackgroundColorType.AllTasksNotCompleted)
        }
        val tasksNotCompletedPercentage = 1 - tasksCompletedPercentage

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(size.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .clip(RoundedCornerShape(5.dp))
                .clickable {
                    onClickDay(day)
                }
                .border(
                    width = 2.dp,
                    color = if(day.date == currentVisibleDayTask?.date) Color.Black else Color.Transparent,
                    shape = RoundedCornerShape(5.dp)
                )

        ){
            Row(Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(
                            if (calendarDaysBackgroundColorType == CalendarDaysBackgroundColorType.NotAllTasksCompleted)
                                (if (tasksCompletedPercentage == 0f) 1f else tasksCompletedPercentage)
                            else 1f
                        )
                        .fillMaxHeight()
                        .background(colorScheme1.backgroundColor)
                )
                if(calendarDaysBackgroundColorType == CalendarDaysBackgroundColorType.NotAllTasksCompleted){
                    Box(
                        modifier = Modifier
                            .weight(tasksNotCompletedPercentage)
                            .fillMaxHeight()
                            .background(colorScheme2.backgroundColor)
                    )
                }
            }
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = if(currentVisibleDayTask?.date == day.date) FontWeight.Bold
                    else FontWeight.Normal
                ),
                color = colorScheme1.fontColor
            )
        }
    }else{
        Spacer(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(size.dp)
        )
    }
}


@Preview
@Composable
fun DayPreview(){
    Day(
        day = CalendarDisplay(
            date = LocalDate.now(),
            isVisible = true
        ),
        calendarDaysBackgroundColorType = CalendarDaysBackgroundColorType.NotAllTasksCompleted,
        currentVisibleDayTask = CalendarDisplay(
            date = LocalDate.now(),
            colorDaysBackgroundColorType = CalendarDaysBackgroundColorType.NotAllTasksCompleted,
            isVisible = true,
            greenPercentage = 0.7f
        ),
        tasksCompletedPercentage = 0.7f,
        size = 30,
        onClickDay = {},
        isVisible = true
    )
}

@Preview
@Composable
fun DayWithWeekdayPreview(){
    DayWithWeekdayText(
        day = CalendarDisplay(
            date = LocalDate.now(),
            isVisible = true
        ),
        onClick = {},
        calendarDaysBackgroundColorType = CalendarDaysBackgroundColorType.NotAllTasksCompleted,
        tasksCompletedPercentage = 0.7f,
        isVisible = true,
        size = 30,
        currentVisibleDayTask = CalendarDisplay(
            date = LocalDate.now(),
            colorDaysBackgroundColorType = CalendarDaysBackgroundColorType.NotAllTasksCompleted,
            isVisible = true,
            greenPercentage = 0.7f
        ),
    )
}

@Preview
@Composable
fun DayRowPreview(){
    val days = mutableListOf<CalendarDisplay>()
    for(i in 1..7){
        days.add(
            CalendarDisplay(
                date = LocalDate.now(),
                colorDaysBackgroundColorType = CalendarDaysBackgroundColorType.NotAllTasksCompleted,
                isVisible = true,
                greenPercentage = 0.7f
            )
        )
        }
    DayRow(
        week = days,
        isRowWithDayText = true,
        unvisiblePossible = false,
        currentVisibleDayTask = CalendarDisplay(
            date = LocalDate.now(),
            colorDaysBackgroundColorType = CalendarDaysBackgroundColorType.NotAllTasksCompleted,
            isVisible = true,
            greenPercentage = 0.7f
        ),
        modifier = Modifier,
        onClickDay = {}
    )
}