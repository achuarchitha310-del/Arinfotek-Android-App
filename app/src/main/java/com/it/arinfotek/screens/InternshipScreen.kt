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

// ---------- INTERNSHIP (+ pricing) ----------
data class Plan(val name: String, val price: String, val feats: List<String>, val btn: String, val rec: Boolean)

@Composable
fun InternshipScreen(go: (Screen) -> Unit) = Page {
    Header("Kickstart Your IT Career", "Internship Program 2026",
        "Gain real-world experience, build skills, and get industry exposure. Learn today, intern tomorrow, lead your future.", center = true)

    T("Internship Domains", 22, FontWeight.Bold, Dark); Gap(14)
    Grid2(listOf(
        "AI / ML" to "Explore AI concepts and build intelligent solutions.",
        "Cloud Computing" to "AWS & Azure. Work with leading cloud platforms and services.",
        "Data Science" to "Analyze data and extract meaningful insights.",
        "Full Stack Web Development" to "Build dynamic web apps from front-end to back-end.",
        "Python Programming" to "Build strong coding fundamentals and problem-solving skills.",
        "Mobile App Development" to "Create user-friendly Android applications.",
        "DevOps" to "Learn CI/CD, automate workflows and deploy like a pro.",
        "Vibe Coding" to "Use AI-powered tools to build apps faster with modern workflows."
    ))

    Gap(14); T("Why Join Our Internship?", 22, FontWeight.Bold, Dark); Gap(14)
    listOf(
        "Real-World Projects" to "Work on live projects and solve real industry problems.",
        "Industry Exposure" to "Learn tools, technologies & best practices used in top companies.",
        "Resume Building" to "Strengthen your resume with hands-on experience.",
        "Interview Preparation" to "Get expert guidance for technical & HR interviews.",
        "Internship Certificate" to "Earn a recognized certificate upon successful completion.",
        "Expert Mentorship" to "Learn from experienced industry professionals and grow."
    ).forEach { (t, d) ->
        Row(Modifier.padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFDBEAFE)).padding(8.dp)) {
                T("✓", 14, FontWeight.Black, Primary)
            }
            Column { T(t, 15, FontWeight.Bold, Slate800); T(d, 13) }
        }
    }

    Gap(10)
    AppCard(bg = Slate50) {
        T("Who Can Join?", 20, FontWeight.Bold, Dark); Gap(12)
        listOf("Students pursuing BE 2nd, 3rd and Final Year", "BCA / BSc 2nd & Final Year",
            "MCA 1st & 2nd Year", "Diploma Students").forEach {
            Row(Modifier.padding(bottom = 10.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(22.dp).clip(CircleShape).background(Primary), contentAlignment = Alignment.Center) {
                    T("✓", 11, FontWeight.Bold, Color.White)
                }
                T(it, 14, FontWeight.Medium, Color(0xFF334155))
            }
        }
    }
    Gap(16)
    AppCard(bg = Dark, border = Color(0xFF334155)) {
        Pill("SPECIAL PRICE", Accent, Color.White, 10); Gap(12)
        Row(verticalAlignment = Alignment.Bottom) {
            T("₹1999", 46, FontWeight.Black, Color.White)
            T(" ONLY", 18, FontWeight.Bold, Accent, modifier = Modifier.padding(bottom = 8.dp))
        }
        Gap(14)
        listOf("📅  Flexible Timings", "🖥  Live Online Sessions", "👥  Dedicated Mentor Support").forEach {
            T(it, 14, c = Color(0xFFDBEAFE), modifier = Modifier.padding(bottom = 10.dp))
        }
        Gap(8)
        OrangeBtn("Register Now", Modifier.fillMaxWidth()) { go(Screen.Contact) }
    }

    Gap(30)
    T("Simple, Transparent Pricing", 26, FontWeight.ExtraBold, Dark); Gap(16)
    listOf(
        Plan("Basic (Group)", "₹2,999", listOf("Live group sessions", "Assignments & quizzes", "Completion certificate"), "Join Group Batch", false),
        Plan("Standard (1:1)", "₹14,999", listOf("Everything in Basic", "1:1 Mentor Support", "Personalized Project"), "Start 1:1 Training", true),
        Plan("Advanced", "₹24,999", listOf("Everything in Standard", "Long-term Mentorship", "Dedicated Q&A"), "Join Advanced", false)
    ).forEach { p ->
        AppCard(border = if (p.rec) Accent else Slate200, bw = if (p.rec) 2 else 1) {
            if (p.rec) { Pill("RECOMMENDED", Accent, Color.White, 10); Gap(8) }
            T(p.name, 20, FontWeight.Bold, Dark); Gap(8)
            Row(verticalAlignment = Alignment.Bottom) {
                T(p.price, 30, FontWeight.Black, Primary)
                T("  / course", 13, c = Slate500, modifier = Modifier.padding(bottom = 4.dp))
            }
            Gap(12)
            p.feats.forEach { T("✓ $it", 14, if (p.rec) FontWeight.Medium else FontWeight.Normal,
                if (p.rec) Dark else Slate600, modifier = Modifier.padding(bottom = 8.dp)) }
            Gap(8)
            if (p.rec) OrangeBtn(p.btn, Modifier.fillMaxWidth()) { go(Screen.Contact) }
            else OutlineBtn(p.btn, Modifier.fillMaxWidth()) { go(Screen.Contact) }
        }
        Gap(16)
    }
}