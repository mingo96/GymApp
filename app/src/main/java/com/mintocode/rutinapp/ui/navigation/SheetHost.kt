package com.mintocode.rutinapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mintocode.rutinapp.ui.theme.LocalRutinAppColors

/**
 * Renders the sheet stack managed by [SheetNavigator].
 *
 * Each [SheetDestination] in the stack becomes an independent [ModalBottomSheet]
 * that stacks on top of the previous one. The user can swipe down to dismiss
 * the topmost sheet, revealing the one below.
 *
 * Uses [LocalRutinAppColors] surfaceElevated as container color to create
 * visual depth between sheets and root pages. Includes a back button header
 * alongside the drag handle for non-swipe users.
 *
 * @param navigator The [SheetNavigator] managing the sheet stack
 * @param content Lambda mapping each [SheetDestination] to its composable content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetHost(
    navigator: SheetNavigator,
    content: @Composable (SheetDestination) -> Unit
) {
    for (destination in navigator.stack) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = { navigator.close() },
            sheetState = sheetState,
            containerColor = LocalRutinAppColors.current.surfaceElevated,
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            dragHandle = null,
            contentWindowInsets = { WindowInsets.systemBars }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SheetHeader(onClose = { navigator.close() })
                content(destination)
            }
        }
    }
}

/**
 * Header bar for bottom sheets with a centered drag handle and a back button.
 *
 * Provides both drag (swipe-down) and tap (back arrow) affordances for closing sheets.
 *
 * @param onClose Callback invoked when the back button is tapped
 */
@Composable
private fun SheetHeader(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
    ) {
        // Centered drag handle indicator
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
                .size(width = 32.dp, height = 4.dp)
                .background(
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    RoundedCornerShape(2.dp)
                )
        )
        // Back button (left-aligned)
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
