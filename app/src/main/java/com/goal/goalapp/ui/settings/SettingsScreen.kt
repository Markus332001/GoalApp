package com.goal.goalapp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.data.user.User
import com.goal.goalapp.ui.AppViewModelProvider

@Composable
fun SettingsScreen(
    toLoginScreen: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
){
    val user = settingsViewModel.user.collectAsState()

    if(user.value != null){
        SettingsBody(
            user = user.value!!,
            logout = {
                settingsViewModel.logout()
                toLoginScreen()
            },
            modifier = modifier
        )
    }
}

@Composable
fun SettingsBody(
    user: User,
    logout: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ){
        Text(
            text = user.username,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 20.dp)
        )
        Text(
            text = user.email,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = logout,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.negative),
                contentColor = colorResource(R.color.white)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp
            ),
            modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth()
        ){
            Text(
                text = stringResource(R.string.logout),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}