package com.goal.goalapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goal.goalapp.R
import com.goal.goalapp.data.DaysOfWeek
import com.goal.goalapp.data.Frequency
import com.goal.goalapp.ui.helper.getDaysOfWeekShort
import com.goal.goalapp.ui.helper.convertDateToStringFormat
import java.util.Date

@Composable
fun RoutineCard(
    title: String,
    frequency: Frequency?,
    progressConnected: Boolean,
    progress: Float = 0f,
    withProgressBar: Boolean,
    startDate: Date?,
    daysOfWeek: List<DaysOfWeek>? = null,
    intervalDays: Int? = null,
    endDate: Date? = null,
    endFrequency: Int? = null,
    modifier: Modifier = Modifier,
){
    Box(
        modifier = modifier
            .shadow(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.cardsBackground))
    ){
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            /**
             * Title and Progress Connected
             */
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            ){
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                if(progressConnected){
                    Text(
                        text = stringResource(R.string.progress_connected),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = colorResource(R.color.today)
                    )
                }
            }

            /**
             * Frequency
             */
            if(frequency != null) {
                Row(
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    when (frequency) {
                        Frequency.Daily -> {
                            Text(
                                text = stringResource(R.string.daily),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Frequency.Weekly -> {
                            Text(
                                text = stringResource(R.string.weekly) + ": " +
                                        getDaysOfWeekShort(daysOfWeek ?: emptyList())
                                            .joinToString(", ") { it },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Frequency.IntervalDays -> {
                            Text(
                                text = stringResource(R.string.every) + " " + intervalDays.toString() + " " + stringResource(
                                    R.string.time
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            /**
             * Start and End date
             */
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ){
                if(startDate != null) {
                    Text(
                        text = stringResource(R.string.start) + ": " + convertDateToStringFormat(startDate),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if(endDate != null || endFrequency != null){
                    Text(
                        text = stringResource(R.string.end) + ": " +
                                if(endDate == null) stringResource(R.string.after) + " " + endFrequency.toString()
                                        + " " + stringResource(R.string.time)  else
                                    convertDateToStringFormat(endDate),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            /**
             * Progressbar
             */
            if(withProgressBar && progress != null){
                ProgressBar(
                    progress = progress,
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 5.dp)
                )
            }
        }

    }
}



@Preview
@Composable
fun RoutineCardPreview(){
    RoutineCard(
        title = "Test",
        frequency = Frequency.Weekly,
        progressConnected = true,
        startDate = Date(),
        daysOfWeek = listOf(DaysOfWeek.Monday, DaysOfWeek.Wednesday, DaysOfWeek.Friday),
        intervalDays = 4,
        endFrequency = 2,
        progress = 0.2f,
        withProgressBar = false
    )
}