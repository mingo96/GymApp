package com.mintocode.rutinapp.ui.screens.root

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
import androidx.compose.material.icons.twotone.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.SettingsViewModel

/**
 * Profile root page — Kinetic Precision design.
 *
 * User info card, theme toggle, settings sections, and navigation
 * to Settings, Notifications, Trainer, Stats, and Auth sheets.
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
            fontSize = 40.sp,
            letterSpacing = (-1).sp,
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

        Spacer(Modifier.height(32.dp))

        // ── User Card ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data?.name?.ifBlank { "Usuario" } ?: "Usuario",
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = data?.email?.ifBlank { "Sin cuenta vinculada" } ?: "Sin cuenta vinculada",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── Personalización ──
        KPSectionLabel("Personalización")
        Spacer(Modifier.height(12.dp))

        // Theme toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
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
        Spacer(Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            KPSettingsItem(
                icon = Icons.TwoTone.ManageAccounts,
                label = "Ajustes de cuenta",
                onClick = { navigator.open(SheetDestination.Settings) }
            )

            if (!data?.authToken.isNullOrBlank()) {
                KPSettingsItem(
                    icon = Icons.AutoMirrored.TwoTone.Logout,
                    label = "Cerrar sesión",
                    onClick = { settingsViewModel.logOut(context) }
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

        // ── Configuración de la App ──
        KPSectionLabel("Configuración")
        Spacer(Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            KPSettingsItem(
                icon = Icons.TwoTone.Widgets,
                label = "Widget flotante",
                onClick = { navigator.open(SheetDestination.AppConfig) }
            )
        }

        Spacer(Modifier.height(48.dp))
    }
}

/**
 * KP section label — uppercase tracking, Space Grotesk, outline color.
 */
@Composable
private fun KPSectionLabel(text: String) {
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
            .clip(RoundedCornerShape(10.dp))
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
            .clip(RoundedCornerShape(16.dp))
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
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
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
