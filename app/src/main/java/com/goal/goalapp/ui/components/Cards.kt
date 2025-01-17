package com.goal.goalapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
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
import com.goal.goalapp.R
import com.goal.goalapp.data.CompletionType
import com.goal.goalapp.data.Frequency
import com.goal.goalapp.data.post.PostWithDetails
import com.goal.goalapp.ui.helper.convertDateToStringFormat
import com.goal.goalapp.ui.helper.getFrequencyString
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun RoutineCard(
    title: String,
    frequency: Frequency?,
    progressConnected: Boolean,
    progress: Float = 0f,
    withProgressBar: Boolean,
    startDate: LocalDate?,
    daysOfWeek: List<DayOfWeek>? = null,
    intervalDays: Int? = null,
    endDate: LocalDate? = null,
    targetValue: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    Box(
        modifier = modifier
            .shadow(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.cardsBackground))
            .clickable { onClick() }
    ){
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
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
                    Text(
                        text = getFrequencyString(frequency = frequency, intervalDays = intervalDays, daysOfWeek = daysOfWeek),
                        style = MaterialTheme.typography.bodyMedium
                    )
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
                if(endDate != null || targetValue != null){
                    Text(
                        text = stringResource(R.string.end) + ": " +
                                if(endDate == null) stringResource(R.string.after) + " " + targetValue.toString()
                                        + " " + stringResource(R.string.time)  else
                                    convertDateToStringFormat(endDate),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            /**
             * Progressbar
             */
            if(withProgressBar){
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

@Composable
fun ChipWithRemove(
    text: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.cardsBackground))
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ){
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
            )

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.delete),
                modifier = Modifier
                    .size(25.dp)
                    .clickable { onRemove() }
            )

        }
    }

}

@Composable
fun PostCard(
    postWithDetails: PostWithDetails,
    currentUserId: Int,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    isPreview: Boolean,
    modifier: Modifier = Modifier
    ){
    Box(
        modifier = modifier
            .shadow(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ){
            Column(
                modifier =  Modifier
                    .background(colorResource(R.color.primary))
                    .padding(vertical = 10.dp, horizontal = 20.dp)
            ){
                Text(
                    text = postWithDetails.user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = postWithDetails.post.goalName,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )
            }
            VerticalDivider(
                color = Color.Black,
                thickness = 2.dp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            if(postWithDetails.post.progress != null){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ){
                    if(postWithDetails.post.completionType != null){
                        if(postWithDetails.post.completionType == CompletionType.ReachTargetValue
                            && postWithDetails.post.targetValue != null && postWithDetails.post.currentValue != null
                            && postWithDetails.post.unit != null){
                            Text(
                                text = postWithDetails.post.currentValue.toString() + "/" + postWithDetails.post.targetValue.toString()
                                        + " " + postWithDetails.post.unit,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }else {
                            Text(
                                text = if(postWithDetails.post.completionType == CompletionType.ConnectRoutine) stringResource(R.string.progress_connected)
                                else stringResource(R.string.complete_goal),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    ProgressBar(
                        progress = postWithDetails.post.progress,
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth().padding(bottom = 10.dp, top = 5.dp)
                    )
                }
            }
            for(routineSummary in postWithDetails.routineSummary){
                Column(
                    modifier.padding(10.dp)
                ){
                    Text(
                        text = routineSummary.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                    Text(
                        text = routineSummary.frequency,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    ProgressBar(
                        progress = routineSummary.progress,
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth().padding(bottom = 10.dp)
                    )
                }
            }

            HorizontalDivider(thickness = 2.dp, color = Color.Black)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ){
                IconButton(
                    enabled = !isPreview,
                    onClick = {
                        onLikeClick()
                    }
                ){
                    if(currentUserId in postWithDetails.post.likesUserIds){
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = stringResource(R.string.like),
                            modifier = Modifier.size(30.dp),
                            tint = Color.Red
                        )
                    }else{
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.like),
                            modifier = Modifier.size(30.dp),
                            tint = Color.Black
                        )
                    }
                }
                Text(
                    text = postWithDetails.post.likesUserIds.size.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                )
                IconButton(
                    enabled = !isPreview,
                    onClick = {
                        onCommentClick()
                    },
                    modifier = Modifier.padding(start = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = stringResource(R.string.comment),
                    )
                }
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
        startDate = LocalDate.now(),
        daysOfWeek = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
        intervalDays = 4,
        targetValue = 2,
        progress = 0.2f,
        onClick = {},
        withProgressBar = false
    )
}

@Preview
@Composable
fun ChipWithRemovePreview(){
    ChipWithRemove(
        text = "Test",
        onRemove = {}
    )
}

@Preview
@Composable
fun PostCardPreview(){
   /* PostCard(
        postWithDetails = PostWithDetails(
            post = Post(
                id = 1,
                userId = 1,
                groupId = 1,
                goalName = "Test",
                progress = 0.2f,
                completionType = CompletionType.ConnectRoutine,
                targetValue = null,
                currentValue = null,
                unit = null,
                likesUserIds = listOf()
            ),
            comments = listOf(
                Comment(
                    id = 1,
                    postId = 1,
                    text = "Test",
                    parentCommentId = 1,
                ),
                Comment(
                    id = 1,
                    postId = 1,
                    text = "Test",
                    parentCommentId = 1,
                ),
            ),
            routineSummary = listOf(
                RoutineSummary(
                    id = 1,
                    postId = 1,
                    title = "Test",
                    frequency = "Test",
                    progress = 0.2f
                )
            ),
            user = User(
                id = 1,
                username = "Test",
                email = "Test",
                passwordHash = "Test"
            )
        ),
        currentUserId = 1,
        onLikeClick = { },
        onCommentClick = {},
        isPreview = true
    )
*/
}