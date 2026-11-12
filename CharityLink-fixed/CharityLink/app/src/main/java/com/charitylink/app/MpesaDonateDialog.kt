package com.charitylink.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charitylink.app.data.Campaign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MpesaDonateDialog(vm: AppViewModel, campaign: Campaign, onDismiss: () -> Unit) {
    var amountText by remember { mutableStateOf("1000") }
    var phone by remember { mutableStateOf("") }
    val state = vm.mpesaState

    val digits = phone.filter(Char::isDigit)
    val validPhone = (digits.length == 10 && digits.startsWith("0")) ||
            (digits.length == 12 && digits.startsWith("254")) ||
            (digits.length == 9 && (digits.startsWith("7") || digits.startsWith("1")))
    val amount = amountText.toIntOrNull() ?: 0

    DisposableEffect(Unit) { onDispose { vm.resetMpesa() } }

    AlertDialog(
        onDismissRequest = { if (state != "waiting") onDismiss() },
        title = { Text("Pay with M-Pesa") },
        text = {
            Column {
                Text(campaign.title, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                when (state) {
                    "waiting" -> Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp))
                        Text("Check your phone and enter your M-Pesa PIN…", fontSize = 13.sp)
                    }
                    "success" -> Text(
                        "✔ Payment received. Thank you!",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    else -> {
                        if (state == "failed") {
                            Text(
                                vm.mpesaMessage ?: "The payment was cancelled or declined.",
                                color = MaterialTheme.colorScheme.error, fontSize = 12.sp
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        if (state == "timeout") {
                            Text("Still waiting for M-Pesa. Check the Donations tab in a moment.", fontSize = 12.sp)
                            Spacer(Modifier.height(8.dp))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("500", "2000", "5000").forEach { v ->
                                FilterChip(
                                    selected = amountText == v,
                                    onClick = { amountText = v },
                                    label = { Text(v) }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it.filter(Char::isDigit) },
                            label = { Text("Amount (KES)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("M-Pesa phone number") },
                            placeholder = { Text("0712 345 678") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }
                }
            }
        },
        confirmButton = {
            when (state) {
                "success" -> Button(onClick = onDismiss) { Text("Done") }
                "waiting" -> Button(onClick = {}, enabled = false) { Text("Waiting…") }
                else -> Button(
                    enabled = validPhone && amount > 0,
                    onClick = { vm.payWithMpesa(campaign, amount, phone) }
                ) { Text("Pay KES ${"%,d".format(amount)}") }
            }
        },
        dismissButton = {
            if (state != "success" && state != "waiting") {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}