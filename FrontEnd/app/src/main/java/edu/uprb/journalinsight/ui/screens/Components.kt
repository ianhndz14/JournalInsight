package edu.uprb.journalinsight.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.uprb.journalinsight.ui.theme.*

// ── Mood pill ────────────────────────────────────────────────────────────────
@Composable
fun MoodPill(mood: String, modifier: Modifier = Modifier) {
    val (bg, fg) = when (mood.lowercase()) {
        "positive", "happiness"  -> MoodPositiveBg to MoodPositiveFg
        "negative", "sadness", "anger" -> MoodNegativeBg to MoodNegativeFg
        "mixed", "mixto", "anxiety" -> MoodMixedBg to MoodMixedFg
        else -> MoodNeutralBg to MoodNeutralFg
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text       = mood,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            color      = fg
        )
    }
}

// ── Avatar circle ─────────────────────────────────────────────────────────────
@Composable
fun AvatarCircle(
    initials: String,
    size: Int = 40,
    bgColor: Color = PeachRose,
    textColor: Color = White,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = initials,
            fontSize   = (size * 0.35).sp,
            fontWeight = FontWeight.Medium,
            color      = textColor
        )
    }
}

// ── Section title ────────────────────────────────────────────────────────────
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text       = text,
        fontSize   = 13.sp,
        fontWeight = FontWeight.Medium,
        color      = TextPrimary,
        modifier   = modifier
    )
}
