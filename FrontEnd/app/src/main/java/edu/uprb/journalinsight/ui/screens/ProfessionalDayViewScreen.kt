package edu.uprb.journalinsight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uprb.journalinsight.network.dto.JournalEntryDto
import edu.uprb.journalinsight.ui.components.MoodPill
import edu.uprb.journalinsight.ui.components.SectionTitle
import edu.uprb.journalinsight.ui.theme.*
import edu.uprb.journalinsight.ui.viewmodel.ProfessionalDayUiState
import edu.uprb.journalinsight.ui.viewmodel.ProfessionalDayViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalDayViewScreen(
    patientId: String,
    dateIso: String,
    onBack: () -> Unit = {},
    onEntryClick: (Long) -> Unit = {},
    vm: ProfessionalDayViewModel = viewModel()
) {
    LaunchedEffect(patientId, dateIso) { vm.loadEntries(patientId, dateIso) }

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Day View", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint               = SoftTeal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is ProfessionalDayUiState.Loading -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SoftTeal)
                }
            }

            is ProfessionalDayUiState.Error -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = TextSecondary, fontSize = 13.sp)
                }
            }

            is ProfessionalDayUiState.Success -> {
                val entries   = state.entries
                val dailyMood = entries
                    .mapNotNull { it.generalClassification }
                    .groupingBy { it }
                    .eachCount()
                    .maxByOrNull { it.value }?.key ?: "No data"

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding      = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        DaySummaryCard(
                            patientId  = patientId,
                            date       = dateIso,
                            dailyMood  = dailyMood,
                            entryCount = entries.size
                        )
                    }

                    item { SectionTitle("Entries for this day") }

                    if (entries.isEmpty()) {
                        item {
                            Box(
                                modifier         = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text     = "No entries recorded for this day.",
                                    color    = TextHint,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(entries) { entry ->
                            DayEntryCard(
                                entry   = entry,
                                onClick = { onEntryClick(entry.journalEntryId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Day summary card ──────────────────────────────────────────────────────────
@Composable
private fun DaySummaryCard(
    patientId: String,
    date: String,
    dailyMood: String,
    entryCount: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(Sage, SoftTeal)))
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text     = "$patientId  ·  $date",
                fontSize = 12.sp,
                color    = White.copy(alpha = 0.8f)
            )
            Text(text = "Day mood", fontSize = 11.sp, color = White.copy(alpha = 0.7f))
            Text(
                text       = dailyMood.replaceFirstChar { it.uppercase() },
                fontSize   = 22.sp,
                fontWeight = FontWeight.Medium,
                color      = White
            )
            Text(
                text     = "$entryCount entr${if (entryCount != 1) "ies" else "y"} recorded",
                fontSize = 12.sp,
                color    = White.copy(alpha = 0.7f)
            )
        }
    }
}

// ── Day entry card ────────────────────────────────────────────────────────────
@Composable
private fun DayEntryCard(entry: JournalEntryDto, onClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Entry #${entry.journalEntryId}", fontSize = 11.sp, color = TextHint)
            Text(
                text       = entry.entryText.take(100) + if (entry.entryText.length > 100) "..." else "",
                fontSize   = 13.sp,
                color      = TextPrimary,
                lineHeight = 20.sp
            )
            HorizontalDivider(color = CreamBorder)
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                MoodPill(mood = entry.generalClassification ?: "N/A")
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text     = entry.detectedEmotions.joinToString(", ").ifBlank { "N/A" },
                        fontSize = 11.sp,
                        color    = TextHint
                    )
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "View details",
                        tint               = TextHint,
                        modifier           = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
