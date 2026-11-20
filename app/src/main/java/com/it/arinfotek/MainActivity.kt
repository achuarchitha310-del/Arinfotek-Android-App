package com.it.arinfotek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

enum class Screen(val title: String, val homeSectionIndex: Int? = null) {
    Home("Home", 0),
    Courses("Courses", 1),
    Why("Why Us", 2),
    Labs("Innovation Labs", 3),
    Internship("Internship", 4),
    Register("Registration Form", null),
    Login("Login", null),
    Profile("Profile", null),
    Attendance("Attendance", null),
    Contact("Talk to Us", 5)
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
    var activeScreen by remember { mutableStateOf(Screen.Home) }
    var highlightedSection by remember { mutableStateOf(Screen.Home) }
    val homeListState = rememberLazyListState()
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val go: (Screen) -> Unit = { target ->
        scope.launch {
            drawer.close()
            val sectionIdx = target.homeSectionIndex
            if (sectionIdx != null) {
                activeScreen = Screen.Home
                highlightedSection = target
                homeListState.animateScrollToItem(sectionIdx)
            } else {
                activeScreen = target
                highlightedSection = target
            }
        }
    }

    BackHandler(enabled = activeScreen != Screen.Home) {
        go(Screen.Home)
    }

    ModalNavigationDrawer(
        drawerState = drawer,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color.White) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    T("MENU", 18, FontWeight.Black, Primary)
                    Text(
                        "✕",
                        color = Slate500,
                        modifier = Modifier.clickable { scope.launch { drawer.close() } }
                    )
                }

                Screen.entries.filter { it != Screen.Contact }.forEach { s ->
                    NavigationDrawerItem(
                        label = { Text(s.title, fontWeight = FontWeight.Bold) },
                        selected = s == highlightedSection,
                        onClick = { go(s) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Slate100,
                            selectedTextColor = Primary,
                            unselectedTextColor = Slate600
                        )
                    )
                }

                Spacer(Modifier.weight(1f))

                Column(
                    Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
            Box(
                Modifier
                    .padding(pad)
                    .fillMaxSize()
            ) {
                when (activeScreen) {
                    Screen.Register -> RegisterScreen(onRegistered = { go(Screen.Login) })
                    Screen.Login -> LoginScreen(go)
                    Screen.Profile -> ProfileScreen(go)
                    Screen.Attendance -> AttendanceScreen(go)
                    else -> HomeScreen(go = go, listState = homeListState)
                }
            }
        }
    }
}