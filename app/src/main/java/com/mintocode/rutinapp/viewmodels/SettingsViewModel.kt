package com.mintocode.rutinapp.viewmodels

import android.content.Context
import android.widget.Toast
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
import com.mintocode.rutinapp.data.api.Rutinappi
import com.mintocode.rutinapp.data.api.classes.User
import com.mintocode.rutinapp.ui.screenStates.SettingsScreenState
import com.mintocode.rutinapp.ui.theme.ContentColor
import com.mintocode.rutinapp.ui.theme.PrimaryColor
import com.mintocode.rutinapp.ui.theme.SecondaryColor
import com.mintocode.rutinapp.ui.theme.TextFieldColor
import com.mintocode.rutinapp.utils.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _data: MutableLiveData<UserDetails> = MutableLiveData()

    val data: LiveData<UserDetails> = _data

    private val auth = Firebase.auth

    var hasLoaded = false
        private set

    private val _uiState: MutableLiveData<SettingsScreenState> =
        MutableLiveData(SettingsScreenState.UserData)

    val uiState: LiveData<SettingsScreenState> = _uiState

    private lateinit var dataStoreManager: DataStoreManager

    fun initiateDataStore(value: DataStoreManager) {
        dataStoreManager = value

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
        _data.postValue(dataStoreManager.getData().first())
        delay(1000)

        if (_data.value != null) {
            val isDarkTheme = dataStoreManager.getData().first().isDarkTheme
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
        email: String = _data.value!!.name,
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
        if (!mail.isValidEmail()) {
            Toast.makeText(context, "Correo no válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (actualState.isRegister) {
            if (password.isBlank() || password.length < 5) {
                Toast.makeText(context, "Contraseña no válida", Toast.LENGTH_SHORT).show()
                return
            }
            auth.createUserWithEmailAndPassword(mail, password).addOnSuccessListener {
                updateUserDetails(
                    authToken = it.user!!.uid, name = it.user!!.displayName ?: _data.value!!.name,
                    email = it.user!!.email!!
                )

                if(it.additionalUserInfo!!.isNewUser){
                    registerUserOnServer(it)
                }

                Toast.makeText(context, "Registrado correctamente", Toast.LENGTH_SHORT).show()
                toggleUiState()
            }.addOnFailureListener {
                Toast.makeText(context, "Error al registrarse", Toast.LENGTH_SHORT).show()
            }.addOnCanceledListener {
                Toast.makeText(context, "Error al registrarse", Toast.LENGTH_SHORT).show()
            }
        } else {
            auth.signInWithEmailAndPassword(mail, password).addOnSuccessListener {
                updateUserDetails(
                    authToken = it.user!!.uid, name = it.user!!.displayName ?: _data.value!!.name,
                    email = it.user!!.email!!
                )
                Toast.makeText(context, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show()
                toggleUiState()
            }.addOnFailureListener {
                Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }.addOnCanceledListener {
                Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
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

    private fun registerUserOnServer(authResult: AuthResult) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = User(
                name = authResult.user?.displayName,
                email = authResult.user!!.email!!,
                authId = authResult.user!!.uid
            )

            Rutinappi.retrofitService.createUser(user)

        }
    }

}

private fun String.isValidEmail(): Boolean {
    return this.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex())
}