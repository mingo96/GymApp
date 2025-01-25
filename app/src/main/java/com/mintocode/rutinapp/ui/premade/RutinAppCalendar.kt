package com.mintocode.rutinapp.ui.premade

import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.ui.theme.PrimaryColor
import com.mintocode.rutinapp.ui.theme.SecondaryColor
import com.mintocode.rutinapp.ui.theme.TextFieldColor
import com.mintocode.rutinapp.utils.dayAndMonthString
import com.mintocode.rutinapp.utils.dayOfWeekString
import com.mintocode.rutinapp.utils.simpleDateString
import com.mintocode.rutinapp.utils.toSimpleDate
import kotlinx.coroutines.delay
import java.util.Date

@Composable
fun RutinAppCalendar(
    listOfDates: List<PlanningModel> = listOf(), onPlanningSelected: (PlanningModel) -> Unit = {}
) {

    var maxIndex by rememberSaveable { mutableIntStateOf(0) }

    val textContent =
        if (listOfDates.isEmpty()) "Calendario" else "Calendario " + listOfDates.first().date.dayAndMonthString() + " - " + listOfDates.last().date.dayAndMonthString()

    LaunchedEffect(listOfDates) {
        while (maxIndex < 14) {
            delay(100)
            maxIndex++
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AdjustableText(text = textContent, TextStyle(fontSize = 30.sp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listOfDates.take(maxIndex)) {

                AnimatedItem(enterAnimation = slideInVertically(), delay = 100) {

                    DateVerticalItem(planning = it) {
                        onPlanningSelected(it)
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.padding(bottom = 40.dp))
            }

        }

    }

}

@Composable
fun DateVerticalItem(
    modifier: Modifier = Modifier, planning: PlanningModel, onPlanningSelected: () -> Unit = {}
) {

    val content = if (planning.statedBodyPart != null) planning.statedBodyPart
    else if (planning.statedRoutine != null) planning.statedRoutine!!.name
    else null

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.heightIn(0.dp, 50.dp)
    ) {
        Text(planning.date.dayAndMonthString(), fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))

        Spacer(modifier = Modifier.width(2.dp).fillMaxHeight().background(SecondaryColor))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.horizontalGradient(listOf(TextFieldColor, TextFieldColor, PrimaryColor)))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = content ?: "Día no planeado", fontSize = 20.sp, modifier = Modifier
            )
            IconButton(onClick = { onPlanningSelected() }, modifier = Modifier) {
                Icon(
                    imageVector = if (content == null) Icons.TwoTone.Add else Icons.TwoTone.Edit,
                    contentDescription = if (content == null) "add planning" else "edit planning"
                )
            }
        }
    }

}

fun String.replacePositionWithNewLine(position: Int): String {

    val line1 = this.substring(0, position - 1)

    val line2 = this.substring(position)

    return line1 + "\n" + line2

}