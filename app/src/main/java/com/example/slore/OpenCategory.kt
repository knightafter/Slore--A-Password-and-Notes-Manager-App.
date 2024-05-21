package com.example.slore

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue


@Composable
fun OpenCategoryScreen(navController: NavHostController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        BasicText(
            text = "Passwords",
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable { navController.navigate("passwords") }
        )
        BasicText(
            text = "Emails",
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable { navController.navigate("emails") }
        )
        BasicText(
            text = "Thoughts",
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable { navController.navigate("hello") }
        )


    }
}




@Composable
fun PasswordsScreenCategory(navController: NavHostController) {
    var items by remember { mutableStateOf<List<PasswordEntry>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore.collection("passwords").get().await()
            val fetchedItems = snapshot.documents.mapNotNull {
                it.toObject<PasswordEntry>()?.copy(id = it.id) // Include the document ID here
            }
            items = fetchedItems
            loading = false
        } catch (e: Exception) {
            e.printStackTrace()
            loading = false
        }
    }

    if (loading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    } else {
        PasswordItemList(navController, items)
    }
}

@Composable
fun PasswordItemList(navController: NavHostController, items: List<PasswordEntry>) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        items.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate("passwordDetail/${item.id}") // Navigate to PasswordDetailScreen
                    }
            ) {
                Text(
                    text = item.heading,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PasswordDetailScreen(navController: NavHostController, passwordId: String) {
    Log.d("PasswordDetailScreen", "Password ID: $passwordId")

    var heading by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var memorableNotes by remember { mutableStateOf(TextFieldValue("")) }
    var message by remember { mutableStateOf(TextFieldValue("")) }
    var showPopupMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(passwordId) {
        val firestore = FirebaseFirestore.getInstance()
        val document = firestore.collection("passwords").document(passwordId).get().await()
        val passwordEntry = document.toObject<PasswordEntry>()
        if (passwordEntry != null) {
            heading = TextFieldValue(passwordEntry.heading)
            username = TextFieldValue(passwordEntry.username)
            password = TextFieldValue(passwordEntry.password)
            memorableNotes = TextFieldValue(passwordEntry.memorableNotes)
            message = TextFieldValue(passwordEntry.message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7E8C2))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    TextField(
                        value = heading,
                        onValueChange = { heading = it },
                        placeholder = { Text(text = "Heading...") },
                        textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text(text = "Username...") },
                        textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text(text = "Password...") },
                        textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = { Text(text = "Message...") },
                        textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                        singleLine = false, // Make it multiline
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .background(Color(0xFFF7E8C2))
                    ) {
                        BasicTextField(
                            value = memorableNotes,
                            onValueChange = {
                                memorableNotes = it
                            },
                            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // Update password logic here
                            scope.launch {
                                showPopupMessage = true
                                delay(1000L) // Show message for 1 second
                                navController.navigate("passwords") {
                                    popUpTo("passwordDetail/$passwordId") { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Save")
                    }

                    if (showPopupMessage) {
                        Snackbar(
                            modifier = Modifier.padding(16.dp),
                            action = {
                                Button(onClick = { showPopupMessage = false }) {
                                    Text(text = "Dismiss")
                                }
                            }
                        ) {
                            Text(text = "Your input is saved.")
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun EmailsEntryScreenCategory(navController: NavHostController) {
    var items by remember { mutableStateOf<List<EmailEntry>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore.collection("emails").get().await()
            val fetchedItems = snapshot.documents.mapNotNull { it.toObject<EmailEntry>() }
            items = fetchedItems
            loading = false
        } catch (e: Exception) {
            e.printStackTrace()
            loading = false
        }
    }

    if (loading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    } else {
        EmailItemList(items)
    }
}

@Composable
fun EmailItemList(items: List<EmailEntry>) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        items.forEach { item ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = item.heading,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
                Text(text = "Sender: ${item.sender}", style = TextStyle(fontSize = 16.sp))
                Text(text = "Recipient: ${item.recipient}", style = TextStyle(fontSize = 16.sp))
                Text(text = "Subject: ${item.subject}", style = TextStyle(fontSize = 16.sp))
                Text(text = "Content: ${item.content}", style = TextStyle(fontSize = 16.sp))
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}








@Composable
fun NotesScreen(navController: NavHostController) {
    var items by remember { mutableStateOf<List<Note>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    
    LaunchedEffect(Unit) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore.collection("Thoughts").get().await()
            val fetchedItems = snapshot.documents.mapNotNull { it.toObject<Note>() }
            items = fetchedItems
            loading = false
        } catch (e: Exception) {
            e.printStackTrace()
            loading = false
        }
    }

    if (loading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    } else {
        NoteItemList(items)
    }
}

@Composable
fun NoteItemList(items: List<Note>) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        items.forEach { item ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = item.heading,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
                Text(text = item.content, style = TextStyle(fontSize = 16.sp))
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}


