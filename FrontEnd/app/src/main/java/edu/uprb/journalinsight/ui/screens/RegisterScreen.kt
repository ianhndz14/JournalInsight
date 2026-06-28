package edu.uprb.journalinsight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uprb.journalinsight.ui.theme.*
import edu.uprb.journalinsight.ui.viewmodel.AuthUiState
import edu.uprb.journalinsight.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onPatient: () -> Unit = {},
    onProfessional: () -> Unit = {},
    onBack: () -> Unit = {},
    vm: AuthViewModel = viewModel()
) {
    var firstName        by remember { mutableStateOf("") }
    var lastNamePaternal by remember { mutableStateOf("") }
    var email            by remember { mutableStateOf("") }
    var password         by remember { mutableStateOf("") }
    var phoneNumber      by remember { mutableStateOf("") }
    var specialty        by remember { mutableStateOf("") }
    var accountType      by remember { mutableStateOf("P") } // "P" = Patient, "R" = Professional

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    // Navigate when registration succeeds
    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.SuccessPatient      -> onPatient()
            is AuthUiState.SuccessProfessional -> onProfessional()
            else                               -> Unit
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Create Account", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = {
                        vm.resetState()
                        onBack()
                    }) {
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Account type toggle ───────────────────────────────────────────
            Text("Account type", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AccountTypeButton(
                    label      = "Patient",
                    selected   = accountType == "P",
                    onClick    = { accountType = "P" },
                    modifier   = Modifier.weight(1f)
                )
                AccountTypeButton(
                    label      = "Professional",
                    selected   = accountType == "R",
                    onClick    = { accountType = "R" },
                    color      = SoftTeal,
                    modifier   = Modifier.weight(1f)
                )
            }

            // ── Personal info ─────────────────────────────────────────────────
            JournalTextField(
                value         = firstName,
                onValueChange = { firstName = it },
                label         = "First name *",
                placeholder   = "Ian"
            )

            JournalTextField(
                value         = lastNamePaternal,
                onValueChange = { lastNamePaternal = it },
                label         = "Last name *",
                placeholder   = "Hernandez"
            )

            JournalTextField(
                value         = phoneNumber,
                onValueChange = { phoneNumber = it },
                label         = "Phone number * (used as your ID)",
                placeholder   = "7875551234",
                keyboardType  = KeyboardType.Phone
            )

            // ── Specialty (only for professionals) ────────────────────────────
            if (accountType == "R") {
                JournalTextField(
                    value         = specialty,
                    onValueChange = { specialty = it },
                    label         = "Specialty (e.g. Psychiatry, Psychology)",
                    placeholder   = "Psychiatry"
                )
            }

            // ── Credentials ───────────────────────────────────────────────────
            JournalTextField(
                value         = email,
                onValueChange = { email = it },
                label         = "Email address *",
                placeholder   = "user@email.com",
                keyboardType  = KeyboardType.Email
            )

            JournalTextField(
                value         = password,
                onValueChange = { password = it },
                label         = "Password *",
                placeholder   = "••••••••",
                isPassword    = true
            )

            // ── Error ─────────────────────────────────────────────────────────
            if (uiState is AuthUiState.Error) {
                Text(
                    text      = (uiState as AuthUiState.Error).message,
                    color     = MaterialTheme.colorScheme.error,
                    fontSize  = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth()
                )
            }

            // ── Submit button ─────────────────────────────────────────────────
            Button(
                onClick = {
                    vm.register(
                        firstName        = firstName,
                        lastNamePaternal = lastNamePaternal,
                        email            = email,
                        password         = password,
                        phoneNumber      = phoneNumber,
                        accountType      = accountType,
                        specialty        = if (accountType == "R") specialty.ifBlank { null } else null
                    )
                },
                enabled  = uiState !is AuthUiState.Loading,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = if (accountType == "P") Sage else SoftTeal
                )
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create Account", fontWeight = FontWeight.Medium, color = White)
                }
            }

            // ── Login link ────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account? ", fontSize = 13.sp, color = TextSecondary)
                Text(
                    text       = "Sign In",
                    fontSize   = 13.sp,
                    color      = Sage,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.clickable {
                        vm.resetState()
                        onBack()
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Account type button ───────────────────────────────────────────────────────
@Composable
private fun AccountTypeButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: androidx.compose.ui.graphics.Color = Sage,
    modifier: Modifier = Modifier
) {
    val bg     = if (selected) color else White
    val fg     = if (selected) White else TextSecondary
    val border = if (selected) color else CreamBorder

    OutlinedButton(
        onClick  = onClick,
        modifier = modifier.height(44.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = ButtonDefaults.outlinedButtonColors(
            containerColor = bg,
            contentColor   = fg
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.linearGradient(listOf(border, border))
        )
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    JournalInsightTheme { RegisterScreen() }
}
