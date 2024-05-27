package com.example.slore

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainContent(navController: NavHostController) {
    val showCategoryDialog = remember { mutableStateOf(false) }
    val showGeminiDialog = remember { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                    .clickable { showCategoryDialog.value = true } // Show category dialog when card is clicked
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

            Spacer(modifier = Modifier.height(100.dp))

            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(width = 310.dp, height = 100.dp).offset(x= 0.dp , y = 100.dp)
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

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Open Your Thoughts!",
                modifier = Modifier.offset(y = 90.dp),
                fontStyle = FontStyle.Italic
            )
            Text(text = "its me")
        }

        // Home icon placed outside the card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter // Centering the Home icon
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home Icon",
                modifier = Modifier
                    .size(64.dp) // Increase the size of the home icon
                    .clickable { showGeminiDialog.value = true } // Show Gemini dialog when home icon is clicked
            )
        }

        // Show category dialog when showCategoryDialog is true
        if (showCategoryDialog.value) {
            AlertDialog(
                onDismissRequest = { showCategoryDialog.value = false },
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
                                        showCategoryDialog.value = false
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
                    TextButton(onClick = { showCategoryDialog.value = false }) {
                        Text("OK")
                    }
                }
            )
        }

        // Show Gemini dialog when showGeminiDialog is true
        if (showGeminiDialog.value) {
            GeminiDialog(showDialog = showGeminiDialog, text = text) { showGeminiDialog.value = false }
        }
    }
}


/*the below code is used getting the data initialized from the the file GeminiAiIntegration.kt and calling or showing in a beautiful way means
* in a alert dialog box the backend is on the GeminiAiIntegration.kt and the visuals is in the current mainpage.kt */

@Composable
fun GeminiDialog(
    showDialog: MutableState<Boolean>,
    text: String,
    dismissDialog: () -> Unit
) {
    var inputText by remember { mutableStateOf(text) }
    var responseText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = dismissDialog,
        title = { Text(text = "Your Slore Assistant") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Enter your query") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            // Submit the query and get the response
                            isLoading = true
                            coroutineScope.launch {
                                val chatResponse = model.startChat(listOf()).sendMessage(inputText)
                                delay(2000) // Simulate 2 seconds delay
                                responseText = chatResponse.text ?: ""
                                isLoading = false
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Enter")
                    }

                    if (isLoading) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator()
                    }
                }

                // Display the response inside SelectionContainer
                SelectionContainer {
                    Text(responseText)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = dismissDialog) {
                Text("OK")
            }
        }
    )
}
