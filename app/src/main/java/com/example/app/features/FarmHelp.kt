package com.example.app.features

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.viewmodel.FarmHelpViewModel

@Composable
fun FarmHelp(viewModel: FarmHelpViewModel, onClose: (() -> Unit)? = null) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Camera: State to hold the last photo Uri
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraImage by remember { mutableStateOf<Uri?>(null) }
    var showCameraPreview by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && cameraImageUri != null) {
            pendingCameraImage = cameraImageUri
            showCameraPreview = true // Show preview before proceeding
        } else {
            pendingCameraImage = null
            showCameraPreview = false
        }
    }

    // Photo Picker for Android 13+ (Tiramisu/API 33)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.setSelectedImageUri(uri)
        if (uri != null) viewModel.nextStep()
    }
    // Legacy Gallery Picker for pre-Android 13
    val legacyGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.setSelectedImageUri(uri)
        if (uri != null) viewModel.nextStep()
    }

    var requestPermissions by remember { mutableStateOf(false) }
    var showPermissionDenied by remember { mutableStateOf(false) }

    if (requestPermissions) {
        PermissionsHandler(
            onGranted = {
                val uri = createImageUri(context)
                cameraImageUri = uri
                if (uri != null) {
                    cameraLauncher.launch(uri)
                } else {
                    Toast.makeText(context, "Could not create image file", Toast.LENGTH_SHORT).show()
                }
                requestPermissions = false
            },
            onDenied = {
                showPermissionDenied = true
                requestPermissions = false
            }
        )
    }
    if (showPermissionDenied) {
        Toast.makeText(context, "Camera and storage permissions are required", Toast.LENGTH_LONG).show()
    }

    // Unified gallery launcher for all Android versions
    fun launchGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            legacyGalleryLauncher.launch("image/*")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepIndicator(currentStep = uiState.currentStep, totalSteps = 3)
            if (onClose != null) {
                TextButton(onClick = onClose) {
                    Text("Close")
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        when {
            // If a camera image is pending confirmation, show the preview step
            showCameraPreview && pendingCameraImage != null -> {
                CameraPreviewStep(
                    imageUri = pendingCameraImage!!,
                    onOkay = {
                        viewModel.setSelectedImageUri(pendingCameraImage)
                        viewModel.nextStep()
                        showCameraPreview = false
                        pendingCameraImage = null
                    },
                    onRetake = {
                        showCameraPreview = false
                        pendingCameraImage = null
                        // Re-launch camera:
                        val uri = createImageUri(context)
                        cameraImageUri = uri
                        if (uri != null) cameraLauncher.launch(uri)
                    }
                )
            }
            else -> {
                AnimatedContent(targetState = uiState.currentStep, label = "Step Animation") { step ->
                    when (step) {
                        1 -> UploadStep(
                            onGallery = { launchGallery() },
                            onCamera = { requestPermissions = true }
                        )
                        2 -> DescribeStep(
                            description = uiState.description,
                            selectedImageUri = uiState.selectedImageUri,
                            onDescriptionChange = { viewModel.setDescription(it) },
                            onSubmit = { viewModel.showConfirmationDialog(true) },
                            onBack = { viewModel.prevStep() }
                        )
                        3 -> SuccessStep(onHome = {
                            viewModel.reset()
                            onClose?.invoke()
                        })
                    }
                }
            }
        }

        if (uiState.showConfirmation) {
            ConfirmationDialog(
                onConfirm = { viewModel.submitPost(context) },
                onDismiss = { viewModel.showConfirmationDialog(false) }
            )
        }
        uiState.errorMessage?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(8.dp))
        }
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        }
    }
}

/**
 * Requests CAMERA and storage permissions at runtime.
 * Calls onGranted if all permissions are granted, or onDenied otherwise.
 * Modernized for minSdk 23+ and Android 13+/14+ behaviors.
 */
@Composable
fun PermissionsHandler(
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    val context = LocalContext.current
    LocalActivity.current

    // Only request what you actually need:
    val permissions = buildList {
        add(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT in 23..32) {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        // For camera-only flow, skip READ_MEDIA_IMAGES, unless you access gallery via MediaStore directly
    }.toTypedArray()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.all { it.value }) onGranted() else onDenied()
    }

    LaunchedEffect(Unit) {
        val granted = permissions.all { perm ->
            androidx.core.content.ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
        if (granted) onGranted() else launcher.launch(permissions)
    }
}

/** Helper to create a MediaStore image Uri for the camera. */
fun createImageUri(context: Context): Uri? {
    val contentResolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "farm_help_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FarmHelp")
        }
    }
    return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
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
fun UploadStep(onGallery: () -> Unit, onCamera: () -> Unit) {
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
                text = "Take a photo or upload a photo of the issue, and our certified officers will help.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onCamera,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = "Take Photo")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Take a Photo")
            }
            Button(
                onClick = onGallery,
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
fun CameraPreviewStep(
    imageUri: Uri,
    onOkay: () -> Unit,
    onRetake: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Preview Photo", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "Captured Photo",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(onClick = onRetake) { Text("Retake") }
            Button(onClick = onOkay) { Text("Okay") }
        }
    }
}

@Composable
fun DescribeStep(
    description: String,
    selectedImageUri: Uri?,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Explain The Photo",
            style = MaterialTheme.typography.headlineSmall
        )
        selectedImageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
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
            ) { Text("Back") }
            Button(
                onClick = onSubmit,
                modifier = Modifier.weight(1f),
                enabled = description.isNotBlank()
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
            Text("Back to Home")
        }
    }
}