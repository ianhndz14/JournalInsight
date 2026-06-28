package edu.uprb.journalinsight.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val JournalColorScheme = lightColorScheme(
    primary          = Sage,
    onPrimary        = White,
    primaryContainer = SageLight,
    onPrimaryContainer = SageDark,

    secondary        = Lavender,
    onSecondary      = White,
    secondaryContainer = LavenderLight,
    onSecondaryContainer = LavenderDark,

    tertiary         = PeachRose,
    onTertiary       = White,
    tertiaryContainer = PeachLight,
    onTertiaryContainer = PeachDark,

    background       = Cream,
    onBackground     = TextPrimary,

    surface          = White,
    onSurface        = TextPrimary,
    surfaceVariant   = CardBg,
    onSurfaceVariant = TextSecondary,

    outline          = CreamBorder,
    outlineVariant   = CreamDark,
)

@Composable
fun JournalInsightTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JournalColorScheme,
        typography  = Typography,
        content     = content
    )
}
