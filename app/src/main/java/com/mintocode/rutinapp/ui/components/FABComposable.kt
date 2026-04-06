package com.mintocode.rutinapp.ui.components

import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.List
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.theme.rutinAppTextButtonColors
import com.mintocode.rutinapp.ui.uiClasses.FABButton
import kotlinx.coroutines.delay

/**
 * Expandable floating action button with animated sub-buttons.
 *
 * @param buttons List of FABButton items with text and onClick
 */
@Composable
fun FABComposable(buttons: List<FABButton>) {

    var extended by rememberSaveable { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (extended) {
            var maxIndex by rememberSaveable { mutableIntStateOf(0) }
            LaunchedEffect(buttons) {
                while (maxIndex < buttons.size) {
                    delay(150)
                    maxIndex++
                }
            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
                modifier = Modifier.rotate(180f)
            ) {
                for (i in buttons.reversed().take(maxIndex)) {
                    AnimatedItem(slideInVertically { -it }, 100) {
                        TextButton(
                            onClick = { i.onClick() },
                            colors = rutinAppTextButtonColors(),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.rotate(180f)
                        ) {
                            Text(text = i.text!!, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
        IconButton(
            onClick = { extended = !extended },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                Icons.AutoMirrored.TwoTone.List,
                contentDescription = "Display Buttons",
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
