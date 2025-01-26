package com.mintocode.rutinapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.mintocode.rutinapp.data.UserDetails
import com.mintocode.rutinapp.ui.screenStates.SettingsScreenState
import com.mintocode.rutinapp.ui.theme.ScreenContainer
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.SettingsViewModel

@Composable
fun SettinsScreen(navController: NavHostController, settingsViewModel: SettingsViewModel) {

    val data by settingsViewModel.data.observeAsState()

    val uiState by settingsViewModel.uiState.observeAsState(initial = SettingsScreenState.Start)

    ScreenContainer(title = "Configuración", navController = navController) {

        Column(
            Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (uiState) {

                SettingsScreenState.Start -> {
                    TextFields(settingsViewModel = settingsViewModel, data = data!!)
                }
            }
        }

    }
}

@Composable
fun TextFields(settingsViewModel: SettingsViewModel, data: UserDetails) {

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