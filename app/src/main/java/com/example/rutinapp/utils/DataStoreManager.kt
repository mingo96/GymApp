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
        val NAME = stringPreferencesKey("name")
        val CODE = stringPreferencesKey("code")
        val ISDARKTHEME = booleanPreferencesKey("isDarkTheme")
    }

    suspend fun saveData(userDetails: UserDetails) {
        context.dataStore.edit {
            it[NAME] = userDetails.name
            it[CODE] = userDetails.code
            it[ISDARKTHEME] = userDetails.isDarkTheme
        }
    }

    fun getData() = context.dataStore.data.map {
        UserDetails(
            code = it[CODE]?:"",
            name = it[NAME] ?: "",
            isDarkTheme = it[ISDARKTHEME] ?: true
        )
    }


}