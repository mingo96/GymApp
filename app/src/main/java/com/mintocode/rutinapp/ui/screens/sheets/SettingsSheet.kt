package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.components.TextFieldWithTitle
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.SettingsViewModel

/**
 * Settings sheet content.
 *
 * Allows the user to edit their name and secret code.
 * Includes a theme toggle and delegates to existing ViewModel actions.
 *
 * @param viewModel SettingsViewModel for user data
 */
@Composable
fun SettingsSheet(viewModel: SettingsViewModel) {
    val data by viewModel.data.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ajustes de cuenta",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (data != null) {
            var name by rememberSaveable { mutableStateOf(data!!.name) }
            var code by rememberSaveable { mutableStateOf(data!!.code) }

            TextFieldWithTitle(title = "Nombre", text = name, onWrite = { name = it })
            TextFieldWithTitle(title = "Código secreto", text = code, onWrite = { code = it })

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.updateUserDetails(name, code) },
                    colors = rutinAppButtonsColours()
                ) {
                    Text(text = "Guardar")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Cambiar tema ", fontSize = 15.sp)
                    Switch(
                        checked = data!!.isDarkTheme,
                        onCheckedChange = { viewModel.toggleRutinAppTheme() }
                    )
                }
            }
        }
    }
}
