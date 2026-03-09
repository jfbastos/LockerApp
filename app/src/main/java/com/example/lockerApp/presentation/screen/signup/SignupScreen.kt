package com.example.lockerApp.presentation.screen.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lockerApp.R
import com.example.lockerApp.data.model.User
import com.example.lockerApp.presentation.theme.Locker_appTheme
import com.example.lockerApp.presentation.viewmodel.SignupViewModel
import com.example.lockerApp.utils.Constants
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignupScreenNavigation(navController: NavHostController, viewModel: SignupViewModel = koinViewModel()) {
    SignupScreen(viewModel)

    LaunchedEffect(Unit) {
        viewModel.onSignupSuccess.collect { email ->
            navController.navigate("${Constants.SIGNIN_SCREEN_DESTINATION}?userEmail=$email"){
                popUpTo(Constants.SIGNUP_SCREEN_DESTINATION) { inclusive = true }
            }
        }
    }
}

@Composable
fun SignupScreen(viewModel: SignupViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onError.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Box(Modifier.fillMaxSize()){
        UserFormScreen(viewModel::onIntent)

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
        )
    }
}

@Composable
fun UserFormScreen(onIntent : (SignupIntents) -> Unit){
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }

    Column(Modifier
        .background(Color.White)
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
        .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Livvi", fontSize = 36.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = Color(red = 58, green = 121, blue = 170), modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.size(24.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { newValue ->
                firstName = newValue.trim()
            },
            label = { Text("Primeiro nome") },
            isError = errorMessage != null,
            supportingText = {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { newValue ->
                lastName = newValue.trim()
            },
            label = { Text("Último nome") },
            isError = errorMessage != null,
            supportingText = {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { newValue ->
                email = newValue.trim()
            },
            label = { Text("E-mail") },
            isError = errorMessage != null,
            supportingText = {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it.trim() },
            label = { Text("Senha") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (showPassword)
                    painterResource(R.drawable.visibility_on_24)
                else
                    painterResource(R.drawable.visibility_off_24)

                val description = if (showPassword) "Mostrar senha" else "Show password"

                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(painter = image, contentDescription = description)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.size(21.dp))

        Button(onClick = { onIntent(SignupIntents.OnSubmit(
            User(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            )
        ))}){
            Text("Sign-Up")
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Locker_appTheme() {
        UserFormScreen {  }
    }
}