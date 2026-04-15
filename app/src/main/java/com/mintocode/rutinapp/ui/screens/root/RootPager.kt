package com.mintocode.rutinapp.ui.screens.root

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.theme.SpaceGroteskFont
import kotlinx.coroutines.launch

/**
 * Tab labels for the 3 root pages.
 */
private val rootTabLabels = listOf("INICIO", "ENTRENAR", "PERFIL")

/**
 * Root pager with horizontal swipe between 3 main pages.
 *
 * Kinetic Precision top navigation: text-only tabs with underline indicator.
 * Tapping a tab or swiping horizontally navigates between pages.
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
        KPTopNavigation(pagerState = pagerState)

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
 * Kinetic Precision top navigation bar with text tabs and underline indicator.
 *
 * Matches the Stitch KP design: uppercase labels in Space Grotesk,
 * active tab highlighted with primary color and a short underline bar.
 */
@Composable
private fun KPTopNavigation(pagerState: PagerState) {
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        rootTabLabels.forEachIndexed { index, label ->
            val isSelected = pagerState.currentPage == index

            val color by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                },
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "tab_color"
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = color,
                    fontFamily = SpaceGroteskFont,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .width(16.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(color)
                    )
                }
            }
        }
    }
}
