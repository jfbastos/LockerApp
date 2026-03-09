package com.example.lockerApp.presentation.screen.signin

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.lockerApp.presentation.theme.Locker_appTheme
import com.example.lockerApp.presentation.viewmodel.SigninViewModel
import com.example.lockerApp.utils.Constants
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SigninScreenNavigation(userEmail : String, navController: NavHostController, viewModel: SigninViewModel = koinViewModel()){

    SigninScreen(userEmail, viewModel)

    LaunchedEffect(Unit) {
        viewModel.onNavigateEvent.collect { navigationEvent ->
            Log.d("DEBUG", "Navigate to $navigationEvent")
            when(navigationEvent){
                NavigationSigninEvents.GoToHomeScreen -> { navController.navigate(Constants.HOME_SCREEN_DESTINATION){
                    popUpTo(Constants.SIGNIN_SCREEN_DESTINATION) { inclusive = true }
                }}
                NavigationSigninEvents.GoToSignUpScreen -> { navController.navigate(Constants.SIGNUP_SCREEN_DESTINATION) }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onSigninSuccess.collect {
            Log.d("DEBUG", "Go to homeScreen")
            viewModel.onIntent(SigninIntents.OnNavigateToHomeScreen)
        }
    }
}

@Composable
fun SigninScreen(userEmail: String, viewModel: SigninViewModel){
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onError.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Box(Modifier.fillMaxSize()){
        SigninFormScreen(userEmail, viewModel::onIntent)

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
        )
    }
}

@Composable
fun SigninFormScreen(userEmail: String, onIntent : (SigninIntents) -> Unit){

    var email by rememberSaveable { mutableStateOf(userEmail) }
    var password by rememberSaveable { mutableStateOf("") }
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
            value = email,
            onValueChange = { newValue ->
                email = newValue.trim()
            },
            label = { Text("E-mail") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it.trim() },
            label = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (showPassword)
                    painterResource(R.drawable.visibility_on_24)
                else
                    painterResource(R.drawable.visibility_off_24)

                val description = if (showPassword) "Hide password" else "Show password"

                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(painter = image, contentDescription = description)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.size(21.dp))

        Button(onClick = {onIntent(SigninIntents.OnSubmitSignIn(email, password))}){
            Text("Sign-in")
        }

        Spacer(Modifier.size(21.dp))

        Text("Don't have account? Click here",
            fontSize = 12.sp,
            modifier =
                Modifier.clickable(true){
                    onIntent(SigninIntents.OnNavigateToSignUp)
                }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Locker_appTheme() {
        SigninFormScreen("joao@email.com"){

        }
    }
}