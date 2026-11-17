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

// ---------- INNOVATION LABS ----------
data class Lab(val icon: String, val title: String, val cat: String, val desc: String, val tags: List<String>)

@Composable
fun LabsScreen(go: (Screen) -> Unit) = Page(Slate50) {
    Header("Innovation Labs", "Build the Future. Live.",
        "Theory doesn't build careers; code does. You will master these 4 cutting-edge industrial projects from scratch.", center = true)
    listOf(
        Lab("🤖", "Generative AI Bot", "AI & Machine Learning",
            "Build a custom RAG chatbot that reads company PDFs and answers queries using OpenAI GPT-4 & Vector DBs.", listOf("Python", "LangChain")),
        Lab("☁️", "Serverless Neo-Bank", "Cloud Computing",
            "Design an event-driven banking backend handling millions of transactions using AWS Lambda & DynamoDB streams.", listOf("AWS Lambda", "DynamoDB")),
        Lab("⚙️", "GitOps K8s Cluster", "DevOps & CI/CD",
            "Construct a fully automated pipeline. Code pushed to GitHub is deployed to a live K8s cluster with zero downtime.", listOf("Jenkins", "Kubernetes")),
        Lab("🛡️", "SOC Threat Hunter", "Cybersecurity",
            "Build a live SOC dashboard. Analyze real-time traffic to detect SQL Injections, DDoS, and unauthorized access.", listOf("Splunk", "Wireshark"))
    ).forEach { l ->
        AppCard {
            Box(Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)).background(Slate50), contentAlignment = Alignment.Center) {
                Text(l.icon, fontSize = 30.sp)
            }
            Gap(14); T(l.title, 20, FontWeight.Bold, Slate800)
            Gap(4); T(l.cat.uppercase(), 10, FontWeight.Bold, Slate500)
            Gap(10); T(l.desc, 14); Gap(14)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                l.tags.forEach { Pill(it, Slate100, Slate600, 10) }
            }
        }
        Gap(16)
    }
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(Modifier.clip(RoundedCornerShape(12.dp)).background(Dark).clickable { go(Screen.Contact) }
            .padding(horizontal = 24.dp, vertical = 14.dp)) {
            T("Download Project Syllabus", 14, FontWeight.Bold, Color.White)
        }
    }
    Gap(16)
}