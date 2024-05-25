package com.example.slore

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

data class PasswordEntry(
    val id: String = "",
    val heading: String = "",
    val username: String = "",
    val password: String = "",
    val memorableNotes: String = "",
    val message: String = ""
)

data class EmailEntry(
    val id: String = "",
    val heading: String = "",
    val username: String = "",
    val password: String = "",
    val message: String = ""
)

data class Note(
    val id: String = "",
    val heading: String = "",
    val content: String = ""
)

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
            text = "Email",
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable { navController.navigate("emailsscreen") }
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
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("passwords")
                .get().await()
            val fetchedItems = snapshot.documents.mapNotNull {
                it.toObject<PasswordEntry>()?.copy(id = it.id)
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
                        navController.navigate("passwordDetail/${item.id}")
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val firestore = FirebaseFirestore.getInstance()
        val document = firestore.collection("users")
            .document(userId)
            .collection("passwords")
            .document(passwordId)
            .get().await()
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
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            val firestore = FirebaseFirestore.getInstance()
                            val updatedPasswordEntry = PasswordEntry(
                                id = passwordId,
                                heading = heading.text,
                                username = username.text,
                                password = password.text,
                                memorableNotes = memorableNotes.text,
                                message = message.text
                            )
                            firestore.collection("users")
                                .document(userId)
                                .collection("passwords")
                                .document(passwordId)
                                .set(updatedPasswordEntry).await()
                            showPopupMessage = true
                        }
                    }) {
                        Icon(Icons.Filled.Save, contentDescription = "Save", tint = Color(0xFF000080))
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
                    .pointerInput(Unit) {}
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 86.dp)
                ) {
                    Text(
                        text = heading.text,
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(8.dp)
                    )

                    TextField(
                        value = heading,
                        onValueChange = { heading = it },
                        placeholder = { Text(text = "Heading...") },
                        textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
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
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (showPopupMessage) {
                        AlertDialog(
                            onDismissRequest = { showPopupMessage = false },
                            title = { Text("Update Successful") },
                            text = { Text("Your password has been updated successfully.") },
                            confirmButton = {
                                Button(onClick = {
                                    showPopupMessage = false
                                    navController.navigate("passwords") {
                                        popUpTo("passwordDetail/$passwordId") { inclusive = true }
                                    }
                                }) {
                                    Text("OK")
                                }
                            }
                        )
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
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("emails")
                .get().await()
            val fetchedItems = snapshot.documents.mapNotNull { document ->
                document.toObject<EmailEntry>()?.copy(id = document.id)
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
                EmailItemList(navController, items)
            }
}

@Composable
fun EmailItemList(navController: NavHostController, items: List<EmailEntry>) {
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
                        navController.navigate("emailDetail/${item.id}")
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
fun EmailDetailScreen(navController: NavHostController, emailId: String) {
    Log.d("EmailDetailScreen", "Email ID: $emailId")

    var heading by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var message by remember { mutableStateOf(TextFieldValue("")) }
    var showPopupMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(emailId) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val firestore = FirebaseFirestore.getInstance()
        val document = firestore.collection("users")
            .document(userId)
            .collection("emails")
            .document(emailId)
            .get().await()
        val emailEntry = document.toObject<EmailEntry>()
        if (emailEntry != null) {
            heading = TextFieldValue(emailEntry.heading)
            username = TextFieldValue(emailEntry.username)
            password = TextFieldValue(emailEntry.password)
            message = TextFieldValue(emailEntry.message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Email") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            val firestore = FirebaseFirestore.getInstance()
                            val updatedEmailEntry = EmailEntry(
                                id = emailId,
                                heading = heading.text,
                                username = username.text,
                                password = password.text,
                                message = message.text
                            )
                            firestore.collection("users")
                                .document(userId)
                                .collection("emails")
                                .document(emailId)
                                .set(updatedEmailEntry).await()
                            showPopupMessage = true
                        }
                    }) {
                        Icon(Icons.Filled.Save, contentDescription = "Save", tint = Color(0xFF000080))
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
                    .pointerInput(Unit) {}
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 86.dp)
                ) {
                    Text(
                        text = heading.text,
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(8.dp)
                    )

                    TextField(
                        value = heading,
                        onValueChange = { heading = it },
                        placeholder = { Text(text = "Heading...") },
                        textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
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
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (showPopupMessage) {
                        AlertDialog(
                            onDismissRequest = { showPopupMessage = false },
                            title = { Text("Update Successful") },
                            text = { Text("Your email has been updated successfully.") },
                            confirmButton = {
                                Button(onClick = {
                                    showPopupMessage = false
                                    navController.navigate("emailsscreen") {
                                        popUpTo("emailDetail/$emailId") { inclusive = true }
                                    }
                                }) {
                                    Text("OK")
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ThoughtsScreenCategory(navController: NavHostController) {
    var items by remember { mutableStateOf<List<Note>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }


            LaunchedEffect(Unit) {
                try {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val firestore = FirebaseFirestore.getInstance()
                    val snapshot = firestore.collection("users")
                        .document(userId)
                        .collection("thoughts")
                        .get().await()
                    val fetchedItems = snapshot.documents.mapNotNull {
                        it.toObject<Note>()?.copy(id = it.id)
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
        ThoughtItemList(navController, items)
    }
}

@Composable
fun ThoughtItemList(navController: NavHostController, items: List<Note>) {
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
                        navController.navigate("thoughtDetail/${item.id}")
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
fun ThoughtDetailScreen(navController: NavHostController, thoughtId: String) {
    Log.d("ThoughtDetailScreen", "Thought ID: $thoughtId")


    var heading by remember { mutableStateOf(TextFieldValue("")) }
    var content by remember { mutableStateOf(TextFieldValue("")) }
    var showPopupMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(thoughtId) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val firestore = FirebaseFirestore.getInstance()
        val document = firestore.collection("users")
            .document(userId)
            .collection("thoughts")
            .document(thoughtId)
            .get().await()
        val note = document.toObject<Note>()
        if (note != null) {
            heading = TextFieldValue(note.heading)
            content = TextFieldValue(note.content)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Thought") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            val firestore = FirebaseFirestore.getInstance()
                            val updatedNote = Note(
                                id = thoughtId,
                                heading = heading.text,
                                content = content.text
                            )
                            firestore.collection("users")
                                .document(userId)
                                .collection("thoughts")
                                .document(thoughtId)
                                .set(updatedNote).await()
                            showPopupMessage = true
                        }
                    }) {
                        Icon(Icons.Filled.Save, contentDescription = "Save", tint = Color(0xFF000080))
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
                    .pointerInput(Unit) {}
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 86.dp)
                ) {
                    Text(
                        text = heading.text,
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(8.dp)
                    )

                    TextField(
                        value = heading,
                        onValueChange = { heading = it },
                        placeholder = { Text(text = "Heading...") },
                        textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = content,
                        onValueChange = { content = it },
                        placeholder = { Text(text = "Content...") },
                        textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (showPopupMessage) {
                        AlertDialog(
                            onDismissRequest = { showPopupMessage = false },
                            title = { Text("Update Successful") },
                            text = { Text("Your thought has been updated successfully.") },
                            confirmButton = {
                                Button(onClick = {
                                    showPopupMessage = false
                                    navController.navigate("hello") {
                                        popUpTo("thoughtDetail/$thoughtId") { inclusive = true }
                                    }
                                }) {
                                    Text("OK")
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}