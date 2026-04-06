package com.mintocode.rutinapp.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * Renders the sheet stack managed by [SheetNavigator].
 *
 * Each [SheetDestination] in the stack becomes an independent [ModalBottomSheet]
 * that stacks on top of the previous one. The user can swipe down to dismiss
 * the topmost sheet, revealing the one below.
 *
 * This creates the Trade Republic-style stacking navigation pattern.
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
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            },
            contentWindowInsets = { WindowInsets.navigationBars }
        ) {
            content(destination)
        }
    }
}
