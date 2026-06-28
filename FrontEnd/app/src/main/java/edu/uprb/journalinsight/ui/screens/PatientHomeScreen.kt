package edu.uprb.journalinsight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uprb.journalinsight.network.dto.JournalEntryDto
import edu.uprb.journalinsight.ui.components.AvatarCircle
import edu.uprb.journalinsight.ui.components.MoodPill
import edu.uprb.journalinsight.ui.components.SectionTitle
import edu.uprb.journalinsight.ui.theme.*
import edu.uprb.journalinsight.ui.viewmodel.PatientHomeUiState
import edu.uprb.journalinsight.ui.viewmodel.PatientHomeViewModel

@Composable
fun PatientHomeScreen(
    onNewEntry: () -> Unit = {},
    onOpenEntry: (Long) -> Unit = {},
    onLinkProfessional: () -> Unit = {},
    onSignOut: () -> Unit = {},
    vm: PatientHomeViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    // Recarga cada vez que esta pantalla vuelve al foco:
    // - después del login
    // - al volver de Nueva Entrada
    // - al volver de Entry Detail (incluyendo después de borrar)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            vm.loadEntries()
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            PatientTopBar(
                onNewEntry         = onNewEntry,
                onLinkProfessional = onLinkProfessional,
                onSignOut          = onSignOut
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is PatientHomeUiState.Loading -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Sage)
                }
            }

            is PatientHomeUiState.Error -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = TextSecondary, fontSize = 13.sp)
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { vm.loadEntries() },
                            colors  = ButtonDefaults.buttonColors(containerColor = Sage)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            is PatientHomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding      = PaddingValues(vertical = 20.dp)
                ) {
                    item { SectionTitle("My journal entries") }

                    if (state.entries.isEmpty()) {
                        item {
                            Box(
                                modifier         = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No entries yet. Tap + to write your first one.",
                                    color    = TextHint,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(state.entries) { entry ->
                            EntryCardDto(
                                entry   = entry,
                                onClick = { onOpenEntry(entry.journalEntryId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PatientTopBar(
    onNewEntry: () -> Unit,
    onLinkProfessional: () -> Unit,
    onSignOut: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val name     = edu.uprb.journalinsight.data.SessionManager.firstName.ifBlank { "User" }
                val initials = name.take(2).uppercase()
                AvatarCircle(initials = initials, size = 38, bgColor = PeachRose)
                Column {
                    Text(
                        text       = "Hello, $name",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color      = TextPrimary
                    )
                    Text(
                        text     = "How are you feeling today?",
                        fontSize = 11.sp,
                        color    = TextSecondary
                    )
                }
            }
        },
        actions = {
            // Link professional button
            IconButton(onClick = onLinkProfessional) {
                Text("🔗", fontSize = 18.sp)
            }
            // New entry button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Sage)
                    .clickable { onNewEntry() },
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 22.sp, color = White, fontWeight = FontWeight.Medium)
            }
            // Sign out button
            TextButton(onClick = onSignOut) {
                Text("Sign out", fontSize = 12.sp, color = TextSecondary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
    )
}

// ── Entry card (API version) ──────────────────────────────────────────────────
@Composable
fun EntryCardDto(
    entry: JournalEntryDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(CreamBorder, CreamBorder))
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = "📅 ${entry.entryDate}", fontSize = 11.sp, color = TextHint)
            Text(
                text       = entry.entryText.take(80) + if (entry.entryText.length > 80) "..." else "",
                fontSize   = 13.sp,
                color      = TextPrimary,
                lineHeight = 20.sp
            )
            MoodPill(mood = entry.generalClassification ?: "Not analyzed")
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true, name = "Patient Home")
@Composable
fun PatientHomeScreenPreview() {
    JournalInsightTheme { PatientHomeScreen() }
}
