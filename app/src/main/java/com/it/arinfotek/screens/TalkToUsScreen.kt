package com.it.arinfotek

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- TALK TO US (CTA + form + info + footer) ----------
@Composable
fun ContactScreen(go: (Screen) -> Unit) = Page(Color.White, pad = 0) {
    Column(Modifier.padding(20.dp)) {
        // CTA banner
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
                .background(Brush.horizontalGradient(listOf(Primary, Color(0xFF2563EB)))).padding(22.dp)
        ) {
            T("Ready to start your journey?", 24, FontWeight.Black, Color.White)
            Gap(6); T("Get a free counselling call and course roadmap tailored to you.", 13, c = Color(0xFFDBEAFE))
            Gap(16)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.clip(RoundedCornerShape(12.dp)).background(Accent)
                    .padding(horizontal = 16.dp, vertical = 10.dp)) { T("Book a Call", 13, FontWeight.Bold, Color.White) }
                Box(Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White)
                    .clickable { go(Screen.Courses) }.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    T("Explore Programs", 13, FontWeight.Bold, Primary)
                }
            }
        }
        Gap(28)
        AdvisorForm()
        Gap(16)
        AppCard(bg = Slate50, border = Slate100) {
            T("Batches & Timings", 20, FontWeight.Bold, Dark); Gap(8)
            T("Weekday: Mon-Fri, 7–9 PM IST\nWeekend: Sat-Sun, 10 AM–1 PM IST", 14); Gap(12)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill("Live Online", Color.White, Slate800); Pill("Recordings Access", Color.White, Slate800)
            }
        }
        Gap(16)
        AppCard(bg = Slate50, border = Slate100) {
            T("Reach Us", 20, FontWeight.Bold, Dark); Gap(10)
            listOf("Email:" to "admissions@arinfotek.co.in", "Phone:" to "+91-9487107253", "WhatsApp:" to "+91-9487107253").forEach { (k, v) ->
                Row(Modifier.padding(bottom = 8.dp)) { T("$k ", 14, FontWeight.Bold, Primary); T(v, 14, c = Color(0xFF334155)) }
            }
        }
        Gap(28)
    }
    Footer()
}

@Composable
fun AdvisorForm() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("AWS Cloud Architect") }
    var open by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    val shape = RoundedCornerShape(12.dp)
    val colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)

    AppCard {
        T("Talk to an advisor", 24, FontWeight.Bold, Dark); Gap(16)
        OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Full Name") },
            placeholder = { Text("Your Name") }, singleLine = true, shape = shape, colors = colors)
        Gap(10)
        OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Email") },
            placeholder = { Text("you@example.com") }, singleLine = true, shape = shape, colors = colors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        Gap(10)
        OutlinedTextField(phone, { phone = it }, Modifier.fillMaxWidth(), label = { Text("Phone") },
            placeholder = { Text("+91-XXXX-XXXXXX") }, singleLine = true, shape = shape, colors = colors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
        Gap(10)
        T("Course Interest", 12, FontWeight.SemiBold, Dark); Gap(4)
        Box {
            Row(
                Modifier.fillMaxWidth().clip(shape).border(1.dp, Color(0xFFCBD5E1), shape)
                    .clickable { open = true }.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) { T(course, 14, c = Dark); T("▾", 14, c = Dark) }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                listOf("AWS Cloud Architect", "Data Science & ML", "Cloud & DevOps", "Cybersecurity").forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { course = it; open = false })
                }
            }
        }
        Gap(10)
        OutlinedTextField(message, { message = it }, Modifier.fillMaxWidth().height(120.dp), label = { Text("Message") },
            placeholder = { Text("Tell us about your goals...") }, shape = shape, colors = colors)
        Gap(14)
        Box(
            Modifier.fillMaxWidth().clip(shape).background(Primary).clickable {
                status = when {
                    name.isBlank() -> "Please enter your name."
                    !email.contains("@") -> "Please enter a valid email."
                    else -> "Thank you, $name! We'll reply within 24 hours."
                }
            }.padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) { T("Send Message", 15, FontWeight.Bold, Color.White) }
        Gap(8)
        T(status.ifEmpty { "We typically reply within 24 hours." }, 12, c = Slate500,
            modifier = Modifier.fillMaxWidth(), a = TextAlign.Center)
    }
}

@Composable
fun Footer() {
    Column(Modifier.fillMaxWidth().background(Primary).padding(24.dp)) {
        T("AR INFOTEK", 24, FontWeight.Black, Color.White); Gap(8)
        T("Practical, mentor-led online IT training to accelerate your career.", 13, c = Color(0xFFCBD5E1))
        Gap(22)
        Row(Modifier.fillMaxWidth()) {
            FooterCol("Programs", listOf("AWS Architect", "Data Science", "DevOps"), Modifier.weight(1f))
            FooterCol("Company", listOf("Why Us", "Instructors", "Testimonials"), Modifier.weight(1f))
            FooterCol("Legal", listOf("Terms", "Privacy"), Modifier.weight(1f))
        }
        Gap(24)
        T("© 2025 AR INFOTEK – All rights reserved.", 11, c = Color(0xFF94A3B8), modifier = Modifier.fillMaxWidth(),
            a = TextAlign.Center)
    }
}

@Composable
fun FooterCol(title: String, items: List<String>, modifier: Modifier = Modifier) {
    Column(modifier) {
        T(title, 14, FontWeight.Bold, Accent); Gap(8)
        items.forEach { T(it, 12, c = Color(0xFFE2E8F0), modifier = Modifier.padding(bottom = 6.dp)) }
    }
}