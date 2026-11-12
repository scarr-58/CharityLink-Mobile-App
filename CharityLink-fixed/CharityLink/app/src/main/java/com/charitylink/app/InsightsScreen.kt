package com.charitylink.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charitylink.app.ui.theme.ForestGreen

private fun formatKES(n: Long): String = when {
    n >= 1_000_000 -> "KES ${"%.1f".format(n / 1_000_000.0)}M"
    n >= 1_000 -> "KES ${n / 1_000}K"
    else -> "KES $n"
}

private fun categoryColor(label: String): Color = when (label) {
    "Water" -> Color(0xFFCA8A04)
    "Education" -> Color(0xFF3B82F6)
    "Health" -> Color(0xFF1BAF7A)
    "Livelihood" -> Color(0xFF15803D)
    "Emergency" -> Color(0xFFA78BFA)
    "Environment" -> Color(0xFFEF6B6B)
    else -> Color(0xFF94A3B8)
}

@Composable
private fun Kpi(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(label.uppercase(), fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = ForestGreen)
        }
    }
}

@Composable
fun InsightsScreen(vm: AppViewModel) {
    LaunchedEffect(Unit) { vm.loadAnalytics() }
    val a = vm.analytics

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().background(ForestGreen).padding(16.dp)) {
            Column(Modifier.weight(1f)) {
                Text("Impact & Insights", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Live transparency analytics", color = Color(0xFFA7F3D0), fontSize = 12.sp)
            }
            TextButton(onClick = { vm.loadAnalytics() }) { Text("Refresh", color = Color.White) }
        }

        if (a == null) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(vm.error ?: "Loading analytics…", color = Color.Gray)
            }
            return@Column
        }

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Kpi("Total raised", formatKES(a.totals.raised), Modifier.weight(1f))
                Kpi("Contributions", "${a.totals.contributions}", Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Kpi("Active campaigns", "${a.totals.campaigns}", Modifier.weight(1f))
                Kpi("Registered donors", "${a.totals.users}", Modifier.weight(1f))
            }

            // Funds raised by category (donut)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Funds Raised by Category", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ForestGreen)
                    val total = a.byCategory.sumOf { it.value }.coerceAtLeast(1L)
                    Spacer(Modifier.height(14.dp))
                    Box(Modifier.fillMaxWidth(), Alignment.Center) {
                        Canvas(Modifier.size(170.dp)) {
                            val stroke = 28.dp.toPx()
                            var start = -90f
                            a.byCategory.forEach { d ->
                                val sweep = 360f * d.value / total
                                drawArc(
                                    color = categoryColor(d.label),
                                    startAngle = start,
                                    sweepAngle = sweep,
                                    useCenter = false,
                                    topLeft = Offset(stroke / 2, stroke / 2),
                                    size = Size(size.width - stroke, size.height - stroke),
                                    style = Stroke(width = stroke)
                                )
                                start += sweep
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(formatKES(total), fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Text("RAISED", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    a.byCategory.forEach { d ->
                        Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(categoryColor(d.label)))
                            Spacer(Modifier.width(8.dp))
                            Text(d.label, Modifier.weight(1f), fontSize = 13.sp)
                            Text(formatKES(d.value), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(Modifier.width(8.dp))
                            Text("${d.value * 100 / total}%", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // Top campaigns
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Top Campaigns", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ForestGreen)
                    Spacer(Modifier.height(10.dp))
                    val max = (a.topCampaigns.maxOfOrNull { it.raised } ?: 1L).coerceAtLeast(1L)
                    a.topCampaigns.forEach { t ->
                        Text("${t.icon} ${t.title}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { t.raised.toFloat() / max },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50))
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "${formatKES(t.raised)} · ${t.raised * 100 / t.target.coerceAtLeast(1L)}% of ${formatKES(t.target)} goal",
                            fontSize = 11.sp, color = Color.Gray
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}