package com.goal.goalapp.ui.login

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goal.goalapp.ui.NavigationScreens


val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

@Composable
fun LoginScreen(
    selectedScreen: NavigationScreens,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
){

    val loginState = viewModel.loginState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize().padding(25.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            LoginBody(
                onLoginClick = { email, password ->
                    viewModel.login(email, password)
                }
                )
        }
    }

}

@Composable
private fun LoginBody(
    onLoginClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isFormValid by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.Start,
        modifier = Modifier)
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
        Button(
            onClick = {onLoginClick(email, password)},
            modifier = modifier.fillMaxWidth().padding(top = 15.dp).height(50.dp),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.primary),
                contentColor = colorResource(R.color.button_font_light)
            )
        ){
            Text(stringResource(R.string.login))
        }
    }
}

@Composable
fun PasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text( stringResource(R.string.password) )},
        placeholder = { Text("********" )},
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password, // suitable keyboard for password input
            imeAction = ImeAction.Done // done button on the keyboard
        ),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None
        else PasswordVisualTransformation(), // Disguises the password
        trailingIcon = {
            val icon = if (isPasswordVisible) Icons.Default.Visibility
            else Icons.Default.VisibilityOff

            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(imageVector = icon, contentDescription = null)
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun EmailInput(
    email: String,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isValid by remember { mutableStateOf(true) }

    OutlinedTextField(
        value = email,
        onValueChange = {
            onEmailChange(it)
            isValid = emailRegex.matches(it) },
        label = { Text( stringResource(R.string.email) )},
        placeholder = { Text("example@example.com") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email, // show the keyboard for email input
            imeAction = ImeAction.Next // enables to move to the next input field
        ),
        isError = !isValid,
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
    if (!isValid) {
        Text(
            text = stringResource(R.string.enter_valid_email),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Preview
@Composable
fun LoginScreenPreview(){
    //LoginScreen()
}
