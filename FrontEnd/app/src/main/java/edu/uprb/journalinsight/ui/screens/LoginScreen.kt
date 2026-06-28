package edu.uprb.journalinsight.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uprb.journalinsight.R
import edu.uprb.journalinsight.ui.theme.*
import edu.uprb.journalinsight.ui.viewmodel.AuthUiState
import edu.uprb.journalinsight.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onPatient: () -> Unit = {},
    onProfessional: () -> Unit = {},
    onRegister: () -> Unit = {},
    vm: AuthViewModel = viewModel()
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    // Navigate when login succeeds
    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.SuccessPatient      -> onPatient()
            is AuthUiState.SuccessProfessional -> onProfessional()
            else                               -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        // ── Hero ──────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(PeachRose, Lavender)))
                .padding(top = 56.dp, bottom = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter            = painterResource(id = R.drawable.ic_journalinsight),
                        contentDescription = "JournalInsight logo",
                        modifier           = Modifier.size(72.dp)
                    )
                }
                Text(
                    text       = "JournalInsight",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color      = Color.White
                )
                Text(
                    text     = "Your personal emotional journal",
                    fontSize = 13.sp,
                    color    = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // ── Form ──────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            JournalTextField(
                value         = email,
                onValueChange = { email = it },
                label         = "Email address",
                placeholder   = "user@email.com",
                keyboardType  = KeyboardType.Email
            )

            JournalTextField(
                value         = password,
                onValueChange = { password = it },
                label         = "Password",
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

            // ── Sign in button ────────────────────────────────────────────────
            Button(
                onClick  = { vm.login(email, password) },
                enabled  = uiState !is AuthUiState.Loading,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Sage)
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign In", fontWeight = FontWeight.Medium, color = White)
                }
            }

            // ── Register link ─────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Don't have an account? ", fontSize = 13.sp, color = TextSecondary)
                Text(
                    text     = "Register",
                    fontSize = 13.sp,
                    color    = Sage,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        vm.resetState()
                        onRegister()
                    }
                )
            }
        }
    }
}

// ── Reusable text field ───────────────────────────────────────────────────────
@Composable
fun JournalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value                = value,
            onValueChange        = onValueChange,
            placeholder          = { Text(placeholder, color = TextHint, fontSize = 13.sp) },
            singleLine           = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation()
                                   else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions      = KeyboardOptions(keyboardType = keyboardType),
            shape                = RoundedCornerShape(12.dp),
            colors               = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor    = CreamBorder,
                focusedBorderColor      = Sage,
                unfocusedContainerColor = White,
                focusedContainerColor   = White,
                unfocusedTextColor      = TextPrimary,
                focusedTextColor        = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    JournalInsightTheme { LoginScreen() }
}
