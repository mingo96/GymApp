package com.mintocode.rutinapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.ArrowForward
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material.icons.twotone.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.ui.premade.AdjustableText
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.premade.RutinAppCalendar
import com.mintocode.rutinapp.ui.screenStates.FieldBeingEdited
import com.mintocode.rutinapp.ui.screenStates.MainScreenState
import com.mintocode.rutinapp.ui.theme.PrimaryColor
import com.mintocode.rutinapp.ui.theme.ScreenContainer
import com.mintocode.rutinapp.ui.theme.TextFieldColor
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.ui.theme.rutinAppDatePickerColors
import com.mintocode.rutinapp.ui.theme.rutinAppTextButtonColors
import com.mintocode.rutinapp.ui.uiClasses.FABButton
import com.mintocode.rutinapp.utils.simpleDateString
import com.mintocode.rutinapp.viewmodels.MainScreenViewModel
import kotlinx.coroutines.delay
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController, mainScreenViewModel: MainScreenViewModel
) {

    val plannings by mainScreenViewModel.plannings.observeAsState(emptyList())

    val uiState by mainScreenViewModel.uiState.observeAsState(MainScreenState.Observation)

    val todaysPlanning by mainScreenViewModel.todaysPlanning.observeAsState()

    when (uiState) {
        MainScreenState.Observation -> {

        }

        is MainScreenState.PlanningOnMainFocus -> {
            PlanningEditionDialog(
                mainScreenViewModel, uiState as MainScreenState.PlanningOnMainFocus
            )
        }
    }

    ScreenContainer(title = "Menú principal", buttonText = "", floatingActionButton = {
        FABComposable(listOf(FABButton("Ejercicios") {
            navController.navigate("exercises")
        }, FABButton("Rutinas") {
            navController.navigate("routines")
        }, FABButton("Entrenamientos") {
            navController.navigate("workouts")
        }, FABButton("Estadísticas") {
            navController.navigate("stats")
        }, FABButton("Configuración") {
            navController.navigate("settings")
        }))
    }) {
        Column(Modifier.padding(it), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            AdjustableText("Plan de hoy " + Date().simpleDateString(), TextStyle(fontSize = 30.sp))
            if (todaysPlanning != null) {
                Row(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(TextFieldColor, TextFieldColor, PrimaryColor)
                            )
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    val content =
                        if (todaysPlanning!!.statedBodyPart != null) todaysPlanning!!.statedBodyPart
                        else if (todaysPlanning!!.statedRoutine != null) todaysPlanning!!.statedRoutine!!.name
                        else "Nada planeado"
                    AdjustableText("Objetivo : $content", TextStyle(fontSize = 20.sp))

                    IconButton(
                        onClick = { mainScreenViewModel.planningClicked(todaysPlanning!!) },
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = if (content == null) Icons.TwoTone.Add else Icons.TwoTone.Edit,
                            contentDescription = if (content == null) "add planning" else "edit planning"
                        )
                    }
                }
            }

            val dateRangePickerState = rememberDateRangePickerState()

            var isExtended by rememberSaveable {
                mutableStateOf(false)
            }

            DateRangePicker(
                dateRangePickerState,
                title = null,
                headline = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            AdjustableText(
                                "Rango de fechas",
                                TextStyle(fontSize = 20.sp),
                                modifier = Modifier.padding(16.dp)
                            )
                            if (isExtended) IconButton(onClick = {
                                mainScreenViewModel.changeDates(
                                    dateRangePickerState.selectedStartDateMillis!!,
                                    dateRangePickerState.selectedEndDateMillis!!
                                )
                                isExtended = false
                            }) {
                                Icon(
                                    Icons.TwoTone.CheckCircle, contentDescription = "edit dates"
                                )
                            }
                        }
                        IconButton(onClick = { isExtended = !isExtended }) {
                            Icon(
                                if (!isExtended) Icons.TwoTone.KeyboardArrowDown else Icons.TwoTone.KeyboardArrowUp,
                                contentDescription = "edit dates"
                            )
                        }
                    }
                },
                colors = rutinAppDatePickerColors(),
                modifier = Modifier
                    .heightIn(0.dp, if (isExtended) 300.dp else 60.dp)
                    .animateContentSize(),
                showModeToggle = false,
            )


            RutinAppCalendar(plannings) {
                mainScreenViewModel.planningClicked(it)
            }
        }
    }

}

