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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charitylink.app.ui.theme.ForestGreen

@Composable
fun DonationsScreen(vm: AppViewModel) {
    LaunchedEffect(Unit) { vm.loadContributions() }
    val total = vm.contributions.sumOf { it.amount }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().background(ForestGreen).padding(16.dp)) {
            Text("My Donations", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "Total given: KES ${"%,d".format(total)}",
                color = Color(0xFFA7F3D0), fontSize = 13.sp
            )
        }

        if (vm.contributions.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("No donations yet. Support a campaign to see it here.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(vm.contributions.reversed(), key = { it.id }) { c ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(c.campaignTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    "${c.category} · ${c.paymentMethod} · ${c.timestamp.take(10)}",
                                    fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                "KES ${"%,d".format(c.amount)}",
                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}