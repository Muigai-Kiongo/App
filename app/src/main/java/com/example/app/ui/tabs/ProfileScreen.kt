package com.example.app.ui.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.R
import com.example.app.auth.AuthManager
import com.example.app.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onToggleTheme: () -> Unit,
    isLoggedIn: Boolean,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    val profile = viewModel.profile?.data
    val error = viewModel.error
    val isLoading = viewModel.isLoading
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Cover Photo & Profile Photo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)

        ) {
            Image(
                painter = painterResource(id = R.drawable.farmhelp_logo_horizontal),
                contentDescription = "Cover Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 55.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.farmhub_logo),
                    contentDescription = "Profile Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(98.dp)
                        .clip(CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(70.dp))

        // Profile Info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }

            error?.let {
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
            }

            profile?.let {
                Text(
                    it.names ?: "No Name",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = "Email", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(it.phone ?: "No Phone", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                it.county?.let { county ->
                    Text("County: $county", fontSize = 14.sp)
                }
                it.subCounty?.let { subCounty ->
                    Text("SubCounty: $subCounty", fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Preferences / Settings
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PreferenceItem(
                icon = Icons.Default.Notifications,
                title = "Notifications"
            ) { }
            PreferenceItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode"
            ) { onToggleTheme() }
            PreferenceItem(
                icon = Icons.Default.Language,
                title = "Language"
            ) { }
            PreferenceItem(
                icon = Icons.Default.Settings,
                title = "Account Settings"
            ) { }
        }

        Spacer(modifier = Modifier.height(34.dp))

        // Auth toggle button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoggedIn) {
                Button(
                    onClick = {
                        scope.launch {
                            AuthManager.logout(context)
                            onSignOutClick()
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign Out", fontSize = 16.sp)
                }
            } else {
                Button(
                    onClick = onSignInClick,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign In", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun PreferenceItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(18.dp))
            Text(title, fontSize = 16.sp)
        }
    }
}