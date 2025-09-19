package com.example.app.routes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun IntroScreen(
    onFarmHelpClick: () -> Unit,
    onVideosClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF388E3C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Welcome text restored
            Text(
                text = "Welcome to",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 34.sp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Enlarged app logo
            Image(
                painter = painterResource(id = R.drawable.farmhub_logo),
                contentDescription = "FarmHub Logo",
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Primary Card Button - Farm Help
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp) // Reduced height
                    .padding(vertical = 4.dp)
                    .clickable(onClick = onFarmHelpClick),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Wider image
                    Image(
                        painter = painterResource(id = R.drawable.farmhelp_logo_horizontal_no_bg),
                        contentDescription = "Farm Help Icon",
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight()
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Smaller text
                    Text(
                        text = "Expert advice on farming.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Card Button - Farm Videos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp) // Reduced height
                    .padding(vertical = 4.dp)
                    .clickable(onClick = onVideosClick),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Wider image
                    Image(
                        painter = painterResource(id = R.drawable.farmers_videos_logo_final_2),
                        contentDescription = "Farm Videos Icon",
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight()
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Smaller text
                    Text(
                        text = "Watch free farming videos.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1.7f)
                    )
                }
            }
        }
    }
}