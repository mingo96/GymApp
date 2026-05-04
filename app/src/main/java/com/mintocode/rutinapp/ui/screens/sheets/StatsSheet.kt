package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.EmojiEvents
import androidx.compose.material.icons.twotone.FitnessCenter
import androidx.compose.material.icons.twotone.LocalFireDepartment
import androidx.compose.material.icons.twotone.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mintocode.rutinapp.ui.components.GlassCard
import com.mintocode.rutinapp.ui.components.SectionLabel
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.viewmodels.StatsViewModel

// ── KP Color constants ──
private val KP_PRIMARY = Color(0xFFBAC3FF)
private val KP_PRIMARY_CONTAINER = Color(0xFF4361EE)
private val KP_ON_PRIMARY_CONTAINER = Color(0xFFF4F2FF)
private val KP_TERTIARY = Color(0xFF27E0A9)
private val KP_TERTIARY_CONTAINER = Color(0xFF007F5D)
private val KP_SURFACE_CONTAINER = Color(0xFF1F1F24)
private val KP_SURFACE_CONTAINER_HIGHEST = Color(0xFF35343A)
private val KP_ON_SURFACE = Color(0xFFE4E1E9)
private val KP_ON_SURFACE_VARIANT = Color(0xFFC4C5D7)
private val KP_OUTLINE = Color(0xFF8E8FA1)
private val KP_ERROR_CONTAINER = Color(0xFF93000A)
private val KP_SECONDARY_CONTAINER = Color(0xFF6800E4)

/**
 * Statistics overview sheet with KP design system.
 *
 * Shows period filter chips, KPI cards, charts (volume, frequency, muscle distribution),
 * top exercises ranking, and personal records grid.
 * Tapping an exercise navigates to ExerciseStats detail sheet.
 *
 * @param viewModel StatsViewModel for stats data and actions
 */
@Composable
fun StatsSheet(viewModel: StatsViewModel) {
    val navigator = LocalSheetNavigator.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val exercises by viewModel.exercisesState.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )

    var selectedPeriod by rememberSaveable { mutableIntStateOf(0) }
    val periods = listOf("Semana", "Mes", "3 Meses", "Año", "Todo")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // ── Drag Handle ──
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(48.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        )

        Spacer(Modifier.height(16.dp))

        // ── Title ──
        Text(
            text = "Estadísticas",
            fontFamily = SpaceGroteskFont,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = KP_ON_SURFACE
        )

        Spacer(Modifier.height(24.dp))

        // ── Period Filter Chips ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            periods.forEachIndexed { index, label ->
                PeriodChip(
                    label = label,
                    isActive = selectedPeriod == index,
                    onClick = { selectedPeriod = index }
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── KPI Row ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KpiCard(
                icon = Icons.TwoTone.FitnessCenter,
                iconColor = KP_PRIMARY,
                value = "${exercises.size}",
                label = "ENTRENAMIENTOS",
                extra = "+12%",
                extraColor = KP_TERTIARY
            )
            KpiCard(
                icon = Icons.TwoTone.FitnessCenter,
                iconColor = KP_PRIMARY,
                value = "8.4k kg",
                label = "VOLUMEN TOTAL"
            )
            KpiCard(
                icon = Icons.TwoTone.Schedule,
                iconColor = KP_PRIMARY,
                value = "75 min",
                label = "DURACIÓN MEDIA"
            )
            KpiCard(
                icon = Icons.TwoTone.LocalFireDepartment,
                iconColor = KP_TERTIARY,
                value = "12 Días",
                label = "RACHA ACTIVA"
            )
        }

        Spacer(Modifier.height(32.dp))

        // ── Volume Chart ──
        SectionLabel("Volumen Semanal")
        Spacer(Modifier.height(8.dp))
        VolumeChart()

        Spacer(Modifier.height(24.dp))

        // ── Frequency Chart ──
        SectionLabel("Frecuencia de Entrenamiento")
        Spacer(Modifier.height(8.dp))
        FrequencyChart()

        Spacer(Modifier.height(24.dp))

        // ── Muscle Distribution ──
        SectionLabel("Distribución Muscular")
        Spacer(Modifier.height(8.dp))
        MuscleDistributionChart()

        Spacer(Modifier.height(32.dp))

        // ── Top Exercises ──
        SectionLabel("Top Ejercicios")
        Spacer(Modifier.height(12.dp))

        val topExercises = exercises.take(3)
        topExercises.forEachIndexed { index, exercise ->
            ExerciseRankRow(
                rank = index + 1,
                name = exercise.name,
                series = "-- series",
                prValue = "-- kg",
                onClick = {
                    viewModel.selectExerciseForStats(exercise)
                    navigator.open(SheetDestination.ExerciseStats)
                }
            )
            if (index < topExercises.lastIndex) Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(32.dp))

        // ── Personal Records ──
        SectionLabel("Records Personales")
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PrCard(
                name = "Peso muerto",
                value = "-- kg",
                reps = "x1",
                date = "---",
                isRecent = true,
                modifier = Modifier.weight(1f)
            )
            PrCard(
                name = "Press militar",
                value = "-- kg",
                reps = "x5",
                date = "---",
                isRecent = false,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(48.dp))
    }
}

