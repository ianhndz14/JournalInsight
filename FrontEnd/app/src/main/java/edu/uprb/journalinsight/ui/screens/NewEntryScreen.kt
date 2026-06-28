package edu.uprb.journalinsight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uprb.journalinsight.ui.theme.*
import edu.uprb.journalinsight.ui.viewmodel.NewEntryUiState
import edu.uprb.journalinsight.ui.viewmodel.NewEntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryScreen(
    onDone: () -> Unit = {},
    vm: NewEntryViewModel = viewModel()
) {
    var text by remember { mutableStateOf("") }
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title  = { Text("New Entry", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint               = Sage
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Date chip ─────────────────────────────────────────────────────
            Text(
                text     = "📅 ${java.time.LocalDate.now()}",
                fontSize = 12.sp,
                color    = TextHint
            )

            // ── Text area ─────────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text       = "How was your day?",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color      = TextSecondary
                )
                OutlinedTextField(
                    value         = text,
                    onValueChange = { text = it },
                    placeholder   = {
                        Text(
                            "Write freely about your emotions, experiences or thoughts...",
                            color      = TextHint,
                            fontSize   = 13.sp,
                            lineHeight = 20.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor    = CreamBorder,
                        focusedBorderColor      = Sage,
                        unfocusedContainerColor = White,
                        focusedContainerColor   = White,
                        unfocusedTextColor      = TextPrimary,
                        focusedTextColor        = TextPrimary
                    )
                )
            }

            // ── Error message ─────────────────────────────────────────────────
            if (uiState is NewEntryUiState.Error) {
                Text(
                    text     = (uiState as NewEntryUiState.Error).message,
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.error
                )
            }

            // ── Action buttons ────────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick  = onDone,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    border   = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(CreamBorder, CreamBorder))
                    )
                ) {
                    Text("Cancel", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick  = { vm.analyzeAndSave(text) },
                    enabled  = uiState !is NewEntryUiState.Loading,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Sage)
                ) {
                    if (uiState is NewEntryUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color    = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Analyze & Save", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // ── Analysis result card ──────────────────────────────────────────
            if (uiState is NewEntryUiState.Success) {
                val success = uiState as NewEntryUiState.Success
                AnalysisResultCard(
                    mood             = success.emotion,
                    detectedEmotions = success.detectedEmotions
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Analysis result card ──────────────────────────────────────────────────────
@Composable
fun AnalysisResultCard(
    mood: String,
    detectedEmotions: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    val isMixed = mood.equals("mixed", ignoreCase = true)

    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = LavenderLight),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text       = "Analysis result",
                fontSize   = 12.sp,
                color      = LavenderDark,
                fontWeight = FontWeight.Medium
            )
            Text(
                text       = if (isMixed) "Detected emotions" else "Detected emotion",
                fontSize   = 11.sp,
                color      = Lavender
            )
            Text(
                text       = mood.replaceFirstChar { it.uppercase() },
                fontSize   = 24.sp,
                fontWeight = FontWeight.Medium,
                color      = LavenderDark
            )
            // When mixed, show which emotions were detected underneath
            if (isMixed && detectedEmotions.size >= 2) {
                Text(
                    text       = detectedEmotions.joinToString(" + ") { it.replaceFirstChar { c -> c.uppercase() } },
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color      = Lavender
                )
            }
            Text(
                text     = "Entry saved and analyzed successfully.",
                fontSize = 12.sp,
                color    = Lavender
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true, name = "New Entry")
@Composable
fun NewEntryScreenPreview() {
    JournalInsightTheme { NewEntryScreen() }
}
