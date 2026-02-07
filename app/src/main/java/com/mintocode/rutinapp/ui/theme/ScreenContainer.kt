package com.mintocode.rutinapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.automirrored.twotone.List
import androidx.compose.material.icons.twotone.DateRange
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
import androidx.compose.runtime.rememberCoroutineScope
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


/**
 * Shared scaffold container for all screens.
 *
 * Handles the navigation drawer, top bar, optional bottom action button,
 * and content padding including safe-area insets.
 *
 * @param title Title displayed in the top bar
 * @param navController Navigation controller for drawer links
 * @param bottomButtonAction Optional click handler – when non-null a full-width bottom button is shown
 * @param buttonText Label for the bottom button
 * @param floatingActionButton Optional FAB composable
 * @param content Screen content receiving scaffold PaddingValues
 */
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
            LateralMenu(
                navController,
                UserDetails.actualValue?.name.orEmpty().ifEmpty { "Usuario" },
                { scope.launch { drawerState.close() } }
            )
        },
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(0f),
            containerColor = PrimaryColor,
            // Let the TopBar and BottomBar handle their own insets
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = { TopBar({ scope.launch { drawerState.open() } }, title) },
            bottomBar = {
                if (bottomButtonAction != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PrimaryColor)
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Button(
                            onClick = { bottomButtonAction() },
                            colors = rutinAppButtonsColours(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = buttonText,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            },
            floatingActionButton = floatingActionButton,
            content = {
                val padding = PaddingValues(
                    start = it.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                    top = it.calculateTopPadding() + 8.dp,
                    end = it.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
                    bottom = it.calculateBottomPadding()
                )

                Column {
                    content(padding)
                }
            }
        )
    }
}

/**
 * Navigation drawer content with safe-area aware padding.
 */
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
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AdjustableText(
                    userName,
                    TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp),
                    Modifier.fillMaxWidth(0.8f)
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                        "close lateral menu",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SecondaryColor.copy(alpha = 0.3f))
            )

            Spacer(Modifier.height(8.dp))

            // Navigation items
            DrawerNavItem(
                icon = { Icon(Icons.TwoTone.DateRange, contentDescription = "plannings", modifier = Modifier.size(24.dp)) },
                label = "Planificaciones",
                onClick = { navController.navigate("start"); onClose() }
            )
            DrawerNavItem(
                icon = { Icon(painterResource(R.drawable.dumbell), contentDescription = "exercises", modifier = Modifier.size(24.dp)) },
                label = "Ejercicios",
                onClick = { navController.navigate("exercises"); onClose() }
            )
            DrawerNavItem(
                icon = { Icon(Icons.AutoMirrored.TwoTone.List, contentDescription = "routines", modifier = Modifier.size(24.dp)) },
                label = "Rutinas",
                onClick = { navController.navigate("routines"); onClose() }
            )
            DrawerNavItem(
                icon = { Icon(painterResource(R.drawable.dumbell), contentDescription = "workouts", modifier = Modifier.size(24.dp)) },
                label = "Entrenamientos",
                onClick = { navController.navigate("workouts"); onClose() }
            )
            DrawerNavItem(
                icon = { Icon(painterResource(R.drawable.twotone_query_stats_24), contentDescription = "stats", modifier = Modifier.size(24.dp)) },
                label = "Estadísticas",
                onClick = { navController.navigate("stats"); onClose() }
            )
            DrawerNavItem(
                icon = { Icon(Icons.TwoTone.Settings, contentDescription = "settings", modifier = Modifier.size(24.dp)) },
                label = "Configuración",
                onClick = { navController.navigate("settings"); onClose() }
            )
        }
    }
}

/**
 * Single navigation item in the drawer.
 */
@Composable
private fun DrawerNavItem(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColor,
            contentColor = ContentColor
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
        ) {
            icon()
            Text(text = label, fontSize = 15.sp)
        }
    }
}