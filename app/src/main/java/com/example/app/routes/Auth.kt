package com.example.app.routes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.R
import com.example.app.ui.theme.GreenColor
import com.example.app.viewmodel.LoginViewModel
import com.example.app.models.login.LoginResponse
import com.example.app.models.signup.RegisterRequest
import com.example.app.models.signup.RegisterResponse
import com.example.app.viewmodel.SignupViewModel

@Composable
fun AuthScreen(
    onLoginSuccess: (LoginResponse) -> Unit,
    onSignupSuccess: (RegisterResponse) -> Unit
) {
    var isLoginScreen by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.farmhub_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoginScreen) {
                LoginForm(
                    onSwitchToSignup = { isLoginScreen = false },
                    onLoginSuccess = onLoginSuccess
                )
            } else {
                SignupForm(
                    onSwitchToLogin = { isLoginScreen = true },
                    onSignupSuccess = onSignupSuccess
                )
            }
        }
    }
}


@Composable
fun LoginForm(
    onSwitchToSignup: () -> Unit,
    onLoginSuccess: (LoginResponse) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisibility by remember { mutableStateOf(false) }

    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.loginError
    val loginResult = viewModel.loginResult

    // Success effect
    LaunchedEffect(loginResult) {
        loginResult?.let {
            onLoginSuccess(it)
            viewModel.clearState()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign in",
            style = MaterialTheme.typography.titleLarge,
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone (e.g. 0712345678)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisibility) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisibility = !passwordVisibility },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(32.dp)
            )
        }

        Button(
            onClick = {
                if (phone.text.isBlank() || password.text.isBlank()) {
                    viewModel.loginError = "Please enter both phone and password."
                } else {
                    viewModel.login(phone.text, password.text)
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(contentColor = Color.White)
        ) {
            Text("Sign in", color = Color.White)
        }

        Text(
            text = "Forgot password?",
            color = GreenColor,
            modifier = Modifier.clickable { /* Handle forgot password */ }
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.Gray.copy(alpha = 0.3f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("New here? ")
            Text(
                text = "Join now",
                color = GreenColor,
                modifier = Modifier.clickable(onClick = onSwitchToSignup)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupForm(
    onSwitchToLogin: () -> Unit,
    onSignupSuccess: (RegisterResponse) -> Unit,
    viewModel: SignupViewModel = viewModel()
) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var county by remember { mutableStateOf("") }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    var fullName by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.signupError
    val signupResult = viewModel.signupResult

    // Success effect
    LaunchedEffect(signupResult) {
        signupResult?.let {
            onSignupSuccess(it)
            viewModel.clearState()
        }
    }

    val kenyanCounties = listOf(
        "Mombasa", "Kwale", "Kilifi", "Tana River", "Lamu",
        "Taita Taveta", "Garissa", "Wajir", "Mandera",
        "Marsabit", "Isiolo", "Meru", "Tharaka-Nithi",
        "Embu", "Kitui", "Machakos", "Makueni",
        "Nyandarua", "Nyeri", "Kirinyaga", "Murang'a",
        "Kiambu", "Turkana", "West Pokot", "Samburu",
        "Trans Nzoia", "Uasin Gishu", "Elgeyo-Marakwet",
        "Nandi", "Baringo", "Laikipia", "Nakuru",
        "Narok", "Kajiado", "Kericho", "Bomet",
        "Kakamega", "Vihiga", "Bungoma", "Busia",
        "Siaya", "Kisumu", "Homa Bay", "Migori",
        "Kisii", "Nyamira", "Nairobi City"
    ).sorted()

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Join Platform",
            style = MaterialTheme.typography.titleLarge
        )

        fun compactTextField() = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = compactTextField()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = compactTextField()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { newValue ->
                if (newValue.text.isBlank() || newValue.text.matches(Regex("^\\d+\$"))) {
                    phone = newValue
                }
            },
            label = { Text("Phone") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = compactTextField()
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = county,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select County") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .padding(vertical = 4.dp),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.heightIn(max = 200.dp)
            ) {
                kenyanCounties.forEach { countyName ->
                    DropdownMenuItem(
                        text = { Text(countyName) },
                        onClick = {
                            county = countyName
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisibility) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = compactTextField(),
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisibility = !passwordVisibility },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            }
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = compactTextField(),
            trailingIcon = {
                IconButton(
                    onClick = { confirmPasswordVisibility = !confirmPasswordVisibility },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (confirmPasswordVisibility) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            }
        )

        Text(
            text = "By joining you agree to our Terms and Privacy Policy",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(32.dp)
            )
        }

        Button(
            onClick = {
                if (
                    fullName.text.isBlank() ||
                    email.text.isBlank() ||
                    phone.text.isBlank() ||
                    county.isBlank() ||
                    password.text.isBlank() ||
                    confirmPassword.text.isBlank()
                ) {
                    viewModel.signupError = "Please fill in all fields."
                } else if (password.text != confirmPassword.text) {
                    viewModel.signupError = "Passwords do not match."
                } else {
                    val req = RegisterRequest(
                        fullName = fullName.text,
                        email = email.text,
                        phone = phone.text,
                        county = county,
                        password = password.text
                    )
                    viewModel.signup(req)
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreenColor,
                contentColor = Color.White
            )
        ) {
            Text("Agree & Join")
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.Gray.copy(alpha = 0.3f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Have an account? ", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Sign in",
                color = GreenColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable(onClick = onSwitchToLogin)
            )
        }
    }
}