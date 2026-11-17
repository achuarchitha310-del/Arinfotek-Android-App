package com.it.arinfotek

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// Emulator + "adb reverse" tunnel (setup_tunnel.bat) -> your PC server
const val BASE_URL = "http://localhost:5000"

// Remembers who is logged in (kept in memory only)
object Session {
    var token: String? = null
    var name: String = ""
}

data class LoginResult(val name: String? = null, val error: String? = null)
data class ApiResult(val json: JSONObject? = null, val error: String? = null)

private suspend fun request(method: String, path: String, json: JSONObject?): Pair<Int, String> =
    withContext(Dispatchers.IO) {
        val conn = URL(BASE_URL + path).openConnection() as HttpURLConnection
        conn.requestMethod = method
        conn.setRequestProperty("Content-Type", "application/json")
        Session.token?.let { conn.setRequestProperty("Authorization", "Bearer $it") }
        conn.connectTimeout = 10000
        conn.readTimeout = 20000
        if (json != null) {
            conn.doOutput = true
            conn.outputStream.use { it.write(json.toString().toByteArray()) }
        }
        val code = conn.responseCode
        val body = (if (code in 200..299) conn.inputStream else conn.errorStream)
            ?.bufferedReader()?.readText().orEmpty()
        Pair(code, body)
    }

private fun serverError(body: String, code: Int): String =
    runCatching { JSONObject(body).optString("error") }.getOrNull()
        .takeUnless { it.isNullOrEmpty() } ?: "Server error ($code)"

// One function for every call: GET / POST / PUT / DELETE
suspend fun api(method: String, path: String, json: JSONObject? = null): ApiResult = try {
    val (code, body) = request(method, path, json)
    if (code in 200..299) {
        ApiResult(json = runCatching { JSONObject(body) }.getOrDefault(JSONObject()))
    } else {
        if (code == 401 && path != "/login") Session.token = null   // token expired
        ApiResult(error = serverError(body, code))
    }
} catch (e: Exception) {
    ApiResult(error = "Cannot connect to the server. Please check the server and tunnel. (${e.javaClass.simpleName})")
}

// Registration: returns null on success, or an error message
suspend fun sendRegistration(json: JSONObject): String? = api("POST", "/register", json).error

// Login: saves the token, returns the user's name or an error message
suspend fun sendLogin(username: String, password: String): LoginResult {
    val r = api("POST", "/login", JSONObject().put("username", username).put("password", password))
    val j = r.json
    if (r.error != null || j == null) return LoginResult(error = r.error ?: "Login failed")
    Session.token = j.optString("token")
    Session.name = j.optString("name")
    return LoginResult(name = Session.name.ifEmpty { "User" })
}