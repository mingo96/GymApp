package com.example.rutinapp.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.rutinapp.ui.screens.TopBar


@Composable
fun ScreenContainer(
    title: String,
    navController: NavHostController,
    bottomButtonAction: () -> Unit,
    buttonText: String,
    content: @Composable (PaddingValues) -> Unit
) {

    Scaffold(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = PrimaryColor,
        topBar = { TopBar(navController = navController, title) },
        bottomBar = {
            Button(
                onClick = { bottomButtonAction() }, colors = rutinAppButtonsColours(), modifier = Modifier.padding(16.dp)
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
        content = {
            val padding = PaddingValues(start = it.calculateStartPadding(LocalLayoutDirection.current)+16.dp, top = it.calculateTopPadding()+16.dp, end = it.calculateEndPadding(
                LocalLayoutDirection.current)+16.dp, bottom = it.calculateBottomPadding()+16.dp)

            content(padding)
        }
    )

}