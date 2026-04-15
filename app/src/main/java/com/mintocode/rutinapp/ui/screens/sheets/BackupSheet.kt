package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Backup
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material.icons.twotone.CloudDownload
import androidx.compose.material.icons.twotone.CloudUpload
import androidx.compose.material.icons.twotone.Error
import androidx.compose.material.icons.twotone.FitnessCenter
import androidx.compose.material.icons.automirrored.twotone.ListAlt
import androidx.compose.material.icons.twotone.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.BackupSelection
import com.mintocode.rutinapp.viewmodels.BackupUiState
import com.mintocode.rutinapp.viewmodels.BackupViewModel

/**
 * Backup sheet — Kinetic Precision design.
 *
 * Shows backup summary, resource selection toggles, and action buttons
 * for upload, download, and catch-up operations.
 *
 * @param viewModel BackupViewModel for state and actions
 */
@Composable
fun BackupSheet(viewModel: BackupViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val selection by viewModel.selection.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSummary()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // ── Page Title ──
        Text(
            text = "Copia de Seguridad",
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            letterSpacing = (-1).sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(48.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.secondary)
        )

        Spacer(Modifier.height(24.dp))

        // ── Summary Card ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Backup,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Estado del servidor",
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (summary != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        BackupStatMini(
                            label = "Ejercicios",
                            count = summary!!.exercises.count,
                            color = MaterialTheme.colorScheme.primary
                        )
                        BackupStatMini(
                            label = "Rutinas",
                            count = summary!!.routines.count,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        BackupStatMini(
                            label = "Entrenos",
                            count = summary!!.workouts.count,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                } else if (uiState is BackupUiState.Loading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Resource Selection ──
        KPBackupSectionLabel("Seleccionar recursos")
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BackupResourceChip(
                label = "Ejercicios",
                icon = Icons.TwoTone.FitnessCenter,
                selected = selection.exercises,
                onClick = { viewModel.toggleResource("exercises") },
                modifier = Modifier.weight(1f)
            )
            BackupResourceChip(
                label = "Rutinas",
                icon = Icons.AutoMirrored.TwoTone.ListAlt,
                selected = selection.routines,
                onClick = { viewModel.toggleResource("routines") },
                modifier = Modifier.weight(1f)
            )
            BackupResourceChip(
                label = "Entrenos",
                icon = Icons.TwoTone.FitnessCenter,
                selected = selection.workouts,
                onClick = { viewModel.toggleResource("workouts") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Action Buttons ──
        KPBackupSectionLabel("Acciones")
        Spacer(Modifier.height(12.dp))

        val isLoading = uiState is BackupUiState.Loading
        val hasSelection = selection.hasSelection()

        // Upload backup
        BackupActionCard(
            icon = Icons.TwoTone.CloudUpload,
            title = "Subir Backup",
            subtitle = "Sincroniza tus datos locales con el servidor",
            gradient = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primaryContainer
                )
            ),
            enabled = !isLoading && hasSelection,
            onClick = { viewModel.uploadBackup() }
        )

        Spacer(Modifier.height(8.dp))

        // Download backup
        BackupActionCard(
            icon = Icons.TwoTone.CloudDownload,
            title = "Descargar Backup",
            subtitle = "Descarga tus datos del servidor",
            gradient = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.secondaryContainer
                )
            ),
            enabled = !isLoading && hasSelection,
            onClick = { viewModel.downloadBackup() }
        )

        Spacer(Modifier.height(8.dp))

        // Catch-up
        BackupActionCard(
            icon = Icons.TwoTone.Sync,
            title = "Ponerse al Día",
            subtitle = "Actualiza cambios recientes desde el servidor",
            gradient = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.tertiary,
                    MaterialTheme.colorScheme.tertiaryContainer
                )
            ),
            enabled = !isLoading && hasSelection,
            onClick = { viewModel.catchUp(java.time.LocalDate.now().minusDays(7).toString()) }
        )

        Spacer(Modifier.height(24.dp))

        // ── State feedback ──
        AnimatedVisibility(
            visible = uiState !is BackupUiState.Idle && uiState !is BackupUiState.Loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            BackupResultCard(uiState, onDismiss = { viewModel.resetState() })
        }

        // ── Loading overlay ──
        AnimatedVisibility(visible = isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                    Text(
                        text = "Procesando...",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

/**
 * Mini stat inside the summary card.
 */
@Composable
private fun BackupStatMini(
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Toggleable resource chip following KP design.
 */
@Composable
private fun BackupResourceChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (selected)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    else
        MaterialTheme.colorScheme.surfaceContainerLow
    val borderColor = if (selected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    else
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f)
    val contentColor = if (selected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}

/**
 * KP section label for backup sheet.
 */
@Composable
private fun KPBackupSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontFamily = SpaceGroteskFont,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 2.sp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

/**
 * Action card with gradient accent and icon.
 */
@Composable
private fun BackupActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradient: Brush,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.let { if (!enabled) it.then(Modifier) else it }
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

/**
 * Result card showing success/error after an operation.
 */
@Composable
private fun BackupResultCard(state: BackupUiState, onDismiss: () -> Unit) {
    val (icon, title, details, iconColor) = when (state) {
        is BackupUiState.ExportDone -> {
            val exerciseCount = state.data.exercises?.size ?: 0
            val routineCount = state.data.routines?.size ?: 0
            val workoutCount = state.data.workouts?.size ?: 0
            ResultInfo(
                Icons.TwoTone.CheckCircle,
                "Descarga completada",
                "$exerciseCount ejercicios · $routineCount rutinas · $workoutCount entrenos",
                MaterialTheme.colorScheme.tertiary
            )
        }

        is BackupUiState.ImportDone -> {
            val ex = state.data.exercises
            val rt = state.data.routines
            val wk = state.data.workouts
            val created = (ex?.created ?: 0) + (rt?.created ?: 0) + (wk?.created ?: 0)
            val updated = (ex?.updated ?: 0) + (rt?.updated ?: 0) + (wk?.updated ?: 0)
            ResultInfo(
                Icons.TwoTone.CheckCircle,
                "Backup subido",
                "$created creados · $updated actualizados",
                MaterialTheme.colorScheme.tertiary
            )
        }

        is BackupUiState.CatchUpDone -> {
            ResultInfo(
                Icons.TwoTone.Sync,
                "Sincronización completada",
                "Datos actualizados correctamente",
                MaterialTheme.colorScheme.tertiary
            )
        }

        is BackupUiState.Error -> {
            ResultInfo(
                Icons.TwoTone.Error,
                "Error",
                state.message,
                MaterialTheme.colorScheme.error
            )
        }

        else -> return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = details,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Aceptar", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/**
 * Helper data class for result card rendering.
 */
private data class ResultInfo(
    val icon: ImageVector,
    val title: String,
    val details: String,
    val iconColor: androidx.compose.ui.graphics.Color
)
