package com.mintocode.rutinapp.ui.screens.sheets

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.viewmodels.SettingsViewModel

/**
 * Sheet de configuración de la aplicación.
 *
 * Contiene ajustes generales como la activación del widget flotante de entrenamiento.
 * Cuando el usuario intenta activar el widget sin tener el permiso de superposición,
 * se muestra un diálogo tutorial explicando cómo concederlo.
 *
 * @param viewModel SettingsViewModel para acceder y modificar preferencias
 */
@Composable
fun AppConfigSheet(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val data by viewModel.data.observeAsState()
    var showPermissionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // ── Floating widget toggle ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.shapes.medium
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Widget flotante",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Muestra un widget fuera de la app durante un entrenamiento activo",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = data?.floatingWidgetEnabled ?: false,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        if (Settings.canDrawOverlays(context)) {
                            viewModel.setFloatingWidgetEnabled(true)
                        } else {
                            showPermissionDialog = true
                        }
                    } else {
                        viewModel.setFloatingWidgetEnabled(false)
                    }
                }
            )
        }

        Spacer(Modifier.height(24.dp))
    }

    // ── Permission tutorial dialog ──
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = {
                Text(
                    text = "Permiso necesario",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Para usar el widget flotante, necesitas conceder el permiso de " +
                            "\"Mostrar sobre otras aplicaciones\". " +
                            "Al pulsar el botón serás redirigido a los ajustes del sistema donde " +
                            "podrás activarlo para RutinApp."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    },
                    colors = rutinAppButtonsColours()
                ) {
                    Text("Ir a ajustes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
