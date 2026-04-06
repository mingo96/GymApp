package com.mintocode.rutinapp.ui.screens.root

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * Data class for each root page shown in the top indicator.
 */
data class RootPageInfo(
    val icon: ImageVector,
    val label: String
)

/** The 3 root pages: Inicio, Entrenar, Perfil. */
val rootPages = listOf(
    RootPageInfo(Icons.Outlined.Home, "Inicio"),
    RootPageInfo(Icons.Outlined.FitnessCenter, "Entrenar"),
    RootPageInfo(Icons.Outlined.Person, "Perfil")
)

/**
 * Root pager with horizontal swipe between 3 main pages.
 *
 * Top indicator shows icons + text for each page. Tapping an indicator
 * or swiping horizontally navigates between pages. This replaces the
 * traditional bottom navigation bar.
 *
 * @param page0 Composable content for Home page
 * @param page1 Composable content for Train page
 * @param page2 Composable content for Profile page
 */
@Composable
fun RootPager(
    page0: @Composable () -> Unit,
    page1: @Composable () -> Unit,
    page2: @Composable () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        RootPageIndicator(pagerState = pagerState)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->
            when (page) {
                0 -> page0()
                1 -> page1()
                2 -> page2()
            }
        }
    }
}

/**
 * Top indicator bar showing icons + labels for each root page.
 *
 * The current page's label and icon are highlighted with primary color.
 * Tapping any indicator animates the pager to that page.
 */
@Composable
private fun RootPageIndicator(pagerState: PagerState) {
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        rootPages.forEachIndexed { index, page ->
            val isSelected = pagerState.currentPage == index

            val color by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                },
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "indicator_color"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = page.label,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = page.label,
                    color = color,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