// ============================================================================
// Period Chip
// ============================================================================

/**
 * Filter chip for period selection.
 *
 * Active: primaryContainer background, onPrimaryContainer text.
 * Inactive: surfaceContainerHighest background, onSurfaceVariant text.
 *
 * @param label Chip text
 * @param isActive Whether this chip is selected
 * @param onClick Callback when tapped
 */
@Composable
private fun PeriodChip(label: String, isActive: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100), label = "chip_scale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(50))
            .background(
                if (isActive) KP_PRIMARY_CONTAINER
                else KP_SURFACE_CONTAINER_HIGHEST
            )
            .clickable(interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontFamily = ManropeFont,
            fontSize = 14.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isActive) KP_ON_PRIMARY_CONTAINER else KP_ON_SURFACE_VARIANT
        )
    }
}

// ============================================================================
// KPI Card
// ============================================================================

/**
 * Glass-style KPI card with icon, value, label and optional trend.
 *
 * @param icon The icon vector
 * @param iconColor Icon tint color
 * @param value Main KPI value text
 * @param label Uppercase label below value
 * @param extra Optional trend text (e.g. "+12%")
 * @param extraColor Color for the trend text
 */
@Composable
private fun KpiCard(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String,
    extra: String? = null,
    extraColor: Color = KP_TERTIARY
) {
    Box(
        modifier = Modifier
            .widthIn(min = 160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Icon(
                icon, contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontFamily = SpaceGroteskFont,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = KP_ON_SURFACE
                )
                if (extra != null) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = extra,
                        fontFamily = ManropeFont,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = extraColor
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                fontFamily = ManropeFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = KP_OUTLINE
            )
        }
    }
}

// ============================================================================
// Charts
// ============================================================================

/**
 * Volume area chart placeholder with KP styling.
 * Uses bars as a simplified representation until a chart library is added.
 */
@Composable
private fun VolumeChart() {
    val data = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.8f, 1f)
    val days = listOf("L", "M", "X", "J", "V", "S", "D")

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Volumen Semanal",
                fontFamily = SpaceGroteskFont,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = KP_ON_SURFACE
            )
            Text(
                text = "Últimos 7 días",
                fontFamily = ManropeFont,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = KP_PRIMARY
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEachIndexed { index, value ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height((value * 100).dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        KP_PRIMARY_CONTAINER,
                                        KP_PRIMARY_CONTAINER.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = days[index],
                        fontFamily = ManropeFont,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = KP_OUTLINE
                    )
                }
            }
        }
    }
}

/**
 * Training frequency bar chart with KP styling.
 * Active days use primaryContainer, rest days use surfaceVariant.
 */
@Composable
private fun FrequencyChart() {
    val frequencies = listOf(0.6f, 0.8f, 0.2f, 0.95f, 0.7f, 0.1f, 0.15f)
    val days = listOf("L", "M", "X", "J", "V", "S", "D")
    val activeThreshold = 0.3f

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Frecuencia de Entrenamiento",
            fontFamily = SpaceGroteskFont,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = KP_ON_SURFACE
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            frequencies.forEachIndexed { index, freq ->
                val isActive = freq > activeThreshold
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height((freq * 80).dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isActive) KP_PRIMARY_CONTAINER
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = days[index],
                        fontFamily = ManropeFont,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = KP_OUTLINE
                    )
                }
            }
        }
    }
}

