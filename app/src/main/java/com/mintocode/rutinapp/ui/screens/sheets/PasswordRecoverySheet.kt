package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material.icons.twotone.Lock
import androidx.compose.material.icons.twotone.LockReset
import androidx.compose.material.icons.twotone.Mail
import androidx.compose.material.icons.twotone.Visibility
import androidx.compose.material.icons.twotone.VisibilityOff
import androidx.compose.material.icons.twotone.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

/**
 * Password recovery sheet — KP design.
 *
 * Two-step flow:
 * 1. Enter email → sends reset link
 * 2. Enter token + new password → resets password
 *
 * @param viewModel SettingsViewModel with password recovery methods
 */
@Composable
fun PasswordRecoverySheet(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var linkSent by rememberSaveable { mutableStateOf(false) }
    var token by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val emailValid = email.contains("@") && email.contains(".")

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
                text = "Recuperar contraseña",
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
                        Icons.TwoTone.LockReset,
                        contentDescription = null,
                        tint = KP_PRIMARY,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = if (linkSent) "Introduce el código" else "¿Olvidaste tu contraseña?",
                fontFamily = SpaceGroteskFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = KP_ON_SURFACE,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (linkSent)
                    "Revisa tu correo e introduce el token de recuperación junto con tu nueva contraseña."
                else
                    "Introduce tu email y te enviaremos un enlace para restablecer tu contraseña.",
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = KP_ON_SURFACE_VARIANT,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            // ── Step 1: Email ──
            RecoveryFieldLabel("CORREO ELECTRÓNICO")
            Spacer(Modifier.height(8.dp))

            RecoveryTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "tucuenta@email.com",
                leadingIcon = Icons.TwoTone.Mail,
                trailingContent = if (emailValid) {
                    {
                        Icon(
                            Icons.TwoTone.CheckCircle,
                            contentDescription = "Email válido",
                            tint = KP_TERTIARY,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else null,
                keyboardType = KeyboardType.Email,
                enabled = !linkSent
            )

            // ── Step 2: Token + New Password (visible after link sent) ──
            AnimatedVisibility(
                visible = linkSent,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(24.dp))

                    RecoveryFieldLabel("TOKEN DE RECUPERACIÓN")
                    Spacer(Modifier.height(8.dp))

                    RecoveryTextField(
                        value = token,
                        onValueChange = { token = it },
                        placeholder = "Pega el token del email",
                        leadingIcon = Icons.TwoTone.VpnKey,
                        keyboardType = KeyboardType.Text
                    )

                    Spacer(Modifier.height(24.dp))

                    RecoveryFieldLabel("NUEVA CONTRASEÑA")
                    Spacer(Modifier.height(8.dp))

                    RecoveryTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = "••••••••",
                        leadingIcon = Icons.TwoTone.Lock,
                        trailingContent = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.TwoTone.Visibility
                                    else Icons.TwoTone.VisibilityOff,
                                    contentDescription = "Toggle contraseña",
                                    tint = KP_OUTLINE,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardType = KeyboardType.Password
                    )

                    Spacer(Modifier.height(24.dp))

                    RecoveryFieldLabel("CONFIRMAR CONTRASEÑA")
                    Spacer(Modifier.height(8.dp))

                    RecoveryTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "••••••••",
                        leadingIcon = Icons.TwoTone.Lock,
                        trailingContent = if (confirmPassword.isNotBlank() && confirmPassword == newPassword) {
                            {
                                Icon(
                                    Icons.TwoTone.CheckCircle,
                                    contentDescription = "Coincide",
                                    tint = KP_TERTIARY,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else null,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        onDone = {
                            if (token.isNotBlank() && newPassword.length >= 8 && newPassword == confirmPassword) {
                                viewModel.resetPassword(email, token, newPassword, context)
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Action Button (Gradient CTA) ──
            val canSubmit = if (linkSent) {
                token.isNotBlank() && newPassword.length >= 8 && newPassword == confirmPassword
            } else {
                emailValid
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (canSubmit)
                            Brush.linearGradient(listOf(KP_PRIMARY, KP_PRIMARY_CONTAINER))
                        else Brush.linearGradient(
                            listOf(KP_OUTLINE_VARIANT.copy(alpha = 0.5f), KP_OUTLINE_VARIANT.copy(alpha = 0.5f))
                        )
                    )
                    .clickable(enabled = canSubmit) {
                        if (linkSent) {
                            viewModel.resetPassword(email, token, newPassword, context)
                        } else {
                            viewModel.sendPasswordResetLink(email, context) {
                                linkSent = true
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (linkSent) "Restablecer contraseña" else "Enviar enlace",
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (canSubmit) KP_ON_PRIMARY else KP_OUTLINE
                    )
                    Icon(
                        Icons.AutoMirrored.TwoTone.ArrowForward,
                        contentDescription = null,
                        tint = if (canSubmit) KP_ON_PRIMARY else KP_OUTLINE,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

// ── Private Helpers ──

/**
 * Uppercase field label — KP recovery style.
 */
@Composable
private fun RecoveryFieldLabel(text: String) {
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

/**
 * Recovery text field — matches AuthSheet field style.
 *
 * @param value Current text value
 * @param onValueChange Text change callback
 * @param placeholder Placeholder text
 * @param leadingIcon Leading icon vector
 * @param trailingContent Optional trailing composable
 * @param visualTransformation Password masking or none
 * @param keyboardType Input type
 * @param imeAction Keyboard action
 * @param onDone Callback for IME done
 * @param enabled Whether the field is editable
 */
@Composable
private fun RecoveryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    trailingContent: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onDone: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        placeholder = {
            Text(
                text = placeholder,
                fontFamily = ManropeFont,
                color = KP_OUTLINE.copy(alpha = 0.5f)
            )
        },
        leadingIcon = {
            Icon(
                leadingIcon,
                contentDescription = null,
                tint = KP_OUTLINE,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = trailingContent,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone?.invoke() }
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = KP_SURFACE_CONTAINER_HIGH,
            unfocusedContainerColor = KP_SURFACE_CONTAINER_HIGH,
            focusedTextColor = KP_ON_SURFACE,
            unfocusedTextColor = KP_ON_SURFACE,
            disabledContainerColor = KP_SURFACE_CONTAINER_HIGH.copy(alpha = 0.5f),
            disabledTextColor = KP_ON_SURFACE.copy(alpha = 0.5f),
            cursorColor = KP_PRIMARY,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}
