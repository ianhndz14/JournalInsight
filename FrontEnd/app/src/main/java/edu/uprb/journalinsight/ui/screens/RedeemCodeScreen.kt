package edu.uprb.journalinsight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.uprb.journalinsight.network.dto.PatientDto
import edu.uprb.journalinsight.ui.components.AvatarCircle
import edu.uprb.journalinsight.ui.components.SectionTitle
import edu.uprb.journalinsight.ui.theme.*
import edu.uprb.journalinsight.ui.viewmodel.LinkActionState
import edu.uprb.journalinsight.ui.viewmodel.RedeemCodeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedeemCodeScreen(
    onBack: () -> Unit = {},
    vm: RedeemCodeViewModel = viewModel()
) {
    val professionals by vm.professionals.collectAsStateWithLifecycle()
    val actionState   by vm.actionState.collectAsStateWithLifecycle()
    var phone         by remember { mutableStateOf("") }
    var unlinkTarget  by remember { mutableStateOf<PatientDto?>(null) }

    // Reset input and go back after 2s on success
    LaunchedEffect(actionState) {
        if (actionState is LinkActionState.Success) {
            kotlinx.coroutines.delay(1500)
            phone = ""
            vm.resetAction()
        }
    }

    // ── Unlink confirmation dialog ────────────────────────────────────────────
    unlinkTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { unlinkTarget = null },
            containerColor   = White,
            title = {
                Text("Unlink professional", fontWeight = FontWeight.Medium, color = TextPrimary)
            },
            text = {
                Text(
                    "Are you sure you want to unlink from ${target.fullName}? They will no longer be able to view your journal entries.",
                    fontSize   = 13.sp,
                    color      = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.unlink(target.phoneNumber)
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

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("My Professionals", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { vm.resetAction(); onBack() }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Linked professionals ──────────────────────────────────────────
            SectionTitle("Linked professionals")

            if (professionals.isEmpty()) {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(White)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text      = "You have no linked professionals yet.",
                        fontSize  = 13.sp,
                        color     = TextHint,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    professionals.forEach { professional ->
                        LinkedProfessionalCard(
                            professional = professional,
                            onUnlink     = { unlinkTarget = professional }
                        )
                    }
                }
            }

            HorizontalDivider(color = CreamBorder)

            // ── Link new professional ─────────────────────────────────────────
            SectionTitle("Link new professional")

            Text(
                text       = "Enter your professional's phone number to grant them access to your journal.",
                fontSize   = 13.sp,
                color      = TextSecondary,
                lineHeight = 20.sp
            )

            // Success banner
            if (actionState is LinkActionState.Success) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(TealLight)
                        .padding(14.dp)
                ) {
                    Text(
                        text      = "✅  ${(actionState as LinkActionState.Success).message}",
                        fontSize  = 13.sp,
                        color     = SoftTeal,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                OutlinedTextField(
                    value         = phone,
                    onValueChange = { phone = it; if (actionState is LinkActionState.Error) vm.resetAction() },
                    modifier      = Modifier.fillMaxWidth(),
                    label         = { Text("Phone number") },
                    placeholder   = { Text("e.g. 7875555678") },
                    singleLine    = true,
                    shape         = RoundedCornerShape(14.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = SoftTeal,
                        unfocusedBorderColor    = CreamBorder,
                        focusedContainerColor   = White,
                        unfocusedContainerColor = White
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction    = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { vm.link(phone.trim()) }),
                    isError         = actionState is LinkActionState.Error
                )

                if (actionState is LinkActionState.Error) {
                    Text(
                        text      = (actionState as LinkActionState.Error).message,
                        color     = MaterialTheme.colorScheme.error,
                        fontSize  = 13.sp
                    )
                }

                Button(
                    onClick  = { vm.link(phone.trim()) },
                    enabled  = actionState !is LinkActionState.Loading && phone.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = SoftTeal)
                ) {
                    if (actionState is LinkActionState.Loading) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(20.dp),
                            color       = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Link Professional", fontWeight = FontWeight.Medium, color = White)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Linked professional card ──────────────────────────────────────────────────
@Composable
private fun LinkedProfessionalCard(
    professional: PatientDto,
    onUnlink: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(White)
            .padding(12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AvatarCircle(initials = professional.initials, size = 40, bgColor = SoftTeal)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = professional.fullName,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium,
                color      = TextPrimary
            )
            Text(
                text     = professional.phoneNumber,
                fontSize = 11.sp,
                color    = TextSecondary
            )
        }
        IconButton(onClick = onUnlink) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = "Unlink",
                tint               = MaterialTheme.colorScheme.error
            )
        }
    }
}
