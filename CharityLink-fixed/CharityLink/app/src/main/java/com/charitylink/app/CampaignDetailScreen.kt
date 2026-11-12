package com.charitylink.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charitylink.app.data.Campaign
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.charitylink.app.ui.theme.ForestGreen

@Composable
fun CampaignDetailScreen(campaign: Campaign, onBack: () -> Unit) {
    val progress = (campaign.raised.toFloat() / campaign.target).coerceIn(0f, 1f)
    val percent = (campaign.raised.toFloat() / campaign.target * 100).toInt()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        TextButton(onClick = onBack, modifier = Modifier.padding(horizontal = 8.dp)) {
            Text("← Back to campaigns", color = MaterialTheme.colorScheme.primary)
        }

        Box(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer).padding(vertical = 28.dp),
            Alignment.Center
        ) { Text(campaign.icon, fontSize = 48.sp) }

        Column(Modifier.padding(16.dp)) {
            Text(campaign.category.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text(campaign.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(campaign.short, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(18.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50))
            )
            Spacer(Modifier.height(8.dp))
            Row {
                Text("KES ${"%,d".format(campaign.raised)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.weight(1f))
                Text("${percent}% of KES ${"%,d".format(campaign.target)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(6.dp))
            Text("${campaign.donors} donors", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            Text("Impact so far", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth()) {
                StatBox("${campaign.donors}", "Donors", Modifier.weight(1f))
                Spacer(Modifier.weight(0.1f))
                StatBox("${percent}%", "Funded", Modifier.weight(1f))
                Spacer(Modifier.weight(0.1f))
                StatBox("✓", "Verified", Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))
            Text("How funds are used", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            FundRow("Direct programs", 0.80f)
            FundRow("Logistics", 0.12f)
            FundRow("Administration", 0.08f)
        }
    }
}

@Composable
fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun FundRow(label: String, pct: Float) {
    Column(Modifier.padding(bottom = 10.dp)) {
        Row {
            Text(label, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text("${(pct * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { pct },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50))
        )
    }
}