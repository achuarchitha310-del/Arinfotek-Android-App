package com.it.arinfotek

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

private val ANavy = Color(0xFF0C1F3F)
private val ABorder = Color(0xFFD5DEEA)
private val AGreen = Color(0xFF22C55E)
private val ARed = Color(0xFFEF4444)
private val AGreenBg = Color(0xFFDCFCE7)
private val AGreenFg = Color(0xFF15803D)
private val ARedBg = Color(0xFFFEE2E2)
private val ARedFg = Color(0xFFB91C1C)
private val AHolidayBg = Color(0xFFFFEDD5)
private val AHolidayFg = Color(0xFFC2410C)
private val AWeekBg = Color(0xFFE2E8F0)
private val ABlueBg = Color(0xFFDBEAFE)
private val ABlueFg = Color(0xFF1D4ED8)

private val monthNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)

private fun JSONObject.str(key: String): String = if (isNull(key)) "" else optString(key)

// Is this location coming from a fake-GPS app?
@Suppress("DEPRECATION")
private fun isMockLocation(l: Location): Boolean =
    if (Build.VERSION.SDK_INT >= 31) l.isMock else l.isFromMockProvider

// Gets the best GPS fix in up to 15 seconds
@SuppressLint("MissingPermission")
private suspend fun getCurrentFix(ctx: Context): Location? {
    val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    if (!gps && !net) return null

    var best: Location? = null
    val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val b = best
            if (b == null || location.accuracy < b.accuracy) best = location
        }
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }
    try {
        if (gps) lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0f, listener, Looper.getMainLooper())
        if (net) lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 0f, listener, Looper.getMainLooper())
        var waited = 0
        while (waited < 15000) {
            delay(500)
            waited += 500
            val b = best
            if (b != null && b.accuracy <= 15f) break
        }
    } finally {
        lm.removeUpdates(listener)
    }
    return best
}

@Composable
private fun AttTitle(t: String) {
    T(t, 18, FontWeight.Bold, ANavy); Gap(10)
}

@Composable
private fun ReportBox(
    label: String,
    value: String,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(vertical = 12.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        T(value, 22, FontWeight.Bold, fg, a = TextAlign.Center)
        Gap(2)
        T(label, 12, FontWeight.SemiBold, fg, a = TextAlign.Center)
    }
}

// One square day in the calendar
@Composable
private fun DayCell(day: JSONObject?, isToday: Boolean, modifier: Modifier, onTap: (String) -> Unit) {
    if (day == null) {
        Spacer(modifier.padding(2.dp).aspectRatio(1f))
        return
    }
    val bg: Color
    val fg: Color
    when (day.str("type")) {
        "PRESENT" -> { bg = AGreen; fg = Color.White }
        "ABSENT" -> { bg = ARed; fg = Color.White }
        "HOLIDAY" -> { bg = AHolidayBg; fg = AHolidayFg }
        "WEEKOFF" -> { bg = AWeekBg; fg = Slate500 }
        else -> { bg = Color.White; fg = Color(0xFF334155) }
    }
    val shape = RoundedCornerShape(10.dp)
    val note = day.str("note")
    Box(
        modifier.padding(2.dp).aspectRatio(1f).clip(shape).background(bg)
            .then(if (isToday) Modifier.border(2.dp, Primary, shape) else Modifier)
            .clickable(enabled = note.isNotEmpty()) { onTap("${day.str("date")}\n$note") },
        contentAlignment = Alignment.Center
    ) { T(day.optInt("day").toString(), 14, FontWeight.SemiBold, fg, a = TextAlign.Center) }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(14.dp).clip(RoundedCornerShape(4.dp)).background(color))
        Spacer(Modifier.width(6.dp))
        T(label, 12, c = Slate500)
    }
}

