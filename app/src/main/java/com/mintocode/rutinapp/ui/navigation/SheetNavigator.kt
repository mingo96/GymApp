package com.mintocode.rutinapp.ui.navigation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Manages a stack of bottom sheet destinations for Trade Republic-style navigation.
 *
 * Sheets stack on top of each other. Swiping down dismisses the top sheet,
 * revealing the one below. The root pages are never part of the sheet stack.
 *
 * Thread-safe: all mutations happen on the main thread via Compose state.
 */
@Stable
class SheetNavigator {

    internal val _stack = mutableStateListOf<SheetDestination>()

    /** Current sheet stack (bottom to top). Empty means only root pager is visible. */
    val stack: List<SheetDestination> get() = _stack

    /** Whether any sheets are currently open. */
    val hasSheets: Boolean get() = _stack.isNotEmpty()

    /** Open a new sheet on top of the stack. Ignores duplicate consecutive opens. */
    fun open(destination: SheetDestination) {
        if (_stack.lastOrNull() == destination) return
        _stack.add(destination)
    }

    /** Close the topmost sheet. No-op if stack is empty. */
    fun close() {
        if (_stack.isNotEmpty()) {
            _stack.removeLast()
        }
    }

    /** Close all sheets, returning to the root pager. */
    fun closeAll() {
        _stack.clear()
    }

    /**
     * Replace the topmost sheet with a new destination.
     * Useful for edit → detail transitions without stacking.
     */
    fun replace(destination: SheetDestination) {
        if (_stack.isNotEmpty()) {
            _stack.removeLast()
        }
        _stack.add(destination)
    }
}

/**
 * CompositionLocal providing the SheetNavigator to any composable in the tree.
 * Must be provided at the root (MainActivity level).
 */
val LocalSheetNavigator = staticCompositionLocalOf<SheetNavigator> {
    error("No SheetNavigator provided. Wrap your content in CompositionLocalProvider.")
}
