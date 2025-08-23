package com.example.app.tabs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.app.features.ConfirmationDialog
import com.example.app.features.DescribeStep
import com.example.app.features.StepIndicator
import com.example.app.features.SuccessStep
import com.example.app.features.UploadStep

@Composable
fun HelpScreen() {
    var showFarmHelp by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Header
                Text(
                    text = "FarmHelp Assistance",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )

                // Description card
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = "Help",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Facing Farming Challenges?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Share your questions about crop issues, livestock problems, or any farming challenges. Our expert extension officers will provide immediate solutions.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // FAQ section header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "Frequently Asked Questions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "FAQ Info",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // FAQ List
                val faqList = remember {
                    mutableStateListOf(
                        FAQItem("Farm News", "Get the latest agricultural news, policy updates, and farming innovations.", Icons.Default.Info),
                        FAQItem("Weather Update", "Real-time weather forecasts and agricultural advisories.", Icons.Default.WbSunny),
                        FAQItem("Market Trends", "Daily market prices, demand forecasts, and trading insights.", Icons.AutoMirrored.Filled.TrendingUp),
                        FAQItem("Crop Tips", "Seasonal cultivation guidance and pest management solutions.", Icons.AutoMirrored.Filled.Help)
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(faqList) { item ->
                        ExpandableFAQCard(faqItem = item)
                    }
                }

                // Ask a Question button
                Button(
                    onClick = { showFarmHelp = !showFarmHelp },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (showFarmHelp) "Close FarmHelp" else "Ask a Question",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // --- Overlay FarmHelp (covers entire screen) ---
            AnimatedVisibility(
                visible = showFarmHelp,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                FarmHelpWizard(
                    onClose = { showFarmHelp = false }
                )
            }
        }
    }
}

@Composable
fun ExpandableFAQCard(faqItem: FAQItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = faqItem.icon,
                    contentDescription = "${faqItem.title} icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = faqItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    text = faqItem.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

data class FAQItem(
    val title: String,
    val description: String,
    val icon: ImageVector
)

// Updated FarmHelpWizard: accepts onClose to close overlay
@Composable
fun FarmHelpWizard(onClose: () -> Unit) {
    var currentStep by remember { mutableIntStateOf(1) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var showConfirmation by remember { mutableStateOf(false) }
    var selectedImageRes by remember { mutableStateOf<Int?>(null) }

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
            StepIndicator(currentStep = currentStep, totalSteps = 3)
            TextButton(onClick = onClose) {
                Text("Close")
            }
        }

        Spacer(Modifier.height(16.dp))

        AnimatedContent(targetState = currentStep, label = "Step Animation") { step ->
            when (step) {
                1 -> UploadStep(
                    onNext = {
                        selectedImageRes = android.R.drawable.ic_menu_camera // placeholder
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
                3 -> SuccessStep(
                    onHome = {
                        currentStep = 1
                        description = TextFieldValue("")
                        selectedImageRes = null
                        onClose()
                    }
                )
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
