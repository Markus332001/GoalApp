package com.goal.goalapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goal.goalapp.R
import java.util.Date

@Composable
fun SelectButton(
    title: String,
    onClick: () -> Unit,
    selected: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onClick,
        colors = ButtonColors(
            containerColor = if (selected) colorResource(R.color.primary) else colorResource(R.color.cardsBackground),
            contentColor = if (selected) colorResource(R.color.button_font_light) else Color.Black,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        modifier = modifier.fillMaxWidth()
    ){
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun AddComponentButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonColors(
            containerColor = colorResource(R.color.cardsBackground),
            contentColor = Color.Black,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Icon",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview
@Composable
fun SelectButtonsPreview(){
    val selected = remember { mutableStateOf(true) }
    SelectButton(title = "Test", onClick = {selected.value = !selected.value}, selected = selected.value)
}

@Preview
@Composable
fun AddComponentButtonPreview(){
    AddComponentButton(title = "Test", onClick = {})
}