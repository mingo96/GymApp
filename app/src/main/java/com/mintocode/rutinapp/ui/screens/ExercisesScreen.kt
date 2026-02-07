package com.mintocode.rutinapp.ui.screens

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.ui.premade.AnimatedItem
import com.mintocode.rutinapp.ui.screenStates.ExercisesState
import com.mintocode.rutinapp.ui.theme.PrimaryColor
import com.mintocode.rutinapp.ui.theme.ScreenContainer
import com.mintocode.rutinapp.ui.theme.SecondaryColor
import com.mintocode.rutinapp.ui.theme.TextFieldColor
import com.mintocode.rutinapp.ui.theme.rutinAppButtonsColours
import com.mintocode.rutinapp.ui.theme.rutinAppTextFieldColors
import com.mintocode.rutinapp.viewmodels.ExercisesViewModel
import kotlinx.coroutines.delay


@Composable
fun ExercisesScreen(viewModel: ExercisesViewModel, navController: NavHostController) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val exercises by viewModel.exercisesState.collectAsStateWithLifecycle(
        initialValue = emptyList(), lifecycle = lifecycle
    )

    val uiState by viewModel.uiState.observeAsState()

    var maxIndex by rememberSaveable { mutableIntStateOf(0) }

    var stateOfSearch: Boolean? by rememberSaveable { mutableStateOf(null) }

    when (uiState) {
        is ExercisesState.Modifying -> {
            ModifyExerciseDialog(viewModel, uiState as ExercisesState.Modifying)
        }

        is ExercisesState.Creating -> {
            CreateExerciseDialog(viewModel = viewModel)
        }

        is ExercisesState.Observe -> {
            stateOfSearch = null
            if ((uiState as ExercisesState.Observe).exercise != null) {
                ObserveExerciseDialog(viewModel, uiState as ExercisesState.Observe)
            }
        }

        is ExercisesState.AddingRelations -> {
            AddRelationsDialog(viewModel, uiState as ExercisesState.AddingRelations)
        }

        is ExercisesState.SearchingForExercise -> {
            stateOfSearch = false
        }

        null -> {}
        is ExercisesState.ExploringExercises -> {
            stateOfSearch = true
        }
    }

    ScreenContainer(buttonText = "Crear un ejercicio",
        title = "Ejercicios",
        navController = navController,
        bottomButtonAction = { viewModel.clickToCreate() }) { it ->

        var name by rememberSaveable { mutableStateOf("") }

    // Observed flags (hoisted so they are visible across the whole content lambda)
    val showOthers by viewModel.showOthers.observeAsState(false)
    val loading by viewModel.isLoading.observeAsState(false)

        SearchTextField(value = name,
            onValueChange = { name = it },
            onSearch = { viewModel.writeOnExerciseName(name) },
            modifier = Modifier.padding(top = it.calculateTopPadding())
        )
    Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current

            // Toggle: Mis ejercicios / De otros
            FilterChip(
                selected = !showOthers,
                onClick = { viewModel.showMine(context) },
                label = { Text("Mis", fontSize = 13.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SecondaryColor,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = showOthers,
                onClick = { viewModel.showOthers(context) },
                label = { Text("De otros", fontSize = 13.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SecondaryColor,
                    selectedLabelColor = Color.White
                )
            )

            Spacer(Modifier.weight(1f))

            // Explore server exercises
            IconButton(onClick = { viewModel.changeToUploadedExercises() },
                modifier = Modifier.size(36.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (stateOfSearch == true) SecondaryColor.copy(alpha = 0.8f) else Color.Transparent
                )) {
                Icon(
                    painter = painterResource(R.drawable.hente),
                    contentDescription = "Explorar ejercicios",
                    modifier = Modifier.size(20.dp)
                )
            }
            // Sync from server
            IconButton(onClick = { viewModel.syncExercises(context = context) },
                modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.TwoTone.Refresh,
                    contentDescription = "Sincronizar ejercicios",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        val items = when (uiState) {
            is ExercisesState.SearchingForExercise -> {
                (uiState as ExercisesState.SearchingForExercise).possibleValues.take(maxIndex)
            }

            is ExercisesState.ExploringExercises -> {
                (uiState as ExercisesState.ExploringExercises).possibleValues.take(maxIndex)
            }

            else -> {
                val showOthers = viewModel.showOthers.observeAsState(false).value
                val filtered = if (showOthers) exercises.filter { !it.isFromThisUser } else exercises.filter { it.isFromThisUser }
                filtered.take(maxIndex)
            }
        }

        LaunchedEffect(maxIndex) {
            while (true) {
                delay(100)
                if (maxIndex < when (stateOfSearch) {
                        false -> (uiState as ExercisesState.SearchingForExercise).possibleValues.size
                        true -> (uiState as ExercisesState.ExploringExercises).possibleValues.size
                        else -> exercises.size
                    }
                ) maxIndex++
            }
        }

        if (loading) {
            Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.material3.CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text(text = "Cargando...")
            }
        }

        val isEmptyList = items.isEmpty() && !loading
        if (isEmptyList) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = if (showOthers) "No hay ejercicios de otros usuarios" else "No tienes ejercicios aún")
            }
        }

        LazyColumn(
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = it.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items) { exercise ->
                AnimatedItem(enterAnimation = slideInHorizontally { +it }, delay = 50) {

                    ExerciseItem(item = exercise,
                        onEditClick = { viewModel.clickToEdit(exercise) },
                        onClick = { viewModel.clickToObserve(exercise) })
                }
            }
        }
    }

}

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(value = value,
        textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
        onValueChange = onValueChange,
        colors = rutinAppTextFieldColors(),
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrectEnabled = true,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        trailingIcon = {
            IconButton(onClick = { onSearch() }) {
                Icon(imageVector = Icons.TwoTone.Search, contentDescription = "Delete")
            }
        })
}

