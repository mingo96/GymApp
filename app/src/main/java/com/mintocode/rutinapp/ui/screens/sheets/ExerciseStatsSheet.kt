package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.FitnessCenter
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.mintocode.rutinapp.ui.premade.RutinAppLineChart
import com.mintocode.rutinapp.ui.premade.RutinAppPieChart
import com.mintocode.rutinapp.ui.screenStates.StatsScreenState
import com.mintocode.rutinapp.utils.dateString
import com.mintocode.rutinapp.utils.timeString
import com.mintocode.rutinapp.utils.truncatedToNDecimals
import com.mintocode.rutinapp.viewmodels.StatsViewModel
import kotlinx.coroutines.delay
import java.util.Date

/**
 * Exercise stats detail sheet shown as a stacked sheet on top of StatsSheet.
 *
 * Reads the ViewModel uiState which should be StatsOfExercise after
 * the overview sheet called selectExerciseForStats.
 *
 * @param viewModel StatsViewModel providing the exercise stats data
 */
@Composable
fun ExerciseStatsSheet(viewModel: StatsViewModel) {
    val uiState by viewModel.uiState.observeAsState(StatsScreenState.Observation())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (val state = uiState) {
            is StatsScreenState.StatsOfExercise -> {
                Text(
                    text = state.exercise.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (!state.hasBeenDone) {
                    Spacer(Modifier.height(32.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.TwoTone.FitnessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Aún no has hecho este ejercicio",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Las estadísticas aparecerán cuando lo entrenes",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                if (state.hasBeenDone) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        item(span = { GridItemSpan(2) }) {
                            Column {
                                StatLabel(title = "Gráfica de rendimiento")
                                RutinAppLineChart(value = state.weigths)
                            }
                        }
                        item(span = { GridItemSpan(2) }) {
                            Column {
                                StatLabel(title = "Días que lo entrenas")
                                RutinAppPieChart(values = state.daysDone)
                            }
                        }
                        item {
                            WeightDetail(
                                content = state.highestWeight,
                                title = "Mayor peso"
                            )
                        }
                        item {
                            StatLabel(
                                text = state.averageWeight.truncatedToNDecimals(2) + " kg",
                                title = "Peso promedio"
                            )
                        }
                        item {
                            StatLabel(
                                text = state.timesDone.toString(),
                                title = "Veces hecho"
                            )
                        }
                        item {
                            StatLabel(
                                text = state.lastTimeDone,
                                title = "Última vez hecho"
                            )
                        }
                    }
                }
            }

            else -> {
                // Loading state while ViewModel processes the exercise stats
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

/**
 * Stat label with optional value text.
 */
@Composable
private fun StatLabel(modifier: Modifier = Modifier, text: String? = null, title: String) {
    Column(modifier.padding(8.dp)) {
        Text(text = title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (text != null) {
            Text(text = text, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

/**
 * Weight detail with expandable info showing date and observations.
 */
@Composable
private fun WeightDetail(content: Triple<Double, Date, String>, title: String) {
    var isOpened by rememberSaveable { mutableStateOf(false) }

    Box(Modifier.padding(8.dp)) {
        AnimatedVisibility(
            visible = !isOpened,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.fillMaxWidth(0.8f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${content.first.truncatedToNDecimals(2)} kg",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { isOpened = true }) {
                    Icon(Icons.TwoTone.Info, contentDescription = "Más información")
                }
            }
        }

        AnimatedVisibility(
            visible = isOpened,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            LaunchedEffect(isOpened) { delay(5000); isOpened = false }
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = content.second.dateString() + " " + content.second.timeString(),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = content.third,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
