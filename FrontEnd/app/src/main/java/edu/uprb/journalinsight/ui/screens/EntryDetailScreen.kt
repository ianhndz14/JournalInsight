package edu.uprb.journalinsight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uprb.journalinsight.network.dto.JournalEntryDto
import edu.uprb.journalinsight.ui.components.MoodPill
import edu.uprb.journalinsight.ui.theme.*
import edu.uprb.journalinsight.ui.viewmodel.EntryDetailUiState
import edu.uprb.journalinsight.ui.viewmodel.EntryDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(
    entryId: Long,
    canDelete: Boolean = true,
    onBack: () -> Unit = {},
    onDeleted: () -> Unit = {},
    vm: EntryDetailViewModel = viewModel()
) {
    LaunchedEffect(entryId) { vm.loadEntry(entryId) }

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Navigate back when deletion completes
    LaunchedEffect(uiState) {
        if (uiState is EntryDetailUiState.Deleted) {
            onDeleted()
        }
    }

    // ── Confirmation dialog ───────────────────────────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor   = White,
            title = {
                Text(
                    text       = "Delete entry",
                    fontWeight = FontWeight.Medium,
                    color      = TextPrimary
                )
            },
            text = {
                Text(
                    text  = "This will permanently delete the entry and its analysis. This action cannot be undone.",
                    fontSize  = 13.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        vm.deleteEntry(entryId)
                    }
                ) {
                    Text(
                        text  = "Delete",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = {
                    val title = if (uiState is EntryDetailUiState.Success)
                        (uiState as EntryDetailUiState.Success).entry.entryDate
                    else "Entry detail"
                    Text(text = title, fontWeight = FontWeight.Medium)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint               = Sage
                        )
                    }
                },
                actions = {
                    if (canDelete && uiState is EntryDetailUiState.Success) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector        = Icons.Outlined.Delete,
                                contentDescription = "Delete entry",
                                tint               = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is EntryDetailUiState.Loading -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Sage)
                }
            }

            is EntryDetailUiState.Error -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = TextSecondary)
                }
            }

            is EntryDetailUiState.Deleted -> {
                // Brief visual while navigating back
                Box(
                    modifier         = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Sage)
                }
            }

            is EntryDetailUiState.Success -> {
                EntryDetailContent(entry = state.entry, padding = padding)
            }
        }
    }
}

@Composable
private fun EntryDetailContent(entry: JournalEntryDto, padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(4.dp))

        // ── Entry text hero ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(LavenderLight)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "📅 ${entry.entryDate}", fontSize = 11.sp, color = Lavender)
                Text(
                    text       = "\"${entry.entryText}\"",
                    fontSize   = 14.sp,
                    lineHeight = 22.sp,
                    color      = TextPrimary
                )
            }
        }

        // ── Analysis card ─────────────────────────────────────────────────────
        Text(
            text       = "Emotional analysis",
            fontSize   = 13.sp,
            fontWeight = FontWeight.Medium,
            color      = TextPrimary
        )

        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(14.dp),
            colors    = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                AnalysisRow(
                    label   = "Classification",
                    content = { MoodPill(mood = entry.generalClassification ?: "Not analyzed") }
                )
                HorizontalDivider(color = CreamBorder, modifier = Modifier.padding(vertical = 6.dp))
                AnalysisRow(
                    label = "Detected emotions",
                    value = entry.detectedEmotions.joinToString(", ").ifBlank { "N/A" }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ── Row helper ────────────────────────────────────────────────────────────────
@Composable
private fun AnalysisRow(
    label: String,
    value: String? = null,
    content: (@Composable () -> Unit)? = null
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        if (content != null) content()
        else Text(value ?: "", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}
