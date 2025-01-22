package com.example.rutinapp.ui.premade

import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutinapp.data.models.PlanningModel
import com.example.rutinapp.ui.theme.PrimaryColor
import com.example.rutinapp.ui.theme.TextFieldColor
import com.example.rutinapp.utils.dayOfWeekString
import com.example.rutinapp.utils.simpleDateString
import com.example.rutinapp.utils.toSimpleDate
import kotlinx.coroutines.delay
import java.util.Date

@Composable
fun RutinAppCalendar(
    listOfDates: List<PlanningModel> = listOf(), onPlanningSelected: (PlanningModel) -> Unit = {}
) {

    var maxIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(listOfDates) {
        while (maxIndex < 7) {
            delay(100)
            maxIndex++
        }
    }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "Calendario", fontSize = 25.sp)

        LazyRow(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .background(TextFieldColor, RoundedCornerShape(15.dp))
        ) {
            items(listOfDates.indices.take(maxIndex)) {

                AnimatedItem(enterAnimation = slideInVertically(), delay = 100) {

                    Column(
                        Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val upperPlanning = listOfDates[it]
                        val lowerPlanning = listOfDates[it + 7]
                        DateItem(
                            planning = upperPlanning,
                        ) {
                            onPlanningSelected(upperPlanning)
                        }
                        DateItem(
                            planning = lowerPlanning,
                        ) {
                            onPlanningSelected(lowerPlanning)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun DateItem(
    modifier: Modifier = Modifier, planning: PlanningModel, onPlanningSelected: () -> Unit = {}
) {
    val content = if (planning.statedBodyPart != null) planning.statedBodyPart
    else if (planning.statedRoutine != null) planning.statedRoutine!!.name
    else null

    Box(
        if (planning.date == Date().toSimpleDate()) modifier.background(
            PrimaryColor, RoundedCornerShape(15.dp)
        ) else modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var dateValue by rememberSaveable {
                mutableStateOf(planning.date.simpleDateString() to true)
            }

            Text(text = dateValue.first, fontSize = 20.sp, modifier = Modifier.clickable {
                dateValue = if (dateValue.second) planning.date.dayOfWeekString() to false
                else planning.date.simpleDateString() to true
            })

            Text(text = content ?: "Día no planeado", fontSize = 20.sp)
            IconButton(onClick = { onPlanningSelected() }) {
                Icon(
                    imageVector = if (content == null) Icons.TwoTone.Add else Icons.TwoTone.Edit,
                    contentDescription = if (content == null) "add planning" else "edit planning"
                )
            }
        }
    }
}

