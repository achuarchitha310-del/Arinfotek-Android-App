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

    // Forgot Password Dialog States
    var showForgotDialog by remember { mutableStateOf(false) }
    var resetStep by remember { mutableStateOf(1) } // 1 = Enter Email, 2 = Enter OTP & New Password
    var resetEmail by remember { mutableStateOf("") }
    var resetOtp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var resetLoading by remember { mutableStateOf(false) }
    var resetError by remember { mutableStateOf<String?>(null) }
    var resetInfo by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Page(Color(0xFFF4F7FB)) {
        AppCard(border = Color(0xFFD5DEEA)) {
            T("Welcome back", 24, FontWeight.Bold, Color(0xFF0C1F3F))
            Gap(4)
            T("Login to your AR INFOTEK account.", 14, c = Color(0xFF64748B))
            Gap(22)

            FormField(
                "Username (Email or Phone)", username, "name@gmail.com",
                { username = it.take(100) }, keyboard = KeyboardType.Text
            )
            FormField(
                "Password", password, "Enter your password",
                { password = it.take(64) }, keyboard = KeyboardType.Password, password = true
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.End
            ) {
                T(
                    "Forgot Password?",
                    13,
                    FontWeight.Bold,
                    Primary,
                    modifier = Modifier.clickable {
                        resetEmail = if (username.contains("@")) username.trim() else ""
                        resetOtp = ""
                        newPassword = ""
                        confirmPassword = ""
                        resetError = null
                        resetInfo = null
                        resetStep = 1
                        showForgotDialog = true
                    }
                )
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
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
                    }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) { T(if (loading) "Please wait..." else "Login", 16, FontWeight.SemiBold, Color.White) }

            Gap(18)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                T("New user?  ", 14, c = Color(0xFF64748B))
                T(
                    "Register here", 14, FontWeight.Bold, Primary,
                    modifier = Modifier.clickable { go(Screen.Register) }
                )
            }
        }
        Gap(20)
    }

    // Login Alert Dialog
    dialog?.let { msg ->
        val close: () -> Unit = {
            dialog = null
            if (loggedIn) {
                loggedIn = false
                username = ""
                go(Screen.Profile)
            }
        }
        AlertDialog(
            onDismissRequest = close,
            containerColor = Color.White,
            text = { Text(msg, color = Color(0xFF1B2430)) },
            confirmButton = { TextButton(onClick = close) { Text("OK", color = Color(0xFF1F5FBF)) } }
        )
    }

    // Forgot Password Email OTP Dialog
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { if (!resetLoading) showForgotDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    if (resetStep == 1) "Reset Password" else "Verify OTP & Set Password",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0C1F3F)
                )
            },
            text = {
                Column {
                    if (resetStep == 1) {
                        T(
                            "Enter your registered email address. We will send a 6-digit OTP to reset your password.",
                            13,
                            c = Color(0xFF64748B)
                        )
                        Gap(12)
                        FormField(
                            "Registered Email",
                            resetEmail,
                            "name@gmail.com",
                            { resetEmail = it.take(100); resetError = null },
                            keyboard = KeyboardType.Email
                        )
                    } else {
                        T(
                            "Enter the 6-digit OTP sent to $resetEmail and choose a new password.",
                            13,
                            c = Color(0xFF64748B)
                        )
                        Gap(12)
                        FormField(
                            "6-Digit OTP",
                            resetOtp,
                            "123456",
                            { resetOtp = it.filter { ch -> ch.isDigit() }.take(6); resetError = null },
                            keyboard = KeyboardType.Number
                        )
                        FormField(
                            "New Password",
                            newPassword,
                            "Min 4 characters",
                            { newPassword = it.take(64); resetError = null },
                            keyboard = KeyboardType.Password,
                            password = true
                        )
                        FormField(
                            "Confirm Password",
                            confirmPassword,
                            "Re-enter new password",
                            { confirmPassword = it.take(64); resetError = null },
                            keyboard = KeyboardType.Password,
                            password = true
                        )
                    }

                    resetInfo?.let {
                        Gap(4)
                        T(it, 12, FontWeight.SemiBold, Color(0xFF15803D))
                    }
                    resetError?.let {
                        Gap(4)
                        T(it, 12, FontWeight.SemiBold, Color(0xFFDC2626))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !resetLoading,
                    onClick = {
                        if (resetStep == 1) {
                            if (resetEmail.isBlank() || !resetEmail.contains("@")) {
                                resetError = "Please enter a valid email address."
                                return@TextButton
                            }
                            scope.launch {
                                resetLoading = true
                                resetError = null
                                resetInfo = null
                                val res = sendForgotPasswordOtp(resetEmail)
                                resetLoading = false
                                if (res.error == null) {
                                    resetInfo = res.json?.optString("message") ?: "OTP sent to your email!"
                                    resetStep = 2
                                } else {
                                    resetError = res.error
                                }
                            }
                        } else {
                            if (resetOtp.length != 6) {
                                resetError = "Please enter the 6-digit OTP."
                                return@TextButton
                            }
                            if (newPassword.length < 4) {
                                resetError = "Password must be at least 4 characters."
                                return@TextButton
                            }
                            if (newPassword != confirmPassword) {
                                resetError = "Passwords do not match."
                                return@TextButton
                            }
                            scope.launch {
                                resetLoading = true
                                resetError = null
                                val res = resetPasswordWithOtp(resetEmail, resetOtp, newPassword)
                                resetLoading = false
                                if (res.error == null) {
                                    showForgotDialog = false
                                    username = resetEmail.trim()
                                    password = ""
                                    dialog = res.json?.optString("message")
                                        ?: "Password updated successfully! Please login."
                                } else {
                                    resetError = res.error
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        when {
                            resetLoading -> "Please wait..."
                            resetStep == 1 -> "Send OTP"
                            else -> "Reset Password"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F5FBF)
                    )
                }
            },
            dismissButton = {
                if (!resetLoading) {
                    TextButton(
                        onClick = {
                            if (resetStep == 2) {
                                resetStep = 1
                                resetError = null
                                resetInfo = null
                            } else {
                                showForgotDialog = false
                            }
                        }
                    ) {
                        Text(if (resetStep == 2) "Back" else "Cancel", color = Color(0xFF64748B))
                    }
                }
            }
        )
    }
}