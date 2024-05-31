package com.example.slore

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

data class PasswordEntry(
    val id: String = "",
    val heading: String = "",
    val username: String = "",
    val password: String = "",
    val memorableNotes: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class EmailEntry(
    val id: String = "",
    val heading: String = "",
    val username: String = "",
    val password: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/*when i click the below 2 composables then the category page is opened and i can see the categories which i have saved before
* then when i click the each category then i can easily see the seperated data for the each category
* */

@Composable
fun OpenCategoryScreen(navController: NavHostController) {
    val iconSize = 24.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Categories",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        CategoryItem(
            text = "Passwords",
            icon = Icons.Default.VpnKey,
            onClick = { navController.navigate("passwords") },
            iconSize = iconSize
        )

        Divider()

        CategoryItem(
            text = "Email",
            icon = Icons.Default.Email,
            onClick = { navController.navigate("emailsscreen") },
            iconSize = iconSize
        )

        Divider()

        CategoryItem(
            text = "Thoughts",
            icon = Icons.Default.Star,
            onClick = { navController.navigate("hello") },
            iconSize = iconSize
        )

        Divider()

        CategoryItem(
            text = "Notes",
            icon = Icons.Default.Note,
            onClick = { navController.navigate("notes1") },
            iconSize = iconSize
        )

        Divider()
    }
}

