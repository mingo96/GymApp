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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.ui.theme.PurpleGrey80

@Composable
fun LoadingScreen() {

    val originalColor = PurpleGrey80
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(originalColor), contentAlignment = Alignment.Center
    ) {
        val smallest = if (maxHeight < maxWidth) maxHeight else maxWidth
        Box(Modifier.size(smallest - 100.dp)) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(smallest)
                    .zIndex(2f),
                color = Color(0xFF121217).copy(0.5f),
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
                                originalColor,
                                originalColor,
                            )
                        )
                    )
            ) {

            }
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