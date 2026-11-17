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

// ---------- WHY US (+ testimonials) ----------
@Composable
fun WhyScreen() = Page {
    Header("", "Why AR INFOTEK?", "We designed every course to maximize learning speed and career impact.", center = true)
    listOf(
        "Live & Interactive" to "No boring recordings. Ask doubts live and collaborate in real time with mentors.",
        "Project-First" to "Ship real apps, dashboards, and deployments you can actually show in interviews.",
        "Industry Experts" to "Learn directly from senior professionals with deep real-world expertise.",
        "Small Cohorts" to "Stay accountable with peer learning and dedicated mentor attention.",
        "Flexible Schedules" to "Weekend and weekday batches designed to fit your busy routine.",
        "Certificates" to "Get recognized for every skill you master with our course completion certificates."
    ).forEach { (t, d) ->
        AppCard(bg = Slate50, border = Slate100) { T(t, 18, FontWeight.Bold, Primary); Gap(6); T(d, 14) }
        Gap(12)
    }
    Gap(20)
    T("What our learners say", 24, FontWeight.ExtraBold, Dark); Gap(14)
    listOf(
        Triple("The AWS Cloud Architect course's labs on VPC and IAM helped me design and deploy a production-ready architecture.", "Harish Kumar", "Cloud Engineer @ StartupX"),
        Triple("Data Science & ML with Python, Pandas, and model building gave me true end-to-end skills for my new job.", "Vikram Rao", "Data Analyst @ RetailCorp"),
        Triple("The Cloud & DevOps track's Docker/Kubernetes pipelines and CI/CD practice made real deployments feel easy.", "Neha Sharma", "DevOps Engineer @ FinTechCo")
    ).forEach { (q, n, r) ->
        AppCard(bg = Color.White, border = Slate100) {
            Text("\"$q\"", fontSize = 14.sp, fontStyle = FontStyle.Italic, color = Slate600, lineHeight = 20.sp)
            Gap(12); T(n, 14, FontWeight.Bold, Dark); T(r, 12, c = Primary)
        }
        Gap(12)
    }
}