/**
 * Horizontal progress bars showing muscle group distribution.
 * Each muscle has a colored proportion bar.
 */
@Composable
private fun MuscleDistributionChart() {
    val muscles = listOf(
        Triple("Pecho", 0.45f, KP_PRIMARY_CONTAINER),
        Triple("Espalda", 0.30f, KP_SECONDARY_CONTAINER),
        Triple("Piernas", 0.20f, KP_TERTIARY_CONTAINER),
        Triple("Core", 0.05f, KP_ERROR_CONTAINER)
    )

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Distribución Muscular",
            fontFamily = SpaceGroteskFont,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = KP_ON_SURFACE
        )

        Spacer(Modifier.height(16.dp))

        muscles.forEach { (name, percent, color) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontFamily = ManropeFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = KP_ON_SURFACE
                )
                Text(
                    text = "${(percent * 100).toInt()}%",
                    fontFamily = ManropeFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = KP_ON_SURFACE_VARIANT
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(KP_SURFACE_CONTAINER_HIGHEST)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percent)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color)
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ============================================================================
// Top Exercises
// ============================================================================

/**
 * Ranked exercise row with rank badge, name, series count and PR value.
 *
 * Rank 1 uses primary highlight; others use neutral styling.
 *
 * @param rank Position (1, 2, 3)
 * @param name Exercise name
 * @param series Series count text
 * @param prValue Personal record text
 * @param onClick Callback when tapped
 */
@Composable
private fun ExerciseRankRow(
    rank: Int,
    name: String,
    series: String,
    prValue: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(KP_SURFACE_CONTAINER)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (rank == 1) KP_PRIMARY_CONTAINER.copy(alpha = 0.2f)
                        else KP_SURFACE_CONTAINER_HIGHEST
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    fontFamily = SpaceGroteskFont,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rank == 1) KP_PRIMARY else KP_ON_SURFACE
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = name,
                    fontFamily = SpaceGroteskFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KP_ON_SURFACE
                )
                Text(
                    text = series,
                    fontFamily = ManropeFont,
                    fontSize = 12.sp,
                    color = KP_ON_SURFACE_VARIANT
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "PR",
                fontFamily = ManropeFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                color = KP_OUTLINE
            )
            Text(
                text = prValue,
                fontFamily = SpaceGroteskFont,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (rank == 1) KP_PRIMARY else KP_ON_SURFACE
            )
        }
    }
}

// ============================================================================
// Personal Record Card
// ============================================================================

/**
 * Personal record card for the 2-column grid.
 *
 * Recent PRs get a tertiary border and icon tint.
 *
 * @param name Exercise name
 * @param value Weight string
 * @param reps Rep count string
 * @param date Date label
 * @param isRecent Whether this is a recent PR (uses tertiary styling)
 * @param modifier Optional modifier
 */
@Composable
private fun PrCard(
    name: String,
    value: String,
    reps: String,
    date: String,
    isRecent: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(KP_SURFACE_CONTAINER)
            .then(
                if (isRecent) Modifier.border(
                    1.dp,
                    KP_TERTIARY.copy(alpha = 0.2f),
                    RoundedCornerShape(16.dp)
                )
                else Modifier.border(
                    1.dp,
                    Color.White.copy(alpha = 0.05f),
                    RoundedCornerShape(16.dp)
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Icon(
                Icons.TwoTone.EmojiEvents,
                contentDescription = null,
                tint = if (isRecent) KP_TERTIARY else KP_SECONDARY_CONTAINER,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = name,
                fontFamily = ManropeFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = KP_ON_SURFACE_VARIANT
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontFamily = SpaceGroteskFont,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = KP_ON_SURFACE
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = reps,
                    fontFamily = ManropeFont,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = KP_OUTLINE
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = date.uppercase(),
                fontFamily = ManropeFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = if (isRecent) KP_TERTIARY else KP_OUTLINE
            )
        }
    }
}
