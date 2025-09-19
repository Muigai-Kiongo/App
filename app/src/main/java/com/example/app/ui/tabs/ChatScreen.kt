package com.example.app.ui.tabs

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.models.feed.FeedItem
import com.example.app.viewmodel.FeedViewModel
import com.example.app.viewmodel.MessageViewModel

private val SubtleGreen = Color(0xFFDFFFE2)
private val SendGreen = Color(0xFF37B24D)

@Composable
fun ChatScreen(
    feedViewModel: FeedViewModel,
    messageViewModel: MessageViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val feedItems by feedViewModel.feedItems.collectAsState()
    val feedLoading by feedViewModel.loading.collectAsState()
    val feedError by feedViewModel.error.collectAsState()
    val messageSending by messageViewModel.sending.collectAsState()
    val messageError by messageViewModel.error.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var previewImageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var showPreview by remember { mutableStateOf(false) }
    var requestCameraPermissions by remember { mutableStateOf(false) }
    var showPermissionDenied by remember { mutableStateOf(false) }

    // Gallery picker
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            previewImageUri = uri
            showPreview = true
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success && cameraImageUri != null) {
            previewImageUri = cameraImageUri
            showPreview = true
        }
    }

    if (requestCameraPermissions) {
        CameraPermissionsHandler(
            onGranted = {
                val uri = createImageUri(context)
                cameraImageUri = uri
                if (uri != null) cameraLauncher.launch(uri)
                else Toast.makeText(context, "Could not create image file", Toast.LENGTH_SHORT).show()
                requestCameraPermissions = false
            },
            onDenied = {
                showPermissionDenied = true
                requestCameraPermissions = false
            }
        )
    }
    if (showPermissionDenied) {
        Toast.makeText(context, "Camera permission required", Toast.LENGTH_LONG).show()
    }

    // Only load feed if needed (prevents redundant fetching)
    LaunchedEffect(Unit) {
        feedViewModel.loadFeedIfNeeded()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Feed area
        Box(Modifier.weight(1f)) {
            when {
                feedLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                feedError != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $feedError")
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        reverseLayout = true
                    ) {
                        items(feedItems) { item ->
                            when (item) {
                                is FeedItem.PostItem -> PostCardStyled(item)
                                is FeedItem.ThreadItemFeed -> ThreadCardStyled(item)
                            }
                        }
                    }
                }
            }
            if (messageError != null) {
                Toast.makeText(context, "Message Error: $messageError", Toast.LENGTH_SHORT).show()
                messageViewModel.clearError()
            }
        }

        // Show "Sending..." overlay if a post or message is being sent
        if (messageSending) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xAAFFFFFF))
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        color = SendGreen,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Sending...", style = MaterialTheme.typography.bodyMedium, color = SendGreen)
                }
            }
        }

        // Attachment preview step for camera/gallery images
        if (showPreview && previewImageUri != null) {
            ImagePreviewStep(
                imageUri = previewImageUri!!,
                description = inputText,
                onDescriptionChange = { inputText = it },
                sending = messageSending,
                onSend = {
                    messageViewModel.createPost(
                        text = inputText,
                        imageUri = previewImageUri!!,
                        context = context
                    ) { success ->
                        if (!success) Toast.makeText(context, "Failed to send attachment", Toast.LENGTH_SHORT).show()
                        else feedViewModel.refreshFeed()
                    }
                    // Reset state
                    inputText = ""
                    previewImageUri = null
                    showPreview = false
                },
                onCancel = {
                    previewImageUri = null
                    showPreview = false
                }
            )
        } else {
            // Regular chat input bar
            ChatInputBar(
                inputText = inputText,
                onInputTextChange = { inputText = it },
                onPickImage = { pickImageLauncher.launch("image/*") },
                messageSending = messageSending,
                onCameraClick = { requestCameraPermissions = true },
                onSendClick = {
                    if (inputText.isNotBlank()) {
                        messageViewModel.sendMessage(
                            inputText
                        ) { success ->
                            if (!success) Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show()
                            else feedViewModel.refreshFeed()
                        }
                        inputText = ""
                    }
                }
            )
        }
    }
}

@Composable
fun ChatInputBar(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onPickImage: () -> Unit,
    messageSending: Boolean,
    onCameraClick: () -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCameraClick, enabled = !messageSending) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
        }
        IconButton(onClick = onPickImage, enabled = !messageSending) {
            Icon(Icons.Default.AttachFile, contentDescription = "Attach")
        }
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message...") },
            maxLines = 4,
            singleLine = false,
            enabled = !messageSending
        )
        IconButton(
            enabled = !messageSending && inputText.isNotBlank(),
            onClick = onSendClick
        ) {
            if (messageSending) {
                CircularProgressIndicator(
                    color = SendGreen,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = SendGreen
                )
            }
        }
    }
}

@Composable
fun ImagePreviewStep(
    imageUri: Uri,
    description: String,
    onDescriptionChange: (String) -> Unit,
    sending: Boolean,
    onSend: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Preview Image", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "Preview",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Add a description...") },
            enabled = !sending
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(onClick = onCancel, enabled = !sending) { Text("Cancel") }
            Button(
                onClick = onSend,
                enabled = !sending && description.isNotBlank()
            ) {
                if (sending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sending...")
                } else {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
fun CameraPermissionsHandler(
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    val context = LocalContext.current
    val permissions = buildList {
        add(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT in 23..32) {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
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

fun createImageUri(context: Context): Uri? {
    val contentResolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "chat_image_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ChatImages")
        }
    }
    return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}

@Composable
fun PostCardStyled(item: FeedItem.PostItem) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Surface(
            color = SubtleGreen,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 1.dp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .widthIn(max = 320.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                if (item.post.imageUrl.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(item.post.imageUrl),
                        contentDescription = "Post Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 140.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = item.post.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF222222)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    item.post.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF888888),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun ThreadCardStyled(item: FeedItem.ThreadItemFeed) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Surface(
            color = Color(0xFFE3F2FD),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 1.dp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .widthIn(max = 320.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                if (item.thread.image.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(item.thread.image),
                        contentDescription = "Thread Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 140.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    item.thread.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF222222)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Last updated: ${item.thread.updatedAt}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF607D8B),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}