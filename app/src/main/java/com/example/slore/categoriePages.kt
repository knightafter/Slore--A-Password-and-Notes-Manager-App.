package com.example.slore

import EmailEntry
import MakeYourOwnEntry
import Note
import PasswordEntry
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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



@Composable
fun CenteredPopupMessage(message: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.White,
            modifier = Modifier
                .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        )
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
                            val firestore = FirebaseFirestore.getInstance()
                            val passwordEntry = PasswordEntry(
                                heading = headerText.text,
                                username = username.text,
                                password = password.text,
                                memorableNotes = memorableNotes.text,
                                message = message.text // Save the message
                            )

                            firestore.collection("passwords") // or "emails", or inputText for MakeYourOwnScreen
                                .add(passwordEntry) // or emailEntry, or makeYourOwnEntry
                                .addOnSuccessListener { documentReference ->
                                    scope.launch {
                                        showPopupMessage = true
                                        delay(1000L) // Show message for 1 second
                                        navController.navigate("main") {
                                            popUpTo("password") { inclusive = true } // or "Emails", or "makeYourOwn/{inputText}"
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    scope.launch {
                                        showPopupMessage = true
                                        delay(1000L) // Show message for 1 second
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
                                if (textFieldHeight > screenHeightPx - 100) {
                                    scope.launch {
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
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




@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EmailsScreen(navController: NavHostController) {
    var heading by remember { mutableStateOf(TextFieldValue("")) }
    var sender by remember { mutableStateOf(TextFieldValue("")) }
    var recipient by remember { mutableStateOf(TextFieldValue("")) }
    var subject by remember { mutableStateOf(TextFieldValue("")) }
    var content by remember { mutableStateOf(TextFieldValue("")) }
    var showPopupMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                            val firestore = FirebaseFirestore.getInstance()
                            val emailEntry = EmailEntry(
                                heading = heading.text,
                                sender = sender.text,
                                recipient = recipient.text,
                                subject = subject.text,
                                content = content.text
                            )

                            firestore.collection("emails")
                                .add(emailEntry)
                                .addOnSuccessListener { documentReference ->
                                    scope.launch {
                                        showPopupMessage = true
                                        delay(1000L) // Show message for 1 second
                                        navController.navigate("main") {
                                            popUpTo("Emails") { inclusive = true }
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    scope.launch {
                                        showPopupMessage = true
                                        delay(1000L) // Show message for 1 second
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
                    text = "Save Your Emails",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Add TextField for each field in EmailEntry
                TextField(
                    value = heading,
                    onValueChange = { heading = it },
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

                // Repeat for other fields...

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
                        value = content,
                        onValueChange = {
                            content = it
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
                                if (textFieldHeight > screenHeightPx - 100) {
                                    scope.launch {
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ThoughtsScreen(navController: NavController) {
    var headerText by remember { mutableStateOf(TextFieldValue("")) }
    var contentText by remember { mutableStateOf(TextFieldValue("")) }
    val maxWords = 6000
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var showPopupMessage by remember { mutableStateOf(false) }

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
                            val firestore = FirebaseFirestore.getInstance()
                            val note = Note(
                                heading = headerText.text,
                                content = contentText.text
                            )

                            firestore.collection("Thoughts")
                                .add(note)
                                .addOnSuccessListener { documentReference ->
                                    // Show success message
                                    scope.launch {
                                        showPopupMessage = true
                                        delay(1000L) // Show message for 1 second
                                        // Navigate back to the main page
                                        navController.navigate("main") {
                                            popUpTo("password") { inclusive = true }
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    // Show failure message
                                    scope.launch {
                                        showPopupMessage = true
                                        delay(1000L) // Show message for 1 second
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
                    text = "Save Your Thoughts",
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
                val context = LocalContext.current

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                        .background(Color(0xFFF7E8C2))
                ) {
                    var textFieldHeight by remember { mutableStateOf(0) }

                    BasicTextField(
                        value = contentText,
                        onValueChange = {
                            if (it.text.split("\\s+".toRegex()).size <= maxWords) {
                                contentText = it
                            } else {
                                scope.launch {
                                    // Show some warning to the user if needed
                                }
                            }
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

                    LaunchedEffect(textFieldHeight, contentText.text) {
                        val screenHeightPx = context.resources.displayMetrics.heightPixels
                        if (textFieldHeight > screenHeightPx - 100) {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    }
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






@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MakeYourOwnScreen(inputText: String?, navController: NavController) {
    var heading by remember { mutableStateOf(TextFieldValue("")) }
    var content by remember { mutableStateOf(TextFieldValue("")) }
    var showPopupMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                            val firestore = FirebaseFirestore.getInstance()
                            val makeYourOwnEntry = MakeYourOwnEntry(
                                heading = heading.text,
                                content = content.text
                            )

                            firestore.collection(inputText ?: "default")
                                .add(makeYourOwnEntry)
                                .addOnSuccessListener { documentReference ->
                                    scope.launch {
                                        showPopupMessage = true
                                        delay(1000L) // Show message for 1 second
                                    }
                                }
                                .addOnFailureListener { e ->
                                    scope.launch {
                                        showPopupMessage = true
                                        delay(1000L) // Show message for 1 second
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
                    text = inputText ?: "No input provided",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = heading,
                    onValueChange = { heading = it },
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
                        value = content,
                        onValueChange = {
                            content = it
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
                                if (textFieldHeight > screenHeightPx - 100) {
                                    scope.launch {
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
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
