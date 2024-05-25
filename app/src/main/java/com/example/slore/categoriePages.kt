package com.example.slore


import android.util.Log
import androidx.annotation.NonNull
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


/**
 ****
 * ****
 * ******
 *
 * have to add the pop-back.stack like it
 *
 * Icon(
 *                 imageVector = Icons.Default.ArrowBack,
 *                 contentDescription = "Back",
 *                 modifier = Modifier.clickable { navController.popBackStack() }
 *             )
 *
 *             so we can go back to the previous state and have to add at almost every place in the app for better user experience
 *
 *  *********
 *  ******
 *  ****
 *  ***
 */

/*the below code demonstrates that when on main screen i press on the big plus button the category pages or option comers are due to the below code*/





data class ThoughtEntry(
    val heading: String = "",
    val thought: String = "",
    val message: String = ""
)

// Functions to Add Data to Firestore
fun addPasswordEntry(@NonNull userId: String, passwordEntry: PasswordEntry) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("users").document(userId).collection("passwords")
        .add(passwordEntry)
        .addOnSuccessListener {
            Log.d("Firestore", "Password entry added successfully")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error adding password entry", e)
        }
}

fun addEmailEntry(@NonNull userId: String, emailEntry: EmailEntry) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("users").document(userId).collection("emails")
        .add(emailEntry)
        .addOnSuccessListener {
            Log.d("Firestore", "Email entry added successfully")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error adding email entry", e)
        }
}

fun addThoughtEntry(@NonNull userId: String, thoughtEntry: ThoughtEntry) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("users").document(userId).collection("thoughts")
        .add(thoughtEntry)
        .addOnSuccessListener {
            Log.d("Firestore", "Thought entry added successfully")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error adding thought entry", e)
        }
}

@Composable
fun CenteredPopupMessage(message: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(text = message, color = Color.White)
        }
    }
    LaunchedEffect(Unit) {
        delay(1000L) // Show message for 1 second
        onDismiss()
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PasswordScreen(navController: NavHostController) {
    var headerText by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var memorableNotes by remember { mutableStateOf(TextFieldValue("")) }
    var message by remember { mutableStateOf(TextFieldValue("")) } // New field
    var showPopupMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    MaterialTheme {
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
                // Save button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            val passwordEntry = PasswordEntry(
                                heading = headerText.text,
                                username = username.text,
                                password = password.text,
                                memorableNotes = memorableNotes.text,
                                message = message.text // Save the message
                            )

                            user?.let {
                                addPasswordEntry(it.uid, passwordEntry)
                                scope.launch {
                                    showPopupMessage = true
                                    delay(1000L) // Show message for 1 second
                                    navController.navigate("main") {
                                        popUpTo("password") { inclusive = true } // or "Emails", or "makeYourOwn/{inputText}"
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            tint = Color(0xFF000080) // Navy blue color
                        )
                    }
                }

                Text(
                    text = "Create a Password",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = headerText,
                    onValueChange = { headerText = it },
                    placeholder = { Text(text = "Heading...") },
                    textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
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
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
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
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // New message field
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text(text = "Message...") },
                    textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                    singleLine = false, // Make it multiline
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                        .background(Color(0xFFF7E8C2))
                ) {
                    var textFieldHeight by remember { mutableStateOf(0) }
                    var screenHeightPx by remember { mutableStateOf(0) }

                    DisposableEffect(context) {
                        val displayMetrics = context.resources.displayMetrics
                        screenHeightPx = displayMetrics.heightPixels
                        onDispose { }
                    }

                    BasicTextField(
                        value = memorableNotes,
                        onValueChange = {
                            memorableNotes = it
                        },
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Default
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coordinates ->
                                textFieldHeight = coordinates.size.height
                            }
                    )
                }
            }

            if (showPopupMessage) {
                CenteredPopupMessage(message = "Your input is saved.") {
                    showPopupMessage = false
                }
            }
        }
    }
}



