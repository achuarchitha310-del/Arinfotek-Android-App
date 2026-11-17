package com.it.arinfotek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
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
import kotlinx.coroutines.launch

enum class Screen(val title: String) {
    Home("Home"), Courses("Courses"), Why("Why Us"),
    Labs("Innovation Labs"), Internship("Internship"),
    Register("Registration Form"), Login("Login"), Profile("Profile"), Attendance("Attendance"), Contact("Talk to Us")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { App() } }
    }
}

// ---------- App shell: top bar + drawer menu ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var screen by remember { mutableStateOf(Screen.Home) }
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val go: (Screen) -> Unit = { screen = it; scope.launch { drawer.close() } }

    BackHandler(enabled = screen != Screen.Home) { screen = Screen.Home }

    ModalNavigationDrawer(
        drawerState = drawer,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color.White) {
                Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    T("MENU", 18, FontWeight.Black, Primary)
                    Text("✕", color = Slate500, modifier = Modifier.clickable { scope.launch { drawer.close() } })
                }
                // Talk to Us is NOT in this list anymore (it is the button at the bottom)
                Screen.entries.filter { it != Screen.Contact }.forEach { s ->
                    NavigationDrawerItem(
                        label = { Text(s.title, fontWeight = FontWeight.Bold) },
                        selected = s == screen,
                        onClick = { go(s) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Slate100, selectedTextColor = Primary,
                            unselectedTextColor = Slate600
                        )
                    )
                }
                Spacer(Modifier.weight(1f))
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlineBtn("Talk to Us", Modifier.fillMaxWidth()) { go(Screen.Contact) }
                    OrangeBtn("View Courses", Modifier.fillMaxWidth()) { go(Screen.Courses) }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Box(Modifier.clickable { go(Screen.Home) }) { Logo() } },
                    actions = {
                        IconButton(onClick = { scope.launch { drawer.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Slate600)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { pad ->
            Box(Modifier.padding(pad).fillMaxSize()) {
                when (screen) {
                    Screen.Home -> HomeScreen(go)
                    Screen.Courses -> CoursesScreen(go)
                    Screen.Why -> WhyScreen()
                    Screen.Labs -> LabsScreen(go)
                    Screen.Internship -> InternshipScreen(go)
                    Screen.Register -> RegisterScreen(onRegistered = { go(Screen.Login) })
                    Screen.Login -> LoginScreen(go)
                    Screen.Profile -> ProfileScreen(go)
                    Screen.Attendance -> AttendanceScreen(go)
                    Screen.Contact -> ContactScreen(go)
                }
            }
        }
    }
}