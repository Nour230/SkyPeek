package com.example.skypeek.composablescreens.fav

import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.skypeek.R
import com.example.skypeek.composablescreens.ScreensRoute
import com.example.skypeek.composablescreens.utiles.LocalNavController

@Composable
fun FavScreen(locationViewModel: MutableState<Location?>) {
    val navController = LocalNavController.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(ScreensRoute.MapScreen.route)
            }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_add_24),
                    contentDescription = "Add"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        FavScreenContent(modifier = Modifier.padding(paddingValues))
    }
}



@Composable
fun FavScreenContent(modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(10) {
                FavItem(
                    modifier = Modifier.padding(16.dp),
                    city = "cairo"
                )
            }
        }
    }
}

@Composable
fun FavItem(modifier: Modifier, city: String) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = city,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { /*TODO*/ },
            ) {
                Text(
                    text = "Delete"
                )
            }
        }
    }
}
