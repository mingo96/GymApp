package com.mintocode.rutinapp.ui.screens.sheets

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.data.models.TrainerRelationModel
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.SettingsViewModel

// ── KP Color constants ──
private val KP_PRIMARY = Color(0xFFBAC3FF)
private val KP_PRIMARY_CONTAINER = Color(0xFF4361EE)
private val KP_ON_PRIMARY = Color(0xFF00218D)
private val KP_TERTIARY = Color(0xFF27E0A9)
private val KP_SURFACE_BG_TOP = Color(0xFF1A1A24)
private val KP_SURFACE_BG_BOTTOM = Color(0xFF131318)
private val KP_SURFACE_CONTAINER_LOW = Color(0xFF1B1B20)
private val KP_SURFACE_CONTAINER_HIGH = Color(0xFF2A292F)
private val KP_SURFACE_CONTAINER_HIGHEST = Color(0xFF35343A)
private val KP_ON_SURFACE = Color(0xFFE4E1E9)
private val KP_ON_SURFACE_VARIANT = Color(0xFFC4C5D7)
private val KP_OUTLINE = Color(0xFF8E8FA1)
private val KP_OUTLINE_VARIANT = Color(0xFF444655)
private val KP_ERROR = Color(0xFFFFB4AB)
private val KP_YELLOW = Color(0xFFEAB308)
private val KP_BACKGROUND = Color(0xFF131318)

/**
 * Trainer management sheet with KP design system.
 *
 * Features gradient background, notification toggle, monospace code input
 * with metallic gradient button, trainer cards (Approved/Pending variants),
 * and capacity indicator with tertiary progress bar.
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
            .background(
                Brush.verticalGradient(listOf(KP_SURFACE_BG_TOP, KP_SURFACE_BG_BOTTOM))
            )
            .padding(horizontal = 24.dp)
    ) {
        // ── Drag Handle ──
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp)
                .width(48.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(KP_OUTLINE_VARIANT.copy(alpha = 0.3f))
        )

        Spacer(Modifier.height(16.dp))

        // ── Header ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Entrenadores",
                fontFamily = SpaceGroteskFont,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = KP_ON_SURFACE,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // ── Notifications Toggle ──
            NotificationsToggle(
                hasPermission = hasPermission,
                onRequestPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        hasPermission = true
                        viewModel.registerFcmTokenIfNeeded()
                    }
                }
            )

            // ── Redeem Code Section ──
            RedeemCodeSection(
                code = inviteCode,
                onCodeChange = { inviteCode = it },
                onRedeem = {
                    viewModel.redeemInviteCode(inviteCode, context)
                    inviteCode = ""
                }
            )

            // ── My Trainers ──
            MyTrainersSection(
                trainers = trainers,
                onRevoke = { viewModel.revokeTrainer(it.id, context) }
            )

            // ── Capacity Indicator ──
            if (trainers.isNotEmpty()) {
                CapacityIndicator(
                    current = trainers.size,
                    max = 3
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

/**
 * Notifications toggle row with tertiary icon and check indicator.
 */
@Composable
private fun NotificationsToggle(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(KP_SURFACE_CONTAINER_LOW)
            .clickable { if (!hasPermission) onRequestPermission() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Notifications,
                contentDescription = null,
                tint = KP_TERTIARY,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = if (hasPermission) "NOTIFICACIONES ACTIVADAS" else "ACTIVAR NOTIFICACIONES",
                fontFamily = ManropeFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = KP_ON_SURFACE
            )
        }

        if (hasPermission) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(KP_TERTIARY.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Activado",
                    tint = KP_TERTIARY,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Redeem code input with monospace styling and metallic gradient button.
 */
@Composable
private fun RedeemCodeSection(
    code: String,
    onCodeChange: (String) -> Unit,
    onRedeem: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Section title
        Text(
            text = "CANJEAR CÓDIGO",
            fontFamily = ManropeFont,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.5.sp,
            color = KP_OUTLINE
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Mono input ──
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(KP_SURFACE_CONTAINER_HIGH)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                BasicTextField(
                    value = code,
                    onValueChange = onCodeChange,
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = KP_PRIMARY
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (code.isEmpty()) {
                            Text(
                                text = "EJ: COACH-2024",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp,
                                color = KP_OUTLINE_VARIANT
                            )
                        }
                        innerTextField()
                    }
                )
            }

            // ── Metallic gradient button ──
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = tween(200), label = "redeem_scale"
            )

            Box(
                modifier = Modifier
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .shadow(
                        8.dp, RoundedCornerShape(12.dp),
                        spotColor = KP_PRIMARY_CONTAINER.copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(listOf(KP_PRIMARY, KP_PRIMARY_CONTAINER))
                    )
                    .clickable(interactionSource, indication = null, onClick = onRedeem)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Canjear",
                    fontFamily = SpaceGroteskFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KP_ON_PRIMARY
                )
            }
        }
    }
}

/**
 * My trainers section with trainer cards.
 */
