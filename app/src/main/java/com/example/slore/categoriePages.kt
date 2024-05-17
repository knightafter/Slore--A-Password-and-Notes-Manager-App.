package com.example.slore

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen() {
    var headerText by remember { mutableStateOf(TextFieldValue("")) }
    var contentText by remember { mutableStateOf(TextFieldValue("")) }
    val maxWords = 6000
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7E8C2))
                .padding(16.dp)
        ) {
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
                        imeAction = ImeAction.Done
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
    }
}


@Composable
fun ThoughtsScreen() {
    Text(
        text = "Save Your Thoughts",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp), // Add space at the top
        textAlign = TextAlign.Center
    )
}

@Composable
fun NotesScreen() {
    Text(
        text = "Save Your Notes",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp), // Add space at the top
        textAlign = TextAlign.Center
    )
}

@Composable
fun MakeYourOwnScreen(inputText: String?) {
    Text(
        text = inputText ?: "No input provided",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp), // Add space at the top
        textAlign = TextAlign.Center
    )
}