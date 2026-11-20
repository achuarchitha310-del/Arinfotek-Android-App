package com.it.arinfotek

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- UNIFIED SCROLLABLE HOME SCREEN ----------
@Composable
fun HomeScreen(
    go: (Screen) -> Unit,
    listState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Index 0: Hero / Home Section
        item(key = "home") {
            HeroHomeSection(go)
        }

        // Index 1: Courses Section
        item(key = "courses") {
            CoursesSection(go)
        }

        // Index 2: Why Us Section
        item(key = "why_us") {
            WhyUsSection()
        }

        // Index 3: Innovation Labs Section
        item(key = "labs") {
            InnovationLabsSection(go)
        }

        // Index 4: Internship + Pricing Section
        item(key = "internship") {
            InternshipSection(go)
        }

        // Index 5: Talk to Us + Footer Section
        item(key = "contact") {
            TalkToUsSection(go)
        }
    }
}

@Composable
private fun HeroHomeSection(go: (Screen) -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(HeroBrush)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
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
            Box(
                Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(horizontal = 18.dp, vertical = 6.dp)
            ) {
                T("AR INFOTEK", 32, FontWeight.Black, Accent)
            }
            Gap(18)
            T(
                "Live instructor-led training in AI/ML, Data Science, DevOps, Cloud (AWS, Azure), Python, and Power BI. Learn by doing.",
                15, FontWeight.Medium, Color(0xFFDBEAFE), a = TextAlign.Center
            )
            Gap(22)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OrangeBtn("Browse Courses", s = 13) { go(Screen.Courses) }
                OutlineBtn(
                    "Get Counselling",
                    c = Color.White.copy(alpha = 0.4f),
                    bg = Color.White.copy(alpha = 0.1f),
                    s = 13
                ) { go(Screen.Contact) }
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
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    T("Upcoming Batches", 18, FontWeight.Bold, Slate800)
                    Text("📅", fontSize = 18.sp)
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
private fun CoursesSection(go: (Screen) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Slate50)
            .padding(20.dp)
    ) {
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
}

@Composable
private fun WhyUsSection() {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Header(
            "",
            "Why AR INFOTEK?",
            "We designed every course to maximize learning speed and career impact.",
            center = true
        )
        listOf(
            "Live & Interactive" to "No boring recordings. Ask doubts live and collaborate in real time with mentors.",
            "Project-First" to "Ship real apps, dashboards, and deployments you can actually show in interviews.",
            "Industry Experts" to "Learn directly from senior professionals with deep real-world expertise.",
            "Small Cohorts" to "Stay accountable with peer learning and dedicated mentor attention.",
            "Flexible Schedules" to "Weekend and weekday batches designed to fit your busy routine.",
            "Certificates" to "Get recognized for every skill you master with our course completion certificates."
        ).forEach { (t, d) ->
            AppCard(bg = Slate50, border = Slate100) {
                T(t, 18, FontWeight.Bold, Primary); Gap(6); T(d, 14)
            }
            Gap(12)
        }
        Gap(20)
        T("What our learners say", 24, FontWeight.ExtraBold, Dark); Gap(14)
        listOf(
            Triple(
                "The AWS Cloud Architect course's labs on VPC and IAM helped me design and deploy a production-ready architecture.",
                "Harish Kumar",
                "Cloud Engineer @ StartupX"
            ),
            Triple(
                "Data Science & ML with Python, Pandas, and model building gave me true end-to-end skills for my new job.",
                "Vikram Rao",
                "Data Analyst @ RetailCorp"
            ),
            Triple(
                "The Cloud & DevOps track's Docker/Kubernetes pipelines and CI/CD practice made real deployments feel easy.",
                "Neha Sharma",
                "DevOps Engineer @ FinTechCo"
            )
        ).forEach { (q, n, r) ->
            AppCard(bg = Color.White, border = Slate100) {
                Text("\"$q\"", fontSize = 14.sp, fontStyle = FontStyle.Italic, color = Slate600, lineHeight = 20.sp)
                Gap(12); T(n, 14, FontWeight.Bold, Dark); T(r, 12, c = Primary)
            }
            Gap(12)
        }
    }
}

@Composable
private fun InnovationLabsSection(go: (Screen) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Slate50)
            .padding(20.dp)
    ) {
        Header(
            "Innovation Labs",
            "Build the Future. Live.",
            "Theory doesn't build careers; code does. You will master these 4 cutting-edge industrial projects from scratch.",
            center = true
        )
        listOf(
            Lab(
                "🤖", "Generative AI Bot", "AI & Machine Learning",
                "Build a custom RAG chatbot that reads company PDFs and answers queries using OpenAI GPT-4 & Vector DBs.",
                listOf("Python", "LangChain")
            ),
            Lab(
                "☁️", "Serverless Neo-Bank", "Cloud Computing",
                "Design an event-driven banking backend handling millions of transactions using AWS Lambda & DynamoDB streams.",
                listOf("AWS Lambda", "DynamoDB")
            ),
            Lab(
                "⚙️", "GitOps K8s Cluster", "DevOps & CI/CD",
                "Construct a fully automated pipeline. Code pushed to GitHub is deployed to a live K8s cluster with zero downtime.",
                listOf("Jenkins", "Kubernetes")
            ),
            Lab(
                "🛡️", "SOC Threat Hunter", "Cybersecurity",
                "Build a live SOC dashboard. Analyze real-time traffic to detect SQL Injections, DDoS, and unauthorized access.",
                listOf("Splunk", "Wireshark")
            )
        ).forEach { l ->
            AppCard {
                Box(
                    Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate50),
                    contentAlignment = Alignment.Center
                ) {
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
            Box(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Dark)
                    .clickable { go(Screen.Contact) }
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                T("Download Project Syllabus", 14, FontWeight.Bold, Color.White)
            }
        }
        Gap(16)
    }
}