// ---------- ATTENDANCE SCREEN ----------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AttendanceScreen(go: (Screen) -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val now = remember { Calendar.getInstance() }
    var year by remember { mutableStateOf(now.get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(now.get(Calendar.MONTH) + 1) }
    var loggedIn by remember { mutableStateOf(Session.token != null) }
    var cal by remember { mutableStateOf<JSONObject?>(null) }
    var holidays by remember { mutableStateOf<JSONArray?>(null) }
    var dialog by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("") }

    suspend fun loadCalendar() {
        val r = api("GET", "/attendance/calendar?year=$year&month=$month")
        loggedIn = Session.token != null
        if (r.json != null) cal = r.json else if (r.error != null) dialog = r.error
    }

    suspend fun loadHolidays() {
        val r = api("GET", "/holidays?year=$year")
        if (r.json != null) holidays = r.json.optJSONArray("holidays")
    }

    LaunchedEffect(year, month) { if (Session.token != null) loadCalendar() }
    LaunchedEffect(year) { if (Session.token != null) loadHolidays() }

    fun markNow() {
        scope.launch {
            busy = true
            statusText = "Getting your location..."
            val loc = getCurrentFix(ctx)
            if (loc == null) {
                dialog = "Could not get your location. Turn ON Location (GPS) and try again near a window or outside."
            } else {
                statusText = "Checking with server..."
                val body = JSONObject()
                    .put("latitude", loc.latitude)
                    .put("longitude", loc.longitude)
                    .put("accuracy", loc.accuracy.toDouble())
                    .put("mock", isMockLocation(loc))
                val r = api("POST", "/attendance/mark", body)
                val j = r.json
                dialog = if (r.error != null || j == null) (r.error ?: "Could not mark attendance.")
                else j.optString("message")
                loadCalendar()
            }
            busy = false
            statusText = ""
        }
    }

    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) markNow() else dialog = "Location permission is needed to mark attendance."
    }

    fun onMarkClick() {
        if (ctx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            markNow()
        } else {
            permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val c = cal
    Page(Color(0xFFF4F7FB)) {
        if (!loggedIn) {
            AppCard(border = ABorder) {
                T("Please login", 22, FontWeight.Bold, ANavy); Gap(6)
                T("Login to see and mark your attendance.", 14); Gap(16)
                OrangeBtn("Go to Login", Modifier.fillMaxWidth()) { go(Screen.Login) }
            }
        } else if (c == null) {
            T("Loading attendance...", 16, c = Slate500, a = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(40.dp))
        } else {
            val company = c.optJSONObject("company") ?: JSONObject()
            val today = c.optJSONObject("today") ?: JSONObject()
            val summary = c.optJSONObject("summary") ?: JSONObject()
            val days = c.optJSONArray("days") ?: JSONArray()
            val shown = c.optJSONObject("month") ?: JSONObject()
            val shownYear = shown.optInt("year", year)
            val shownMonth = shown.optInt("month", month)
            val monthTitle = "${monthNames[(shownMonth - 1).coerceIn(0, 11)]} $shownYear"
            val todayDate = today.str("date")
            val canMark = today.optBoolean("can_mark")
            val reason = today.str("reason")
            val st = today.str("status")

            val workingDays = summary.optInt("working_days")
            val presentDays = summary.optInt("present")
            val absentDays = summary.optInt("absent")
            val percentage = summary.optInt("percentage")

            // ---- Calendar ----
            AppCard(border = ABorder) {
                T("Attendance", 24, FontWeight.Bold, ANavy); Gap(2)
                T("${company.str("name")} • Allowed distance: ${company.optInt("radius_m")} m", 13, c = Slate500)
                Gap(16)

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    T("◀", 20, FontWeight.Bold, Primary, modifier = Modifier.clickable {
                        if (month == 1) { month = 12; year -= 1 } else { month -= 1 }
                    }.padding(10.dp))
                    T(monthTitle, 18, FontWeight.Bold, ANavy)
                    T("▶", 20, FontWeight.Bold, Primary, modifier = Modifier.clickable {
                        if (month == 12) { month = 1; year += 1 } else { month += 1 }
                    }.padding(10.dp))
                }
                Gap(8)

                Row(Modifier.fillMaxWidth()) {
                    listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                        T(it, 12, FontWeight.SemiBold, Slate500, a = TextAlign.Center, modifier = Modifier.weight(1f))
                    }
                }
                Gap(4)

                val firstDow = Calendar.getInstance().apply { set(shownYear, shownMonth - 1, 1) }
                    .get(Calendar.DAY_OF_WEEK) - 1          // Sunday = 0
                val cells = ArrayList<JSONObject?>()
                repeat(firstDow) { cells.add(null) }
                for (i in 0 until days.length()) cells.add(days.getJSONObject(i))
                cells.chunked(7).forEach { week ->
                    Row(Modifier.fillMaxWidth()) {
                        for (i in 0 until 7) {
                            val cell = week.getOrNull(i)
                            DayCell(cell, cell != null && cell.str("date") == todayDate, Modifier.weight(1f)) { dialog = it }
                        }
                    }
                }
                Gap(12)

                FlowRow(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LegendItem(AGreen, "Present")
                    LegendItem(ARed, "Absent")
                    LegendItem(AHolidayBg, "Public holiday")
                    LegendItem(AWeekBg, "Sat / Sun")
                }
            }
            Gap(16)

            // ---- Attendance Report ----
            AppCard(border = ABorder) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    T("Attendance Report", 18, FontWeight.Bold, ANavy)
                    Pill(monthTitle, ABlueBg, ABlueFg, 12)
                }
                Gap(12)

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ReportBox("Total Working Days", "$workingDays", ABlueBg, ABlueFg, Modifier.weight(1f))
                    ReportBox("Attendance %", "$percentage%", if (percentage >= 75) AGreenBg else ARedBg,
                        if (percentage >= 75) AGreenFg else ARedFg, Modifier.weight(1f))
                }
                Gap(10)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ReportBox("Total Present Days", "$presentDays", AGreenBg, AGreenFg, Modifier.weight(1f))
                    ReportBox("Total Absent Days", "$absentDays", ARedBg, ARedFg, Modifier.weight(1f))
                }
                Gap(12)

                // Progress bar
                val progress = (percentage.coerceIn(0, 100)) / 100f
                val barColor = if (percentage >= 75) AGreen else ARed
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE2E8F0))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(barColor)
                    )
                }
                Gap(8)
                T(
                    if (workingDays == 0) "No working days recorded yet for this month."
                    else if (percentage >= 75) "Good standing ($presentDays out of $workingDays working days attended)."
                    else "Low attendance ($presentDays out of $workingDays working days). Minimum 75% recommended.",
                    12,
                    c = Slate500
                )
            }
            Gap(16)

            // ---- Mark attendance ----
            AppCard(border = ABorder) {
                AttTitle("Mark Today's Attendance")
                if (st.isEmpty()) Pill("NOT MARKED", Color(0xFFF1F5F9), Slate600, 13)
                else Pill(st, if (st == "PRESENT") AGreenBg else ARedBg, if (st == "PRESENT") AGreenFg else ARedFg, 13)
                if (st.isNotEmpty()) {
                    Gap(6)
                    T("${today.str("time")} • ${today.optInt("distance_m")} m from company", 12, c = Slate500)
                }
                Gap(12)

                if (reason.isNotEmpty()) {
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(AHolidayBg).padding(12.dp)) {
                        T(reason + ". Attendance cannot be marked today.", 13, FontWeight.Medium, AHolidayFg)
                    }
                    Gap(12)
                }

                if (canMark) {
                    OrangeBtn(
                        if (busy) statusText.ifEmpty { "Please wait..." } else "Mark Attendance",
                        Modifier.fillMaxWidth()
                    ) { if (!busy) onMarkClick() }
                    Gap(8)
                    T("Stand inside the company area, turn ON Location (GPS), then tap the button.", 12, c = Slate500)
                } else {
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFCBD5E1))
                        .padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        T(if (st == "PRESENT") "Already marked PRESENT" else "Mark Attendance (not available today)",
                            14, FontWeight.Bold, Color.White, a = TextAlign.Center)
                    }
                }
            }
            Gap(16)

            // ---- Public holidays of the year ----
            AppCard(border = ABorder) {
                AttTitle("Public Holidays $year")
                val hs = holidays
                if (hs == null) {
                    T("Loading...", 14, c = Slate500)
                } else if (hs.length() == 0) {
                    T("No holidays added for $year yet.", 14, c = Slate500)
                } else {
                    for (i in 0 until hs.length()) {
                        val h = hs.getJSONObject(i)
                        val past = h.optBoolean("past")
                        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(0.42f)) {
                                T(h.str("date"), 13, FontWeight.SemiBold, if (past) Slate500 else ANavy)
                                T(h.str("weekday"), 12, c = Slate500)
                            }
                            T(h.str("name"), 14, c = if (past) Slate500 else Color(0xFF1B2430),
                                modifier = Modifier.weight(0.58f))
                        }
                    }
                }
            }
            Gap(24)
        }
    }

    dialog?.let { msg ->
        AlertDialog(
            onDismissRequest = { dialog = null },
            containerColor = Color.White,
            text = { Text(msg, color = Color(0xFF1B2430)) },
            confirmButton = { TextButton(onClick = { dialog = null }) { Text("OK", color = Color(0xFF1F5FBF)) } }
        )
    }
}