@Composable
fun CategoryItem(text: String, icon: ImageVector, onClick: () -> Unit, iconSize: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = Color(0xFF000080)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
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
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    var showDialog by remember { mutableStateOf(false) }
    var documentIdToDelete by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Created on: ${dateFormat.format(Date(item.timestamp))}",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Light),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = item.heading,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        )
                    }
                    Box {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    documentIdToDelete = item.id
                                    expanded = false
                                    showDialog = true
                                },
                                text = { Text("Remove Document") }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Caution") },
            text = { Text("Once you remove the document it cannot be recovered.") },
            confirmButton = {
                Button(
                    onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("users")
                            .document(userId)
                            .collection("passwords")
                            .document(documentIdToDelete ?: "")
                            .delete()
                            .addOnSuccessListener {
                                showDialog = false
                                showSnackbar = true
                                scope.launch {
                                    delay(1500)
                                    showSnackbar = false
                                }
                            }
                            .addOnFailureListener { e ->
                                e.printStackTrace()
                                // Handle failure case
                            }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSnackbar) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Successfully deleted",
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                    .background(Color(0xFFF5F5F5))
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
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    var showDialog by remember { mutableStateOf(false) }
    var documentIdToDelete by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Created on: ${dateFormat.format(Date(item.timestamp))}",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Light),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = item.heading,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        )
                    }
                    Box {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    documentIdToDelete = item.id
                                    expanded = false
                                    showDialog = true
                                },
                                text = { Text("Remove Document") }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Caution") },
            text = { Text("Once you remove the document it cannot be recovered.") },
            confirmButton = {
                Button(
                    onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("users")
                            .document(userId)
                            .collection("passwords")
                            .document(documentIdToDelete ?: "")
                            .delete()
                            .addOnSuccessListener {
                                showDialog = false
                                showSnackbar = true
                                scope.launch {
                                    delay(1500)
                                    showSnackbar = false
                                }
                            }
                            .addOnFailureListener { e ->
                                e.printStackTrace()
                                // Handle failure case
                            }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSnackbar) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Successfully deleted",
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                    .background(Color(0xFFF5F5F5))
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
fun ThoughtsEntryScreenCategory(navController: NavHostController) {
    var items by remember { mutableStateOf<List<ThoughtEntry>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("thoughts")
                .get().await()
            val fetchedItems = snapshot.documents.mapNotNull { document ->
                document.toObject<ThoughtEntry>()?.copy(id = document.id)
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
fun ThoughtItemList(navController: NavHostController, items: List<ThoughtEntry>) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    var showDialog by remember { mutableStateOf(false) }
    var documentIdToDelete by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Created on: ${dateFormat.format(Date(item.timestamp))}",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Light),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = item.heading,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        )
                    }
                    Box {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    documentIdToDelete = item.id
                                    expanded = false
                                    showDialog = true
                                },
                                text = { Text("Remove Document") }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Caution") },
            text = { Text("Once you remove the document it cannot be recovered.") },
            confirmButton = {
                Button(
                    onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("users")
                            .document(userId)
                            .collection("passwords")
                            .document(documentIdToDelete ?: "")
                            .delete()
                            .addOnSuccessListener {
                                showDialog = false
                                showSnackbar = true
                                scope.launch {
                                    delay(1500)
                                    showSnackbar = false
                                }
                            }
                            .addOnFailureListener { e ->
                                e.printStackTrace()
                                // Handle failure case
                            }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSnackbar) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Successfully deleted",
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
    var thought by remember { mutableStateOf(TextFieldValue("")) }
    var showPopupMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(thoughtId) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val firestore = FirebaseFirestore.getInstance()
        try {
            val document = firestore.collection("users")
                .document(userId)
                .collection("thoughts")
                .document(thoughtId)
                .get().await()
            val thoughtEntry = document.toObject<ThoughtEntry>()
            if (thoughtEntry != null) {
                heading = TextFieldValue(thoughtEntry.heading)
                thought = TextFieldValue(thoughtEntry.thought)
            }
        } catch (e: Exception) {
            Log.e("ThoughtDetailScreen", "Error fetching thought details", e)
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
                            val updatedThoughtEntry = ThoughtEntry(
                                id = thoughtId,
                                heading = heading.text,
                                thought = thought.text
                            )
                            try {
                                firestore.collection("users")
                                    .document(userId)
                                    .collection("thoughts")
                                    .document(thoughtId)
                                    .set(updatedThoughtEntry).await()
                                showPopupMessage = true
                            } catch (e: Exception) {
                                Log.e("ThoughtDetailScreen", "Error updating thought", e)
                            }
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
                    .background(Color(0xFFF5F5F5))
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
                        value = thought,
                        onValueChange = { thought = it },
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
                                    navController.navigate("thoughtsscreen") {
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

@Composable
fun NotesEntryScreenCategory(navController: NavHostController) {
    var items by remember { mutableStateOf<List<NoteEntry>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("notes")
                .get().await()
            val fetchedItems = snapshot.documents.mapNotNull { document ->
                document.toObject<NoteEntry>()?.copy(id = document.id)
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
        NoteItemList(navController, items)
    }
}

@Composable
fun NoteItemList(navController: NavHostController, items: List<NoteEntry>) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    var showDialog by remember { mutableStateOf(false) }
    var documentIdToDelete by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                        navController.navigate("noteDetail/${item.id}")
                    }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Created on: ${dateFormat.format(Date(item.timestamp))}",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Light),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = item.heading,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        )
                    }
                    Box {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    documentIdToDelete = item.id
                                    expanded = false
                                    showDialog = true
                                },
                                text = { Text("Remove Document") }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Caution") },
            text = { Text("Once you remove the document it cannot be recovered.") },
            confirmButton = {
                Button(
                    onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("users")
                            .document(userId)
                            .collection("passwords")
                            .document(documentIdToDelete ?: "")
                            .delete()
                            .addOnSuccessListener {
                                showDialog = false
                                showSnackbar = true
                                scope.launch {
                                    delay(1500)
                                    showSnackbar = false
                                }
                            }
                            .addOnFailureListener { e ->
                                e.printStackTrace()
                                // Handle failure case
                            }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSnackbar) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Successfully deleted",
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteDetailScreen(navController: NavHostController, noteId: String) {
    Log.d("NoteDetailScreen", "Note ID: $noteId")

    var heading by remember { mutableStateOf(TextFieldValue("")) }
    var note by remember { mutableStateOf(TextFieldValue("")) }
    var showPopupMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(noteId) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val firestore = FirebaseFirestore.getInstance()
        try {
            val document = firestore.collection("users")
                .document(userId)
                .collection("notes")
                .document(noteId)
                .get().await()
            val noteEntry = document.toObject<NoteEntry>()
            if (noteEntry != null) {
                heading = TextFieldValue(noteEntry.heading)
                note = TextFieldValue(noteEntry.note)
            }
        } catch (e: Exception) {
            Log.e("NoteDetailScreen", "Error fetching note details", e)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Note") },
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
                            val updatedNoteEntry = NoteEntry(
                                id = noteId,
                                heading = heading.text,
                                note = note.text
                            )
                            try {
                                firestore.collection("users")
                                    .document(userId)
                                    .collection("notes")
                                    .document(noteId)
                                    .set(updatedNoteEntry).await()
                                showPopupMessage = true
                            } catch (e: Exception) {
                                Log.e("NoteDetailScreen", "Error updating note", e)
                            }
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
                    .background(Color(0xFFF5F5F5))
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
                        value = note,
                        onValueChange = { note = it },
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
                            text = { Text("Your note has been updated successfully.") },
                            confirmButton = {
                                Button(onClick = {
                                    showPopupMessage = false
                                    navController.navigate("notescreen") {
                                        popUpTo("noteDetail/$noteId") { inclusive = true }
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
