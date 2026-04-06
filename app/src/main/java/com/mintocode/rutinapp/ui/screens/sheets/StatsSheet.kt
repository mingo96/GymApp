package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mintocode.rutinapp.ui.screens.StatsScreen
import com.mintocode.rutinapp.viewmodels.StatsViewModel

/**
 * Statistics overview sheet content.
 *
 * Wraps the existing StatsScreen composable inside the sheet context.
 * StatsScreen already handles its own layout with exercise search and stats detail.
 *
 * @param viewModel StatsViewModel for stats data and actions
 */
@Composable
fun StatsSheet(viewModel: StatsViewModel) {
    StatsScreen(statsViewModel = viewModel)
}
