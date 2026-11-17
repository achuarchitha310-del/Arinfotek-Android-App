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

// ---------- COURSES ----------
data class Course(val badge: String, val bg: Color, val fg: Color, val title: String,
                  val desc: String, val weeks: Int, val level: String)

val courses = listOf(
    Course("Industry Focused", Color(0xFFDCFCE7), Color(0xFF15803D), "AWS Cloud Architect",
        "Design scalable architectures, VPC, IAM, EC2, Load Balancing & Security.", 16, "Beginner to Advanced"),
    Course("Most Popular", Color(0xFFF3E8FF), Color(0xFF7E22CE), "Data Science & ML",
        "Python, Pandas, Machine Learning models, and MLOps overview.", 14, "Intermediate"),
    Course("Career Switch", Color(0xFFDBEAFE), Color(0xFF1D4ED8), "Cloud & DevOps",
        "AWS/Azure basics, Docker, Kubernetes, CI/CD pipelines & Terraform.", 12, "Intermediate"),
    Course("New", Color(0xFFFEF9C3), Color(0xFF854D0E), "Cybersecurity",
        "Network security, SIEM, threat modeling, and Blue Team labs.", 10, "Beginner")
)

@Composable
fun CoursesScreen(go: (Screen) -> Unit) = Page(Slate50) {
    Header("Our Programs", "In-Demand Tech Skills")
    courses.forEach { c ->
        AppCard {
            Pill(c.badge, c.bg, c.fg); Gap(10)
            T(c.title, 20, FontWeight.Bold, Dark); Gap(6)
            T(c.desc, 14); Gap(10)
            T("• ${c.weeks} Weeks Duration", 12, c = Slate500)
            T("• ${c.level}", 12, c = Slate500); Gap(14)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OrangeBtn("Enroll", Modifier.weight(1f)) { go(Screen.Contact) }
                OutlineBtn("Syllabus", Modifier.weight(1f)) { go(Screen.Contact) }
            }
        }
        Gap(16)
    }
}