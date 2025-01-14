package com.goal.goalapp.ui.components

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.goal.goalapp.R
import com.goal.goalapp.ui.helper.convertDateToStringFormat
import java.time.LocalDate


val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

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
        label = { Text( stringResource(R.string.password) ) },
        placeholder = { Text("********" ) },
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
        label = { Text( stringResource(R.string.email) ) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInput(
    date: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    label: String,
    color: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    iconColor: Color =  LocalContentColor.current,
    modifier: Modifier = Modifier
){
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // listen for changes of the selected date and convert it to LocalDate
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            // converts milliseconds to days for LocalDate
            onDateChange(LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000)))
        }
    }

        Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = if(date != null) convertDateToStringFormat(date) else "",
            onValueChange = { },
            label = { Text(text = label) },
            readOnly = true,
            colors = color,
            trailingIcon = {

                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date",
                        tint = iconColor
                    )
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        if (showDatePicker) {
            Box(
                modifier = Modifier
                    .offset(y = 64.dp)
                    .padding(30.dp)
            ){
                Popup(
                    onDismissRequest = { showDatePicker = false },

                    ) {
                    Box(
                        modifier = Modifier
                            .shadow(elevation = 4.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotesInput(
    noteText: String,
    onNoteChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = noteText,
        onValueChange = onNoteChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        maxLines = 10,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        singleLine = false,
        isError = false
    )
}

@Composable
fun SearchBar(
    searchInput: String,
    onSearchInputChanged: (String) -> Unit,
    label: String,
    height: Int = 64,
    modifier: Modifier = Modifier
){
    OutlinedTextField(
        value = searchInput,
        onValueChange = onSearchInputChanged,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = label
            )
        },
        trailingIcon = {
            if(searchInput.isNotEmpty()){
                IconButton(onClick = { onSearchInputChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = label
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .height(height.dp),
    )
}







@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DateInputPreview() {
    val date = remember { mutableStateOf(LocalDate.now()) }
    DateInput(
        date = date.value,
        onDateChange = { date.value = it },
        label = "Date of Birth"
    )
}

@Preview
@Composable
fun PasswordInputPreview() {
    val password = remember { mutableStateOf("") }
    PasswordInput(
        password = password.value,
        onPasswordChange = { password.value = it }
    )
}

@Preview
@Composable
fun EmailInputPreview() {
    val email = remember { mutableStateOf("") }
    EmailInput(email.value, { email.value = it })
}

@Preview
@Composable
fun NotesInputPreview() {
    val noteText = remember { mutableStateOf("") }
    NotesInput(
        noteText = noteText.value,
        onNoteChange = { noteText.value = it },
        label = "Notes"
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    val searchInput = remember { mutableStateOf("sadfs") }
    SearchBar(
        searchInput = searchInput.value,
        onSearchInputChanged = { searchInput.value = it },
        label = "Search"
    )
}


