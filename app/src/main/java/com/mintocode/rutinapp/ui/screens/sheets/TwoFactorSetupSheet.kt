package com.mintocode.rutinapp.ui.screens.sheets

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ContentCopy
import androidx.compose.material.icons.twotone.Security
import androidx.compose.material.icons.twotone.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.SettingsViewModel

// ── KP Color Constants ──
private val KP_PRIMARY = Color(0xFFBAC3FF)
private val KP_PRIMARY_CONTAINER = Color(0xFF4361EE)
private val KP_ON_PRIMARY = Color(0xFF00218D)
private val KP_TERTIARY = Color(0xFF27E0A9)
private val KP_ON_SURFACE = Color(0xFFE4E1E9)
private val KP_ON_SURFACE_VARIANT = Color(0xFFC4C5D7)
private val KP_OUTLINE = Color(0xFF8E8FA1)
private val KP_OUTLINE_VARIANT = Color(0xFF444655)
private val KP_SURFACE_CONTAINER_HIGH = Color(0xFF2A292F)
private val KP_SURFACE_CONTAINER_HIGHEST = Color(0xFF35343A)

/**
 * Two-factor authentication setup sheet — KP design.
 *
 * Three-step flow:
 * 1. Enable 2FA → shows secret key to add to authenticator app
 * 2. Enter TOTP code to confirm → activates 2FA
 * 3. Show recovery codes (one-time display)
 *
 * @param viewModel SettingsViewModel with 2FA methods
 */