@Composable
fun AddRelationsDialog(
    viewModel: ExercisesViewModel, addingRelations: ExercisesState.AddingRelations
) {

    Dialog(onDismissRequest = { viewModel.backToObserve() }) {

        DialogContainer {
            Text(
                text = "Añadir ejercicios relacionados",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(100.dp),
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .fillMaxWidth()
                    .background(TextFieldColor, RoundedCornerShape(15.dp))
                    .padding(16.dp)
            ) {
                if (addingRelations.possibleValues.isNotEmpty()) {
                    items(addingRelations.possibleValues) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.name,
                                maxLines = 3,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(0.6f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            IconButton(onClick = {
                                viewModel.toggleExercisesRelation(it)
                            }) {
                                Icon(
                                    imageVector = Icons.TwoTone.Add,
                                    contentDescription = "Add exercise relation"
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Text(text = "No hay ejercicios disponibles")
                    }
                }
            }

            Button(
                onClick = { viewModel.clickToEdit(addingRelations.exerciseModel) },
                colors = rutinAppButtonsColours(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Volver")
            }
        }

    }

}

@Composable
fun ExerciseItem(item: ExerciseModel, onEditClick: () -> Unit, onClick: () -> Unit = {}) {

    Row(
        Modifier
            .fillMaxWidth()
            .background(TextFieldColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1
            )
            if (item.description.isNotBlank()) {
                Text(
                    text = item.description.take(50),
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }
        }
        if (item.isFromThisUser) Icon(imageVector = Icons.TwoTone.Edit,
            contentDescription = "editar",
            modifier = Modifier
                .size(22.dp)
                .clickable { onEditClick() })
    }
}

@Composable
fun TopBar(onExpandPressed: (() -> Unit)?, text: String) {
    Column(
        modifier = Modifier
            .background(PrimaryColor)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .wrapContentHeight()
        ) {
            if (onExpandPressed != null) Icon(imageVector = Icons.Outlined.Menu,
                contentDescription = "expand menu",
                Modifier
                    .clickable { onExpandPressed() }
                    .size(28.dp))
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                style = TextStyle(
                    fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center
                )
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(SecondaryColor.copy(alpha = 0.5f))
        )
    }
}

@Composable
fun TextFieldWithTitle(
    title: String,
    onWrite: (String) -> Unit = {},
    text: String,
    editing: Boolean = true,
    typeOfKeyBoard: KeyboardType = KeyboardType.Text,
    sendFunction: (() -> Unit)? = null
) {

    val focusManager = LocalFocusManager.current

    Text(text = title)
    TextField(
        value = text,
        enabled = editing,
        onValueChange = onWrite,
        colors = rutinAppTextFieldColors(),
        textStyle = TextStyle(fontWeight = FontWeight.Bold),
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            imeAction = if (sendFunction == null) ImeAction.Next else ImeAction.Done,
            keyboardType = typeOfKeyBoard,
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrectEnabled = true
        ),
        visualTransformation = if (typeOfKeyBoard == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardActions = KeyboardActions(onDone = {
            if (sendFunction != null) sendFunction() else focusManager.moveFocus(
                FocusDirection.Down
            )
        }),
    )
}

@Composable
fun DialogContainer(backGroundIsOn: Boolean = true, content: @Composable ColumnScope.() -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (backGroundIsOn) PrimaryColor else Color.Transparent, RoundedCornerShape(15.dp)
            )
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(
            16.dp, Alignment.CenterVertically
        ), horizontalAlignment = Alignment.Start, content = content
    )
}

