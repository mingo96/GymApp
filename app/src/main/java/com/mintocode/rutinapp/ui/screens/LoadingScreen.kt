package com.mintocode.rutinapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mintocode.rutinapp.R

/**
 * Splash/loading screen shown while user data loads from DataStore.
 * Displays the app logo with a circular progress indicator and radial gradient mask.
 */
@Composable
fun LoadingScreen() {
    val bgColor = MaterialTheme.colorScheme.background
    val progressColor = MaterialTheme.colorScheme.primary

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        val smallest = if (maxHeight < maxWidth) maxHeight else maxWidth
        Box(Modifier.size(smallest - 100.dp)) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(smallest)
                    .zIndex(2f),
                color = progressColor.copy(alpha = 0.7f),
                strokeWidth = 15.dp
            )
            Box(
                modifier = Modifier
                    .requiredWidth(smallest)
                    .requiredHeight(smallest)
                    .zIndex(1f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                bgColor,
                                bgColor,
                            )
                        )
                    )
            )
            Image(
                painter = painterResource(R.drawable.icon_for_a_notes_app_for_gym__name_is_rutinapp__3_),
                contentDescription = "logo",
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(0f)
            )
        }
    }
}