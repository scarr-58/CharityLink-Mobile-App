package com.charitylink.app

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
import com.charitylink.app.data.Campaign

@Composable
fun AdminCampaignsPanel(vm: AppViewModel, onEdit: (Campaign) -> Unit, onCreate: () -> Unit) {
    var toDelete by remember { mutableStateOf<Campaign?>(null) }
    LaunchedEffect(Unit) { vm.loadCampaigns() }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${vm.campaigns.size} campaigns", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Button(onClick = onCreate) { Text("+ New campaign") }
        }

        vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp)) }
        if (vm.loading) LinearProgressIndicator(Modifier.fillMaxWidth())

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(vm.campaigns, key = { it.id }) { c ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("${c.icon} ${c.title}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.height(2.dp))
                        Text("${c.category} · ${c.org}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "KES ${"%,d".format(c.raised)} of KES ${"%,d".format(c.target)} · ${c.donors} donors",
                            fontSize = 12.sp, color = Color.Gray
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(checked = c.verified, onCheckedChange = { vm.setVerified(c, it) })
                            Spacer(Modifier.width(8.dp))
                            Text(if (c.verified) "Verified" else "Unverified", fontSize = 12.sp)
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { onEdit(c) }) { Text("Edit") }
                            TextButton(onClick = { toDelete = c }) {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    toDelete?.let { c ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Delete campaign?") },
            text = {
                Text("\"${c.title}\" and all donation records for it will be permanently deleted. This cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = { vm.removeCampaign(c); toDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancel") } }
        )
    }
}