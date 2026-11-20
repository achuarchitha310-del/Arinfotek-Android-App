package com.it.arinfotek

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

private val PNavy = Color(0xFF0C1F3F)
private val PBorder = Color(0xFFD5DEEA)
private const val MAX_FILE_BYTES = 200 * 1024 // 200 KB
private val activityOptions = listOf(
    "Studying", "Learning at AR INFOTEK", "Doing an internship", "Looking for a job", "Working"
)

// ---------- helpers ----------
private fun JSONObject.str(key: String): String = if (isNull(key)) "" else optString(key)

private fun decodeImage(b64: String): ImageBitmap? {
    if (b64.isEmpty()) return null
    return try {
        val bytes = Base64.decode(b64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

// Picked photo -> small JPEG (max 400 px) -> base64
private fun encodePhoto(ctx: Context, uri: Uri): String? {
    return try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        ctx.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (bounds.outWidth / sample > 800) sample *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val bmp = ctx.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
            ?: return null
        val scale = 400f / maxOf(bmp.width, bmp.height)
        val out = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                bmp, (bmp.width * scale).toInt().coerceAtLeast(1), (bmp.height * scale).toInt().coerceAtLeast(1), true
            )
        } else bmp
        val bos = ByteArrayOutputStream()
        out.compress(Bitmap.CompressFormat.JPEG, 80, bos)
        Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP)
    } catch (e: Exception) {
        null
    }
}

private class PickedFile(val name: String, val b64: String?)

// Picked file -> base64 (max 200 KB)
private fun readPickedFile(ctx: Context, uri: Uri): PickedFile? {
    return try {
        var name = "file"
        var size = -1L
        ctx.contentResolver.query(uri, null, null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val ni = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (ni >= 0) name = c.getString(ni)
                val si = c.getColumnIndex(OpenableColumns.SIZE)
                if (si >= 0) size = c.getLong(si)
            }
        }
        if (size > MAX_FILE_BYTES) return PickedFile(name, null)
        val bytes = ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        if (bytes == null || bytes.size > MAX_FILE_BYTES) PickedFile(name, null)
        else PickedFile(name, Base64.encodeToString(bytes, Base64.NO_WRAP))
    } catch (e: Exception) {
        null
    }
}

private fun validateEdit(name: String, phone: String, college: String, city: String, address: String): String? {
    if (!Regex("^[A-Za-z ]{3,}$").matches(name.trim())) return "Name must be at least 3 letters (letters and spaces only)."
    if (!Regex("^[6-9]\\d{9}$").matches(phone)) return "Phone must be 10 digits and start with 6, 7, 8 or 9."
    if (college.isBlank()) return "Please enter your college name."
    if (city.isBlank()) return "Please enter your city."
    if (address.trim().length < 10) return "Address is too short. Please enter your full address."
    return null
}

@Composable
private fun SectionTitle(t: String) {
    T(t, 18, FontWeight.Bold, PNavy); Gap(10)
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        T(label, 13, FontWeight.SemiBold, Slate500, modifier = Modifier.weight(0.4f))
        T(value.ifBlank { "-" }, 14, c = Color(0xFF1B2430), modifier = Modifier.weight(0.6f))
    }
}

