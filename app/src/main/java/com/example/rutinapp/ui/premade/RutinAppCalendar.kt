package com.example.rutinapp.ui.premade

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rutinapp.ui.screens.dateString
import com.example.rutinapp.ui.theme.SecondaryColor
import java.util.Date

@Composable
fun RutinAppCalendar(
    listOfDates: List<Date> = listOf(), onDateSelected: (Date) -> Unit = {}
) {

    Column(Modifier.fillMaxSize()) {
        Text(text = "Entrenamientos próximos")

        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            modifier = Modifier.heightIn(0.dp, 500.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listOfDates) {
                DateItem(
                    date = it.dateString().substring(0, 5),
                    isToday = it == Date(125, 0, 9)
                )
            }
        }
    }

}

@Composable
fun DateItem(modifier: Modifier = Modifier,isToday: Boolean = false, date: String ) {
    Box(
        if (isToday) modifier.border(
            2.dp, SecondaryColor, RoundedCornerShape(15.dp)
        ) else modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(text = date)
            Text(text = "Día vacío")
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.TwoTone.Add, contentDescription = "Add workout")
            }
        }
    }
}

