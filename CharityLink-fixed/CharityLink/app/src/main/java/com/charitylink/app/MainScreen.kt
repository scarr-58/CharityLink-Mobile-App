package com.charitylink.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.charitylink.app.data.Campaign
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charitylink.app.ui.theme.Emerald500
import com.charitylink.app.ui.theme.ForestGreen
import com.charitylink.app.ui.theme.LogoGreen
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.fillMaxSize
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
@Composable
fun MainScreen(vm: AppViewModel, onLogout: () -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    var detail by remember { mutableStateOf<Campaign?>(null) }
    val isAdmin = vm.user?.role == "admin"
    val context = LocalContext.current
    val userKey = vm.user?.email ?: "guest"
    var photo by remember(userKey) { mutableStateOf(loadProfilePhoto(context, userKey)) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null && saveProfilePhoto(context, uri, userKey)) {
            photo = loadProfilePhoto(context, userKey)
        }
    }

    Scaffold(
        topBar = {
            BrandTopBar(
                name = vm.user?.name,
                photo = photo,
                onAvatarClick = {
                    picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onLogout = { vm.logout(); onLogout() }
            )
        },        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = tab == 0, onClick = { tab = 0 },
                    icon = { Text("🏠") }, label = { Text("Campaigns") }
                )
                NavigationBarItem(
                    selected = tab == 1, onClick = { tab = 1 },
                    icon = { Text("🧾") }, label = { Text("Donations") }
                )
                NavigationBarItem(
                    selected = tab == 2, onClick = { tab = 2 },
                    icon = { Text("📊") }, label = { Text("Insights") }
                )
                NavigationBarItem(
                    selected = tab == 3, onClick = { tab = 3 },
                    icon = { Text("💬") }, label = { Text("Assistant") }
                )
                if (isAdmin) {
                    NavigationBarItem(
                        selected = tab == 4, onClick = { tab = 4 },
                        icon = { Text("🛡️") }, label = { Text("Admin") }
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad)) {
            when (tab) {
                0 -> {
                    val d = detail
                    if (d != null) CampaignDetailScreen(d) { detail = null }
                    else CampaignsScreen(vm, onLogout) { detail = it }
                }
                1 -> DonationsScreen(vm)
                2 -> InsightsScreen(vm)
                3 -> ChatScreen(vm)
                else -> if (isAdmin) AdminScreen(vm)
            }
        }
    }
}

@Composable
fun BrandTopBar(
    name: String?,
    photo: Bitmap? = null,
    onAvatarClick: () -> Unit = {},
    onLogout: () -> Unit
) {    Row(
        Modifier.fillMaxWidth().background(ForestGreen).statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(Emerald500),
            Alignment.Center
        ) { Text("♥", color = ForestGreen, fontSize = 18.sp) }
        Spacer(Modifier.width(10.dp))
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) { append("Charity") }
                withStyle(SpanStyle(color = LogoGreen)) { append("Link") }
            },
            fontFamily = PlayfairFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp
        )
        Spacer(Modifier.weight(1f))
        if (!name.isNullOrBlank()) {
            Box(
                Modifier.size(34.dp).clip(CircleShape).background(Color(0xFF047857))
                    .clickable { onAvatarClick() },
                Alignment.Center
            ) {
                if (photo != null) {
                    Image(
                        bitmap = photo.asImageBitmap(),
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(name.trim().take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
        TextButton(onClick = onLogout) { Text("Log out", color = Color.White, fontSize = 13.sp) }
    }
}