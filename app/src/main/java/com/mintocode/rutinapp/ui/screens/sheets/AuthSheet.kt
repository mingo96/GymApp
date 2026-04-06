package com.mintocode.rutinapp.ui.screens.sheets

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.premade.AdjustableText
import com.mintocode.rutinapp.ui.screenStates.SettingsScreenState
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.utils.isConnectedToInternet
import com.mintocode.rutinapp.viewmodels.SettingsViewModel

/**
 * Authentication sheet content.
 *
 * Provides login/register form with email+password and Google sign-in.
 * Reuses the SettingsViewModel authentication actions.
 *
 * @param viewModel SettingsViewModel for auth actions
 */
@Composable
fun AuthSheet(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.observeAsState(initial = SettingsScreenState.UserData)

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

    // Ensure we're in LogIn state
    val loginState = if (uiState is SettingsScreenState.LogIn) {
        uiState as SettingsScreenState.LogIn
    } else {
        // Switch to login mode if not already there
        viewModel.toggleUiState()
        return
    }

    var mail by rememberSaveable { mutableStateOf(loginState.userMail) }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Inicio de sesión",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        TextFieldWithTitle(
            title = "Correo",
            text = mail,
            onWrite = { mail = it },
            typeOfKeyBoard = KeyboardType.Email
        )
        TextFieldWithTitle(
            title = "Contraseña",
            text = password,
            onWrite = { password = it },
            typeOfKeyBoard = KeyboardType.Password,
            sendFunction = { viewModel.tryToAuthenticate(mail, password, context) }
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { viewModel.tryToAuthenticate(mail, password, context) },
                    colors = rutinAppButtonsColours(),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    AdjustableText(
                        text = if (loginState.isRegister) "Registrarse" else "Iniciar sesión",
                        style = TextStyle(fontSize = 15.sp)
                    )
                }
                IconButton(onClick = { viewModel.toggleLogInState() }) {
                    Icon(
                        painter = painterResource(R.drawable.swap),
                        contentDescription = "Alternar registro/login"
                    )
                }
            }
            Button(
                onClick = onGoogleClick,
                colors = rutinAppButtonsColours(),
                shape = MaterialTheme.shapes.small,
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
}
