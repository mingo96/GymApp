package com.mintocode.rutinapp.ui.screens.sheets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.FitnessCenter
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.ui.components.EmptyStateMessage
import com.mintocode.rutinapp.ui.components.LoadingIndicator
import com.mintocode.rutinapp.ui.components.SearchTextField
import com.mintocode.rutinapp.ui.components.rememberStaggeredRevealIndex
import com.mintocode.rutinapp.ui.navigation.LocalSheetNavigator
import com.mintocode.rutinapp.ui.navigation.SheetDestination
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.RoutinesScreenState
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import com.mintocode.rutinapp.ui.theme.ManropeFont
import com.mintocode.rutinapp.viewmodels.RoutinesViewModel
import java.util.Locale

/**
 * Routine list with Kinetic Precision design (Guide 09).
 *
 * Features: 48sp title, SearchBar, 3-tab TabRow (MIS RUTINAS / COMPARTIDAS / DEL ENTRENADOR),
 * routines grouped by body part with gradient-border cards, gradient FAB (rounded-full, 3-color).
 *
 * @param viewModel RoutinesViewModel for data and actions
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoutineListSheet(viewModel: RoutinesViewModel) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) { viewModel.autoSync() }

    val routines by viewModel.routines.collectAsStateWithLifecycle(lifecycle = lifecycle)
    val uiState by viewModel.uiState.observeAsState()
    val showOthers by viewModel.showOthers.observeAsState(false)
    val loading by viewModel.isLoading.observeAsState(false)

    val maxIndex = rememberStaggeredRevealIndex(key = routines, totalSize = routines.size)

    val navigator = LocalSheetNavigator.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is RoutinesScreenState.Creating -> navigator.open(SheetDestination.RoutineCreate)
            is RoutinesScreenState.Editing -> navigator.open(SheetDestination.RoutineEdit(0))
            is RoutinesScreenState.Observe -> {
                if ((uiState as RoutinesScreenState.Observe).routine != null) {
                    navigator.open(SheetDestination.RoutineDetail(0))
                }
            }
            null, RoutinesScreenState.Overview -> {}
        }
    }

    var searchText by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val tabTitles = listOf("MIS RUTINAS", "COMPARTIDAS", "DEL ENTRENADOR")

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 96.dp)
        ) {
            // ── Title: 48sp ──
            item {
                Text(
                    text = "Rutinas",
                    fontFamily = SpaceGroteskFont,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1.5).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(24.dp))
            }

            // ── Search ──
            item {
                SearchTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    onSearch = { },
                    placeholder = "Buscar rutinas...",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
            }

            // ── TabRow ──
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 0.dp,
                    divider = {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f)
                                )
                        )
                    },
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                height = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                when (index) {
                                    0 -> viewModel.showMine()
                                    1 -> viewModel.showOthers()
                                    2 -> viewModel.showOthers()
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontFamily = SpaceGroteskFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 2.sp,
                                    color = if (selectedTab == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outline
                                )
                            }
                        )
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            if (loading) {
                item { LoadingIndicator() }
            }

            // ── Filter routines ──
            val filtered = run {
                val byOwnership = if (selectedTab == 0) routines.filter { it.isFromThisUser }
                else routines.filter { !it.isFromThisUser }
                if (searchText.isBlank()) byOwnership else byOwnership.filter {
                    it.name.contains(searchText, ignoreCase = true) ||
                            it.targetedBodyPart.contains(searchText, ignoreCase = true)
                }
            }

            if (!loading && filtered.isEmpty()) {
                item {
                    EmptyStateMessage(
                        text = when (selectedTab) {
                            0 -> "No tienes rutinas aún"
                            1 -> "No hay rutinas compartidas"
                            else -> "No hay rutinas del entrenador"
                        }
                    )
                }
            }

            // ── Group by body part ──
            val groups = filtered
                .groupBy { it.targetedBodyPart.replaceFirstChar { c ->
                    if (c.isLowerCase()) c.titlecase(Locale.ROOT) else c.toString()
                } }
                .entries.toList()
                .take(maxIndex)

            items(groups, key = { it.key }) { (bodyPart, groupRoutines) ->
                AnimatedItem(enterAnimation = slideInHorizontally(), delay = 80) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Section label: primary uppercase
                        Text(
                            text = bodyPart.uppercase(),
                            fontFamily = SpaceGroteskFont,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 2.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(end = 4.dp)
                        ) {
                            items(groupRoutines) { routine ->
                                KPRoutineCard(
                                    routine = routine,
                                    gradientColor = bodyPartGradientColor(routine.targetedBodyPart),
                                    modifier = Modifier.combinedClickable(
                                        onClick = {
                                            viewModel.clickObserveRoutine(routine)
                                            navigator.open(SheetDestination.RoutineDetail(routine.id))
                                        },
                                        onLongClick = {
                                            viewModel.clickEditRoutine(routine)
                                            navigator.open(SheetDestination.RoutineEdit(routine.id))
                                        }
                                    )
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }

        // ── FAB: gradient 3-color, rounded-full ──
        val fabScale by animateFloatAsState(targetValue = 1f, label = "fab")

        FloatingActionButton(
            onClick = { navigator.open(SheetDestination.RoutineCreate) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
                .size(64.dp)
                .scale(fabScale)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                ),
            shape = CircleShape,
            containerColor = Color.Transparent,
            contentColor = Color.White
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.TwoTone.Add,
                    contentDescription = "Crear rutina",
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * Routine card with gradient border wrapper (Guide 09 pattern).
 *
 * Wrapper p-[1px] with gradient border, inner surfaceContainerLowest card.
 *
 * @param routine Routine model to display
 * @param gradientColor Color for gradient border based on body part
 * @param modifier Modifier with click handlers
 */
@Composable
private fun KPRoutineCard(
    routine: RoutineModel,
    gradientColor: Color,
    modifier: Modifier = Modifier
) {
    // Gradient border wrapper
    Box(
        modifier = modifier
            .width(280.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        gradientColor.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(1.dp)
    ) {
        // Inner card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceContainerLowest,
                    RoundedCornerShape(16.dp)
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Top row: body part tag + more icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Body part tag
                Box(
                    modifier = Modifier
                        .background(
                            gradientColor.copy(alpha = 0.1f),
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = routine.targetedBodyPart
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                            .uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = gradientColor
                    )
                }
                Icon(
                    Icons.TwoTone.MoreVert,
                    contentDescription = "Opciones",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(Modifier.height(12.dp))

            // Focus label
            Text(
                text = "FOCUS",
                fontFamily = ManropeFont,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline
            )

            // Routine name
            Text(
                text = routine.name,
                fontFamily = SpaceGroteskFont,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(16.dp))

            // Footer: exercise count
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.TwoTone.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${routine.exercises.size} EJERC",
                        fontFamily = ManropeFont,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Returns gradient color for routine card based on body part.
 *
 * @param bodyPart The targeted body part string
 * @return Color matching the body part group
 */
@Composable
private fun bodyPartGradientColor(bodyPart: String): Color {
    val lower = bodyPart.lowercase()
    return when {
        lower.contains("pecho") -> MaterialTheme.colorScheme.tertiary
        lower.contains("pierna") -> MaterialTheme.colorScheme.primary
        lower.contains("espalda") -> MaterialTheme.colorScheme.secondary
        lower.contains("hombro") -> MaterialTheme.colorScheme.primary
        lower.contains("brazo") -> MaterialTheme.colorScheme.tertiary
        lower.contains("core") -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }
}
