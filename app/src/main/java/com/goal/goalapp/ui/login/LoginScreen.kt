package com.goal.goalapp.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.ui.AppViewModelProvider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goal.goalapp.ui.NavigationScreens
import androidx.compose.foundation.layout.*
import com.goal.goalapp.ui.components.EmailInput
import com.goal.goalapp.ui.components.PasswordInput
import com.goal.goalapp.ui.components.emailRegex


@Composable
fun LoginScreen(
    toRegisterScreen: () -> Unit,
    toHomeScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
){

    val loginState = viewModel.loginState.collectAsState()

    Box(
        modifier = modifier.fillMaxSize().padding(25.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {

            LoginBody(
                onLoginClick = { email, password ->
                    viewModel.login(email, password)
                },
                loginState = loginState.value
            )
            Row(modifier = Modifier.padding(top = 20.dp)){
                Text(
                    text = stringResource(R.string.no_account)

                )
                Text(
                    text = stringResource(R.string.register),
                    color = colorResource(R.color.today),
                    modifier = Modifier
                        .padding(start = 3.dp)
                        .clickable {
                            toRegisterScreen()
                        }
                )
            }


        }


        if (loginState.value is LoginState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }else if(loginState.value is LoginState.Success){
            toHomeScreen()
        }
    }

}

@Composable
private fun LoginBody(
    onLoginClick: (String, String) -> Unit,
    loginState: LoginState,
    modifier: Modifier = Modifier
){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isFormValid by remember { mutableStateOf(false) }

    // Loading circle when the login process is loading


    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
    )
    {
        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.headlineLarge,
            color = colorResource(R.color.primary),
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(
            text = stringResource(R.string.login_text),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 15.dp)
        )
        EmailInput(email, onEmailChange = {
            email = it
            isFormValid = emailRegex.matches(it) && password.isNotEmpty()
        })
        PasswordInput(password, onPasswordChange = {
            password = it
            isFormValid = emailRegex.matches(email) && it.isNotEmpty()
        })
        if (loginState is LoginState.Error) {
            Text(
                text = loginState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }
        Button(
            onClick = { onLoginClick(email, password) },
            modifier = modifier.fillMaxWidth().padding(top = 15.dp).height(50.dp),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.primary),
                contentColor = colorResource(R.color.button_font_light)
            )
        ) {
            Text(stringResource(R.string.login))
        }
        // Lade-Kreisel

    }

}


@Preview
@Composable
fun LoginScreenPreview(){
    LoginScreen({}, {})
}
