package com.charitylink.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charitylink.app.ui.theme.Emerald400
import com.charitylink.app.ui.theme.Emerald500
import com.charitylink.app.ui.theme.ForestGreen
import com.charitylink.app.ui.theme.LogoGreen

@Composable
fun LoginScreen(vm: AppViewModel, onLoggedIn: () -> Unit) {
    var isRegister by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Emerald400,
        unfocusedBorderColor = Color(0xFF4F8A68),
        focusedLabelColor = Emerald400,
        unfocusedLabelColor = Color(0xFFA7F3D0),
        cursorColor = Emerald400
    )

    Column(
        modifier = Modifier.fillMaxSize().background(ForestGreen).statusBarsPadding().imePadding().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BrandLogo()
        Spacer(Modifier.height(8.dp))
        Text("Transparent Giving Platform", color = Color(0xFFA7F3D0), fontSize = 14.sp)
        Spacer(Modifier.height(32.dp))

        if (isRegister) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Full name") }, singleLine = true,
                colors = fieldColors, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") }, singleLine = true,
            colors = fieldColors, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password (min 8 characters)") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = fieldColors, modifier = Modifier.fillMaxWidth()
        )

        vm.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = Color(0xFFFCA5A5))
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                if (isRegister) vm.register(name, email, password, onLoggedIn)
                else vm.login(email, password, onLoggedIn)
            },
            enabled = !vm.loading,
            colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = ForestGreen),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (vm.loading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = ForestGreen)
            else Text(if (isRegister) "Create account" else "Log in", fontWeight = FontWeight.Bold)
        }

        TextButton(onClick = { isRegister = !isRegister; vm.error = null }) {
            Text(
                if (isRegister) "Already have an account? Log in" else "New here? Create an account",
                color = Emerald400
            )
        }
    }
}

@Composable
fun BrandLogo(modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(Emerald500),
            Alignment.Center
        ) { Text("♥", color = ForestGreen, fontSize = 26.sp) }
        Spacer(Modifier.width(12.dp))
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) { append("Charity") }
                withStyle(SpanStyle(color = LogoGreen)) { append("Link") }
            },
            fontFamily = PlayfairFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
    }
}