// EmailsScreen Composable
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EmailsScreen(navController: NavHostController) {
    var headerText by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var message by remember { mutableStateOf(TextFieldValue("")) } // New field
    var showPopupMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser

    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    MaterialTheme {
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
                // Save button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            val emailEntry = EmailEntry(
                                heading = headerText.text,
                                username = username.text,
                                password = password.text,
                                message = message.text // Save the message
                            )

                            user?.let {
                                addEmailEntry(it.uid, emailEntry)
                                scope.launch {
                                    showPopupMessage = true
                                    delay(1000L) // Show message for 1 second
                                    navController.navigate("main") {
                                        popUpTo("emails") { inclusive = true }
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            tint = Color(0xFF000080) // Navy blue color
                        )
                    }
                }

                Text(
                    text = "Create an Email Entry",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = headerText,
                    onValueChange = { headerText = it },
                    placeholder = { Text(text = "Heading...") },
                    textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
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
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
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
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // New message field
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text(text = "Message...") },
                    textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                    singleLine = false, // Make it multiline
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                        .background(Color(0xFFF7E8C2))
                ) {
                    var textFieldHeight by remember { mutableStateOf(0) }
                    var screenHeightPx by remember { mutableStateOf(0) }

                    DisposableEffect(context) {
                        val displayMetrics = context.resources.displayMetrics
                        screenHeightPx = displayMetrics.heightPixels
                        onDispose { }
                    }

                    BasicTextField(
                        value = TextFieldValue("Memorable Notes..."), // Replace with actual value if needed
                        onValueChange = { /* Handle change */ },
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Default
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coordinates ->
                                textFieldHeight = coordinates.size.height
                            }
                    )
                }

                if (showPopupMessage) {
                    CenteredPopupMessage(message = "Your input is saved.") {
                        showPopupMessage = false
                    }
                }
            }
        }
    }
}


// ThoughtsScreen Composable
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ThoughtsScreen(navController: NavHostController) {
    var headerText by remember { mutableStateOf(TextFieldValue("")) }
    var thought by remember { mutableStateOf(TextFieldValue("")) }
    var message by remember { mutableStateOf(TextFieldValue("")) } // New field
    var showPopupMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser

    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    MaterialTheme {
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
                // Save button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            val thoughtEntry = ThoughtEntry(
                                heading = headerText.text,
                                thought = thought.text,
                                message = message.text // Save the message
                            )

                            user?.let {
                                addThoughtEntry(it.uid, thoughtEntry)
                                scope.launch {
                                    showPopupMessage = true
                                    delay(1000L) // Show message for 1 second
                                    navController.navigate("main") {
                                        popUpTo("thoughts") { inclusive = true }
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            tint = Color(0xFF000080) // Navy blue color
                        )
                    }
                }

                Text(
                    text = "Create a Thought Entry",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = headerText,
                    onValueChange = { headerText = it },
                    placeholder = { Text(text = "Heading...") },
                    textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = thought,
                    onValueChange = { thought = it },
                    placeholder = { Text(text = "Thought...") },
                    textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                    singleLine = false, // Make it multiline
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // New message field
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text(text = "Message...") },
                    textStyle = TextStyle(fontSize = 24.sp, color = Color.Gray),
                    singleLine = false, // Make it multiline
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                        .background(Color(0xFFF7E8C2))
                ) {
                    var textFieldHeight by remember { mutableStateOf(0) }
                    var screenHeightPx by remember { mutableStateOf(0) }

                    DisposableEffect(context) {
                        val displayMetrics = context.resources.displayMetrics
                        screenHeightPx = displayMetrics.heightPixels
                        onDispose { }
                    }

                    BasicTextField(
                        value = TextFieldValue("Memorable Notes..."), // Replace with actual value if needed
                        onValueChange = { /* Handle change */ },
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Default
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coordinates ->
                                textFieldHeight = coordinates.size.height
                            }
                    )
                }

                if (showPopupMessage) {
                    CenteredPopupMessage(message = "Your input is saved.") {
                        showPopupMessage = false
                    }
                }
            }
        }
    }



    @Composable
    fun main(){
        Text(text = "just trying the things out for testing")

    }