package com.example.app.routes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app.R

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your logo
            contentDescription = "Farm Hub Logo",
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // App Tagline
        Text(
            text = "Your Smart Farming Companion",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32) // Green color matching farming theme
        )

        Spacer(modifier = Modifier.height(24.dp))

        // App Description
        Text(
            text = "Farm Hub is a revolutionary agricultural platform that connects farmers with modern farming solutions. " +
                    "Our intuitive, reliable, and feature-rich app empowers farmers with tools to enhance productivity " +
                    "and streamline operations.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Key Features Title
        Text(
            text = "Key Features",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Feature List
        FeatureItem("🌱 Smart Crop Advisor", "Get AI-powered recommendations for optimal planting times, crop rotations, and pest management")
        Spacer(modifier = Modifier.height(8.dp))
        FeatureItem("📊 Farm Analytics Dashboard", "Track your farm's performance with real-time data visualization and historical trends")
        Spacer(modifier = Modifier.height(8.dp))
        FeatureItem("🌦️ Hyperlocal Weather Forecast", "Precision weather forecasts tailored to your exact farm location")
        Spacer(modifier = Modifier.height(8.dp))
        FeatureItem("🛒 Digital Marketplace", "Connect directly with buyers and sell your produce at competitive prices")
        Spacer(modifier = Modifier.height(8.dp))
        FeatureItem("🧑‍🌾 Community Hub", "Connect with other farmers, share knowledge, and learn best practices")

        Spacer(modifier = Modifier.height(32.dp))

        // How To Use Title
        Text(
            text = "How To Use Farm Hub",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Usage Steps
        StepItem(1, "Create your farmer profile with details about your farm")
        StepItem(2, "Connect your farm equipment for seamless data integration")
        StepItem(3, "Set up your crops/livestock information in the system")
        StepItem(4, "Access real-time insights and recommendations")
        StepItem(5, "Engage with marketplace and community features")

        Spacer(modifier = Modifier.height(32.dp))

        // Closing Note
        Text(
            text = "Join thousands of farmers already transforming their operations with Farm Hub!",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun FeatureItem(title: String, description: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium
        )
    }

}

@Composable
fun StepItem(stepNumber: Int, description: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$stepNumber.",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
