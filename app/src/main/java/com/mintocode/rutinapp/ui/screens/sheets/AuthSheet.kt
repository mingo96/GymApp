package com.mintocode.rutinapp.ui.screens.sheets

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.twotone.Mail
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.Visibility
import androidx.compose.material.icons.twotone.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.screenStates.SettingsScreenState
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.utils.isConnectedToInternet
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
 * Authentication sheet — KP design (Guide 17).
 *
 * Login/register form with email+password, register toggle,
 * Google sign-in, forgot password link, and disclaimer.
 * Uses gradient CTA button, custom input fields, and "o" divider.
 *
 * @param viewModel SettingsViewModel for auth actions
 */
@Composable
fun AuthSheet(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.observeAsState(initial = SettingsScreenState.UserData)

    // Google Sign-In launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val googleIdToken = account.idToken
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            viewModel.logInWithGoogle(
                credential = credential,
                context = context,
                googleIdToken = googleIdToken ?: ""
            )
        } catch (_: Exception) {
            Toast.makeText(context, "Cuenta no válida", Toast.LENGTH_SHORT).show()
        }
    }

    val token = stringResource(R.string.default_web_client_id)

    val onGoogleClick = {
        if (isConnectedToInternet(context)) {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token).requestEmail().build()
            val googleSignInClient = GoogleSignIn.getClient(context, options)
            googleSignInClient.signOut()
            launcher.launch(googleSignInClient.signInIntent)
        } else {
            Toast.makeText(context, "No hay conexión a internet", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle 2FA challenge state
    if (uiState is SettingsScreenState.TwoFactorChallenge) {
        TwoFactorChallengeContent(viewModel = viewModel)
        return
    }

    // Ensure we're in LogIn state
    val loginState = if (uiState is SettingsScreenState.LogIn) {
        uiState as SettingsScreenState.LogIn
    } else {
        viewModel.toggleUiState()
        return
    }

    var mail by rememberSaveable { mutableStateOf(loginState.userMail) }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val navigator = LocalSheetNavigator.current

    val isRegister = loginState.isRegister
    val emailValid = mail.contains("@") && mail.contains(".")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp)
            .padding(top = 8.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ── Header ──
        Text(
            text = if (isRegister) "Crear cuenta" else "Iniciar sesión",
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = (-0.5).sp,
            color = KP_ON_SURFACE
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (isRegister) "Regístrate para sincronizar tus datos"
            else "Accede a tu cuenta para continuar",
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = KP_ON_SURFACE_VARIANT,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(32.dp))

        // ── Register Name Field (visible only in register mode) ──
        AnimatedVisibility(
            visible = isRegister,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                AuthFieldLabel("NOMBRE")
                Spacer(Modifier.height(8.dp))
                AuthTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Tu nombre",
                    leadingIcon = Icons.TwoTone.Person,
                    keyboardType = KeyboardType.Text
                )
                Spacer(Modifier.height(24.dp))
            }
        }

        // ── Email Field ──
        AuthFieldLabel("CORREO ELECTRÓNICO")
        Spacer(Modifier.height(8.dp))
        AuthTextField(
            value = mail,
            onValueChange = { mail = it },
            placeholder = "tucuenta@email.com",
            leadingIcon = Icons.TwoTone.Mail,
            trailingContent = {
                if (emailValid) {
                    Icon(
                        Icons.TwoTone.CheckCircle,
                        contentDescription = "Email válido",
                        tint = KP_TERTIARY,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            keyboardType = KeyboardType.Email
        )

        Spacer(Modifier.height(24.dp))

        // ── Password Field ──
        AuthFieldLabel("CONTRASEÑA")
        Spacer(Modifier.height(8.dp))
        AuthTextField(
            value = password,
            onValueChange = { password = it },
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
            keyboardType = KeyboardType.Password,
            imeAction = if (isRegister) ImeAction.Next else ImeAction.Done,
            onDone = {
                if (!isRegister) viewModel.tryToAuthenticate(mail, password, context)
            }
        )

        // Forgot password link (login mode only)
        if (!isRegister) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = KP_PRIMARY,
                    modifier = Modifier.clickable {
                        navigator.open(SheetDestination.PasswordRecovery)
                    }
                )
            }
        }

        // ── Confirm Password (register mode) ──
        AnimatedVisibility(
            visible = isRegister,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                Spacer(Modifier.height(24.dp))
                AuthFieldLabel("CONFIRMAR CONTRASEÑA")
                Spacer(Modifier.height(8.dp))
                AuthTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "••••••••",
                    leadingIcon = Icons.TwoTone.Lock,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onDone = { viewModel.tryToAuthenticate(mail, password, context) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Register Toggle ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "¿Nuevo en RutinApp?",
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = KP_ON_SURFACE
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Registrarse",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = KP_ON_SURFACE_VARIANT
                )
                Switch(
                    checked = isRegister,
                    onCheckedChange = { viewModel.toggleLogInState() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = KP_PRIMARY_CONTAINER,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = KP_SURFACE_CONTAINER_HIGHEST
                    )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Submit Button (Gradient CTA) ──
        val submitInteraction = remember { MutableInteractionSource() }
        val isPressed by submitInteraction.collectIsPressedAsState()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .graphicsLayer(
                    scaleX = if (isPressed) 0.98f else 1f,
                    scaleY = if (isPressed) 0.98f else 1f
                )
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(listOf(KP_PRIMARY, KP_PRIMARY_CONTAINER))
                )
                .clickable(submitInteraction, indication = null) {
                    viewModel.tryToAuthenticate(mail, password, context)
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isRegister) "Crear cuenta" else "Iniciar sesión",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = KP_ON_PRIMARY
                )
                Icon(
                    Icons.AutoMirrored.TwoTone.ArrowForward,
                    contentDescription = null,
                    tint = KP_ON_PRIMARY,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── Divider with "o" ──
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            HorizontalDivider(
                color = KP_OUTLINE_VARIANT.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Text(
                text = "o",
                fontFamily = ManropeFont,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                color = KP_OUTLINE,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(horizontal = 16.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        // ── Google Button ──
        val googleInteraction = remember { MutableInteractionSource() }
        val googlePressed by googleInteraction.collectIsPressedAsState()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .graphicsLayer(
                    scaleX = if (googlePressed) 0.98f else 1f,
                    scaleY = if (googlePressed) 0.98f else 1f
                )
                .clip(RoundedCornerShape(12.dp))
                .border(
                    2.dp,
                    KP_OUTLINE_VARIANT.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                )
                .clickable(googleInteraction, indication = null) { onGoogleClick() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Continuar con Google",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = KP_ON_SURFACE
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── Disclaimer ──
        Text(
            text = "Al continuar, aceptas nuestros Términos de Servicio y Política de Privacidad",
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = KP_ON_SURFACE_VARIANT.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Private Helpers ──

/**
 * Uppercase field label — KP auth style.
 */
@Composable
private fun AuthFieldLabel(text: String) {
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
 * Auth text field — surfaceContainerHigh bg, rounded-xl, leading icon,
 * optional trailing content, focus ring.
 *
 * @param value Current text value
 * @param onValueChange Text change callback
 * @param placeholder Placeholder text
 * @param leadingIcon Leading icon vector
 * @param trailingContent Optional trailing composable (validation icon, visibility toggle)
 * @param visualTransformation Password masking or none
 * @param keyboardType Input type (email, password, text)
 * @param imeAction Keyboard action button
 * @param onDone Callback when IME done is pressed
 */
@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    trailingContent: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onDone: (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
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
            cursorColor = KP_PRIMARY,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}

// ── 2FA Challenge UI ──

/**
 * Inline 2FA challenge screen shown when login requires TOTP code.
 *
 * @param viewModel SettingsViewModel with verifyTwoFactor method
 */
@Composable
private fun TwoFactorChallengeContent(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    var code by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp)
            .padding(top = 8.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Text(
            text = "Verificación 2FA",
            fontFamily = SpaceGroteskFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = (-0.5).sp,
            color = KP_ON_SURFACE
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Introduce el código de 6 dígitos de tu aplicación de autenticación.",
            fontFamily = ManropeFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = KP_ON_SURFACE_VARIANT,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(32.dp))

        AuthFieldLabel("CÓDIGO DE VERIFICACIÓN")
        Spacer(Modifier.height(8.dp))

        AuthTextField(
            value = code,
            onValueChange = { if (it.length <= 6) code = it },
            placeholder = "000000",
            leadingIcon = Icons.TwoTone.Lock,
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done,
            onDone = {
                if (code.length == 6) viewModel.verifyTwoFactor(code, context)
            }
        )

        Spacer(Modifier.height(32.dp))

        // Verify button (gradient CTA)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (code.length == 6)
                        Brush.linearGradient(listOf(KP_PRIMARY, KP_PRIMARY_CONTAINER))
                    else Brush.linearGradient(
                        listOf(KP_SURFACE_CONTAINER_HIGHEST, KP_SURFACE_CONTAINER_HIGHEST)
                    )
                )
                .clickable(enabled = code.length == 6) {
                    viewModel.verifyTwoFactor(code, context)
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Verificar",
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (code.length == 6) KP_ON_PRIMARY else KP_OUTLINE
                )
                Icon(
                    Icons.AutoMirrored.TwoTone.ArrowForward,
                    contentDescription = null,
                    tint = if (code.length == 6) KP_ON_PRIMARY else KP_OUTLINE,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
