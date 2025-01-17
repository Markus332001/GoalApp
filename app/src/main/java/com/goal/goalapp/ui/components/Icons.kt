package com.goal.goalapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.goal.goalapp.R

@Composable
fun BackArrow(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
){
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = stringResource(R.string.back_arrow),
        modifier = modifier
            .clickable {
                navigateBack()
            }
            .size(30.dp)
    )
}