@Composable
private fun InternshipSection(go: (Screen) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Header(
            "Kickstart Your IT Career",
            "Internship Program 2026",
            "Gain real-world experience, build skills, and get industry exposure. Learn today, intern tomorrow, lead your future.",
            center = true
        )

        T("Internship Domains", 22, FontWeight.Bold, Dark); Gap(14)
        Grid2(
            listOf(
                "AI / ML" to "Explore AI concepts and build intelligent solutions.",
                "Cloud Computing" to "AWS & Azure. Work with leading cloud platforms and services.",
                "Data Science" to "Analyze data and extract meaningful insights.",
                "Full Stack Web Development" to "Build dynamic web apps from front-end to back-end.",
                "Python Programming" to "Build strong coding fundamentals and problem-solving skills.",
                "Mobile App Development" to "Create user-friendly Android applications.",
                "DevOps" to "Learn CI/CD, automate workflows and deploy like a pro.",
                "Vibe Coding" to "Use AI-powered tools to build apps faster with modern workflows."
            )
        )

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
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDBEAFE))
                        .padding(8.dp)
                ) {
                    T("✓", 14, FontWeight.Black, Primary)
                }
                Column { T(t, 15, FontWeight.Bold, Slate800); T(d, 13) }
            }
        }

        Gap(10)
        AppCard(bg = Slate50) {
            T("Who Can Join?", 20, FontWeight.Bold, Dark); Gap(12)
            listOf(
                "Students pursuing BE 2nd, 3rd and Final Year",
                "BCA / BSc 2nd & Final Year",
                "MCA 1st & 2nd Year",
                "Diploma Students"
            ).forEach {
                Row(
                    Modifier.padding(bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
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
                if (p.rec) {
                    Pill("RECOMMENDED", Accent, Color.White, 10); Gap(8)
                }
                T(p.name, 20, FontWeight.Bold, Dark); Gap(8)
                Row(verticalAlignment = Alignment.Bottom) {
                    T(p.price, 30, FontWeight.Black, Primary)
                    T("  / course", 13, c = Slate500, modifier = Modifier.padding(bottom = 4.dp))
                }
                Gap(12)
                p.feats.forEach {
                    T(
                        "✓ $it",
                        14,
                        if (p.rec) FontWeight.Medium else FontWeight.Normal,
                        if (p.rec) Dark else Slate600,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Gap(8)
                if (p.rec) OrangeBtn(p.btn, Modifier.fillMaxWidth()) { go(Screen.Contact) }
                else OutlineBtn(p.btn, Modifier.fillMaxWidth()) { go(Screen.Contact) }
            }
            Gap(16)
        }
    }
}

@Composable
private fun TalkToUsSection(go: (Screen) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Column(Modifier.padding(20.dp)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.horizontalGradient(listOf(Primary, Color(0xFF2563EB))))
                    .padding(22.dp)
            ) {
                T("Ready to start your journey?", 24, FontWeight.Black, Color.White)
                Gap(6)
                T("Get a free counselling call and course roadmap tailored to you.", 13, c = Color(0xFFDBEAFE))
                Gap(16)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Accent)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) { T("Book a Call", 13, FontWeight.Bold, Color.White) }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable { go(Screen.Courses) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
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
                    Pill("Live Online", Color.White, Slate800)
                    Pill("Recordings Access", Color.White, Slate800)
                }
            }
            Gap(16)
            AppCard(bg = Slate50, border = Slate100) {
                T("Reach Us", 20, FontWeight.Bold, Dark); Gap(10)
                listOf(
                    "Email:" to "admissions@arinfotek.co.in",
                    "Phone:" to "+91-9487107253",
                    "WhatsApp:" to "+91-9487107253"
                ).forEach { (k, v) ->
                    Row(Modifier.padding(bottom = 8.dp)) {
                        T("$k ", 14, FontWeight.Bold, Primary)
                        T(v, 14, c = Color(0xFF334155))
                    }
                }
            }
            Gap(28)
        }
        Footer()
    }
}

@Composable
fun BatchRow(name: String, date: String, onEnroll: () -> Unit) {
    val sh = RoundedCornerShape(16.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(sh)
            .background(Color.White)
            .border(1.dp, Slate100, sh)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            T(name, 14, FontWeight.Bold, Slate800)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                )
                Spacer(Modifier.width(5.dp))
                T(date, 11, FontWeight.Medium, Slate500)
            }
        }
        Box(
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Accent)
                .clickable(onClick = onEnroll)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) { T("Enroll", 10, FontWeight.Bold, Color.White) }
    }
}