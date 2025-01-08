package com.goal.goalapp.ui.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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

    val isLoggedIn by loginViewModel.isLoggedIn.observeAsState()
    val sessionExpiry by loginViewModel.sessionExpiry.observeAsState()

    LaunchedEffect(isLoggedIn, sessionExpiry) {
        if(isLoggedIn != null && sessionExpiry != null){
            if(loginViewModel.isLoggedIn()){
                toGoalOverview()
            }else{
                toLoginScreen()
            }
        }

    }

    StartScreenBody()

}

@Composable
fun StartScreenBody(
    modifier: Modifier = Modifier
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.start_screen))

    ){
        Image(
            painter = painterResource(id = R.drawable.start_screen),
            contentDescription = stringResource(R.string.start_screen),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun StartScreenPreview() {
    StartScreenBody()
}