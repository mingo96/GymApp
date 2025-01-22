package com.example.rutinapp.ui.premade

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.expandIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import kotlinx.coroutines.delay
import java.util.Collections.list

@Composable
fun AnimatedItem(enterAnimation : EnterTransition, delay : Long, content: @Composable () -> Unit){

    var isShown by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true){
        delay(delay)
        isShown = true
    }

    AnimatedVisibility(visible = isShown,enter = enterAnimation ) {
        Column {

            content()
        }
    }

}