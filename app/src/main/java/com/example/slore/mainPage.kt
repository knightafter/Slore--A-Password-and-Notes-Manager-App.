package com.example.slore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import coil.compose.rememberImagePainter

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

                Image(  painter = rememberAsyncImagePainter
                    ("https://i.imgur.com/c3pJ22r.png"),
                    contentDescription = "Brand text",
                    modifier = Modifier
                        .height(400.dp) // Increase the height to make the image larger
                        .width(400.dp)
                        .offset(x = -40.dp ,y = (-40).dp)
                )

                Icon(                    imageVector = Icons.Default.PersonPin,
                    contentDescription = "Profile Icon",
                    modifier = Modifier.offset(y = 50.dp)
                        .size(32.dp)
                        .clickable { navController.navigate("home") }
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF103A5E), // Navy blue color
                ),
                modifier = Modifier
                    .size(width = 120.dp, height = 130.dp)
                    .offset(y = -75.dp)
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
                    .size(width = 310.dp, height = 100.dp)
                    .offset(x = 0.dp, y = -100.dp)
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
                                .clickable { navController.navigate("TasksScreen") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Open Your Thoughts!",
                modifier = Modifier.offset(y = -110.dp),
                fontStyle = FontStyle.Italic
            )

        }

        // Home icon placed outside the card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter // Centering the Home icon
        ) {
            Image(
                painter = painterResource(id = R.drawable.artboard_7), // Replace with your image resource ID
                contentDescription = "Home Image",
                modifier = Modifier
                    .size(90.dp) // Increase the size of the image
                    .clickable { showGeminiDialog.value = true } // Show Gemini dialog when the image is clicked
            )
        }

        // Show category dialog when showCategoryDialog is true
        if (showCategoryDialog.value) {
            CategoryDialog(navController = navController, showDialog = showCategoryDialog)
        }

        // Show Gemini dialog when showGeminiDialog is true
        if (showGeminiDialog.value) {
            GeminiDialog(showDialog = showGeminiDialog, text = text) { showGeminiDialog.value = false }
        }
    }
}


@Composable
fun CategoryDialog(navController: NavHostController, showDialog: MutableState<Boolean>) {
    val categories = listOf(
        Pair("Thoughts", Icons.Default.Star),
        Pair("Notes", Icons.Default.Note),
        Pair("Passwords", Icons.Default.Lock),
        Pair("Emails", Icons.Default.Email)
    )

    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF103A5E))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select a category",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        text = {
            LazyColumn {
                items(categories) { category ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (category.first) {
                                    "Passwords" -> navController.navigate("password")
                                    "Thoughts" -> navController.navigate("thoughts")
                                    "Notes" -> navController.navigate("notes")
                                    "Emails" -> navController.navigate("emails")
                                }
                                showDialog.value = false
                            }
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = category.second,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category.first,
                            fontSize = 16.sp
                        )
                    }
                    Divider()
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
                                try {
                                    val chatResponse = model.startChat(listOf()).sendMessage(inputText)
                                    delay(2000) // Simulate 2 seconds delay
                                    responseText = chatResponse.text ?: ""
                                } catch (e: Exception) {
                                    // Handle the exception by setting the responseText to your error message
                                    responseText = "You are offline. Please connect to the internet and try again."
                                }
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