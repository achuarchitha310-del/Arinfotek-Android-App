package com.it.arinfotek

import androidx.compose.foundation.BorderStroke
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

// ---------- Colors (same as website) ----------
val Primary = Color(0xFF1E5AA8)
val Accent = Color(0xFFFF891C)
val Dark = Color(0xFF0B1324)
val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate500 = Color(0xFF64748B)
val Slate600 = Color(0xFF475569)
val Slate800 = Color(0xFF1E293B)
val AccentBrush = Brush.horizontalGradient(listOf(Accent, Color(0xFFEA580C)))
val HeroBrush = Brush.linearGradient(listOf(Color(0xFF1E5AA8), Color(0xFF103466), Color(0xFF0F172A)))

// ---------- Small reusable pieces ----------
@Composable
fun T(
    t: String, s: Int = 14, w: FontWeight = FontWeight.Normal, c: Color = Slate600,
    modifier: Modifier = Modifier, a: TextAlign = TextAlign.Start
) = Text(t, fontSize = s.sp, fontWeight = w, color = c, textAlign = a, modifier = modifier, lineHeight = (s * 1.4).sp)

@Composable
fun Gap(h: Int) = Spacer(Modifier.height(h.dp))

@Composable
fun Logo() {
    Image(
        painter = painterResource(R.drawable.ar_infotek_logo),
        contentDescription = "AR INFOTEK",
        modifier = Modifier.height(38.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun OrangeBtn(t: String, modifier: Modifier = Modifier, s: Int = 14, onClick: () -> Unit) {
    Box(
        modifier.clip(RoundedCornerShape(12.dp)).background(AccentBrush).clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) { T(t, s, FontWeight.Bold, Color.White, a = TextAlign.Center) }
}

@Composable
fun OutlineBtn(
    t: String, modifier: Modifier = Modifier, c: Color = Primary,
    bg: Color = Color.Transparent, s: Int = 14, onClick: () -> Unit
) {
    val sh = RoundedCornerShape(12.dp)
    Box(
        modifier.clip(sh).background(bg).border(2.dp, c, sh).clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) { T(t, s, FontWeight.Bold, c, a = TextAlign.Center) }
}

@Composable
fun Pill(t: String, bg: Color, fg: Color, s: Int = 11, border: BorderStroke? = null) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .then(if (border != null) Modifier.border(border, RoundedCornerShape(50)) else Modifier)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        T(t, s, FontWeight.Bold, fg)
    }
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier, bg: Color = Color.White, border: Color = Slate200,
    bw: Int = 1, content: @Composable ColumnScope.() -> Unit
) {
    val sh = RoundedCornerShape(20.dp)
    Column(modifier.fillMaxWidth().clip(sh).background(bg).border(bw.dp, border, sh).padding(18.dp), content = content)
}

@Composable
fun Page(bg: Color = Color.White, pad: Int = 20, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(bg).verticalScroll(rememberScrollState()).padding(pad.dp), content = content)
}

@Composable
fun Header(label: String, title: String, sub: String = "", center: Boolean = false) {
    val al = if (center) Alignment.CenterHorizontally else Alignment.Start
    val ta = if (center) TextAlign.Center else TextAlign.Start
    Column(Modifier.fillMaxWidth(), horizontalAlignment = al) {
        if (label.isNotEmpty()) { T(label.uppercase(), 12, FontWeight.Bold, Accent, a = ta); Gap(6) }
        T(title, 28, FontWeight.ExtraBold, Dark, a = ta)
        Gap(10)
        if (sub.isNotEmpty()) T(sub, 14, c = Slate600, a = ta)
        else Box(Modifier.width(60.dp).height(5.dp).clip(RoundedCornerShape(3.dp))
            .background(Brush.horizontalGradient(listOf(Primary, Accent))))
    }
    Gap(20)
}

@Composable
fun Grid2(items: List<Pair<String, String>>) {
    items.chunked(2).forEach { row ->
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            row.forEach { (t, d) ->
                AppCard(Modifier.weight(1f).fillMaxHeight(), bg = Slate50) {
                    T(t, 15, FontWeight.Bold, Primary); Gap(4); T(d, 12)
                }
            }
            if (row.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}