// ---------- PROFILE SCREEN ----------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(go: (Screen) -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var loggedIn by remember { mutableStateOf(Session.token != null) }
    var data by remember { mutableStateOf<JSONObject?>(null) }
    var dialog by remember { mutableStateOf<String?>(null) }
    var editing by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }

    // edit form fields
    var eName by remember { mutableStateOf("") }
    var ePhone by remember { mutableStateOf("") }
    var eCollege by remember { mutableStateOf("") }
    var eCity by remember { mutableStateOf("") }
    var eAddress by remember { mutableStateOf("") }
    var eCourse by remember { mutableStateOf("") }
    var eLocation by remember { mutableStateOf("") }
    var eActivity by remember { mutableStateOf("") }
    var eCurrentCourse by remember { mutableStateOf("") }

    suspend fun load() {
        val r = api("GET", "/profile")
        loggedIn = Session.token != null
        if (r.json != null) data = r.json else if (r.error != null) dialog = r.error
    }

    fun startEdit() {
        val d = data ?: return
        val reg = d.getJSONObject("registration")
        val prof = d.optJSONObject("profile") ?: JSONObject()
        eName = reg.str("full_name"); ePhone = reg.str("phone"); eCollege = reg.str("college")
        eCity = reg.str("city"); eAddress = reg.str("address"); eCourse = reg.str("course")
        eLocation = prof.str("location"); eActivity = prof.str("current_activity")
        eCurrentCourse = prof.str("current_course")
        editing = true
    }

    LaunchedEffect(Unit) { if (Session.token != null) load() }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) scope.launch {
            busy = true
            val b64 = withContext(Dispatchers.IO) { encodePhoto(ctx, uri) }
            if (b64 == null) {
                dialog = "Could not read this image."
            } else {
                val r = api("POST", "/profile/photo", JSONObject().put("image", b64))
                if (r.error != null) dialog = r.error else load()
            }
            busy = false
        }
    }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) scope.launch {
            busy = true
            val f = withContext(Dispatchers.IO) { readPickedFile(ctx, uri) }
            if (f == null || f.b64 == null) {
                dialog = "File size must be under 200 KB."
            } else {
                val r = api("POST", "/profile/files", JSONObject().put("file_name", f.name).put("data", f.b64))
                if (r.error != null) dialog = r.error else { dialog = "File uploaded."; load() }
            }
            busy = false
        }
    }

    val d = data
    Page(Color(0xFFF4F7FB)) {
        if (!loggedIn) {
            AppCard(border = PBorder) {
                T("Please login", 22, FontWeight.Bold, PNavy); Gap(6)
                T("Login to see your profile, files and login details.", 14); Gap(16)
                OrangeBtn("Go to Login", Modifier.fillMaxWidth()) { go(Screen.Login) }
            }
        } else if (d == null) {
            T("Loading your profile...", 16, c = Slate500, a = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(40.dp))
        } else {
            val reg = d.getJSONObject("registration")
            val prof = d.optJSONObject("profile") ?: JSONObject()
            val logins = d.optJSONObject("logins") ?: JSONObject()
            val files = d.optJSONArray("files") ?: JSONArray()
            val photo = remember(prof.str("photo")) { decodeImage(prof.str("photo")) }
            val initials = reg.str("full_name").split(" ").filter { it.isNotBlank() }
                .take(2).joinToString("") { it.first().uppercase() }

            // ---- Header: photo + name ----
            AppCard(border = PBorder) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(110.dp).clip(CircleShape).background(Primary), contentAlignment = Alignment.Center) {
                        if (photo != null) {
                            Image(bitmap = photo, contentDescription = "Photo", contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize())
                        } else {
                            T(initials, 36, FontWeight.Bold, Color.White)
                        }
                    }
                    Gap(10)
                    OutlineBtn(if (busy) "Please wait..." else "Change photo", s = 13) {
                        if (!busy) photoPicker.launch("image/*")
                    }
                    Gap(12)
                    T(reg.str("full_name"), 22, FontWeight.Bold, PNavy, a = TextAlign.Center)
                    T(reg.str("email"), 14, a = TextAlign.Center)
                    T(reg.str("phone"), 14, a = TextAlign.Center)
                    if (prof.str("current_activity").isNotBlank()) {
                        Gap(10)
                        Pill(prof.str("current_activity"), Color(0xFFDBEAFE), Primary, 12)
                    }
                }
            }
            Gap(16)

            // ---- Attendance button ----
            if (!editing) {
                OrangeBtn("📅  View Attendance", Modifier.fillMaxWidth()) { go(Screen.Attendance) }
                Gap(16)
            }

            if (editing) {
                // ---- Edit form ----
                AppCard(border = PBorder) {
                    SectionTitle("Edit Profile")
                    FormField("Full Name", eName, "Your full name", { eName = it.take(60) })
                    FormField("Phone No", ePhone, "10-digit mobile number",
                        { ePhone = it.filter { ch -> ch.isDigit() }.take(10) }, keyboard = KeyboardType.Phone)
                    FormField("College", eCollege, "Your college name", { eCollege = it.take(100) })
                    FormField("City", eCity, "Your city", { eCity = it.take(50) })
                    FormField("Address", eAddress, "Door no, street, area, pincode",
                        { eAddress = it.take(200) }, minLines = 3)
                    FormField("Current Location", eLocation, "Area / city where you are now",
                        { eLocation = it.take(100) })
                    FormDropdown("What are you doing now?", eActivity, "Select", plain(activityOptions)) { eActivity = it }
                    FormField("Current Course (learning now)", eCurrentCourse, "e.g. Python Full Stack",
                        { eCurrentCourse = it.take(100) })
                    FormDropdown("Course Interest", eCourse, "Select a course", courseGroups) { eCourse = it }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlineBtn("Cancel", Modifier.weight(1f)) { editing = false }
                        OrangeBtn(if (busy) "Saving..." else "Save", Modifier.weight(1f)) {
                            val err = validateEdit(eName, ePhone, eCollege, eCity, eAddress)
                            if (err != null) {
                                dialog = err
                            } else if (!busy) {
                                scope.launch {
                                    busy = true
                                    val body = JSONObject()
                                        .put("full_name", eName.trim()).put("phone", ePhone)
                                        .put("college", eCollege.trim()).put("city", eCity.trim())
                                        .put("address", eAddress.trim()).put("course", eCourse)
                                        .put("location", eLocation.trim()).put("current_activity", eActivity)
                                        .put("current_course", eCurrentCourse.trim())
                                    val r = api("PUT", "/profile", body)
                                    busy = false
                                    if (r.error == null) {
                                        editing = false
                                        dialog = "Profile updated successfully."
                                        load()
                                    } else dialog = r.error
                                }
                            }
                        }
                    }
                }
            } else {
                // ---- What they are doing now ----
                AppCard(border = PBorder) {
                    SectionTitle("Currently")
                    InfoRow("Doing now", prof.str("current_activity"))
                    InfoRow("Current course", prof.str("current_course"))
                    InfoRow("Course interest", reg.str("course"))
                    InfoRow(
                        "Education status",
                        if (reg.str("degree_status") == "Studying")
                            "Studying - ${reg.str("year_of_study")}, ${reg.str("semester")}"
                        else "Degree completed (${reg.str("completion_year")})"
                    )
                    InfoRow("Location", prof.str("location"))
                }
                Gap(16)

                // ---- Registration details ----
                AppCard(border = PBorder) {
                    SectionTitle("Registration Details")
                    InfoRow("Register No", reg.str("register_no"))
                    InfoRow("College", reg.str("college"))
                    InfoRow("Degree", reg.str("degree"))
                    InfoRow("Department", reg.str("department"))
                    InfoRow("Gender", reg.str("gender"))
                    InfoRow("City", reg.str("city"))
                    InfoRow("Address", reg.str("address"))
                    InfoRow("Registered on", reg.str("created_at"))
                    val skills = reg.str("skills").split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    if (skills.isNotEmpty()) {
                        Gap(8); T("Skills", 13, FontWeight.SemiBold, Slate500); Gap(6)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            skills.forEach { Pill(it, Color(0xFFDBEAFE), Primary, 11) }
                        }
                    }
                }
                Gap(16)

                // ---- Login details ----
                AppCard(border = PBorder) {
                    SectionTitle("Login Details")
                    InfoRow("Username", reg.str("email"))
                    InfoRow("Successful logins", logins.optInt("total").toString())
                    InfoRow("Last login", logins.str("last"))
                    val recent = logins.optJSONArray("recent") ?: JSONArray()
                    if (recent.length() > 0) {
                        Gap(8); T("Recent attempts", 13, FontWeight.SemiBold, Slate500); Gap(4)
                        for (i in 0 until recent.length()) {
                            val item = recent.getJSONObject(i)
                            val ok = item.str("status") == "SUCCESS"
                            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Pill(
                                    if (ok) "SUCCESS" else "FAILED",
                                    if (ok) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                    if (ok) Color(0xFF15803D) else Color(0xFFB91C1C), 10
                                )
                                T(item.str("login_time"), 13)
                            }
                        }
                    }
                }
            }
            Gap(16)

            // ---- Files ----
            AppCard(border = PBorder) {
                SectionTitle("My Files")
                if (files.length() == 0) T("No files uploaded yet.", 14, c = Slate500)
                for (i in 0 until files.length()) {
                    val f = files.getJSONObject(i)
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            T(f.str("file_name"), 14, FontWeight.SemiBold, Color(0xFF1B2430))
                            T("${f.optLong("file_size") / 1024} KB  •  ${f.str("uploaded_at")}", 12, c = Slate500)
                        }
                        T("✕", 18, FontWeight.Bold, Color(0xFFDC2626),
                            modifier = Modifier.clickable {
                                scope.launch { api("DELETE", "/profile/files/${f.optInt("id")}"); load() }
                            }.padding(8.dp))
                    }
                }
                Gap(12)
                OutlineBtn(if (busy) "Please wait..." else "Upload file (PDF / image / doc, max 200 KB)",
                    Modifier.fillMaxWidth(), s = 13) { if (!busy) filePicker.launch("*/*") }
            }
            Gap(16)

            // ---- Buttons ----
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!editing) OrangeBtn("Edit Profile", Modifier.weight(1f)) { startEdit() }
                OutlineBtn("Logout", Modifier.weight(1f), c = Color(0xFFDC2626)) {
                    Session.token = null; Session.name = ""
                    go(Screen.Login)
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