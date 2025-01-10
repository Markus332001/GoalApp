package com.goal.goalapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.goal.goalapp.R

@Composable
fun ProgressBar(progress: Float, modifier: Modifier = Modifier) {
    Box(
    modifier = modifier
        .fillMaxSize(),
    contentAlignment = Alignment.Center
) {

        val primaryColor = colorResource(R.color.primary)
    // Background
    Canvas(modifier = Modifier.fillMaxSize()) {
        // draw background
        drawRoundRect(
            color = Color.LightGray, // Backgroundcolor
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx())
        )
        // draw Stroke
        drawRoundRect(
            color = Color.Black, // stroke color
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }

    // Progressbar
        Box(
            modifier = Modifier
            .fillMaxWidth(fraction = progress) // fills the progress of the progressbar
            .fillMaxHeight()
            .padding(3.dp)
            .align(Alignment.TopStart)){

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawRoundRect(
                    color = primaryColor,
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx())
                )

            }

            // progress text
            if(progress >= 0.2f){
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 15.dp)
                )
            }
        }

        // progress text
        if(progress < 0.2f){
            Text(
                text = "${(progress * 100).toInt()}%",
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }

    }
}

@Composable
fun ScrollableTextField(
    height: Int,
    text: String
) {

    val scrollState = rememberScrollState()

    // Box with the scrollable text
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .height(height.dp)
            .verticalScroll(scrollState)
            .background(colorResource(R.color.cardsBackground))
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }

}

@Preview
@Composable
fun ProgressBarPreview() {
    Box (modifier = Modifier.fillMaxWidth().height(60.dp).padding(10.dp)){
        ProgressBar(progress = 0.7f)
    }
}
