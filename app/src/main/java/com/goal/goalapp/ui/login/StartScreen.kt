package com.goal.goalapp.ui.login

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.ui.AppViewModelProvider
import com.goal.goalapp.ui.NavigationScreens

@Composable
fun StartScreen(
    selectedScreen: NavigationScreens,
    toLoginScreen: () -> Unit,
    toGoalOverview: () -> Unit,
    loginViewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
){
    // Load the login status when the screen is first displayed
    LaunchedEffect(true) {
        loginViewModel.loadLoginStatus()
    }

    LaunchedEffect(loginViewModel) {
        loginViewModel.navigationEvent.collect{ isLoggedOut ->
            if(isLoggedOut){
                toGoalOverview()
            }else{
                toLoginScreen()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ){
        Text(
            text = "StartScreen",
            color = colorResource(R.color.primary)
        )
    }
}