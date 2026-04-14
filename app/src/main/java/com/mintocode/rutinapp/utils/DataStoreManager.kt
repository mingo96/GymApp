package com.mintocode.rutinapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mintocode.rutinapp.data.UserDetails
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


private const val SETTINGS_DATASTORE = "settings"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_DATASTORE)

class DataStoreManager(val context: Context) {

    companion object {
        val NAME = stringPreferencesKey("name")
        val CODE = stringPreferencesKey("code")
        val ISDARKTHEME = booleanPreferencesKey("isDarkTheme")
        val AUTHTOKEN = stringPreferencesKey("authToken")
        val EMAIL = stringPreferencesKey("email")
        val FLOATING_WIDGET_ENABLED = booleanPreferencesKey("floatingWidgetEnabled")
    }

    suspend fun saveData(userDetails: UserDetails) {
        context.dataStore.edit {
            it[NAME] = userDetails.name
            it[CODE] = userDetails.code
            it[ISDARKTHEME] = userDetails.isDarkTheme
            it[AUTHTOKEN] = userDetails.authToken
            it[EMAIL] = userDetails.email
            it[FLOATING_WIDGET_ENABLED] = userDetails.floatingWidgetEnabled
        }
    }

    fun getData() = context.dataStore.data.map {
        UserDetails(
            code = it[CODE]?:"",
            name = it[NAME] ?: "",
            isDarkTheme = it[ISDARKTHEME] ?: true,
            authToken = it[AUTHTOKEN] ?: "",
            email = it[EMAIL] ?: "@gmail.com",
            floatingWidgetEnabled = it[FLOATING_WIDGET_ENABLED] ?: false
        )
    }

    suspend fun data() : UserDetails {
        return getData().first()
    }

}