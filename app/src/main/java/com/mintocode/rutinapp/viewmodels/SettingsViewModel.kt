package com.mintocode.rutinapp.viewmodels

import android.content.Context
import android.widget.Toast
import android.util.Log
import android.util.Patterns
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.auth
import com.mintocode.rutinapp.data.UserDetails
import com.mintocode.rutinapp.data.UserDetails.Companion.actualValue
import com.mintocode.rutinapp.data.api.v1.ApiV1Service
import com.mintocode.rutinapp.data.api.v1.dto.LoginRequest
import com.mintocode.rutinapp.data.api.v1.dto.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.mintocode.rutinapp.ui.screenStates.SettingsScreenState
import com.mintocode.rutinapp.ui.theme.ContentColor
import com.mintocode.rutinapp.ui.theme.PrimaryColor
import com.mintocode.rutinapp.ui.theme.SecondaryColor
import com.mintocode.rutinapp.ui.theme.TextFieldColor
import com.mintocode.rutinapp.utils.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.lang.Thread.State

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiV1: ApiV1Service
) : ViewModel() {

    private val _data: MutableLiveData<UserDetails> = MutableLiveData()

    val data: LiveData<UserDetails> = _data

    private lateinit var userDataUpdater : StateFlow<UserDetails?>

    private val auth = Firebase.auth

    var hasLoaded = false
        private set

    private val _uiState: MutableLiveData<SettingsScreenState> =
        MutableLiveData(SettingsScreenState.UserData)

    val uiState: LiveData<SettingsScreenState> = _uiState

    private lateinit var dataStoreManager: DataStoreManager

    fun initiateDataStore(value: DataStoreManager) {
        dataStoreManager = value

        userDataUpdater = dataStoreManager.getData().map {
            actualValue = it
            it
        }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

        viewModelScope.launch(Dispatchers.IO) {
            getUserDetails()
        }
    }

    fun toggleUiState() {
        if (_uiState.value is SettingsScreenState.LogIn) {
            _uiState.postValue(SettingsScreenState.UserData)
        } else _uiState.postValue(SettingsScreenState.LogIn(false, userMail = _data.value!!.email))
    }

    private suspend fun getUserDetails() {
        _data.postValue(dataStoreManager.data())
        delay(1000)

        if (_data.value != null) {
            val isDarkTheme = dataStoreManager.data().isDarkTheme
            if (!isDarkTheme) {
                changeToClearTheme()
            }
        }
        hasLoaded = true
    }

    fun updateUserDetails(
        name: String = _data.value!!.name,
        code: String = _data.value!!.code,
        isDarkTheme: Boolean = _data.value!!.isDarkTheme,
        email: String = _data.value!!.email, // fixed: previously used name erroneously
        authToken: String = _data.value!!.authToken
    ) {
        val newData = UserDetails(
            code, name, isDarkTheme, authToken, email
        )
        viewModelScope.launch(Dispatchers.IO) {

            _data.postValue(newData)
            dataStoreManager.saveData(newData)

        }
    }

    fun toggleRutinAppTheme() {
        if (_data.value != null) {
            if (_data.value!!.isDarkTheme) {
                changeToClearTheme()
            } else {
                changeToDarkTheme()
            }
        }
    }

    private fun changeToClearTheme() {
        PrimaryColor = Color.White
        SecondaryColor = Color.DarkGray
        ContentColor = Color.Black
        TextFieldColor = Color(40, 40, 58).copy(0.3f)

        updateUserDetails(isDarkTheme = false)
    }

    private fun changeToDarkTheme() {
        PrimaryColor = Color(0xFF121217)
        SecondaryColor = Color(0xFF1212ED)
        ContentColor = Color.White
        TextFieldColor = Color(40, 40, 58).copy(0.5f)

        updateUserDetails(isDarkTheme = true)
    }

    fun toggleLogInState() {
        val actualState = _uiState.value as SettingsScreenState.LogIn

        _uiState.postValue(SettingsScreenState.LogIn(!actualState.isRegister))
    }

    fun tryToAuthenticate(mail: String, password: String, context: Context) {
        val actualState = _uiState.value as SettingsScreenState.LogIn
        val normalizedMail = mail.trim()
        if (!normalizedMail.isValidEmail()) {
            Toast.makeText(context, "Correo no válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Backend auth (preferred). Firebase kept temporarily for Google Sign-In only.
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (actualState.isRegister) {
                    if (password.isBlank() || password.length < 5) {
                        launch(Dispatchers.Main) { Toast.makeText(context, "Contraseña no válida", Toast.LENGTH_SHORT).show() }
                        return@launch
                    }
                    val res = apiV1.register(RegisterRequest(
                        name = normalizedMail.substringBefore('@'),
                        email = normalizedMail,
                        password = password,
                        password_confirmation = password
                    ))
                    val token = res.access_token
                    if (!token.isNullOrBlank()) {
                        updateUserDetails(
                            authToken = token,
                            name = res.data?.user?.name ?: normalizedMail.substringBefore('@'),
                            email = res.data?.user?.email ?: normalizedMail
                        )
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "Registrado correctamente", Toast.LENGTH_SHORT).show()
                            toggleUiState()
                        }
                    } else launch(Dispatchers.Main) { Toast.makeText(context, "Error registro", Toast.LENGTH_SHORT).show() }
                } else {
                    val res = apiV1.login(LoginRequest(email = normalizedMail, password = password))
                    val token = res.access_token
                    if (!token.isNullOrBlank()) {
                        updateUserDetails(
                            authToken = token,
                            name = res.data?.user?.name ?: _data.value?.name ?: normalizedMail.substringBefore('@'),
                            email = res.data?.user?.email ?: normalizedMail
                        )
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show()
                            toggleUiState()
                        }
                    } else launch(Dispatchers.Main) { Toast.makeText(context, "Error login", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) {
                Log.e("Auth", "Login/Register failure", e)
                var detail: String? = null
                if (e is HttpException) {
                    try { detail = e.response()?.errorBody()?.string()?.take(300) } catch (_: Exception) {}
                }
                val msg = when (e) {
                    is HttpException -> when (e.code()) {
                        401 -> "No autorizado"
                        422 -> "Credenciales inválidas"
                        429 -> "Demasiados intentos"
                        else -> "HTTP ${e.code()}"
                    }
                    is IOException -> "Sin conexión"
                    else -> "Error inesperado"
                }
                val finalMsg = if (!detail.isNullOrBlank()) "$msg" else msg
                launch(Dispatchers.Main) { Toast.makeText(context, finalMsg, Toast.LENGTH_SHORT).show() }
            }
        }

    }

    fun logInWithGoogle(context: Context, credential: AuthCredential)= viewModelScope.launch {
        try {
            auth.signInWithCredential(credential).addOnSuccessListener { authUser ->

                if (authUser.additionalUserInfo!!.isNewUser){
                    registerUserOnServer(authUser)
                }
                updateUserDetails(authToken = authUser.user!!.uid, name = authUser.user!!.displayName!!, email = authUser.user!!.email!!)

                val actualState = _uiState.value as SettingsScreenState.LogIn

                _uiState.postValue(actualState.copy(userMail = authUser.user!!.email!!))

                Toast.makeText(context, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show()
                toggleUiState()
            }.addOnFailureListener {
                Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {

            Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUserOnServer(authResult: AuthResult) { /* deprecated with backend auth */ }

}

private fun String.isValidEmail(): Boolean {
    if (this.isBlank()) return false
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}