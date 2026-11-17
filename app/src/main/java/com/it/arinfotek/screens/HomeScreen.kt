package com.it.arinfotek

import androidx.compose.foundation.BorderStroke
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

// ---------- HOME ----------
@Composable
fun HomeScreen(go: (Screen) -> Unit) {
    Box(Modifier.fillMaxSize().background(HeroBrush)) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Gap(16)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Pill(
                    t = "LIVE • ONLINE",
                    bg = Color.Transparent,
                    fg = Color.White,
                    s = 10,
                    border = BorderStroke(1.dp, Color.LightGray)
                )
                Pill(
                    t = "SMALL COHORTS",
                    bg = Color.Transparent,
                    fg = Color.White,
                    s = 10,
                    border = BorderStroke(1.dp, Color.LightGray)
                )
            }
            Gap(20)
            T("Level-Up Your Tech Career with", 32, FontWeight.Black, Color.White, a = TextAlign.Center)
            Gap(14)
            Box(Modifier.clip(RoundedCornerShape(14.dp)).background(Color.White).padding(horizontal = 18.dp, vertical = 6.dp)) {
                T("AR INFOTEK", 32, FontWeight.Black, Accent)
            }
            Gap(18)
            T("Live instructor-led training in AI/ML, Data Science, DevOps, Cloud (AWS, Azure), Python, and Power BI. Learn by doing.",
                15, FontWeight.Medium, Color(0xFFDBEAFE), a = TextAlign.Center)
            Gap(22)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OrangeBtn("Browse Courses", s = 13) { go(Screen.Courses) }
                OutlineBtn("Get Counselling", c = Color.White.copy(alpha = 0.4f), bg = Color.White.copy(alpha = 0.1f), s = 13) { go(Screen.Contact) }
            }
            Gap(22)
            Row(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                listOf("3k+" to "LEARNERS", "4.8/5" to "RATING", "100%" to "PRACTICAL").forEach { (v, l) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        T(v, 24, FontWeight.Black, Color.White)
                        T(l, 10, FontWeight.Bold, Color(0xFFBFDBFE))
                    }
                }
            }
            Gap(28)
            AppCard(bg = Color.White.copy(alpha = 0.95f), border = Color.White.copy(alpha = 0.4f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    T("Upcoming Batches", 18, FontWeight.Bold, Slate800); Text("📅", fontSize = 18.sp)
                }
                Gap(14)
                BatchRow("AWS Architect", "25 Nov • Weekdays") { go(Screen.Contact) }
                Gap(10)
                BatchRow("Data Science", "02 Dec • Weekends") { go(Screen.Contact) }
                Gap(10)
                BatchRow("DevOps Master", "09 Dec • Weekdays") { go(Screen.Contact) }
                Gap(16)
                OutlineBtn("REQUEST SYLLABUS", Modifier.fillMaxWidth(), s = 12) { go(Screen.Contact) }
            }
            Gap(24)
        }
    }
}

@Composable
fun BatchRow(name: String, date: String, onEnroll: () -> Unit) {
    val sh = RoundedCornerShape(16.dp)
    Row(
        Modifier.fillMaxWidth().clip(sh).background(Color.White).border(1.dp, Slate100, sh).padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            T(name, 14, FontWeight.Bold, Slate800)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF22C55E)))
                Spacer(Modifier.width(5.dp))
                T(date, 11, FontWeight.Medium, Slate500)
            }
        }
        Box(Modifier.clip(RoundedCornerShape(8.dp)).background(Accent).clickable(onClick = onEnroll)
            .padding(horizontal = 12.dp, vertical = 6.dp)) { T("Enroll", 10, FontWeight.Bold, Color.White) }
    }
}