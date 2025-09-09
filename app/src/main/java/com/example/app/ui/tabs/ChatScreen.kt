package com.example.app.ui.tabs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.app.viewmodel.MessageViewModel
import com.example.app.viewmodel.SendMessageUiState

sealed class MessageContent {
    data class TextMessage(val text: String) : MessageContent()
    data class MediaMessage(val uri: Uri) : MessageContent()
}

data class Message(val sender: String, val content: MessageContent)

@Composable
fun ChatScreen(viewModel: MessageViewModel = remember { MessageViewModel() }) {
    val messages = remember {
        mutableStateListOf(
            Message("Bot", MessageContent.TextMessage("Hello! How can I assist you today?")),
            Message("User", MessageContent.TextMessage("I have a question about crop rotation.")),
            Message("Bot", MessageContent.TextMessage("Sure! What would you like to know about crop rotation?"))
        )
    }

    var inputText by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            messages.add(Message("User", MessageContent.MediaMessage(it)))
        }
    }

    // Camera capture
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            // Convert bitmap to Image via Coil (preview only, not persisted)
            // For real apps, save to file first and return Uri
        }
    }

    // Observe send message result
    LaunchedEffect(uiState) {
        when (uiState) {
            is SendMessageUiState.Success -> {
                val response = (uiState as SendMessageUiState.Success).response
                messages.add(
                    Message("Bot", MessageContent.TextMessage("ID: ${response.message}\nStatus: ${response.status}"))
                )
                viewModel.resetState()
            }
            is SendMessageUiState.Error -> {
                val error = (uiState as SendMessageUiState.Error).message
                messages.add(
                    Message("Bot", MessageContent.TextMessage("Error sending message: $error"))
                )
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                ChatBubble(message)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Attach")
            }
            IconButton(onClick = { cameraLauncher.launch(null) }) {
                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera")
            }
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedContainerColor = Color(0xFFF0F0F0),
                    disabledContainerColor = Color(0xFFF0F0F0),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            val isSending = uiState is SendMessageUiState.Loading
            IconButton(
                enabled = !isSending,
                onClick = {
                    if (inputText.isNotBlank()) {
                        messages.add(Message("User", MessageContent.TextMessage(inputText)))
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                }
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isUser = message.sender == "User"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isUser) Color(0xFFDCF8C6) else Color(0xFFEFEFEF),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            when (val content = message.content) {
                is MessageContent.TextMessage -> {
                    Text(
                        text = content.text,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
                is MessageContent.MediaMessage -> {
                    Image(
                        painter = rememberAsyncImagePainter(content.uri),
                        contentDescription = "Media",
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}