package com.mintocode.rutinapp.viewmodels

import android.content.Context
import android.widget.Toast
import android.util.Log
import android.util.Patterns
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
import com.mintocode.rutinapp.data.api.v2.ApiV2Service
import com.mintocode.rutinapp.data.api.v2.dto.GoogleAuthRequest
import com.mintocode.rutinapp.data.api.v2.dto.DeviceSessionDto
import com.mintocode.rutinapp.data.api.v2.dto.ForgotPasswordRequest
import com.mintocode.rutinapp.data.api.v2.dto.LoginRequest
import com.mintocode.rutinapp.data.api.v2.dto.RedeemInviteCodeRequest
import com.mintocode.rutinapp.data.api.v2.dto.RegisterRequest
import com.mintocode.rutinapp.data.api.v2.dto.ResetPasswordRequest
import com.mintocode.rutinapp.data.api.v2.dto.TwoFactorCodeRequest
import com.mintocode.rutinapp.data.api.v2.dto.TwoFactorVerifyRequest
import com.mintocode.rutinapp.data.models.TrainerRelationModel
import com.mintocode.rutinapp.data.api.v2.dto.DtoMapper
import com.mintocode.rutinapp.data.notifications.NotificationHelper
import com.mintocode.rutinapp.security.SessionDataSanitizer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.mintocode.rutinapp.ui.screenStates.SettingsScreenState
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

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiV2: ApiV2Service
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
        hasLoaded = true
    }

    fun updateUserDetails(
        name: String = _data.value!!.name,
        code: String = _data.value!!.code,
        isDarkTheme: Boolean = _data.value!!.isDarkTheme,
        email: String = _data.value!!.email, // fixed: previously used name erroneously
        authToken: String = _data.value!!.authToken,
        floatingWidgetEnabled: Boolean = _data.value!!.floatingWidgetEnabled
    ) {
        val newData = UserDetails(
            code, name, isDarkTheme, authToken, email, floatingWidgetEnabled
        )
        viewModelScope.launch(Dispatchers.IO) {

            _data.postValue(newData)
            dataStoreManager.saveData(newData)

        }
    }

    fun toggleRutinAppTheme() {
        if (_data.value != null) {
            val newIsDark = !_data.value!!.isDarkTheme
            updateUserDetails(isDarkTheme = newIsDark)
        }
    }

    /**
     * Activa o desactiva la herramienta flotante de entrenamiento.
     *
     * @param enabled true para activar, false para desactivar
     */
    fun setFloatingWidgetEnabled(enabled: Boolean) {
        updateUserDetails(floatingWidgetEnabled = enabled)
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
                    val res = apiV2.register(RegisterRequest(
                        name = normalizedMail.substringBefore('@'),
                        email = normalizedMail,
                        password = password,
                        passwordConfirmation = password
                    ))
                    val token = res.accessToken
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
                    val res = apiV2.login(LoginRequest(email = normalizedMail, password = password))

                    // Handle 2FA challenge
                    if (res.twoFactorRequired == true && !res.twoFactorToken.isNullOrBlank()) {
                        _uiState.postValue(
                            SettingsScreenState.TwoFactorChallenge(
                                twoFactorToken = res.twoFactorToken,
                                email = normalizedMail
                            )
                        )
                        return@launch
                    }

                    val token = res.accessToken
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

    /**
     * Authenticates with Google via the backend API.
     *
     * Sends the Google ID token to the backend for verification. The backend
     * verifies the token using Google's public keys, creates/finds the user,
     * and returns a Sanctum bearer token. Firebase sign-in is maintained
     * alongside for push notifications (FCM).
     *
     * @param context Android context for Toast messages
     * @param credential Firebase AuthCredential (for optional Firebase sign-in)
     * @param googleIdToken The Google ID token from GoogleSignInAccount.idToken
     */
    fun logInWithGoogle(context: Context, credential: AuthCredential, googleIdToken: String) = viewModelScope.launch {
        try {
            // 1. Send the Google ID token to the backend for verification
            val backendRes = apiV2.googleAuth(GoogleAuthRequest(idToken = googleIdToken))
            val sanctumToken = backendRes.accessToken

            if (sanctumToken.isNullOrBlank()) {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Error al autenticar con Google", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            // 2. Also sign into Firebase (for FCM and other Firebase services)
            auth.signInWithCredential(credential).addOnSuccessListener { authUser ->
                // Store the Sanctum token (NOT the Firebase UID)
                updateUserDetails(
                    authToken = sanctumToken,
                    name = backendRes.data?.user?.name ?: authUser.user?.displayName ?: "",
                    email = backendRes.data?.user?.email ?: authUser.user?.email ?: ""
                )

                val actualState = _uiState.value as? SettingsScreenState.LogIn
                if (actualState != null) {
                    _uiState.postValue(actualState.copy(userMail = backendRes.data?.user?.email ?: authUser.user?.email ?: ""))
                }

                Toast.makeText(context, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show()
                toggleUiState()
            }.addOnFailureListener {
                // Firebase sign-in failed, but backend auth succeeded.
                // Store the Sanctum token anyway (Firebase is secondary).
                updateUserDetails(
                    authToken = sanctumToken,
                    name = backendRes.data?.user?.name ?: "",
                    email = backendRes.data?.user?.email ?: ""
                )
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Sesión iniciada (sin Firebase)", Toast.LENGTH_SHORT).show()
                    toggleUiState()
                }
            }
        } catch (e: Exception) {
            Log.e("Auth", "Google login failure", e)
            val msg = when (e) {
                is retrofit2.HttpException -> when (e.code()) {
                    401 -> "Token de Google inválido"
                    422 -> "Error de validación"
                    429 -> "Demasiados intentos"
                    else -> "Error HTTP ${e.code()}"
                }
                is IOException -> "Sin conexión"
                else -> "Error al iniciar sesión"
            }
            launch(Dispatchers.Main) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Closes the active session on backend and locally.
     *
     * Logout is best-effort: even if network calls fail, local sensitive
     * session data is cleared to prevent stale authenticated state.
     *
     * @param context Android context for user feedback
     */
    fun logOut(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val hasToken = !actualValue?.authToken.isNullOrBlank()
                if (hasToken) {
                    runCatching { apiV2.unregisterFcmToken() }
                    runCatching { apiV2.logout() }
                }
            } catch (e: Exception) {
                Log.e("Auth", "Logout failure", e)
            } finally {
                auth.signOut()

                val current = _data.value ?: UserDetails()
                val sanitized = SessionDataSanitizer.clearSession(current)

                actualValue = sanitized
                _data.postValue(sanitized)

                if (::dataStoreManager.isInitialized) {
                    dataStoreManager.saveData(sanitized)
                }

                _uiState.postValue(SettingsScreenState.LogIn(isRegister = false, userMail = ""))

                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun registerUserOnServer(authResult: AuthResult) { /* deprecated: backend handles via /auth/google */ }

    // ========================================================================
    // 2FA
    // ========================================================================

    /** Observable state for 2FA enablement in the UI. */
    private val _twoFactorEnabled = MutableLiveData(false)
    val twoFactorEnabled: LiveData<Boolean> = _twoFactorEnabled

    /** Observable state for notification of new accesses. */
    private val _notifyNewAccess = MutableLiveData(false)
    val notifyNewAccess: LiveData<Boolean> = _notifyNewAccess

    /** Observable state for device sessions list. */
    private val _sessions = MutableLiveData<List<DeviceSessionDto>>(emptyList())
    val sessions: LiveData<List<DeviceSessionDto>> = _sessions

    /** 2FA enable response (secret + otpauth URL) for setup sheet. */
    private val _twoFactorSetupSecret = MutableLiveData<String?>(null)
    val twoFactorSetupSecret: LiveData<String?> = _twoFactorSetupSecret

    private val _twoFactorSetupUrl = MutableLiveData<String?>(null)
    val twoFactorSetupUrl: LiveData<String?> = _twoFactorSetupUrl

    /** Recovery codes shown once after 2FA confirmation. */
    private val _recoveryCodes = MutableLiveData<List<String>?>(null)
    val recoveryCodes: LiveData<List<String>?> = _recoveryCodes

    /**
     * Verifica un código TOTP durante el flujo de login 2FA.
     *
     * @param code Código de 6 dígitos del autenticador
     * @param context Contexto para Toast
     */
    fun verifyTwoFactor(code: String, context: Context) {
        val state = _uiState.value as? SettingsScreenState.TwoFactorChallenge ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = apiV2.verifyTwoFactor(
                    TwoFactorVerifyRequest(
                        twoFactorToken = state.twoFactorToken,
                        code = code.trim()
                    )
                )
                val token = res.accessToken
                if (!token.isNullOrBlank()) {
                    updateUserDetails(
                        authToken = token,
                        name = res.data?.user?.name ?: _data.value?.name ?: "",
                        email = res.data?.user?.email ?: state.email
                    )
                    _twoFactorEnabled.postValue(true)
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show()
                    }
                    _uiState.postValue(SettingsScreenState.UserData)
                }
            } catch (e: Exception) {
                Log.e("Auth", "2FA verify failure", e)
                val msg = if (e is HttpException && e.code() == 422) "Código 2FA incorrecto"
                else "Error al verificar 2FA"
                launch(Dispatchers.Main) { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    /**
     * Inicia el proceso de activación de 2FA solicitando un secreto al backend.
     *
     * @param context Contexto para Toast
     */
    fun enableTwoFactor(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = apiV2.enableTwoFactor()
                _twoFactorSetupSecret.postValue(res.secret)
                _twoFactorSetupUrl.postValue(res.otpauthUrl)
            } catch (e: Exception) {
                Log.e("2FA", "Enable failure", e)
                val msg = if (e is HttpException && e.code() == 409) "2FA ya está activo"
                else "Error al activar 2FA"
                launch(Dispatchers.Main) { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    /**
     * Confirma la activación de 2FA con un código del autenticador.
     *
     * @param code Código TOTP de 6 dígitos
     * @param context Contexto para Toast
     */
    fun confirmTwoFactor(code: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = apiV2.confirmTwoFactor(TwoFactorCodeRequest(code = code.trim()))
                _recoveryCodes.postValue(res.recoveryCodes)
                _twoFactorEnabled.postValue(true)
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "2FA activado correctamente", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("2FA", "Confirm failure", e)
                val msg = if (e is HttpException && e.code() == 422) "Código incorrecto"
                else "Error al confirmar 2FA"
                launch(Dispatchers.Main) { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    /**
     * Desactiva 2FA con un código TOTP o de recuperación.
     *
     * @param code Código TOTP o de recuperación
     * @param context Contexto para Toast
     */
    fun disableTwoFactor(code: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                apiV2.disableTwoFactor(TwoFactorCodeRequest(code = code.trim()))
                _twoFactorEnabled.postValue(false)
                _twoFactorSetupSecret.postValue(null)
                _twoFactorSetupUrl.postValue(null)
                _recoveryCodes.postValue(null)
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "2FA desactivado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("2FA", "Disable failure", e)
                val msg = if (e is HttpException && e.code() == 422) "Código incorrecto"
                else "Error al desactivar 2FA"
                launch(Dispatchers.Main) { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    /** Limpia el estado temporal de configuración 2FA. */
    fun clearTwoFactorSetup() {
        _twoFactorSetupSecret.postValue(null)
        _twoFactorSetupUrl.postValue(null)
        _recoveryCodes.postValue(null)
    }

    // ========================================================================
    // Device Sessions
    // ========================================================================

    /**
     * Carga las sesiones activas desde el backend.
     */
    fun loadSessions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = apiV2.getSessions()
                _sessions.postValue(res.data?.sessions ?: emptyList())
            } catch (e: Exception) {
                Log.e("Sessions", "Load failure", e)
            }
        }
    }

    /**
     * Revoca una sesión de dispositivo remota.
     *
     * @param sessionId ID de la sesión a revocar
     * @param context Contexto para Toast
     */
    fun revokeSession(sessionId: Long, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                apiV2.revokeSession(sessionId)
                _sessions.postValue(_sessions.value?.filter { it.id != sessionId })
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Sesión revocada", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Sessions", "Revoke failure", e)
                val msg = if (e is HttpException && e.code() == 409) "No puedes revocar tu sesión actual"
                else "Error al revocar sesión"
                launch(Dispatchers.Main) { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    // ========================================================================
    // Token Refresh
    // ========================================================================

    /**
     * Regenera el token de acceso actual.
     *
     * @param context Contexto para Toast
     */
    fun refreshToken(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = apiV2.refreshToken()
                val newToken = res.accessToken
                if (!newToken.isNullOrBlank()) {
                    updateUserDetails(authToken = newToken)
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Token regenerado", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Auth", "Refresh failure", e)
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Error al regenerar token", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ========================================================================
    // Notify New Access
    // ========================================================================

    /**
     * Sincroniza el estado de 2FA y notificaciones desde /me.
     */
    fun loadSecuritySettings() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = apiV2.me()
                _twoFactorEnabled.postValue(res.data?.twoFactorEnabled ?: false)
                _notifyNewAccess.postValue(res.data?.notifyNewAccess ?: false)
            } catch (e: Exception) {
                Log.e("Settings", "Load security settings failure", e)
            }
        }
    }

    /**
     * Alterna la notificación de nuevos accesos en el backend.
     *
     * @param enabled true para activar, false para desactivar
     * @param context Contexto para Toast
     */
    fun setNotifyNewAccess(enabled: Boolean, context: Context) {
        _notifyNewAccess.postValue(enabled)
        // Backend toggling would require a dedicated endpoint.
        // For now, we update the local state; a future PUT /user/settings endpoint
        // should persist this.
    }

    // ========================================================================
    // Password Recovery
    // ========================================================================

    /**
     * Envía un enlace de recuperación de contraseña al email.
     *
     * @param email Email del usuario
     * @param context Contexto para Toast
     * @param onSuccess Callback tras envío exitoso
     */
    fun sendPasswordResetLink(email: String, context: Context, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                apiV2.forgotPassword(ForgotPasswordRequest(email = email.trim()))
                launch(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Si la cuenta existe, recibirás un email",
                        Toast.LENGTH_LONG
                    ).show()
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("Auth", "Password reset request failure", e)
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Error al enviar email", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Restablece la contraseña con el token recibido por email.
     *
     * @param email Email del usuario
     * @param token Token recibido por email
     * @param password Nueva contraseña
     * @param context Contexto para Toast
     */
    fun resetPassword(email: String, token: String, password: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = apiV2.resetPassword(
                    ResetPasswordRequest(
                        email = email.trim(),
                        token = token.trim(),
                        password = password,
                        passwordConfirmation = password
                    )
                )
                val newToken = res.accessToken
                if (!newToken.isNullOrBlank()) {
                    updateUserDetails(
                        authToken = newToken,
                        email = email.trim(),
                        name = res.data?.user?.name ?: _data.value?.name ?: ""
                    )
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                    }
                    _uiState.postValue(SettingsScreenState.UserData)
                }
            } catch (e: Exception) {
                Log.e("Auth", "Password reset failure", e)
                val msg = when {
                    e is HttpException && e.code() == 422 -> "Token inválido o contraseña débil"
                    else -> "Error al restablecer contraseña"
                }
                launch(Dispatchers.Main) { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    /** Instancia del helper de notificaciones, inicializada bajo demanda. */
    private var notificationHelper: NotificationHelper? = null

    /**
     * Inicializa el helper de notificaciones.
     *
     * Debe llamarse con el contexto de la actividad para poder crear canales.
     *
     * @param context Contexto de la aplicación
     */
    fun initNotificationHelper(context: Context) {
        if (notificationHelper == null) {
            notificationHelper = NotificationHelper(context.applicationContext, apiV2)
            notificationHelper?.createNotificationChannels()
        }
    }

    /**
     * Verifica si el permiso de notificaciones está concedido.
     *
     * @return true si concedido (o pre-Android 13)
     */
    fun hasNotificationPermission(): Boolean {
        return notificationHelper?.hasNotificationPermission() ?: false
    }

    /**
     * Registra el token FCM con el backend si el usuario está autenticado.
     *
     * Se invoca tras conceder el permiso de notificaciones o tras login exitoso.
     */
    fun registerFcmTokenIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userData = _data.value
                if (userData != null && userData.authToken.isNotBlank()) {
                    notificationHelper?.registerTokenWithBackend()
                }
            } catch (e: Exception) {
                Log.e("SettingsVM", "Error registrando FCM token", e)
            }
        }
    }

    // ========================================================================
    // Trainer management
    // ========================================================================

    private val _trainers = MutableLiveData<List<TrainerRelationModel>>(emptyList())

    /**
     * Active trainer relations for the current user.
     */
    val trainers: LiveData<List<TrainerRelationModel>> = _trainers

    /**
     * Loads the list of trainers linked to the current user.
     *
     * Fetches from the GET /my-trainers endpoint and maps to domain models.
     */
    fun loadTrainers() {
        val token = actualValue?.authToken
        if (token.isNullOrBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = apiV2.getMyTrainers()
                _trainers.postValue(res.data?.map { DtoMapper.toTrainerRelationModel(it) } ?: emptyList())
            } catch (e: Exception) {
                Log.e("SettingsVM", "Error loading trainers", e)
            }
        }
    }

    /**
     * Redeems a trainer invite code to establish a trainer-client relationship.
     *
     * @param code The invite code to redeem
     * @param context Android context for Toast messages
     */
    fun redeemInviteCode(code: String, context: Context) {
        if (code.isBlank()) {
            Toast.makeText(context, "Introduce un código válido", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                apiV2.redeemInviteCode(RedeemInviteCodeRequest(code = code))
                loadTrainers()
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Código canjeado correctamente", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SettingsVM", "Error redeeming invite code", e)
                val msg = when (e) {
                    is retrofit2.HttpException -> when (e.code()) {
                        404 -> "Código no encontrado"
                        409 -> "Código ya utilizado"
                        422 -> "Código inválido"
                        else -> "Error HTTP ${e.code()}"
                    }
                    is IOException -> "Sin conexión"
                    else -> "Error al canjear código"
                }
                launch(Dispatchers.Main) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Revokes access from a trainer.
     *
     * @param trainerId The server ID of the trainer relation to revoke
     * @param context Android context for Toast messages
     */
    fun revokeTrainer(trainerId: Long, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                apiV2.revokeTrainer(trainerId)
                loadTrainers()
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Entrenador revocado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SettingsVM", "Error revoking trainer", e)
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Error al revocar entrenador", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}

private fun String.isValidEmail(): Boolean {
    if (this.isBlank()) return false
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}