package com.mintocode.rutinapp.ui.screens.sheets

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material.icons.automirrored.twotone.Logout
import androidx.compose.material.icons.twotone.CameraAlt
import androidx.compose.material.icons.twotone.DeleteForever
import androidx.compose.material.icons.twotone.Lock
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.Visibility
import androidx.compose.material.icons.twotone.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.SettingsViewModel

/**
 * Account settings sheet — KP design (Guide 21).
 *
 * Avatar section, tab selector (Cuenta/Notificaciones/Apariencia),
 * personal data fields, secret code, security section, danger zone,
 * KPI stats row, and gradient save button.
 *
 * @param viewModel SettingsViewModel for user data and actions
 */
@Composable
fun AccountSettingsSheet(viewModel: SettingsViewModel) {
    val data by viewModel.data.observeAsState()
    val context = LocalContext.current

    if (data == null) {
        Text(
            text = "Cargando...",
            fontFamily = ManropeFont,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(32.dp)
        )
        return
    }

    var name by rememberSaveable { mutableStateOf(data!!.name) }
    var code by rememberSaveable { mutableStateOf(data!!.code) }
    var codeVisible by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteConfirmText by remember { mutableStateOf("") }

    val hasChanges = name != data!!.name || code != data!!.code

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // ── TopBar ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ajustes de cuenta",
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // ── Avatar Section ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.TwoTone.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    // Camera overlay
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
                            Icons.TwoTone.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = data!!.name.ifBlank { "Usuario" },
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = data!!.email.ifBlank { "Sin cuenta vinculada" },
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Elite member badge (show if logged in)
                if (!data!!.authToken.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "MIEMBRO",
                            fontFamily = ManropeFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Account Content (directly, no tabs) ──
            AccountTab(
                name = name,
                onNameChange = { name = it },
                email = data!!.email,
                isVerified = !data!!.authToken.isNullOrBlank(),
                code = code,
                onCodeChange = { code = it },
                codeVisible = codeVisible,
                onCodeVisibilityToggle = { codeVisible = !codeVisible },
                onLogout = { viewModel.logOut(context) },
                onShowDeleteDialog = { showDeleteDialog = true }
            )
        }

        // ── Floating Save Button ──
        if (hasChanges) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = tween(150),
                label = "save_scale"
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
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
                        viewModel.updateUserDetails(name = name, code = code)
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "GUARDAR CAMBIOS",
                        fontFamily = SpaceGroteskFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Icon(
                        Icons.AutoMirrored.TwoTone.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    // ── Delete Account Dialog ──
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                deleteConfirmText = ""
            },
            title = {
                Text(
                    text = "¿Eliminar cuenta permanentemente?",
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Esta acción no se puede deshacer. Se borrarán todos tus datos.",
                        fontFamily = ManropeFont,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    OutlinedTextField(
                        value = deleteConfirmText,
                        onValueChange = { deleteConfirmText = it },
                        label = { Text("Escribe ELIMINAR para confirmar") },
                        singleLine = true,
                        colors = kpTextFieldColors()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        deleteConfirmText = ""
                        // TODO: Call delete account API
                    },
                    enabled = deleteConfirmText == "ELIMINAR"
                ) {
                    Text(
                        "Eliminar",
                        color = if (deleteConfirmText == "ELIMINAR")
                            MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    deleteConfirmText = ""
                }) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    }
}

// ── Tab: Cuenta ──

/**
 * Account tab content — personal data, secret code, security, danger zone, KPIs.
 */
@Composable
private fun AccountTab(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    isVerified: Boolean,
    code: String,
    onCodeChange: (String) -> Unit,
    codeVisible: Boolean,
    onCodeVisibilityToggle: () -> Unit,
    onLogout: () -> Unit,
    onShowDeleteDialog: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ── Datos Personales ──
        KPFieldLabel("DATOS PERSONALES")
        Spacer(Modifier.height(12.dp))

        KPTextField(
            value = name,
            onValueChange = onNameChange,
            label = "NOMBRE"
        )
        Spacer(Modifier.height(16.dp))

        // Email field (read-only if verified)
        Box {
            KPTextField(
                value = email,
                onValueChange = {},
                label = "EMAIL",
                readOnly = true
            )
            if (isVerified) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "VERIFICADO",
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── Código Secreto ──
        KPFieldLabel("CÓDIGO SECRETO")
        Text(
            text = "Este será su código de activación único",
            fontFamily = ManropeFont,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        OutlinedTextField(
            value = code,
            onValueChange = onCodeChange,
            label = {
                Text(
                    text = "CÓDIGO",
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
            },
            visualTransformation = if (codeVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = onCodeVisibilityToggle) {
                    Icon(
                        imageVector = if (codeVisible) Icons.TwoTone.Visibility
                        else Icons.TwoTone.VisibilityOff,
                        contentDescription = "Toggle visibilidad",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            },
            singleLine = true,
            colors = kpTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        // ── Seguridad y Conexiones ──
        KPFieldLabel("SEGURIDAD Y CONEXIONES")
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KPOutlinedButton(
                icon = Icons.TwoTone.Lock,
                text = "Cambiar contraseña",
                onClick = { /* TODO: Navigate to change password */ },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(40.dp))

        // ── Zona de Peligro ──
        KPFieldLabel("ZONA DE PELIGRO", isError = true)
        Spacer(Modifier.height(12.dp))

        KPDangerButton(
            icon = Icons.AutoMirrored.TwoTone.Logout,
            text = "Cerrar sesión",
            onClick = onLogout,
            outlined = true
        )
        Spacer(Modifier.height(8.dp))
        KPDangerButton(
            icon = Icons.TwoTone.DeleteForever,
            text = "Eliminar cuenta",
            onClick = onShowDeleteDialog,
            outlined = false
        )

        Spacer(Modifier.height(32.dp))

        // ── Stats KPI Row ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            KPStatKpi(value = "12", label = "DÍAS")
            KPDivider()
            KPStatKpi(value = "14", label = "ENTRENAMIENTOS")
            KPDivider()
            KPStatKpi(value = "82%", label = "ADHERENCIA")
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ── Shared KP Helpers ──

/**
 * KP-styled text field with outlined border and uppercase label.
 */
@Composable
private fun KPTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        label = {
            Text(
                text = label,
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 2.sp
            )
        },
        singleLine = true,
        colors = kpTextFieldColors(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Returns KP-styled OutlinedTextField colors.
 */
@Composable
private fun kpTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedLabelColor = MaterialTheme.colorScheme.outline,
    unfocusedLabelColor = MaterialTheme.colorScheme.outline
)

/**
 * Uppercase field section label — small, bold, tracking-wide.
 */
@Composable
private fun KPFieldLabel(text: String, isError: Boolean = false) {
    Text(
        text = text,
        fontFamily = SpaceGroteskFont,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 2.sp,
        color = if (isError) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    )
}

/**
 * Outlined button with icon and text — KP style.
 */
@Composable
private fun KPOutlinedButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Danger zone button — outlined or text-only, error color.
 */
@Composable
private fun KPDangerButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    outlined: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (outlined) Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error.copy(alpha = if (outlined) 1f else 0.6f),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            fontFamily = ManropeFont,
            fontWeight = if (outlined) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.error.copy(alpha = if (outlined) 1f else 0.6f)
        )
    }
}

/**
 * KPI stat column — value + label.
 */
@Composable
private fun KPStatKpi(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

/**
 * Thin vertical divider for KPI rows.
 */
@Composable
private fun KPDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(Color.White.copy(alpha = 0.1f))
    )
}
