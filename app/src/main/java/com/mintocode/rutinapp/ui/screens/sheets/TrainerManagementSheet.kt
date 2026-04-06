package com.mintocode.rutinapp.ui.screens.sheets

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.SettingsViewModel

/**
 * Trainer management sheet content.
 *
 * Allows the user to redeem trainer invite codes, view linked trainers,
 * and manage notification permissions. Reuses SettingsViewModel logic.
 *
 * @param viewModel SettingsViewModel for trainer and notification actions
 */
@Composable
fun TrainerManagementSheet(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val trainers by viewModel.trainers.observeAsState(emptyList())

    var inviteCode by rememberSaveable { mutableStateOf("") }
    var hasPermission by rememberSaveable {
        mutableStateOf(viewModel.hasNotificationPermission())
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            viewModel.registerFcmTokenIfNeeded()
            Toast.makeText(context, "Notificaciones activadas", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notificaciones denegadas", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) { viewModel.loadTrainers() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Entrenadores",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // ── Notification permission ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Notificaciones", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)

            if (hasPermission) {
                Text(
                    text = "✓ Notificaciones activadas",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            } else {
                Text(
                    text = "Activa las notificaciones para recibir avisos de tu entrenador.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            hasPermission = true
                            viewModel.registerFcmTokenIfNeeded()
                        }
                    },
                    colors = rutinAppButtonsColours()
                ) {
                    Text(text = "Activar notificaciones")
                }
            }
        }

        // ── Redeem invite code ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Canjea un código de invitación para vincular a tu entrenador.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TextFieldWithTitle(
                        title = "Código de invitación",
                        text = inviteCode,
                        onWrite = { inviteCode = it }
                    )
                }
                Button(
                    onClick = {
                        viewModel.redeemInviteCode(inviteCode, context)
                        inviteCode = ""
                    },
                    colors = rutinAppButtonsColours()
                ) {
                    Text(text = "Canjear")
                }
            }
        }

        // ── Trainer list ──
        if (trainers.isNotEmpty()) {
            Text(
                text = "Entrenadores vinculados:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            LazyColumn(
                modifier = Modifier.heightIn(0.dp, 250.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(trainers) { trainer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Entrenador #${trainer.trainerUserId}", fontSize = 14.sp)
                            Text(
                                text = "Estado: ${trainer.status}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = { viewModel.revokeTrainer(trainer.id, context) },
                            colors = rutinAppButtonsColours()
                        ) {
                            Text(text = "Revocar", fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            Text(
                text = "No hay entrenadores vinculados",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
