package com.mintocode.rutinapp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)

    for (destination in navigator.stack) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = { navigator.close() },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            scrimColor = Color.Black.copy(alpha = 0.5f),
            dragHandle = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .drawBehind {
                            drawLine(
                                color = borderColor,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navigator.close() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    BottomSheetDefaults.DragHandle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    // Spacer to balance the row layout (same width as IconButton)
                    IconButton(onClick = {}, enabled = false) {
                        // Invisible placeholder for symmetry
                    }
                }
            },
            contentWindowInsets = { WindowInsets.navigationBars }
        ) {
            content(destination)
        }
    }
}
