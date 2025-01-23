package com.mintocode.rutinapp.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mintocode.rutinapp.data.UserDetails
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

    private val _uiState: MutableLiveData<SettingsScreenState> =
        MutableLiveData(SettingsScreenState.Start)

    val uiState: LiveData<SettingsScreenState> = _uiState

    private lateinit var dataStoreManager: DataStoreManager

    fun initiateDataStore(value: DataStoreManager) {
        dataStoreManager = value

        viewModelScope.launch(Dispatchers.IO) {
            getUserDetails()
        }
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
    }

    fun updateUserDetails(
        name: String,
        code: String,
        isDarkTheme: Boolean = _data.value!!.isDarkTheme
    ) {
        val newData = UserDetails(
            code, name, isDarkTheme
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

        updateUserDetails(code = data.value!!.code, name = data.value!!.name, isDarkTheme =  false)
    }

    private fun changeToDarkTheme() {
        PrimaryColor = Color(0xFF121217)
        SecondaryColor = Color(0xFF1212ED)
        ContentColor = Color.White
        TextFieldColor = Color(40, 40, 58).copy(0.5f)

        updateUserDetails(code = data.value!!.code ,name = data.value!!.name, isDarkTheme =  true)
    }

}