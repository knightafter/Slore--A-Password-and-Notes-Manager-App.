package com.example.slore

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun MainContent(navController: NavHostController) {
    val showDialog = remember { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Profile icon in the top-right corner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Icon",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { navController.navigate("home") }
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF000080), // Navy blue color
                ),
                modifier = Modifier
                    .size(width = 120.dp, height = 130.dp)
                    .offset(y = 35.dp)
                    .padding(bottom = 16.dp)
                    .clickable { showDialog.value = true } // Show dialog when card is clicked
            ) {
                Text(
                    text = "+",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                        .offset(y = -10.dp),
                    color = Color.White,
                    fontSize = 80.sp,
                )
            }

            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(width = 310.dp, height = 100.dp)
                    .offset(y = 135.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.categorie_removebg_preview),
                            contentDescription = "Category Image",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable { navController.navigate("openCategory") }
                        )
                        Image(
                            painter = painterResource(id = R.drawable.infinite_removebg_preview),
                            contentDescription = "Image 2",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { /* Handle click here */ }
                        )
                        Image(
                            painter = painterResource(id = R.drawable.timeline_removebg_preview),
                            contentDescription = "Image 3",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { navController.navigate("openTimeline") }
                        )
                    }
                }
            }

            Text(text = "Open Your Thoughts!", modifier = Modifier.offset(y = 150.dp),
                fontStyle = FontStyle.Italic
            )

            // Show dialog when showDialog is true
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text(text = "Select a category") },
                    text = {
                        LazyColumn {
                            items(listOf("Passwords", "Emails", "Thoughts")) { category ->
                                Text(
                                    text = category,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            when (category) {
                                                "Passwords" -> navController.navigate("password")
                                                "Thoughts" -> navController.navigate("thoughts")
                                                "Emails" -> navController.navigate("Emails")
                                            }
                                            showDialog.value = false
                                        }
                                        .padding(16.dp)
                                )
                            }
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = text,
                                        onValueChange = { newText -> text = newText },
                                        label = { Text("Make Your Own") },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(16.dp)
                                    )
                                    IconButton(onClick = {
                                        navController.navigate("makeYourOwn/${text}")
                                    }, modifier = Modifier.padding(16.dp)) {
                                        Icon(Icons.Default.ArrowForward, contentDescription = "Go to Make Your Own Screen")
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog.value = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}
