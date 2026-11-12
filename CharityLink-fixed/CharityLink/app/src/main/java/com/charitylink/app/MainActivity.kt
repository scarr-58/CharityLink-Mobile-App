package com.charitylink.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.charitylink.app.data.User
import com.charitylink.app.ui.theme.CharityLinkTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("charitylink", MODE_PRIVATE)
        val gson = Gson()

        setContent {
            CharityLinkTheme {
                val vm: AppViewModel = viewModel()

                var loggedIn by remember {
                    if (vm.user == null) {
                        prefs.getString("user", null)?.let { json ->
                            vm.user = try { gson.fromJson(json, User::class.java) } catch (e: Exception) { null }
                        }
                    }
                    mutableStateOf(vm.user != null)
                }

                if (!loggedIn) {
                    Scaffold { pad ->
                        Box(Modifier.padding(pad)) {
                            LoginScreen(vm) {
                                prefs.edit().putString("user", gson.toJson(vm.user)).apply()
                                loggedIn = true
                            }
                        }
                    }
                } else {
                    MainScreen(vm) {
                        prefs.edit().remove("user").apply()
                        loggedIn = false
                    }
                }
            }
        }
    }
}