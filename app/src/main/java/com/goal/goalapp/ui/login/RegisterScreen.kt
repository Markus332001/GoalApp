package com.goal.goalapp.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goal.goalapp.R
import com.goal.goalapp.ui.components.EmailInput
import com.goal.goalapp.ui.components.PasswordInput
import com.goal.goalapp.ui.components.emailRegex
import com.goal.goalapp.ui.AppViewModelProvider


@Composable
fun RegisterScreen(
    toLoginScreen: () -> Unit,
    registerViewModel: RegisterViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
){

    val registerState by registerViewModel.registerState.collectAsState()

    if(registerState is RegisterState.Success){
        toLoginScreen()
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(25.dp)
    ){
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            RegisterBody(
                onRegisterClick = { username, email, password ->
                    registerViewModel.register(username, email, password)
                },
                registerState = registerState
            )

            Row(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    text = stringResource(R.string.has_account)
                )
                Text(
                    text = stringResource(R.string.login_you),
                    color = colorResource(R.color.today),
                    modifier = Modifier
                        .padding(start = 3.dp)
                        .clickable {
                            toLoginScreen()
                        }
                )
            }
        }
    }
}

@Composable
private fun RegisterBody(
    onRegisterClick: (String, String, String) -> Unit,
    registerState: RegisterState,
    modifier: Modifier = Modifier
){
    val maxLength = 20
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var isFormValid by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
    ){
        Text(
            text = stringResource(R.string.register),
            style = MaterialTheme.typography.headlineLarge,
            color = colorResource(R.color.primary),
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(
            text = stringResource(R.string.register_text),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 15.dp)
        )
        OutlinedTextField(
            value = username,
            onValueChange = {
                if (it.length <= maxLength) {
                    username = it
                    isFormValid = emailRegex.matches(email) && password.isNotEmpty() && it.isNotEmpty()
                }
            },
            label = { Text( stringResource(R.string.username) ) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = modifier.fillMaxWidth(),
            singleLine = true,
        )
        EmailInput(email, onEmailChange = {
            email = it
            isFormValid = emailRegex.matches(it) && password.isNotEmpty() && username.isNotEmpty()
        })
        PasswordInput(password, onPasswordChange = {
            password = it
            isFormValid = emailRegex.matches(email) && it.isNotEmpty() && username.isNotEmpty()
        })
        if(registerState is RegisterState.Error){
            Text(
                text = registerState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }
        Button(
            onClick = { onRegisterClick(username, email, password) },
            modifier = modifier.fillMaxWidth().padding(top = 15.dp).height(50.dp),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.primary),
                contentColor = colorResource(R.color.button_font_light)
            )
        ){
            Text(stringResource(R.string.register))
        }
    }
}


