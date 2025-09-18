package com.example.app.ui.tabs

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
private val SendGreen = Color(0xFF37B24D) // A lively green for the send icon

@Composable
fun ChatScreen(
    feedViewModel: FeedViewModel,
    messageViewModel: MessageViewModel,
    userPhone: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val feedItems by feedViewModel.feedItems.collectAsState()
    val feedLoading by feedViewModel.loading.collectAsState()
    val feedError by feedViewModel.error.collectAsState()

    val messageSending by messageViewModel.sending.collectAsState()
    val messageError by messageViewModel.error.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(Unit) {
        feedViewModel.fetchFeed()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
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

        ChatInputBar(
            inputText = inputText,
            onInputTextChange = { inputText = it },
            selectedImageUri = selectedImageUri,
            onPickImage = { pickImageLauncher.launch("image/*") },
            onRemoveImage = { selectedImageUri = null },
            onSendClick = {
                if (selectedImageUri != null) {
                    messageViewModel.createPost(
                        inputText, selectedImageUri!!, context
                    ) { success ->
                        if (!success) Toast.makeText(context, "Failed to send post", Toast.LENGTH_SHORT).show()
                        else feedViewModel.fetchFeed()
                    }
                    selectedImageUri = null
                } else {
                    messageViewModel.sendMessage(
                        inputText
                    ) { success ->
                        if (!success) Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show()
                        else feedViewModel.fetchFeed()
                    }
                }
                inputText = ""
            },
            messageSending = messageSending,
            onCameraClick = { /* TODO: Camera logic */ }
        )
    }
}

@Composable
fun ChatInputBar(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    selectedImageUri: Uri?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    onSendClick: () -> Unit,
    messageSending: Boolean,
    onCameraClick: () -> Unit
) {
    Column {
        if (selectedImageUri != null) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                        .padding(4.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCameraClick) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
            }
            IconButton(onClick = onPickImage) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach")
            }
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") }
            )
            IconButton(
                enabled = !messageSending && inputText.isNotBlank(),
                onClick = onSendClick
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = SendGreen // Green send icon
                )
            }
        }
    }
}

// Subtle green card for posts, right-aligned
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
                // Optional post image
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

// Card style for thread items, visually distinct (right aligned, blue background)
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
                // Optional thread image
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