package com.example.slore

import CategoryItem
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
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore








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
            val fetchedItems = snapshot.documents.mapNotNull { it.toObject<PasswordEntry>() }
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
        PasswordItemList(items)
    }
}

@Composable
fun PasswordItemList(items: List<PasswordEntry>) {
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
                Text(text = "Username: ${item.username}", style = TextStyle(fontSize = 16.sp))
                Text(text = "Password: ${item.password}", style = TextStyle(fontSize = 16.sp))
                Text(
                    text = "Memorable Notes: ${item.memorableNotes}",
                    style = TextStyle(fontSize = 16.sp)
                )
                Text(text = "Message: ${item.message}", style = TextStyle(fontSize = 16.sp))
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
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

    
    Text(text = "hello welcome!")
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