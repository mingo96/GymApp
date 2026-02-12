package com.mintocode.rutinapp.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.UserDetails
import com.mintocode.rutinapp.isConnectedToInternet
import com.mintocode.rutinapp.ui.premade.AdjustableText
import com.mintocode.rutinapp.ui.screenStates.SettingsScreenState
import com.mintocode.rutinapp.ui.theme.ScreenContainer
import com.mintocode.rutinapp.ui.theme.TextFieldColor
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.SettingsViewModel

@Composable
fun SettinsScreen(navController: NavHostController, settingsViewModel: SettingsViewModel) {

    val data by settingsViewModel.data.observeAsState()

    val uiState by settingsViewModel.uiState.observeAsState(initial = SettingsScreenState.UserData)

    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val googleIdToken = account.idToken
                val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
                settingsViewModel.logInWithGoogle(
                    credential = credential,
                    context = context,
                    googleIdToken = googleIdToken ?: "")
            } catch (e: Exception) {

                Toast.makeText(context, "Cuenta no valida", Toast.LENGTH_SHORT).show()
            }

        }

    val token = stringResource(R.string.default_web_client_id)

    val onGoogleClick = {
        if(isConnectedToInternet(context)) {

            val options = GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
            ).requestIdToken(token).requestEmail().build()
            val googleSignInClient = GoogleSignIn.getClient(context, options)
            googleSignInClient.signOut()
            launcher.launch(googleSignInClient.signInIntent)

        }else{
            Toast.makeText(context, "No hay conexión a internet", Toast.LENGTH_SHORT).show()
        }
    }

    ScreenContainer(title = "Configuración", navController = navController, floatingActionButton = {
        Button(onClick = {
            settingsViewModel.toggleUiState()
        }, colors = rutinAppButtonsColours()) {
            Text(if (uiState is SettingsScreenState.LogIn) "Datos del usuario" else "Inicio de sesión")
        }
    }) {

        Column(
            Modifier
                .padding(it)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (uiState) {

                SettingsScreenState.UserData -> {
                    UserSettingsInput(
                        settingsViewModel = settingsViewModel, data = data!!
                    )
                }

                is SettingsScreenState.LogIn -> {
                    UserLogInInput(
                        settingsViewModel = settingsViewModel,
                        uiState = (uiState as SettingsScreenState.LogIn),
                        onGoogleClick = onGoogleClick
                    )
                }
            }
        }

    }
}

@Composable
fun UserLogInInput(
    settingsViewModel: SettingsViewModel,
    uiState: SettingsScreenState.LogIn,
    onGoogleClick: () -> Unit
) {

    val context = LocalContext.current

    var mail by rememberSaveable { mutableStateOf(uiState.userMail) }
    var password by rememberSaveable { mutableStateOf("") }

    TextFieldWithTitle(title = "Correo", text = mail, onWrite = { mail = it }, typeOfKeyBoard = KeyboardType.Email)
    TextFieldWithTitle(
        title = "Contraseña",
        text = password,
        onWrite = { password = it },
        typeOfKeyBoard = KeyboardType.Password,
        sendFunction = {
            settingsViewModel.tryToAuthenticate(mail, password, context)
        }
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .weight(1f)
                .background(TextFieldColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = { settingsViewModel.tryToAuthenticate(mail, password, context) },
                colors = rutinAppButtonsColours(),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                AdjustableText(
                    text = if (uiState.isRegister) "Registrarse" else "Iniciar sesión",
                    style = TextStyle(fontSize = 15.sp)
                )
            }
            IconButton(onClick = { settingsViewModel.toggleLogInState() }) {
                Icon(painter = painterResource(R.drawable.swap), "swap register/log in")
            }
        }
        Button(
            onClick = onGoogleClick,
            colors = rutinAppButtonsColours(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.google),
                contentDescription = "Google",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp)
            )
            Text(text = "Google", fontSize = 14.sp)
        }
    }


}

@Composable
fun UserSettingsInput(
    settingsViewModel: SettingsViewModel, data: UserDetails
) {

    var name by rememberSaveable { mutableStateOf(data.name) }

    var code by rememberSaveable { mutableStateOf(data.code) }

    TextFieldWithTitle(title = "Nombre", text = name, onWrite = { name = it })
    TextFieldWithTitle(title = "Código secreto", text = code, onWrite = { code = it })

    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { settingsViewModel.updateUserDetails(name, code) },
            colors = rutinAppButtonsColours()
        ) {
            Text(text = "Guardar")
        }

        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {

            Text(text = "Cambiar tema ", fontSize = 15.sp)
            Switch(checked = data.isDarkTheme, onCheckedChange = {
                settingsViewModel.toggleRutinAppTheme()
            })
        }

    }

    // Sección de notificaciones
    NotificationPermissionSection(settingsViewModel = settingsViewModel)

}

/**
 * Sección de configuración de notificaciones push.
 *
 * Muestra el estado actual del permiso de notificaciones y permite
 * al usuario activarlas si no lo ha hecho. En Android 13+ se solicita
 * el permiso POST_NOTIFICATIONS explícitamente.
 *
 * @param settingsViewModel ViewModel de configuración
 */
@Composable
fun NotificationPermissionSection(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current

    var hasPermission by rememberSaveable {
        mutableStateOf(settingsViewModel.hasNotificationPermission())
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            settingsViewModel.registerFcmTokenIfNeeded()
            Toast.makeText(context, "Notificaciones activadas", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notificaciones denegadas", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TextFieldColor, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Notificaciones",
            fontSize = 16.sp,
            color = Color.White
        )

        if (hasPermission) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "✓ Notificaciones activadas",
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50)
                )
            }
        } else {
            Text(
                text = "Activa las notificaciones para recibir avisos sobre tus entrenamientos.",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        // Pre-Android 13: permiso ya concedido por defecto
                        hasPermission = true
                        settingsViewModel.registerFcmTokenIfNeeded()
                    }
                },
                colors = rutinAppButtonsColours()
            ) {
                Text(text = "Activar notificaciones")
            }
        }
    }
}