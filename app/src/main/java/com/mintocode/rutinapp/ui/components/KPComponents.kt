package com.mintocode.rutinapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.theme.ManropeFont

/**
 * Glass-style card with surfaceContainerLow background and subtle border.
 *
 * Standard KP card container used across multiple screens.
 *
 * @param modifier Optional modifier
 * @param content Column content inside the card
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        content = { Column(modifier = Modifier.padding(24.dp), content = content) }
    )
}

/**
 * Uppercase section label with extra-bold weight and wide letter spacing.
 *
 * @param text The label text (will be uppercased automatically)
 */
@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = ManropeFont,
        letterSpacing = 2.5.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/**
 * Full-width gradient button with press-scale animation.
 *
 * Uses primaryContainer → primary gradient by default.
 *
 * @param onClick Action on tap
 * @param modifier Optional modifier
 * @param content Row content inside the button
 */
@Composable
fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(150),
        label = "gradient_btn_scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
            .clickable(interactionSource, indication = null, onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/**
 * Decorative accent bar — short colored pill shape.
 *
 * @param color Bar color (defaults to secondary)
 * @param width Bar width (defaults to 64dp)
 */
@Composable
fun AccentBar(
    color: Color = MaterialTheme.colorScheme.secondary,
    width: Dp = 64.dp
) {
    Box(
        modifier = Modifier
            .height(6.dp)
            .width(width)
            .background(color, RoundedCornerShape(50))
    )
}

/**
 * Circular icon container with tinted background.
 *
 * @param icon The icon to display
 * @param tint Icon tint color
 * @param backgroundColor Circle background color
 * @param size Overall container size
 */
@Composable
fun IconCircle(
    icon: ImageVector,
    tint: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    size: Dp = 40.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(size * 0.5f))
    }
}
