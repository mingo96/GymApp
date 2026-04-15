package com.mintocode.rutinapp.ui.screens.root

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Login
import androidx.compose.material.icons.automirrored.twotone.Logout
import androidx.compose.material.icons.twotone.Backup
import androidx.compose.material.icons.twotone.ChevronRight
import androidx.compose.material.icons.twotone.DarkMode
import androidx.compose.material.icons.twotone.Group
import androidx.compose.material.icons.twotone.Insights
import androidx.compose.material.icons.twotone.Lock
import androidx.compose.material.icons.twotone.ManageAccounts
import androidx.compose.material.icons.twotone.Notifications
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.Verified
import androidx.compose.material.icons.twotone.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.SettingsViewModel

/**
 * Profile root page — Kinetic Precision design.
 *
 * User info card with avatar, tags, and edit button.
 * Settings grouped by section with icon containers.
 * Theme toggle and logout button.
 *
 * @param settingsViewModel ViewModel for user data and settings
 */
@Composable
fun ProfilePage(settingsViewModel: SettingsViewModel) {
    val navigator = LocalSheetNavigator.current
    val data by settingsViewModel.data.observeAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // ── Page Title ──
        Text(
            text = "Perfil",
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 48.sp,
            letterSpacing = (-1.5).sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(48.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.tertiary)
        )

        Spacer(Modifier.height(40.dp))

        // ── User Card ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            // Decorative blob
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 16.dp, y = (-16).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with rotation + verified badge
                Box {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .rotate(3f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    // Verified badge
                    if (!data?.authToken.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.TwoTone.Verified,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Name + email
                Text(
                    text = data?.name?.ifBlank { "Usuario" } ?: "Usuario",
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = data?.email?.ifBlank { "Sin cuenta vinculada" }
                        ?: "Sin cuenta vinculada",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                // Tags
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KPTag(
                        text = "MIEMBRO",
                        color = MaterialTheme.colorScheme.primary,
                        bgColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Edit Profile button (gradient)
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    animationSpec = tween(150),
                    label = "edit_btn_scale"
                )
                Box(
                    modifier = Modifier
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                        .clickable(interactionSource, indication = null) {
                            navigator.open(SheetDestination.Settings)
                        }
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Editar Perfil",
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(48.dp))

        // ── Personalización ──
        KPSectionLabel("Personalización")
        Spacer(Modifier.height(16.dp))

        // Theme toggle row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                KPIconContainer(Icons.TwoTone.DarkMode, MaterialTheme.colorScheme.primary)
                Text(
                    text = "Tema oscuro",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Switch(
                checked = data?.isDarkTheme ?: true,
                onCheckedChange = { settingsViewModel.toggleRutinAppTheme() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Cuenta y Seguridad ──
        KPSectionLabel("Cuenta y Seguridad")
        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            KPSettingsItem(
                icon = Icons.TwoTone.ManageAccounts,
                label = "Ajustes de cuenta",
                onClick = { navigator.open(SheetDestination.Settings) }
            )

            if (!data?.authToken.isNullOrBlank()) {
                KPSettingsItem(
                    icon = Icons.TwoTone.Lock,
                    label = "Sesión iniciada",
                    onClick = { navigator.open(SheetDestination.Auth) }
                )
            } else {
                KPSettingsItem(
                    icon = Icons.TwoTone.Lock,
                    label = "Inicio de sesión",
                    onClick = { navigator.open(SheetDestination.Auth) }
                )
            }

            KPSettingsItem(
                icon = Icons.TwoTone.Notifications,
                label = "Notificaciones",
                onClick = { navigator.open(SheetDestination.Notifications) }
            )

            KPSettingsItem(
                icon = Icons.TwoTone.Group,
                label = "Entrenadores",
                onClick = { navigator.open(SheetDestination.TrainerManagement) }
            )

            KPSettingsItem(
                icon = Icons.TwoTone.Insights,
                label = "Estadísticas",
                onClick = { navigator.open(SheetDestination.StatsOverview) }
            )

            KPSettingsItem(
                icon = Icons.TwoTone.Backup,
                label = "Copia de seguridad",
                onClick = { navigator.open(SheetDestination.Backup) }
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Configuración ──
        KPSectionLabel("Configuración")
        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            KPSettingsItem(
                icon = Icons.TwoTone.Widgets,
                label = "Widget flotante",
                onClick = { navigator.open(SheetDestination.AppConfig) }
            )
        }

        Spacer(Modifier.height(48.dp))

        // ── Logout ──
        if (!data?.authToken.isNullOrBlank()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CERRAR SESIÓN",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.error,
                    textDecoration = TextDecoration.None,
                    modifier = Modifier
                        .clickable { settingsViewModel.logOut(context) }
                        .padding(vertical = 4.dp)
                )
                Spacer(Modifier.height(24.dp))
            }
        }

        // ── Version ──
        Text(
            text = "KINETIC VAULT • RutinApp 2024",
            fontFamily = ManropeFont,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(48.dp))
    }
}

// ── Private helpers ──

/**
 * Tag pill with custom color and background.
 */
@Composable
private fun KPTag(text: String, color: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            color = color
        )
    }
}

/**
 * KP section label — uppercase, bold, outline color.
 */
@Composable
private fun KPSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontFamily = SpaceGroteskFont,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 2.sp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

/**
 * KP icon container — rounded square, surfaceContainerHighest bg.
 */
@Composable
private fun KPIconContainer(
    icon: ImageVector,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

/**
 * KP settings menu item — surfaceContainerLow, icon container, chevron.
 */
@Composable
private fun KPSettingsItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KPIconContainer(icon)
            Text(
                text = label,
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            Icons.TwoTone.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )
    }
}
