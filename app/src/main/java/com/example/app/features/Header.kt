package com.example.app.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.ui.theme.GreenColor
import com.example.app.ui.theme.OrangeColor

// Define custom colors for the green and orange theme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    containerColor: Color = GreenColor, // Use green as the container color
    contentColor: Color = Color.White, // Use white for text and icons
    title: String = "Farm Hub",
    onChatClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor
        ),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        actions = {
            ActionButtons(
                onSearchClick = onSearchClick,
                onChatClick = onChatClick
            )
        }
    )
}

@Composable
private fun ActionButtons(
    onSearchClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White // Use orange for the search icon
            )
        }
        IconButton(onClick = onChatClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = "Chat",
                tint = Color.White // Use orange for the chat icon
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppHeaderPreview() {
    MaterialTheme {
        Surface {
            AppHeader(
                onChatClick = {},
                onSearchClick = {}
            )
        }
    }
}