@Composable
private fun MyTrainersSection(
    trainers: List<TrainerRelationModel>,
    onRevoke: (TrainerRelationModel) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "MIS ENTRENADORES",
            fontFamily = ManropeFont,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.5.sp,
            color = KP_OUTLINE
        )

        if (trainers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(KP_SURFACE_CONTAINER_LOW)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay entrenadores vinculados",
                    fontFamily = ManropeFont,
                    fontSize = 14.sp,
                    color = KP_ON_SURFACE_VARIANT
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(0.dp, 400.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(trainers) { trainer ->
                    TrainerCard(
                        trainer = trainer,
                        onRevoke = { onRevoke(trainer) }
                    )
                }
            }
        }
    }
}

/**
 * Trainer card with Approved/Pending variants.
 *
 * Approved: full color, verified badge, Block + Revoke action buttons.
 * Pending: grayscale avatar, muted name, yellow badge, no actions.
 */
@Composable
private fun TrainerCard(
    trainer: TrainerRelationModel,
    onRevoke: () -> Unit
) {
    val isApproved = trainer.status == "approved"
    val isPending = trainer.status == "pending"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(KP_SURFACE_CONTAINER_LOW)
            .then(
                if (isPending) Modifier.border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                else Modifier
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Avatar with verified badge ──
            Box {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isPending) KP_SURFACE_CONTAINER_HIGH.copy(alpha = 0.7f)
                            else KP_SURFACE_CONTAINER_HIGH
                        )
                        .then(
                            if (isPending) Modifier.graphicsLayer(alpha = 0.7f) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = null,
                        tint = if (isPending) KP_OUTLINE else KP_ON_SURFACE_VARIANT,
                        modifier = Modifier.size(28.dp)
                    )
                }

                if (isApproved) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(KP_BACKGROUND)
                            .padding(2.dp)
                    ) {
                        Icon(
                            Icons.Filled.Verified,
                            contentDescription = "Verificado",
                            tint = KP_TERTIARY,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Entrenador #${trainer.trainerUserId}",
                    fontFamily = SpaceGroteskFont,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPending) KP_ON_SURFACE.copy(alpha = 0.8f) else KP_ON_SURFACE
                )

                Spacer(Modifier.height(4.dp))

                // ── Status badge ──
                StatusBadge(status = trainer.status)
            }
        }

        // ── Action buttons (only for approved) ──
        if (isApproved) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Block button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, KP_ERROR.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { /* block action */ }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Block,
                            contentDescription = null,
                            tint = KP_ERROR,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Bloquear",
                            fontFamily = ManropeFont,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = KP_ERROR
                        )
                    }
                }

                // Revoke button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(KP_SURFACE_CONTAINER_HIGHEST)
                        .clickable(onClick = onRevoke)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Revocar",
                        fontFamily = ManropeFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = KP_ON_SURFACE
                    )
                }
            }
        }
    }
}

/**
 * Status badge with color variants per state.
 *
 * Approved: tertiary bg/border/text.
 * Pending: yellow bg/border/text.
 * Other: outline colors.
 */
@Composable
private fun StatusBadge(status: String) {
    val (bgColor, textColor, borderColor, label) = when (status) {
        "approved" -> listOf(
            KP_TERTIARY.copy(alpha = 0.1f),
            KP_TERTIARY,
            KP_TERTIARY.copy(alpha = 0.2f),
            "APROBADO"
        )
        "pending" -> listOf(
            KP_YELLOW.copy(alpha = 0.1f),
            KP_YELLOW,
            KP_YELLOW.copy(alpha = 0.2f),
            "PENDIENTE"
        )
        "blocked" -> listOf(
            KP_ERROR.copy(alpha = 0.1f),
            KP_ERROR,
            KP_ERROR.copy(alpha = 0.2f),
            "BLOQUEADO"
        )
        else -> listOf(
            KP_OUTLINE.copy(alpha = 0.1f),
            KP_OUTLINE,
            KP_OUTLINE.copy(alpha = 0.2f),
            status.uppercase()
        )
    }

    @Suppress("UNCHECKED_CAST")
    Text(
        text = label as String,
        fontFamily = ManropeFont,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = textColor as Color,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor as Color)
            .border(1.dp, borderColor as Color, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

/**
 * Capacity indicator with progress bar and label.
 *
 * Shows a 1dp-height tertiary progress bar with glow and a capacity label.
 */
@Composable
private fun CapacityIndicator(current: Int, max: Int) {
    val progress = (current.toFloat() / max).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Progress track ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(KP_SURFACE_CONTAINER_HIGHEST)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(KP_TERTIARY)
                    .drawBehind {
                        drawRoundRect(
                            color = KP_TERTIARY.copy(alpha = 0.5f),
                            cornerRadius = CornerRadius(50f),
                            size = size.copy(
                                width = size.width,
                                height = size.height + 8.dp.toPx()
                            )
                        )
                    }
            )
        }

        Text(
            text = "Capacidad del equipo: $current/$max",
            fontFamily = ManropeFont,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = KP_OUTLINE,
            textAlign = TextAlign.Center
        )
    }
}
