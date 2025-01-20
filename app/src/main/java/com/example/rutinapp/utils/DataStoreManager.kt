package com.example.rutinapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.rutinapp.data.UserDetails
import kotlinx.coroutines.flow.map


private const val SETTINGS_DATASTORE = "settings"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_DATASTORE)

class DataStoreManager(val context: Context) {

    companion object {
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password")
        val NAME = stringPreferencesKey("name")
        val ISDARKTHEME = booleanPreferencesKey("isDarkTheme")
    }

    suspend fun saveData(userDetails: UserDetails) {
        context.dataStore.edit {
            it[EMAIL] = userDetails.email
            it[PASSWORD] = userDetails.password
            it[NAME] = userDetails.name
            it[ISDARKTHEME] = userDetails.isDarkTheme
        }
    }

    fun getData() = context.dataStore.data.map {
        UserDetails(
            email = it[EMAIL] ?: "",
            password = it[PASSWORD] ?: "",
            name = it[NAME] ?: "",
            isDarkTheme = it[ISDARKTHEME] ?: true
        )
    }


}