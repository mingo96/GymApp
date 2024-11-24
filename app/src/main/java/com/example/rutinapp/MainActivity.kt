package com.example.rutinapp

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rutinapp.newData.models.ExerciseModel
import com.example.rutinapp.newData.models.RoutineModel
import com.example.rutinapp.ui.RutinApp.ExercisesDialog
import com.example.rutinapp.ui.RutinApp.RoutinesDialog
import com.example.rutinapp.ui.theme.RutinAppTheme
import com.example.rutinapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RutinAppApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RutinAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var showing : Boolean? by rememberSaveable { mutableStateOf(null) }
                    if (showing==null){
                        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = {showing = true}) {
                                Text(text = "pestaña de Rutinas")
                            }
                            Button(onClick = {showing = false}) {
                                Text(text = "pestaña de ejercicios")
                            }
                        }
                    }else if (showing as Boolean){
                        RoutinesDialog(viewModel = viewModel, innerPadding = innerPadding) {
                            showing = null
                        }
                    }else{
                        ExercisesDialog(viewModel) {
                            showing = null
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RutinAppTheme {
        Greeting("Android")
    }
}