@Composable
fun ModifyExerciseDialog(viewModel: ExercisesViewModel, uiState: ExercisesState.Modifying) {

    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(uiState.exerciseModel.name) }
    var description by rememberSaveable { mutableStateOf(uiState.exerciseModel.description) }
    var targetedBodyPart by rememberSaveable { mutableStateOf(uiState.exerciseModel.targetedBodyPart) }
    Dialog(onDismissRequest = { viewModel.backToObserve() }) {

        DialogContainer {

            TextFieldWithTitle(title = "Nombre",
                onWrite = { name = it },
                text = name,
                sendFunction = {
                    viewModel.updateExercise(
                        name, description, targetedBodyPart, context
                    )
                })
            TextFieldWithTitle(title = "Descripción",
                onWrite = { description = it },
                text = description,
                sendFunction = {
                    viewModel.updateExercise(
                        name, description, targetedBodyPart, context
                    )
                })
            TextFieldWithTitle(title = "Parte del cuerpo",
                onWrite = { targetedBodyPart = it },
                text = targetedBodyPart,
                sendFunction = {
                    viewModel.updateExercise(
                        name, description, targetedBodyPart, context
                    )
                })


            Text(text = "Ejercicios relacionados")
            LazyVerticalGrid(
                columns = GridCells.Adaptive(100.dp),
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .fillMaxWidth()
                    .background(TextFieldColor, RoundedCornerShape(15.dp))
                    .padding(16.dp)
            ) {
                item {
                    IconButton(onClick = { viewModel.clickToAddRelatedExercises(context) }) {
                        Icon(
                            imageVector = Icons.TwoTone.Add,
                            contentDescription = "Add related exercises"
                        )
                    }
                }
                items(uiState.relatedExercises) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .animateItem()
                    ) {
                        Text(
                            text = it.name,
                            Modifier
                                .fillMaxWidth(0.8f)
                                .clickable { viewModel.clickToObserve(it) },
                            maxLines = 2
                        )
                        IconButton(onClick = { viewModel.toggleExercisesRelation(it) }) {
                            Icon(
                                imageVector = Icons.TwoTone.Delete, contentDescription = "Unrelate"
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    viewModel.updateExercise(
                        name, description, targetedBodyPart, context
                    )
                }, colors = rutinAppButtonsColours()) {
                    Text(text = "Guardar")
                }
                Button(
                    onClick = { viewModel.backToObserve() }, colors = rutinAppButtonsColours()
                ) {
                    Text(text = "Salir")
                }
            }

        }

    }
}

@Composable
fun CreateExerciseDialog(viewModel: ExercisesViewModel, onExit: (() -> Unit)? = null) {

    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var targetedBodyPart by rememberSaveable { mutableStateOf("") }
    Dialog(onDismissRequest = { viewModel.backToObserve() }) {
        DialogContainer {

            TextFieldWithTitle(title = "Nombre", onWrite = { name = it }, text = name)
            TextFieldWithTitle(
                title = "Descripción", onWrite = { description = it }, text = description
            )
            TextFieldWithTitle(title = "Parte del cuerpo",
                onWrite = { targetedBodyPart = it },
                text = targetedBodyPart,
                sendFunction = {
                    viewModel.addExercise(
                        name, description, targetedBodyPart, context
                    )
                })
            Button(
                onClick = {
                    viewModel.addExercise(
                        name, description, targetedBodyPart, context
                    )
                    if (onExit != null) onExit()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.CenterHorizontally),
                colors = rutinAppButtonsColours()
            ) {
                Text(text = "Añadir ejercicio")
            }

        }
    }
}

@Composable
fun ObserveExerciseDialog(viewModel: ExercisesViewModel, uiState: ExercisesState.Observe) {
    Dialog(onDismissRequest = { viewModel.backToObserve() }) {

        DialogContainer {

            TextFieldWithTitle(title = "Nombre", text = uiState.exercise!!.name, editing = false)
            TextFieldWithTitle(
                title = "Descripción", text = uiState.exercise.description, editing = false
            )
            TextFieldWithTitle(
                title = "Parte del cuerpo",
                text = uiState.exercise.targetedBodyPart,
                editing = false
            )
            if (uiState.exercise.equivalentExercises.isNotEmpty()) {
                Text(text = "Ejercicios relacionados")
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .background(TextFieldColor, RoundedCornerShape(15.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(uiState.exercise.equivalentExercises) {
                        Text(text = it.name,
                            maxLines = 2,
                            modifier = Modifier.clickable { viewModel.clickToObserve(it) })
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.backToObserve() }, colors = rutinAppButtonsColours()
                ) {
                    Text(text = "Salir")
                }
                if (uiState.exercise.realId == 0L) {
                    Button(
                        onClick = { viewModel.uploadExercise(uiState.exercise) },
                        colors = rutinAppButtonsColours()
                    ) {
                        Text(text = "Subir")
                    }
                }
                if (uiState.exercise.isFromThisUser) {
                    Button(
                        onClick = { viewModel.clickToEdit(uiState.exercise) },
                        colors = rutinAppButtonsColours()
                    ) {
                        Text(text = "Editar")
                    }
                }else{
                    if(uiState.exercise.id == "0") {
                        Button(
                            onClick = { viewModel.saveExercise(uiState.exercise) },
                            colors = rutinAppButtonsColours()
                        ) {
                            Text(text = "Obtener")
                        }
                    }
                }
            }

        }

    }
}