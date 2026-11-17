package com.it.arinfotek

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import kotlinx.coroutines.launch
import org.json.JSONObject

// ---------- Colors (from style.css) ----------
private val FormNavy = Color(0xFF0C1F3F)
private val FormBlue = Color(0xFF1F5FBF)
private val FormBlueDark = Color(0xFF174A96)
private val FormInk = Color(0xFF1B2430)
private val FormMuted = Color(0xFF64748B)
private val FormLine = Color(0xFFD5DEEA)
private val FormFieldBg = Color(0xFFF8FAFD)

// ---------- Degree data (same as script.js) ----------
private val degreeData: Map<String, Pair<Int, List<String>>> = mapOf(
    "BE" to Pair(4, listOf(
        "Computer Science (CSE)", "Information Technology (IT)", "Electronics and Communication (ECE)",
        "Electrical and Electronics (EEE)", "Mechanical", "Civil", "AI and Data Science")),
    "ME" to Pair(2, listOf(
        "Computer Science and Engineering", "Software Engineering", "VLSI Design",
        "Embedded System Technologies", "Communication Systems", "Applied Electronics",
        "Power Systems Engineering", "Power Electronics and Drives", "Structural Engineering",
        "Environmental Engineering", "Thermal Engineering", "CAD/CAM",
        "Manufacturing Engineering", "Engineering Design")),
    "B.Tech" to Pair(4, listOf(
        "Information Technology", "AI and Data Science", "Biotechnology",
        "Chemical Engineering", "Food Technology")),
    "B.Sc" to Pair(3, listOf(
        "Computer Science", "Mathematics", "Physics", "Chemistry", "Electronics", "Data Science")),
    "BCA" to Pair(3, listOf("Computer Applications"))
)

private val yearNames = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")

val courseGroups: List<Pair<String?, List<String>>> = listOf(
    "Programming and Development" to listOf(
        "Python Programming", "Java Programming", "Python Full Stack", "Java Full Stack",
        "MERN Stack Development", "Web Development (HTML, CSS, JS)", "Android App Development",
        "SQL and Database Management"),
    "Data and AI" to listOf(
        "Data Science & ML", "Data Analytics", "Power BI and Tableau",
        "Artificial Intelligence", "Machine Learning", "Generative AI"),
    "Cloud and DevOps" to listOf(
        "AWS Cloud Architect", "Cloud & DevOps", "Microsoft Azure",
        "Docker and Kubernetes", "Linux Administration"),
    "Security and Testing" to listOf(
        "Cybersecurity", "Ethical Hacking", "Software Testing (Manual and Automation)"),
    "Design and Others" to listOf(
        "UI/UX Design", "Embedded Systems and IoT", "Internship Program")
)

private val skillList = listOf(
    "Python", "Java", "C/C++", "SQL", "HTML/CSS", "JavaScript", "React", "Node.js", "Flask",
    "Django", "Git/GitHub", "Linux", "AWS/Cloud", "Machine Learning", "Data Analysis",
    "Cybersecurity", "MS Excel"
)

fun plain(items: List<String>): List<Pair<String?, List<String>>> = listOf(null to items)

// ---------- Reusable form pieces ----------
@Composable
fun FormLabel(t: String) {
    T(t, 13, FontWeight.SemiBold, Color(0xFF33415C)); Gap(6)
}

@Composable
fun FormField(
    label: String, value: String, placeholder: String, onChange: (String) -> Unit,
    keyboard: KeyboardType = KeyboardType.Text, password: Boolean = false, minLines: Int = 1
) {
    FormLabel(label)
    OutlinedTextField(
        value = value, onValueChange = onChange, modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Color(0xFFA3AFC2), fontSize = 15.sp) },
        textStyle = TextStyle(fontSize = 15.sp, color = FormInk),
        shape = RoundedCornerShape(10.dp),
        singleLine = minLines == 1, minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FormBlue, unfocusedBorderColor = FormLine,
            focusedContainerColor = Color.White, unfocusedContainerColor = FormFieldBg,
            cursorColor = FormBlue
        )
    )
    Gap(16)
}

@Composable
fun FormDropdown(
    label: String, value: String, placeholder: String,
    groups: List<Pair<String?, List<String>>>, onSelect: (String) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    val enabled = groups.any { it.second.isNotEmpty() }
    val shape = RoundedCornerShape(10.dp)
    FormLabel(label)
    Box {
        Row(
            Modifier.fillMaxWidth().alpha(if (enabled) 1f else 0.6f).clip(shape)
                .background(FormFieldBg).border(1.5.dp, FormLine, shape)
                .clickable(enabled = enabled) { open = true }
                .padding(horizontal = 13.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            T(value.ifEmpty { placeholder }, 15, c = if (value.isEmpty()) Color(0xFF94A3B8) else FormInk,
                modifier = Modifier.weight(1f))
            T("▾", 14, c = FormMuted)
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            groups.forEach { (header, items) ->
                if (header != null) {
                    Text(header, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FormBlue,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                }
                items.forEach { item ->
                    DropdownMenuItem(text = { Text(item) }, onClick = { onSelect(item); open = false })
                }
            }
        }
    }
    Gap(16)
}

@Composable
fun FormChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        Modifier.clip(shape).background(if (selected) FormBlue else FormFieldBg)
            .border(1.5.dp, if (selected) FormBlue else FormLine, shape)
            .clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 8.dp)
    ) { T(text, 14, FontWeight.Medium, if (selected) Color.White else Color(0xFF475569)) }
}

