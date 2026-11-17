package com.it.arinfotek

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(go: (Screen) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var dialog by remember { mutableStateOf<String?>(null) }
    var loggedIn by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Page(Color(0xFFF4F7FB)) {
        AppCard(border = Color(0xFFD5DEEA)) {
            T("Welcome back", 24, FontWeight.Bold, Color(0xFF0C1F3F))
            Gap(4)
            T("Login to your AR INFOTEK account.", 14, c = Color(0xFF64748B))
            Gap(22)

            FormField("Username (Email or Phone)", username, "name@gmail.com",
                { username = it.take(100) }, keyboard = KeyboardType.Text)
            FormField("Password", password, "Enter your password",
                { password = it.take(64) }, keyboard = KeyboardType.Password, password = true)

            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF1F5FBF), Color(0xFF174A96))))
                    .clickable(enabled = !loading) {
                        if (username.isBlank()) {
                            dialog = "Please enter your username."
                        } else if (password.isEmpty()) {
                            dialog = "Please enter your password."
                        } else {
                            scope.launch {
                                loading = true
                                val result = sendLogin(username.trim(), password)
                                loading = false
                                if (result.error == null) {
                                    loggedIn = true
                                    dialog = "Login successful! Welcome, ${result.name}."
                                    password = ""
                                } else {
                                    dialog = result.error
                                }
                            }
                        }
                    }.padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) { T(if (loading) "Please wait..." else "Login", 16, FontWeight.SemiBold, Color.White) }

            Gap(18)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                T("New user?  ", 14, c = Color(0xFF64748B))
                T("Register here", 14, FontWeight.Bold, Primary,
                    modifier = Modifier.clickable { go(Screen.Register) })
            }
        }
        Gap(20)
    }

    dialog?.let { msg ->
        val close: () -> Unit = {
            dialog = null
            if (loggedIn) { loggedIn = false; username = ""; go(Screen.Profile) }
        }
        AlertDialog(
            onDismissRequest = close,
            containerColor = Color.White,
            text = { Text(msg, color = Color(0xFF1B2430)) },
            confirmButton = { TextButton(onClick = close) { Text("OK", color = Color(0xFF1F5FBF)) } }
        )
    }
}