@Composable
fun FABComposable(buttons: List<FABButton>) {

    var extended by rememberSaveable {
        mutableStateOf(false)
    }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (extended) {

            var maxIndex by rememberSaveable {
                mutableIntStateOf(0)
            }
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
                            border = ButtonDefaults.outlinedButtonBorder,
                            shape = RoundedCornerShape(15.dp),
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
            colors = IconButtonDefaults.iconButtonColors(containerColor = PrimaryColor),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                Icons.TwoTone.List,
                contentDescription = "Display Buttons",
                modifier = Modifier.size(50.dp)
            )
        }
    }

}

@Composable
fun PlanningEditionDialog(
    viewModel: MainScreenViewModel, uistate: MainScreenState.PlanningOnMainFocus
) {

    val context = LocalContext.current

    Dialog(onDismissRequest = { viewModel.backToObservation() }) {
        DialogContainer() {
            Text(
                text = "Objetivo el " + uistate.planningModel.date.simpleDateString(),
                fontSize = 25.sp
            )

            when (uistate.fieldBeingEdited) {
                FieldBeingEdited.NONE -> {
                    NoFieldSelectedContent(viewModel)
                }

                FieldBeingEdited.BODYPART -> {
                    BodyPartSelectedContent(onSend = { viewModel.saveBodypart(it, context) }) {
                        viewModel.backToSelection()
                    }
                }

                FieldBeingEdited.ROUTINE -> {
                    RoutineSelectedContent(uistate = uistate,
                        onSelect = { viewModel.saveRoutine(it) },
                        onBack = {
                            viewModel.backToSelection()
                        })
                }
            }

        }
    }
}

@Composable
fun NoFieldSelectedContent(viewModel: MainScreenViewModel) {

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        IconButton(
            onClick = { viewModel.selectBodypartClicked() }, modifier = Modifier.size(80.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.body),
                contentDescription = "select body part",
                modifier = Modifier.size(200.dp)
            )
        }
        IconButton(
            onClick = { viewModel.selectRoutineClicked() }, modifier = Modifier.size(80.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.list),
                contentDescription = "select routine",
                modifier = Modifier.size(200.dp)
            )
        }
    }
    Button(
        onClick = { viewModel.backToObservation() },
        colors = rutinAppButtonsColours(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Salir")
    }
}

@Composable
fun BodyPartSelectedContent(onSend: (String) -> Unit, onExit: () -> Unit) {

    var bodyPart by rememberSaveable {
        mutableStateOf("")
    }
    TextFieldWithTitle(title = "Parte del cuerpo",
        text = bodyPart,
        onWrite = { bodyPart = it },
        sendFunction = {
            onSend(bodyPart)
        })

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

        Button(
            onClick = { onSend(bodyPart) }, colors = rutinAppButtonsColours()
        ) {
            Text(text = "Guardar")
        }
        Button(
            onClick = { onExit() }, colors = rutinAppButtonsColours()
        ) {
            Text(text = "Volver")
        }
    }
}

@Composable
fun RoutineSelectedContent(
    uistate: MainScreenState.PlanningOnMainFocus,
    onSelect: (RoutineModel) -> Unit,
    onBack: () -> Unit
) {

    Text(text = "Rutinas disponibles", fontSize = 25.sp)

    LazyColumn(
        Modifier
            .background(TextFieldColor, RoundedCornerShape(15.dp))
            .heightIn(0.dp, 300.dp)
    ) {
        if (uistate.availableRoutines.isEmpty()) item {
            Text(
                text = "No hay rutinas disponibles",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp),
                color = Color.Red
            )
        }
        items(uistate.availableRoutines) {
            Column(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                var isOpened by rememberSaveable {
                    mutableStateOf(false)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = it.name, fontSize = 20.sp, modifier = Modifier.fillMaxWidth(0.7f))
                    Row {
                        IconButton(onClick = { isOpened = !isOpened }) {
                            Icon(
                                imageVector = if (isOpened) Icons.TwoTone.KeyboardArrowDown else Icons.TwoTone.KeyboardArrowUp,
                                contentDescription = "openclose ejercises"
                            )
                        }
                        IconButton(onClick = { onSelect(it) }) {
                            Icon(
                                imageVector = Icons.TwoTone.ArrowForward,
                                contentDescription = "select routine"
                            )
                        }
                    }
                }
                if (isOpened) {
                    Column {
                        for (i in it.exercises) {
                            Text(text = i.name, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
    Button(
        onClick = { onBack() },
        colors = rutinAppButtonsColours(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Volver")
    }

}