// ---------- REGISTRATION SCREEN ----------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(onRegistered: () -> Unit = {}) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var regNo by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var degree by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }            // "Studying" or "Completed"
    var year by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var completionYear by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    val skills = remember { mutableStateListOf<String>() }
    var terms by remember { mutableStateOf(false) }
    var dialog by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var registered by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Dependent dropdown options
    val info = degreeData[degree]
    val deptOptions = info?.second ?: emptyList()
    val yearOptions = if (info != null) yearNames.take(info.first) else emptyList()
    val yearNumber = yearNames.indexOf(year) + 1
    val semOptions = if (yearNumber > 0)
        listOf("Semester ${yearNumber * 2 - 1}", "Semester ${yearNumber * 2}") else emptyList()
    val thisYear = Calendar.getInstance().get(Calendar.YEAR)
    val completionOptions = (thisYear downTo thisYear - 15).map { it.toString() }

    fun reset() {
        fullName = ""; email = ""; phone = ""; password = ""; confirmPassword = ""
        college = ""; regNo = ""; city = ""; address = ""
        degree = ""; department = ""; status = ""; year = ""; semester = ""; completionYear = ""
        course = ""; gender = ""; skills.clear(); terms = false
    }

    // Same rules and messages as script.js
    fun validate(): String? {
        val name = fullName.trim()
        if (name.isEmpty()) return "Please enter your full name."
        if (name.length < 3) return "Name must be at least 3 letters."
        if (!Regex("^[A-Za-z ]+$").matches(name)) return "Name should contain only letters and spaces."

        val mail = email.trim()
        if (mail.isEmpty()) return "Please enter your email."
        if (!Regex("^\\S+@\\S+\\.\\S+$").matches(mail)) return "Email is not correct. Example: name@gmail.com"

        val ph = phone.trim()
        if (ph.isEmpty()) return "Please enter your phone number."
        if (ph.length != 10) return "Phone number must be exactly 10 digits."
        if (ph[0] !in '6'..'9') return "Phone number should start with 6, 7, 8 or 9."

        if (password.isEmpty()) return "Please enter a password."
        if (password.length < 8) return "Password must be at least 8 characters."
        if (!Regex("[A-Z]").containsMatchIn(password)) return "Password needs at least one CAPITAL letter."
        if (!Regex("[a-z]").containsMatchIn(password)) return "Password needs at least one small letter."
        if (!Regex("[0-9]").containsMatchIn(password)) return "Password needs at least one number."
        if (!Regex("[^A-Za-z0-9]").containsMatchIn(password)) return "Password needs at least one symbol like @ or #."
        if (confirmPassword.isEmpty()) return "Please confirm your password."
        if (confirmPassword != password) return "Passwords do not match. Please type the same password."

        if (college.trim().isEmpty()) return "Please enter your college name."

        val reg = regNo.trim()
        if (reg.isEmpty()) return "Please enter your register number."
        if (!Regex("^[A-Za-z0-9]+$").matches(reg)) return "Register number should contain only letters and numbers."
        if (reg.length < 6) return "Register number must be at least 6 characters."

        val cty = city.trim()
        if (cty.isEmpty()) return "Please enter your city."
        if (!Regex("^[A-Za-z ]+$").matches(cty)) return "City should contain only letters."

        val addr = address.trim()
        if (addr.isEmpty()) return "Please enter your address."
        if (addr.length < 10) return "Address is too short. Please enter your full address."

        if (degree.isEmpty()) return "Please select your degree."
        if (department.isEmpty()) return "Please select your discipline / department."
        if (status.isEmpty()) return "Please select whether you are studying or completed your degree."
        if (status == "Studying") {
            if (year.isEmpty()) return "Please select your year of study."
            if (semester.isEmpty()) return "Please select your semester."
        }
        if (status == "Completed" && completionYear.isEmpty()) return "Please select your year of completion."

        if (course.isEmpty()) return "Please select a course."
        if (gender.isEmpty()) return "Please select your gender."
        if (skills.isEmpty()) return "Please select at least one skill."
        if (!terms) return "Please agree to the Terms and Privacy Policy."
        return null
    }

    Page(Color(0xFFF4F7FB)) {
        AppCard(border = FormLine) {
            T("Create your account", 24, FontWeight.Bold, FormNavy)
            Gap(4)
            T("Fill in your details to register.", 14, c = FormMuted)
            Gap(22)

            FormField("Full Name", fullName, "Enter your full name",
                { fullName = it.take(60) })
            FormField("Email", email, "name@gmail.com",
                { email = it.take(100) }, keyboard = KeyboardType.Email)
            FormField("Phone No", phone, "10-digit mobile number", { v ->
                if (v.any { !it.isDigit() }) dialog = "Phone number should contain digits only."
                phone = v.filter { it.isDigit() }.take(10)
            }, keyboard = KeyboardType.Phone)
            FormField("Password", password, "Create a password",
                { password = it.take(64) }, keyboard = KeyboardType.Password, password = true)
            FormField("Confirm Password", confirmPassword, "Re-enter password",
                { confirmPassword = it.take(64) }, keyboard = KeyboardType.Password, password = true)
            FormField("College", college, "Your college name",
                { college = it.take(100) })
            FormField("Register No", regNo, "Your register number",
                { regNo = it.take(15) })
            FormField("City", city, "Your city",
                { city = it.take(50) })
            FormField("Address", address, "Door no, street, area, pincode",
                { address = it.take(200) }, minLines = 3)

            FormDropdown("Degree", degree, "Select degree", plain(degreeData.keys.toList())) {
                if (it != degree) { degree = it; department = ""; year = ""; semester = "" }
            }
            FormDropdown("Discipline / Department", department,
                if (degree.isEmpty()) "Select degree first" else "Select department",
                plain(deptOptions)) { department = it }

            // Degree Status
            FormLabel("Degree Status")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FormChip("Currently studying", status == "Studying") { status = "Studying" }
                FormChip("Degree completed", status == "Completed") { status = "Completed" }
            }
            Gap(16)

            // Currently studying -> Year + Semester
            if (status == "Studying") {
                FormDropdown("Year of Study", year,
                    if (degree.isEmpty()) "Select degree first" else "Select year",
                    plain(yearOptions)) { if (it != year) { year = it; semester = "" } }
                FormDropdown("Semester", semester,
                    if (year.isEmpty()) "Select year first" else "Select semester",
                    plain(semOptions)) { semester = it }
            }
            // Degree completed -> Year of Completion
            if (status == "Completed") {
                FormDropdown("Year of Completion", completionYear, "Select year",
                    plain(completionOptions)) { completionYear = it }
            }

            FormDropdown("Course Interest", course, "Select a course", courseGroups) { course = it }

            // Gender
            FormLabel("Gender")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Female", "Male", "Other").forEach { g -> FormChip(g, gender == g) { gender = g } }
            }
            Gap(16)

            // Skills (multi select)
            FormLabel("Skills (select at least one)")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                skillList.forEach { s ->
                    FormChip(s, s in skills) { if (s in skills) skills.remove(s) else skills.add(s) }
                }
            }
            Gap(16)

            // Terms
            Row(Modifier.fillMaxWidth().clickable { terms = !terms }, verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = terms, onCheckedChange = { terms = it },
                    colors = CheckboxDefaults.colors(checkedColor = FormBlue))
                T("I agree to the Terms and Privacy Policy", 14, c = Color(0xFF475569))
            }
            Gap(10)

            // Register button
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(FormBlue, FormBlueDark)))
                    .clickable(enabled = !loading) {
                        val err = validate()
                        if (err != null) {
                            dialog = err
                        } else {
                            val name = fullName.trim()
                            val json = JSONObject().apply {
                                put("full_name", name)
                                put("email", email.trim())
                                put("phone", phone)
                                put("password", password)
                                put("college", college.trim())
                                put("register_no", regNo.trim())
                                put("city", city.trim())
                                put("address", address.trim())
                                put("degree", degree)
                                put("department", department)
                                put("degree_status", status)
                                put("year_of_study", year)
                                put("semester", semester)
                                put("completion_year", completionYear)
                                put("course", course)
                                put("gender", gender)
                                put("skills", skills.joinToString(", "))
                            }
                            scope.launch {
                                loading = true
                                val error = sendRegistration(json)
                                loading = false
                                if (error == null) {
                                    registered = true
                                    dialog = "Registration successful! Welcome, $name. Please login now."
                                    reset()
                                } else {
                                    dialog = error
                                }
                            }
                        }
                    }.padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) { T(if (loading) "Please wait..." else "Register", 16, FontWeight.SemiBold, Color.White) }
        }
        Gap(20)
    }

    dialog?.let { msg ->
        val close: () -> Unit = {
            dialog = null
            if (registered) { registered = false; onRegistered() }   // open the Login page
        }
        AlertDialog(
            onDismissRequest = close,
            containerColor = Color.White,
            text = { Text(msg, color = FormInk) },
            confirmButton = { TextButton(onClick = close) { Text("OK", color = FormBlue) } }
        )
    }
}