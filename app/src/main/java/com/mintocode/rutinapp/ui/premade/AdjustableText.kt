package com.mintocode.rutinapp.ui.premade

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.isUnspecified
import com.mintocode.rutinapp.utils.MainScreenStyle

@Composable
fun AdjustableText(
    text: String,
    style: TextStyle = LocalTextStyle.current,
    modifier: Modifier = Modifier,
    color: Color = style.color
) {
    var shouldDraw by remember {
        mutableStateOf(false)
    }

    val defaultFontSize = LocalTextStyle.current.fontSize

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
            }
        },
        softWrap = false,
        style = MainScreenStyle,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                if (style.fontSize.isUnspecified) {
                    MainScreenStyle = MainScreenStyle.copy(
                        fontSize = defaultFontSize
                    )
                }
                MainScreenStyle = MainScreenStyle.copy(
                    fontSize = MainScreenStyle.fontSize * 0.95
                )
            } else {
                shouldDraw = true
            }
        }
    )
}