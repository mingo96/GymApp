package com.mintocode.rutinapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material.icons.twotone.List
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.mintocode.rutinapp.R
import com.mintocode.rutinapp.data.UserDetails
import com.mintocode.rutinapp.ui.premade.AdjustableText
import com.mintocode.rutinapp.ui.screens.TopBar
import kotlinx.coroutines.launch


@Composable
fun ScreenContainer(
    title: String,
    navController: NavHostController,
    bottomButtonAction: (() -> Unit)? = null,
    buttonText: String = "",
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            LateralMenu(navController, UserDetails.actualValue?.name?: "Usuario", {
                scope.launch { drawerState.close() }
            })
        },
    ) {
        Scaffold(modifier = Modifier
            .fillMaxWidth()
            .zIndex(0f),
            containerColor = PrimaryColor,
            topBar = { TopBar({ scope.launch { drawerState.open() } }, title) },
            bottomBar = {
                if (bottomButtonAction != null) Button(
                    onClick = { bottomButtonAction() },
                    colors = rutinAppButtonsColours(),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = buttonText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            floatingActionButton = floatingActionButton,
            content = {
                val padding = PaddingValues(
                    start = it.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                    top = it.calculateTopPadding() + 16.dp,
                    end = it.calculateEndPadding(
                        LocalLayoutDirection.current
                    ) + 16.dp,
                    bottom = it.calculateBottomPadding()
                )

                Column {
                    content(padding)
                }
            })
    }


}

@Composable
fun LateralMenu(
    navController: NavHostController, userName: String, onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight()
                .background(PrimaryColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AdjustableText(userName, TextStyle(fontWeight = FontWeight.Bold, fontSize = 40.sp), Modifier.fillMaxWidth(0.8f))
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.TwoTone.ArrowBack,
                        "close lateral menu",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Button(
                onClick = { navController.navigate("start") },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor, contentColor = ContentColor)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.DateRange,
                        contentDescription = "plannings",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(text = "Planificaciones")
                }
            }
            Button(
                onClick = { navController.navigate("exercises") },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor, contentColor = ContentColor)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.dumbell),
                        contentDescription = "exercises",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(text = "Ejercicios")
                }
            }
            Button(
                onClick = { navController.navigate("routines") },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor, contentColor = ContentColor)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.List,
                        contentDescription = "routines",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(text = "Rutinas")
                }
            }
            Button(
                onClick = { navController.navigate("workouts") },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor, contentColor = ContentColor)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.dumbell),
                        contentDescription = "workouts",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(text = "Entrenamientos")
                }
            }
            Button(
                onClick = { navController.navigate("stats") },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor, contentColor = ContentColor)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.twotone_query_stats_24),
                        contentDescription = "stats",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(text = "Estadísticas")
                }
            }
            Button(
                onClick = { navController.navigate("settings") },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor, contentColor = ContentColor)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Settings,
                        contentDescription = "settings",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(text = "Configuración")
                }
            }

        }
    }


}