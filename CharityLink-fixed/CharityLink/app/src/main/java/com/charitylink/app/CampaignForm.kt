package com.charitylink.app

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charitylink.app.data.Campaign
import com.charitylink.app.data.CampaignInput

private val categories = listOf("Water", "Education", "Health", "Livelihood", "Emergency", "Environment")
private val categoryIcon = mapOf(
    "Water" to "💧", "Education" to "📚", "Health" to "🏥",
    "Livelihood" to "🌱", "Emergency" to "🆘", "Environment" to "🌳"
)
private val categoryColor = mapOf(
    "Water" to "#e8f4fd", "Education" to "#fef9e7", "Health" to "#fdf2f8",
    "Livelihood" to "#eafaf1", "Emergency" to "#fef5e7", "Environment" to "#e8f8f5"
)

@Composable
fun CampaignFormDialog(vm: AppViewModel, existing: Campaign?, onClose: () -> Unit) {
    LaunchedEffect(Unit) { vm.error = null }

    var title by remember { mutableStateOf(existing?.title ?: "") }
    var org by remember { mutableStateOf(existing?.org ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: "Water") }
    var icon by remember { mutableStateOf(existing?.icon ?: "💧") }
    var color by remember { mutableStateOf(existing?.color ?: "#e8f4fd") }
    var short by remember { mutableStateOf(existing?.short ?: "") }
    var desc by remember { mutableStateOf(existing?.desc ?: "") }
    var target by remember { mutableStateOf(existing?.target?.toString() ?: "") }

    val valid = title.isNotBlank() && org.isNotBlank() && short.isNotBlank() &&
            desc.isNotBlank() && icon.isNotBlank() && (target.toIntOrNull() ?: 0) > 0

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(if (existing == null) "New campaign" else "Edit campaign") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Title") }, singleLine = true)
                OutlinedTextField(org, { org = it }, label = { Text("Organisation") }, singleLine = true)

                Text("Category", fontSize = 12.sp)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = {
                                category = cat
                                color = categoryColor[cat] ?: color
                                if (existing == null) icon = categoryIcon[cat] ?: icon
                            },
                            label = { Text(cat) }
                        )
                    }
                }

                OutlinedTextField(icon, { icon = it }, label = { Text("Icon (emoji)") }, singleLine = true)
                OutlinedTextField(short, { short = it }, label = { Text("Short summary") }, maxLines = 3)
                OutlinedTextField(desc, { desc = it }, label = { Text("Full description") }, minLines = 3, maxLines = 6)
                OutlinedTextField(
                    target, { target = it.filter(Char::isDigit) },
                    label = { Text("Target (KES)") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            }
        },
        confirmButton = {
            Button(
                enabled = valid && !vm.loading,
                onClick = {
                    vm.saveCampaign(
                        existing?.id,
                        CampaignInput(
                            title.trim(), org.trim(), desc.trim(), short.trim(),
                            target.toInt(), category, icon.trim(), color, ""
                        ),
                        onClose
                    )
                }
            ) { Text(if (existing == null) "Create" else "Save") }
        },
        dismissButton = { TextButton(onClick = onClose) { Text("Cancel") } }
    )
}