package com.mintocode.rutinapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.ui.theme.rutinAppTextFieldColors
import kotlinx.coroutines.delay

/**
 * Search text field with trailing search icon button.
 *
 * Used across Exercises, Stats, and Workouts screens for filtering lists.
 *
 * @param value Current search text
 * @param onValueChange Callback when text changes
 * @param onSearch Callback when search action is triggered (keyboard or button)
 * @param modifier Optional modifier
 */
@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
        onValueChange = onValueChange,
        colors = rutinAppTextFieldColors(),
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrectEnabled = true,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        trailingIcon = {
            IconButton(onClick = { onSearch() }) {
                Icon(imageVector = Icons.TwoTone.Search, contentDescription = "Buscar")
            }
        }
    )
}

/**
 * Labeled text input field.
 *
 * Shows a title label above a styled TextField. Supports password masking,
 * custom keyboard types, and optional submit action.
 *
 * @param title Label text above the field
 * @param text Current field value
 * @param onWrite Callback when value changes
 * @param editing Whether the field is editable
 * @param typeOfKeyBoard Keyboard type (text, number, password, etc.)
 * @param sendFunction Optional submit callback (replaces focus-next behavior)
 */
@Composable
fun TextFieldWithTitle(
    title: String,
    onWrite: (String) -> Unit = {},
    text: String,
    editing: Boolean = true,
    typeOfKeyBoard: KeyboardType = KeyboardType.Text,
    sendFunction: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current

    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    TextField(
        value = text,
        enabled = editing,
        onValueChange = onWrite,
        colors = rutinAppTextFieldColors(),
        textStyle = TextStyle(fontWeight = FontWeight.Bold),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            imeAction = if (sendFunction == null) ImeAction.Next else ImeAction.Done,
            keyboardType = typeOfKeyBoard,
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrectEnabled = true
        ),
        visualTransformation = if (typeOfKeyBoard == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardActions = KeyboardActions(onDone = {
            if (sendFunction != null) sendFunction() else focusManager.moveFocus(FocusDirection.Down)
        })
    )
}

/**
 * Container for dialog content with consistent padding and arrangement.
 *
 * Uses Material3 surface colors and shapes for proper theming.
 *
 * @param content Composable content inside the dialog
 */
@Composable
fun DialogContainer(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
            content = content
        )
    }
}

// ============================================================================
// Staggered reveal animation
// ============================================================================

/**
 * Efecto de revelación progresiva para listas.
 *
 * Incrementa un índice desde 0 hasta [totalSize] con un [delayMs] entre cada paso,
 * opcionalmente esperando [initialDelayMs] antes de empezar.
 * Usar con `.take(index)` sobre la lista para animar la aparición de items.
 *
 * @param key Clave que reinicia la animación cuando cambia
 * @param totalSize Tamaño total de la lista
 * @param delayMs Retardo entre cada incremento (ms)
 * @param initialDelayMs Retardo antes de empezar (ms)
 * @return Índice actual para usar con `.take()`
 */
@Composable
fun rememberStaggeredRevealIndex(
    key: Any?,
    totalSize: Int,
    delayMs: Long = 100L,
    initialDelayMs: Long = 0L
): Int {
    var maxIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(key) {
        maxIndex = 0
        if (initialDelayMs > 0) delay(initialDelayMs)
        while (maxIndex < totalSize) {
            delay(delayMs)
            maxIndex++
        }
    }

    return maxIndex
}

// ============================================================================
// Ownership filter chips
// ============================================================================

/**
 * Par de FilterChips para alternar entre "Mis" y "De otros" recursos.
 *
 * Reutilizado en ExercisesScreen y RoutinesScreen para filtrar por propiedad.
 *
 * @param showOthers True si se muestran los de otros usuarios
 * @param onShowMine Callback al seleccionar "Mis"
 * @param onShowOthers Callback al seleccionar "De otros"
 */
@Composable
fun OwnershipFilterRow(
    showOthers: Boolean,
    onShowMine: () -> Unit,
    onShowOthers: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = !showOthers,
            onClick = onShowMine,
            label = { Text("Mis", fontSize = 13.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondary
            )
        )
        FilterChip(
            selected = showOthers,
            onClick = onShowOthers,
            label = { Text("De otros", fontSize = 13.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondary
            )
        )
    }
}

// ============================================================================
// Loading indicator
// ============================================================================

/**
 * Indicador de carga centrado con texto "Cargando..." debajo.
 *
 * @param modifier Modifier opcional
 */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Cargando...",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ============================================================================
// Dialog action buttons
// ============================================================================

/**
 * Fila de botones de acción para diálogos.
 *
 * Muestra entre 1 y 3 botones con colores del tema y disposición uniforme.
 *
 * @param primaryText Texto del botón principal
 * @param onPrimary Callback del botón principal
 * @param secondaryText Texto del botón secundario (por defecto "Salir")
 * @param onSecondary Callback del botón secundario
 * @param tertiaryText Texto opcional del tercer botón
 * @param onTertiary Callback opcional del tercer botón
 */
@Composable
fun DialogActionRow(
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String = "Salir",
    onSecondary: () -> Unit,
    tertiaryText: String? = null,
    onTertiary: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onPrimary, colors = rutinAppButtonsColours()) {
            Text(text = primaryText)
        }
        Button(onClick = onSecondary, colors = rutinAppButtonsColours()) {
            Text(text = secondaryText)
        }
        if (tertiaryText != null && onTertiary != null) {
            Button(onClick = onTertiary, colors = rutinAppButtonsColours()) {
                Text(text = tertiaryText)
            }
        }
    }
}

// ============================================================================
// Empty state
// ============================================================================

/**
 * Mensaje centrado para listas vacías.
 *
 * @param text Texto a mostrar
 * @param modifier Modifier opcional
 */
@Composable
fun EmptyStateMessage(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
