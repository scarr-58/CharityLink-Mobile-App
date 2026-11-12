package com.charitylink.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charitylink.app.data.Campaign

private fun tint(hex: String): Color =
    try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color(0xFFEAFAF1) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignsScreen(
    vm: AppViewModel,
    onLogout: () -> Unit,
    onCampaignClick: (Campaign) -> Unit = {}
) {
    var selected by remember { mutableStateOf<Campaign?>(null) }
    LaunchedEffect(Unit) { vm.loadCampaigns() }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 14.dp)) {
            Text("Featured Campaigns", fontFamily = PlayfairFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text(
                "Hi, ${vm.user?.name?.substringBefore(" ") ?: ""}. Verified causes that need support now.",
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) }
        if (vm.loading && vm.campaigns.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(vm.campaigns, key = { it.id }) { c ->
                val progress = (c.raised.toFloat() / c.target).coerceIn(0f, 1f)
                val percent = (c.raised.toFloat() / c.target * 100).toInt()
                Card(
                    onClick = { onCampaignClick(c) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        Box(
                            Modifier.fillMaxWidth().background(tint(c.color)).padding(vertical = 22.dp),
                            Alignment.Center
                        ) { Text(c.icon, fontSize = 40.sp) }

                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    c.category.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clip(RoundedCornerShape(50))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                                Spacer(Modifier.weight(1f))
                                if (c.verified) Text(
                                    "✔ Verified", fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(c.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(c.short, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(14.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50))
                            )
                            Spacer(Modifier.height(6.dp))
                            Row {
                                Text(
                                    "KES ${"%,d".format(c.raised)}", fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp, color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    "$percent% of KES ${"%,d".format(c.target)}", fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "${c.donors} donors", fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.weight(1f))
                                Button(onClick = { selected = c }) { Text("Donate") }
                            }
                        }
                    }
                }
            }
        }
    }

    selected?.let { c ->
        MpesaDonateDialog(vm, c) { selected = null }
    }
}