@Composable
fun TwoFactorSetupSheet(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val secret by viewModel.twoFactorSetupSecret.observeAsState(null)
    val recoveryCodes by viewModel.recoveryCodes.observeAsState(null)
    var code by rememberSaveable { mutableStateOf("") }

    // Trigger enable on open to get the secret
    LaunchedEffect(Unit) {
        viewModel.enableTwoFactor(context)
    }

    // Clear setup state when leaving
    DisposableEffect(Unit) {
        onDispose { viewModel.clearTwoFactorSetup() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── TopBar ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Configurar 2FA",
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = KP_ON_SURFACE
            )
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(16.dp))

            // ── Hero Icon ──
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(KP_PRIMARY_CONTAINER.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.TwoTone.Shield,
                        contentDescription = null,
                        tint = KP_PRIMARY,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            if (recoveryCodes != null) {
                // ── Step 3: Recovery Codes ──
                Text(
                    text = "¡2FA activado!",
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = KP_TERTIARY,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Guarda estos códigos de recuperación en un lugar seguro. Solo se muestran una vez.",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = KP_ON_SURFACE_VARIANT,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // Recovery codes grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(KP_SURFACE_CONTAINER_HIGH)
                        .border(1.dp, KP_OUTLINE_VARIANT.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recoveryCodes!!.forEachIndexed { index, recoveryCode ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}.",
                                fontFamily = ManropeFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = KP_OUTLINE,
                                modifier = Modifier.width(24.dp)
                            )
                            Text(
                                text = recoveryCode,
                                fontFamily = ManropeFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 2.sp,
                                color = KP_ON_SURFACE
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Copy all codes button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, KP_PRIMARY, RoundedCornerShape(12.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(
                                ClipData.newPlainText("Recovery Codes", recoveryCodes!!.joinToString("\n"))
                            )
                            Toast.makeText(context, "Códigos copiados", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.TwoTone.ContentCopy,
                        contentDescription = null,
                        tint = KP_PRIMARY,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Copiar códigos",
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = KP_PRIMARY
                    )
                }
            } else if (secret != null) {
                // ── Step 2: Enter code to confirm ──
                Text(
                    text = "Añade la clave a tu autenticador",
                    fontFamily = SpaceGroteskFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = KP_ON_SURFACE,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Abre Google Authenticator u otra app TOTP y añade esta clave manualmente.",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = KP_ON_SURFACE_VARIANT,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // Secret key display
                SectionLabel("CLAVE SECRETA")
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(KP_SURFACE_CONTAINER_HIGH)
                        .border(1.dp, KP_OUTLINE_VARIANT.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("2FA Secret", secret))
                            Toast.makeText(context, "Clave copiada", Toast.LENGTH_SHORT).show()
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = secret!!,
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 2.sp,
                        color = KP_ON_SURFACE,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.TwoTone.ContentCopy,
                        contentDescription = "Copiar clave",
                        tint = KP_OUTLINE,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.height(32.dp))

                // Code input
                SectionLabel("CÓDIGO DE VERIFICACIÓN")
                Spacer(Modifier.height(8.dp))

                TextField(
                    value = code,
                    onValueChange = { if (it.length <= 6) code = it },
                    placeholder = {
                        Text(
                            text = "000000",
                            fontFamily = ManropeFont,
                            color = KP_OUTLINE.copy(alpha = 0.5f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.TwoTone.Security,
                            contentDescription = null,
                            tint = KP_OUTLINE,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (code.length == 6) viewModel.confirmTwoFactor(code, context)
                        }
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = KP_SURFACE_CONTAINER_HIGH,
                        unfocusedContainerColor = KP_SURFACE_CONTAINER_HIGH,
                        focusedTextColor = KP_ON_SURFACE,
                        unfocusedTextColor = KP_ON_SURFACE,
                        cursorColor = KP_PRIMARY,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(Modifier.height(24.dp))

                // Confirm button (gradient CTA)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (code.length == 6)
                                Brush.linearGradient(listOf(KP_PRIMARY, KP_PRIMARY_CONTAINER))
                            else Brush.linearGradient(
                                listOf(
                                    KP_SURFACE_CONTAINER_HIGHEST,
                                    KP_SURFACE_CONTAINER_HIGHEST
                                )
                            )
                        )
                        .clickable(enabled = code.length == 6) {
                            viewModel.confirmTwoFactor(code, context)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Verificar y activar",
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (code.length == 6) KP_ON_PRIMARY else KP_OUTLINE
                    )
                }
            } else {
                // ── Step 1: Loading ──
                Text(
                    text = "Configurando autenticación de dos factores...",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = KP_ON_SURFACE_VARIANT,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp)
                )
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

/**
 * Two-factor authentication disable sheet — KP design.
 *
 * Asks for a TOTP or recovery code to disable 2FA.
 *
 * @param viewModel SettingsViewModel with disableTwoFactor method
 */
@Composable
fun TwoFactorDisableSheet(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    var code by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── TopBar ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Desactivar 2FA",
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = KP_ON_SURFACE
            )
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(16.dp))

            // ── Hero Icon ──
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.TwoTone.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Desactivar autenticación",
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = KP_ON_SURFACE,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Introduce un código de tu autenticador o un código de recuperación para desactivar 2FA.",
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = KP_ON_SURFACE_VARIANT,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            SectionLabel("CÓDIGO")
            Spacer(Modifier.height(8.dp))

            TextField(
                value = code,
                onValueChange = { code = it },
                placeholder = {
                    Text(
                        text = "Código TOTP o de recuperación",
                        fontFamily = ManropeFont,
                        color = KP_OUTLINE.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.TwoTone.Security,
                        contentDescription = null,
                        tint = KP_OUTLINE,
                        modifier = Modifier.size(20.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (code.isNotBlank()) viewModel.disableTwoFactor(code, context)
                    }
                ),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = KP_SURFACE_CONTAINER_HIGH,
                    unfocusedContainerColor = KP_SURFACE_CONTAINER_HIGH,
                    focusedTextColor = KP_ON_SURFACE,
                    unfocusedTextColor = KP_ON_SURFACE,
                    cursorColor = KP_PRIMARY,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Disable button (error-tinted)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (code.isNotBlank()) MaterialTheme.colorScheme.error
                        else KP_OUTLINE_VARIANT.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable(enabled = code.isNotBlank()) {
                        viewModel.disableTwoFactor(code, context)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Desactivar 2FA",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (code.isNotBlank()) MaterialTheme.colorScheme.error
                    else KP_OUTLINE
                )
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

// ── Private Helpers ──

/**
 * Uppercase section label — KP style.
 */
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = ManropeFont,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 2.sp,
        color = KP_ON_SURFACE_VARIANT,
        modifier = Modifier.padding(start = 4.dp)
    )
}
