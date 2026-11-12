package com.charitylink.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
fun ChatScreen(vm: AppViewModel) {
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(vm.chat.size) {
        if (vm.chat.isNotEmpty()) listState.animateScrollToItem(vm.chat.size - 1)
    }

    Column(Modifier.fillMaxSize().imePadding()) {
        Column(Modifier.fillMaxWidth().background(ForestGreen).padding(16.dp)) {
            Text("CharityLink Assistant", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Ask about campaigns or your donations", color = Color(0xFFA7F3D0), fontSize = 12.sp)
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (vm.chat.isEmpty()) {
                item {
                    Text(
                        "Try: \"Which campaigns are verified?\" or \"How much have I donated?\"",
                        color = Color.Gray, fontSize = 13.sp
                    )
                }
            }
            items(vm.chat) { (fromUser, text) ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = if (fromUser) Arrangement.End else Arrangement.Start
                ) {
                    Text(
                        text,
                        color = if (fromUser) Color.White else Color(0xFF1C2B22),
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .background(
                                if (fromUser) Color(0xFF10B981) else Color(0xFFE5F3EC),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 14.sp
                    )
                }
            }
            if (vm.chatLoading) item { Text("Thinking…", color = Color.Gray, fontSize = 12.sp) }
        }

        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type your question") },
                maxLines = 3
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { vm.sendChat(input); input = "" },
                enabled = input.isNotBlank() && !vm.chatLoading
            ) { Text("Send") }
        }
    }
}