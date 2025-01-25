package com.mintocode.rutinapp.ui.premade

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun AnimatedItem(enterAnimation: EnterTransition, delay: Long, content: @Composable () -> Unit) {

    var isShown by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        delay(delay)
        isShown = true
    }

    AnimatedVisibility(visible = isShown, enter = enterAnimation) {
        Column() {

            content()
        }
    }

}