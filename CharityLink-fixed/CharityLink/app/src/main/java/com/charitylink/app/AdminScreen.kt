package com.charitylink.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charitylink.app.ui.theme.ForestGreen
import com.charitylink.app.data.Campaign

@Composable
fun FraudPanel(vm: AppViewModel) {
    LaunchedEffect(Unit) { vm.loadFlags() }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().background(ForestGreen).padding(16.dp)) {
            Column(Modifier.weight(1f)) {
                Text("Fraud & Anomaly Review", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Admin only", color = Color(0xFFA7F3D0), fontSize = 12.sp)
            }
            TextButton(onClick = { vm.loadFlags() }) { Text("Refresh", color = Color.White) }
        }

        vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) }
        if (vm.loading) LinearProgressIndicator(Modifier.fillMaxWidth())

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            vm.flagSummary?.let {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5))) {
                        Text("✨ $it", Modifier.padding(14.dp), fontSize = 13.sp, color = Color(0xFF1C2B22))
                    }
                }
            }
            items(vm.flags, key = { it.id }) { f ->
                val color = when (f.severity) {
                    "high" -> Color(0xFFEF4444)
                    "medium" -> Color(0xFFF59E0B)
                    else -> Color(0xFF3B82F6)
                }
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Row {
                            Text(
                                f.severity.uppercase(), color = Color.White, fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.background(color, RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(f.type, fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(f.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(f.detail, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminScreen(vm: AppViewModel) {
    var tab by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf<Campaign?>(null) }
    var showForm by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Flags") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Campaigns") })
        }
        Box(Modifier.weight(1f)) {
            if (tab == 0) {
                FraudPanel(vm)
            } else {
                AdminCampaignsPanel(
                    vm,
                    onEdit = { editing = it; showForm = true },
                    onCreate = { editing = null; showForm = true }
                )
            }
        }
    }

    if (showForm) CampaignFormDialog(vm, editing) { showForm = false }
}