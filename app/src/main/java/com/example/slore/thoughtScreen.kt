package com.example.slore

import android.util.Log
import androidx.annotation.NonNull
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import androidx.compose.material.icons.filled.Download
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileOutputStream
import android.widget.Toast
import androidx.navigation.NavHostController


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ThoughtsScreen(navController: NavHostController) {
    var headerText by remember { mutableStateOf(TextFieldValue("")) }
    var thought by remember { mutableStateOf(TextFieldValue("")) }
    var showPopupMessage by remember { mutableStateOf(false) }
    val showGeminiDialog = remember { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var zoomLevel by remember { mutableStateOf(1f) }


    val currentDateTime = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    var showDownloadDialog by remember { mutableStateOf(false) }
    var selectedFormat by remember { mutableStateOf("") }
    var showPreviewDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Top bar with back arrow, date/time, save button, and download icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(16.dp)
                            .size(35.dp)
                            .offset(x = (-27).dp)
                    )

                    Text(
                        text = currentDateTime,
                        fontSize = 14.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.padding(16.dp)
                    )

                    IconButton(
                        onClick = { showDownloadDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download",
                            tint = Color.Black
                        )
                    }

                    TextButton(
                        onClick = {
                            val thoughtEntry = ThoughtEntry(
                                heading = headerText.text,
                                thought = thought.text,
                                timestamp = System.currentTimeMillis()
                            )

                            user?.let {
                                addThoughtEntry(it.uid, thoughtEntry)
                                scope.launch {
                                    showPopupMessage = true
                                    delay(1000L)
                                    navController.navigate("main") {
                                        popUpTo("thoughts") { inclusive = true }
                                    }
                                }
                            }
                        }
                    ) {
                        Text(
                            text = "Save",
                            color = Color(0xFF000080),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Plus and minus icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = -20.dp)
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (zoomLevel > 0.5f) {
                            zoomLevel -= 0.1f
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Zoom Out",
                            tint = Color.Black
                        )
                    }

                    IconButton(onClick = {
                        if (zoomLevel < 2f) {
                            zoomLevel += 0.1f
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom In",
                            tint = Color.Black
                        )
                    }
                }

                Text(
                    text = "Create a Thought",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.offset(x = 20.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = headerText,
                    onValueChange = { headerText = it },
                    placeholder = { Text(text = "Heading...") },
                    textStyle = TextStyle(fontSize = 24.sp, color = Color.Black),
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

                var totalPages by remember { mutableStateOf(3) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                        .background(Color(0xFFF5F5F5))
                ) {
                    Column {
                        repeat(totalPages) { pageIndex ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((297.mm * zoomLevel).toDp().dp)
                                    .background(Color.White, shape = RectangleShape)
                                    .padding(16.dp)
                            ) {
                                BasicTextField(
                                    value = thought,
                                    onValueChange = { thought = it },
                                    textStyle = TextStyle(fontSize = (16.sp * zoomLevel), color = Color.Black),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Default
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { keyboardController?.hide() }
                                    ),
                                    modifier = Modifier.fillMaxSize(),
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            if (thought.text.isEmpty()) {
                                                Text(
                                                    text = "Thoughts...",
                                                    style = TextStyle(fontSize = 16.sp * zoomLevel, color = Color.Gray),
                                                    modifier = Modifier
                                                        .padding(start = 16.dp, bottom = 16.dp)
                                                )
                                            }
                                            innerTextField()
                                        }
                                    }
                                )

                                if (pageIndex == totalPages - 1) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp)
                                            .background(Color.Transparent)
                                    ) {
                                        Text(
                                            text = "Page ends here",
                                            color = Color.Gray,
                                            fontSize = 12.sp,
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(end = 8.dp, bottom = 8.dp)
                                        )
                                    }
                                }
                            }

                            if (pageIndex < totalPages - 1) {
                                Divider(
                                    color = Color.Gray,
                                    thickness = 1.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                )
                            }
                        }

                        if (thought.text.length > 2000 && totalPages < 10) {
                            totalPages += 1
                        }
                    }
                }
            }

            if (showPopupMessage) {
                CenteredPopupMessage(message = "Your input is saved.") {
                    showPopupMessage = false
                }
            }

            if (showDownloadDialog) {
                AlertDialog(
                    onDismissRequest = { showDownloadDialog = false },
                    title = { Text(text = "Download As") },
                    text = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedFormat == "PDF",
                                    onClick = { selectedFormat = "PDF" }
                                )
                                Text(text = "PDF")
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedFormat == "Word",
                                    onClick = { selectedFormat = "Word" }
                                )
                                Text(text = "Word")
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (selectedFormat.isNotEmpty()) {
                                    showPreviewDialog = true
                                    showDownloadDialog = false
                                } else {
                                    // Show caution message if no option is selected
                                    Toast.makeText(context, "Select one option to proceed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDownloadDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showPreviewDialog) {
                AlertDialog(
                    onDismissRequest = { showPreviewDialog = false },
                    title = {
                        Text(text = "Document Preview")
                    },
                    text = {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Heading: ${headerText.text}\n\nThought:\n${thought.text}",
                                    modifier = Modifier.padding(16.dp),
                                    style = TextStyle(fontSize = 16.sp, color = Color.Black)
                                )
                            }
                            Text(text = "$selectedFormat format", color = Color.Gray, fontSize = 12.sp)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (selectedFormat == "PDF") {
                                createPdf(context, headerText.text, thought.text)
                            } else {
                                createWord(context, headerText.text, thought.text)
                            }
                            showPreviewDialog = false
                        }) {
                            Text("Download")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPreviewDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }


        // Home icon placed outside the card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.artboard_7),
                contentDescription = "Home Image",
                modifier = Modifier
                    .offset(y = -650.dp)
                    .size(70.dp)
                    .clickable { showGeminiDialog.value = true }
            )
        }
        if (showGeminiDialog.value) {
            GeminiDialog(showDialog = showGeminiDialog, text = text) { showGeminiDialog.value = false }
        }
    }
}



fun createPdf(context: Context, heading: String, thought: String) {
    val path = "/storage/emulated/0/Download/Slore-thought.pdf"
    val writer = PdfWriter(path)
    val pdfDoc = com.itextpdf.kernel.pdf.PdfDocument(writer)
    val document = Document(pdfDoc)
    document.add(Paragraph("Heading: $heading"))
    document.add(Paragraph("Thought: $thought"))
    document.close()
    Toast.makeText(context, "PDF downloaded successfully and it may appear in the files after a minute ", Toast.LENGTH_SHORT).show()
}

fun createWord(context: Context, heading: String, thought: String) {
    val path = "/storage/emulated/0/Download/Slore-thought.docx"
    val document = XWPFDocument()
    val fileOut = FileOutputStream(path)
    val paragraph = document.createParagraph()
    val run = paragraph.createRun()
    run.setText("Heading: $heading\n\nThought:\n$thought")
    document.write(fileOut)
    fileOut.close()
    Toast.makeText(context, "Word document downloaded successfully and it may appear in the files after a minute", Toast.LENGTH_SHORT).show()
}