package com.example.rutinapp.ui.premade

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.rutinapp.ui.theme.ContentColor
import com.example.rutinapp.ui.theme.SecondaryColor
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Line

@Composable
fun RutinAppLineChart(value: List<Double>) {

    LineChart(modifier = Modifier
        .height(200.dp)
        .padding(8.dp),
        labelHelperProperties = LabelHelperProperties(enabled = false),
        gridProperties = GridProperties(enabled = false),
        indicatorProperties = HorizontalIndicatorProperties(textStyle = TextStyle(color = ContentColor)),
        dotsProperties = DotProperties(color = SolidColor(SecondaryColor), enabled = true),
        data = remember {
            listOf(
                Line(
                    label = "Pesos utlizados",
                    values = value,
                    color = SolidColor(SecondaryColor),
                    firstGradientFillColor = SecondaryColor.copy(alpha = .5f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                    drawStyle = DrawStyle.Stroke(width = 2.dp),

                    )
            )
        })
}