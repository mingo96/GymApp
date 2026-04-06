package com.mintocode.rutinapp.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import com.mintocode.rutinapp.ui.navigation.topLevelDestinations

/**
 * Bottom navigation bar for RutinApp v2.
 *
 * Displays 5 top-level destinations with animated icons.
 * Center "Entrenar" item is visually prominent with a slightly larger scale.
 *
 * @param currentRoute The currently active navigation route
 * @param onNavigate Callback when a destination is selected
 */
@Composable
fun RutinAppBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        topLevelDestinations.forEachIndexed { index, destination ->
            val isSelected = currentRoute == destination.route
            val isCenterItem = index == 2

            val scale by animateFloatAsState(
                targetValue = if (isCenterItem && isSelected) 1.15f
                else if (isCenterItem) 1.1f
                else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "navItemScale"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon
                        else destination.unselectedIcon,
                        contentDescription = destination.label,
                        modifier = Modifier.scale(scale)
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (isCenterItem) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.primary,
                    selectedTextColor = if (isCenterItem) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = if (isCenterItem) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
