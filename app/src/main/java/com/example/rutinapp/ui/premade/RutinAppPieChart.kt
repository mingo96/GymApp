package com.example.rutinapp.ui.premade

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kotlin.random.Random

@Composable
fun RutinAppPieChart(values: List<Pair<String, Double>>) {

    var data by remember {
        mutableStateOf(values.map {
            Pie(
                label = it.first, it.second, Color(
                    Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)
                )
            )
        })
    }

    LaunchedEffect(key1 = values) {
        data = values.map {
            Pie(
                label = it.first, it.second, Color(
                    Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)
                )
            )
        }
    }

    var selectedDay by rememberSaveable {
        mutableStateOf(Triple<String?, Double, Color>(null, 0.0, Color.Unspecified))
    }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        PieChart(
            modifier = Modifier
                .size(200.dp)
                .padding(8.dp),
            data = data,
            onPieClick = {
                selectedDay = Triple(it.label!!, it.data, it.color)
                val pieIndex = data.indexOf(it)
                data =
                    data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
            },
            selectedScale = 1.2f,
            scaleAnimEnterSpec = spring<Float>(
                dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
            ),
            colorAnimEnterSpec = tween(300),
            colorAnimExitSpec = tween(300),
            scaleAnimExitSpec = tween(300),
            spaceDegreeAnimExitSpec = tween(300),
            style = Pie.Style.Fill
        )
        if (selectedDay.first != null) Text(
            text = selectedDay.first!! + " : " + selectedDay.second.toInt()
                .toString() + "%", color = selectedDay.third
        )
    }
}