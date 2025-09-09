package com.example.app.ui.tabs

import android.net.Uri
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.app.repository.MessageRepository
import com.example.app.viewmodel.MessageViewModel
import com.example.app.viewmodel.SendMessageUiState

sealed class MessageContent {
    data class TextMessage(val text: String) : MessageContent()
    data class MediaMessage(val uri: Uri, val description: String? = null) : MessageContent()
}

data class Message(val sender: String, val content: MessageContent)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val context = LocalContext.current
    val viewModel = remember { MessageViewModel(MessageRepository(context)) }

    val messages = remember {
        mutableStateListOf(
            Message("Bot", MessageContent.TextMessage("Hello! How can I assist you today?")),
            Message("User", MessageContent.TextMessage("I have a question about crop rotation.")),
            Message("Bot", MessageContent.TextMessage("Sure! What would you like to know about crop rotation?"))
        )
    }

    var inputText by remember { mutableStateOf("") }
    var pendingAttachment by remember { mutableStateOf<Uri?>(null) }
    var pendingAttachmentDescription by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            pendingAttachment = it
        }
    }

    // Observe send message result
    LaunchedEffect(uiState) {
        when (uiState) {
            is SendMessageUiState.Success -> {
                val response = (uiState as SendMessageUiState.Success).response
                messages.add(
                    Message(
                        "Bot",
                        MessageContent.TextMessage("ID: ${response.message}\nStatus: ${response.status}")
                    )
                )
                viewModel.resetState()
                pendingAttachment = null
                pendingAttachmentDescription = ""
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
            .background(Color.White)
    ) {
        // App Header
        TopAppBar(
            title = { Text("FarmHub Chat", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF388E3C),
                titleContentColor = Color.White
            )
        )

        // Chat messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                ChatBubble(message)
            }
        }

        // Pending attachment awaiting description
        if (pendingAttachment != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberAsyncImagePainter(pendingAttachment),
                        contentDescription = "Attachment Preview",
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TextField(
                        value = pendingAttachmentDescription,
                        onValueChange = { pendingAttachmentDescription = it },
                        placeholder = { Text("Describe your attachment...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    IconButton(onClick = {
                        pendingAttachment = null
                        pendingAttachmentDescription = ""
                    }) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Remove Attachment", tint = Color(0xFF388E3C))
                    }
                }
            }
        }

        // Message input & send bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 8.dp, end = 8.dp, bottom = 10.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { galleryLauncher.launch("image/*") }
            ) {
                Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Attach")
            }

            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White),
                placeholder = { Text("Type a message...") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 4
            )

            val isSending = uiState is SendMessageUiState.Loading
            IconButton(
                enabled = !isSending,
                onClick = {
                    if (pendingAttachment != null) {
                        // Send attachment and its description
                        messages.add(
                            Message(
                                "User",
                                MessageContent.MediaMessage(pendingAttachment!!, pendingAttachmentDescription)
                            )
                        )
                        viewModel.sendMessage(pendingAttachmentDescription, pendingAttachment)
                        pendingAttachment = null
                        pendingAttachmentDescription = ""
                        inputText = ""
                    } else if (inputText.isNotBlank()) {
                        // Send plain text
                        messages.add(Message("User", MessageContent.TextMessage(inputText)))
                        viewModel.sendMessage(inputText, null)
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
                    Column {
                        Image(
                            painter = rememberAsyncImagePainter(content.uri),
                            contentDescription = "Media",
                            modifier = Modifier
                                .size(200.dp)
                                .background(Color.LightGray, shape = RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        if (!content.description.isNullOrBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = content.description,
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}