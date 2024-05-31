package com.example.slore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size // Correct import here
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun OpenTimelineScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF103A5E), // Set the background color for the card
            ),
            border = BorderStroke(10.dp, Color.White),
            modifier = Modifier.size(width = 300.dp, height = 140.dp)
        ) {
            Text(
                text = "Currently, we are working on this section till then hold tight on your seats",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                color = Color.White, // Set the text color to white
                fontSize = 20.sp // Increase the text size
            )
        }
    }
}