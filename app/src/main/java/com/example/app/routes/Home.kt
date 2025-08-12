package com.example.app.routes

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.app.R
import com.example.app.features.AppHeader
import com.example.app.features.BottomNavBar
import com.example.app.tabs.ChatScreen
import com.example.app.tabs.HelpScreen
import com.example.app.tabs.ProfileScreen
import com.example.app.tabs.VideoScreen

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            AppHeader(
                onChatClick = { selectedTabIndex = 4 },
                onSearchClick = onSearchClick
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index -> selectedTabIndex = index }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (selectedTabIndex) {
                0 -> HomeContent()
                1 -> HelpScreen()
                2 -> VideoScreen()
                3 -> ProfileScreen()
                4 -> ChatScreen()

            }
        }
    }
}

@Composable
fun HomeContent() {
    var currentStep by remember { mutableIntStateOf(1) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var showConfirmation by remember { mutableStateOf(false) }
    var selectedImageRes by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        StepIndicator(currentStep = currentStep, totalSteps = 3)

        Spacer(Modifier.height(16.dp))

        AnimatedContent(targetState = currentStep, label = "Step Animation") { step ->
            when (step) {
                1 -> UploadStep(
                    onNext = {
                        selectedImageRes = R.drawable.ic_launcher_foreground // Placeholder
                        currentStep = 2
                    }
                )
                2 -> DescribeStep(
                    description = description,
                    selectedImageRes = selectedImageRes,
                    onDescriptionChange = { description = it },
                    onSubmit = { showConfirmation = true },
                    onBack = { currentStep = 1 }
                )
                3 -> SuccessStep(onHome = {
                    currentStep = 1
                    description = TextFieldValue("")
                    selectedImageRes = null
                })
            }
        }

        if (showConfirmation) {
            ConfirmationDialog(
                onConfirm = {
                    showConfirmation = false
                    currentStep = 3
                },
                onDismiss = { showConfirmation = false }
            )
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalSteps) { index ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .padding(4.dp)
                    .background(
                        if (index + 1 <= currentStep) MaterialTheme.colorScheme.primary
                        else Color.Gray,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun UploadStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "What Challenges are you facing on your Farm?",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Take a photo or upload a video of the issue, and our certified officers will help.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = "Take Photo")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Take a Photo")
            }

            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Image, contentDescription = "Upload from Gallery")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Upload from Gallery")
            }
        }
    }
}

@Composable
fun DescribeStep(
    description: TextFieldValue,
    selectedImageRes: Int?,
    onDescriptionChange: (TextFieldValue) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Explain The Video or Photo",
            style = MaterialTheme.typography.headlineSmall
        )

        selectedImageRes?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "Preview",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
        }

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = { Text("Describe your issue...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
            Button(
                onClick = onSubmit,
                modifier = Modifier.weight(1f),
                enabled = description.text.isNotBlank()
            ) {
                Icon(Icons.Default.Check, contentDescription = "Submit")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit")
            }
        }
    }
}

@Composable
fun ConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Is this Information correct?") },
        text = { Text("Are you sure you want to submit?") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Yes") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("No") }
        }
    )
}

@Composable
fun SuccessStep(onHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Success",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Submission Successful!",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your farm issue has been sent to a certified extension officer. Expect feedback soon.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onHome) {
            Text("Go Back Home")
        }
    }
}
