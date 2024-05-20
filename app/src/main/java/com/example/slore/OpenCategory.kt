package com.example.slore


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

sealed class CategoryItem

data class PasswordEntry(
    val heading: String = "",
    val username: String = "",
    val password: String = "",
    val memorableNotes: String = "",
    val message: String = ""
) : CategoryItem()

@Composable
fun OpenCategoryScreen(navController: NavHostController) {
    var items by remember { mutableStateOf<List<PasswordEntry>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

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
    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        items.forEach { item ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(text = item.heading, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
                Text(text = "Username: ${item.username}", style = TextStyle(fontSize = 16.sp))
                Text(text = "Password: ${item.password}", style = TextStyle(fontSize = 16.sp))
                Text(text = "Memorable Notes: ${item.memorableNotes}", style = TextStyle(fontSize = 16.sp))
                Text(text = "Message: ${item.message}", style = TextStyle(fontSize = 16.sp))
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}
