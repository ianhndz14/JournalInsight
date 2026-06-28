package edu.uprb.journalinsight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uprb.journalinsight.network.dto.PatientDto
import edu.uprb.journalinsight.ui.components.AvatarCircle
import edu.uprb.journalinsight.ui.components.SectionTitle
import edu.uprb.journalinsight.ui.theme.*
import edu.uprb.journalinsight.ui.viewmodel.ProfessionalHomeViewModel
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalHomeScreen(
    onViewDay: (String, String) -> Unit = { _, _ -> },
    onSignOut: () -> Unit = {},
    vm: ProfessionalHomeViewModel = viewModel()
) {
    val patients       by vm.patients.collectAsStateWithLifecycle()
    val availableDates by vm.availableDates.collectAsStateWithLifecycle()

    var selectedPatientId by remember { mutableStateOf<String?>(null) }
    var selectedDate      by remember { mutableStateOf<String?>(null) }
    var showDatePicker    by remember { mutableStateOf(false) }
    var unlinkTarget      by remember { mutableStateOf<PatientDto?>(null) }

    // ── Unlink confirmation dialog ────────────────────────────────────────────
    unlinkTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { unlinkTarget = null },
            containerColor   = White,
            title = {
                Text("Unlink patient", fontWeight = FontWeight.Medium, color = TextPrimary)
            },
            text = {
                Text(
                    "Are you sure you want to unlink ${target.fullName}? You will no longer see their journal entries.",
                    fontSize   = 13.sp,
                    color      = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (selectedPatientId == target.phoneNumber) {
                        selectedPatientId = null
                        selectedDate = null
                    }
                    vm.unlinkPatient(target.phoneNumber)
                    unlinkTarget = null
                }) {
                    Text("Unlink", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { unlinkTarget = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Auto-select first patient when list loads
    LaunchedEffect(patients) {
        if (patients.isNotEmpty() && selectedPatientId == null) {
            selectedPatientId = patients.first().phoneNumber
        }
    }

    // Reload dates when patient changes
    LaunchedEffect(selectedPatientId) {
        selectedDate = null
        selectedPatientId?.let { vm.loadAvailableDates(it) }
    }

    // DatePicker state — only selectable dates that have entries
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()
                    .toString()
                return availableDates.contains(date)
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                                .toString()
                        }
                        showDatePicker = false
                    }
                ) { Text("OK", color = SoftTeal) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        ) {
            DatePicker(
                state           = datePickerState,
                showModeToggle  = false,
                colors          = DatePickerDefaults.colors(
                    selectedDayContainerColor    = SoftTeal,
                    todayDateBorderColor         = SoftTeal,
                    disabledDayContentColor      = TextHint.copy(alpha = 0.3f)
                )
            )
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AvatarCircle(initials = "Dr", size = 38, bgColor = SoftTeal)
                        Column {
                            Text(
                                text       = "Professional panel",
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color      = TextPrimary
                            )
                            Text(
                                text     = "Query by patient",
                                fontSize = 11.sp,
                                color    = TextSecondary
                            )
                        }
                    }
                },
                actions = {
                    TextButton(onClick = onSignOut) {
                        Text("Sign out", fontSize = 12.sp, color = TextSecondary)
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Patient selector ──────────────────────────────────────────────
            SectionTitle("Select patient")

            if (patients.isEmpty()) {
                Text(
                    text     = "No patients assigned to your account yet.",
                    fontSize = 13.sp,
                    color    = TextHint
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    patients.forEach { patient ->
                        PatientSelectorCard(
                            name       = patient.fullName,
                            initials   = patient.initials,
                            subtitle   = patient.phoneNumber,
                            isSelected = patient.phoneNumber == selectedPatientId,
                            onClick    = { selectedPatientId = patient.phoneNumber },
                            onUnlink   = { unlinkTarget = patient }
                        )
                    }
                }
            }

            // ── Date selector ─────────────────────────────────────────────────
            SectionTitle("Select date")

            OutlinedButton(
                onClick  = {
                    if (availableDates.isNotEmpty()) showDatePicker = true
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.outlinedButtonColors(
                    containerColor = White,
                    contentColor   = if (selectedDate != null) TextPrimary else TextHint
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(
                            if (selectedDate != null) SoftTeal else CreamBorder,
                            if (selectedDate != null) SoftTeal else CreamBorder
                        )
                    )
                )
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text     = selectedDate ?: if (availableDates.isEmpty())
                            "No entries available for this patient"
                        else
                            "Tap to select a date",
                        fontSize = 14.sp
                    )
                    if (availableDates.isNotEmpty()) {
                        Text("📅", fontSize = 16.sp)
                    }
                }
            }

            if (availableDates.isNotEmpty() && selectedDate == null) {
                Text(
                    text     = "${availableDates.size} date(s) with entries available",
                    fontSize = 11.sp,
                    color    = TextHint
                )
            }

            Spacer(Modifier.weight(1f))

            // ── View day button ───────────────────────────────────────────────
            Button(
                onClick  = { selectedDate?.let { d -> selectedPatientId?.let { p -> onViewDay(p, d) } } },
                enabled  = selectedDate != null && selectedPatientId != null,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = SoftTeal)
            ) {
                Text(
                    text       = "View day",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color      = White
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Patient selector card ─────────────────────────────────────────────────────
@Composable
private fun PatientSelectorCard(
    name: String,
    initials: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onUnlink: () -> Unit
) {
    val bgColor = if (isSelected) TealLight else White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AvatarCircle(
            initials = initials,
            size     = 40,
            bgColor  = if (isSelected) SoftTeal else PeachRose
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
        IconButton(onClick = onUnlink) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = "Unlink patient",
                tint               = MaterialTheme.colorScheme.error
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true, name = "Professional Home")
@Composable
fun ProfessionalHomeScreenPreview() {
    JournalInsightTheme { ProfessionalHomeScreen() }
}
