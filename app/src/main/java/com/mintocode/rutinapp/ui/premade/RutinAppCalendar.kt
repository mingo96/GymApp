package com.mintocode.rutinapp.ui.premade

import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mintocode.rutinapp.data.models.CalendarPhaseModel
import com.mintocode.rutinapp.data.models.PlanningModel
import com.mintocode.rutinapp.ui.theme.PrimaryColor
import com.mintocode.rutinapp.ui.theme.SecondaryColor
import com.mintocode.rutinapp.ui.theme.TextFieldColor
import com.mintocode.rutinapp.utils.dayAndMonthString
import com.mintocode.rutinapp.utils.toSimpleDate
import kotlinx.coroutines.delay
import java.util.Date

@Composable
fun RutinAppCalendar(
    listOfDates: List<PlanningModel> = listOf(),
    calendarPhases: List<CalendarPhaseModel> = listOf(),
    onPlanningSelected: (PlanningModel) -> Unit = {}
) {

    var maxIndex by rememberSaveable { mutableIntStateOf(0) }

    val textContent =
        if (listOfDates.isEmpty()) "Calendario" else "Calendario " + listOfDates.first().date.dayAndMonthString() + " - " + listOfDates.last().date.dayAndMonthString()

    LaunchedEffect(listOfDates) {
        while (true) {
            delay(100)
            if (maxIndex < listOfDates.size) maxIndex++
        }
    }

    Column(
        Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AdjustableText(text = textContent, TextStyle(fontSize = 30.sp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listOfDates.take(maxIndex)) { planning ->

                // Find calendar phases active on this date
                val dateMs = planning.date.toSimpleDate().time
                val activePhasesForDate = calendarPhases.filter { phase ->
                    phase.startDate.time <= dateMs && phase.endDate.time >= dateMs
                }

                AnimatedItem(enterAnimation = slideInVertically(), delay = 100) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        // Phase indicators above the planning row
                        if (activePhasesForDate.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(start = 60.dp)
                            ) {
                                activePhasesForDate.forEach { phase ->
                                    PhaseIndicator(phase = phase)
                                }
                            }
                        }

                        DateVerticalItem(planning = planning) {
                            onPlanningSelected(planning)
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.padding(bottom = 64.dp))
            }

        }

    }

}

/**
 * Small colored pill showing a calendar phase name.
 *
 * @param phase The calendar phase to display
 */
@Composable
fun PhaseIndicator(phase: CalendarPhaseModel) {
    val phaseColor = try {
        Color(android.graphics.Color.parseColor(phase.color))
    } catch (_: Exception) {
        SecondaryColor
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(phaseColor.copy(alpha = 0.3f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = phase.name,
            fontSize = 10.sp,
            color = phaseColor,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.heightIn(0.dp, 50.dp)
    ) {
        Text(
            planning.date.dayAndMonthString(),
            fontSize = 20.sp,
            modifier = Modifier.padding(end = 8.dp)
        )

        Spacer(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(SecondaryColor)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            TextFieldColor, TextFieldColor, PrimaryColor
                        )
                    )
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Trainer badge when planning was created by a trainer
                if (planning.isFromTrainer) {
                    Icon(
                        imageVector = Icons.TwoTone.Person,
                        contentDescription = "Creado por entrenador",
                        tint = SecondaryColor,
                        modifier = Modifier.height(16.dp)
                    )
                }
                Text(
                    text = content ?: "Día no planeado", fontSize = 20.sp, modifier = Modifier
                )
            }
            if (planning.date.time >= Date().toSimpleDate().time)
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