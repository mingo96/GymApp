package com.mintocode.rutinapp.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.credentials.CredentialManager
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
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                settingsViewModel.logInWithGoogle(
                    credential = credential,
                    context = context)
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
            Modifier.padding(it),
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .background(TextFieldColor, RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { settingsViewModel.tryToAuthenticate(mail, password, context) },
                colors = rutinAppButtonsColours(),
                modifier = Modifier.padding(8.dp)
            ) {
                AdjustableText(
                    text = if (uiState.isRegister) "Registrarse" else "Iniciar sesión",
                    style = TextStyle(fontSize = 20.sp)
                )
            }
            IconButton(onClick = { settingsViewModel.toggleLogInState() }) {
                Icon(painter = painterResource(R.drawable.swap), "swap register/log in")
            }
        }
        IconButton(
            onClick = onGoogleClick,
            modifier = Modifier
                .background(TextFieldColor, RoundedCornerShape(20.dp))
                .padding(8.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
        ) {
            Image(
                painter = painterResource(R.drawable.google), "google log in", modifier = Modifier
            )
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


}