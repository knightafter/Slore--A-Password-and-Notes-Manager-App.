package com.example.slore.SignInProcess

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun HomeScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }

    fun deleteUserDataAndAccount(userId: String, onComplete: (Boolean, String) -> Unit) {
        db.collection("users").document(userId).collection("subCollection").get().addOnCompleteListener { subCollectionTask ->
            if (subCollectionTask.isSuccessful) {
                val batch = db.batch()
                val subCollectionDocs = subCollectionTask.result

                for (document in subCollectionDocs) {
                    batch.delete(document.reference)
                }

                batch.commit().addOnCompleteListener { batchTask ->
                    if (batchTask.isSuccessful) {
                        db.collection("users").document(userId).delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onComplete(true, "Account and data successfully deleted.")
                            } else {
                                onComplete(false, task.exception?.message ?: "Failed to delete user data.")
                            }
                        }
                    } else {
                        onComplete(false, batchTask.exception?.message ?: "Failed to delete user sub-collections.")
                    }
                }
            } else {
                onComplete(false, subCollectionTask.exception?.message ?: "Failed to retrieve user sub-collections.")
            }
        }
    }

    fun deleteUserAccount() {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            deleteUserDataAndAccount(userId) { success, message ->
                if (success) {
                    user.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            alertMessage = "Account successfully deleted."
                            showAlert = true
                        } else {
                            alertMessage = deleteTask.exception?.message ?: "Failed to delete account."
                            showAlert = true
                        }
                    }
                } else {
                    alertMessage = message
                    showAlert = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(8.dp)
                    .size(34.dp)
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Account Information",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://illustrations.popsy.co/blue/engineer.svg")
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.size(128.dp)
        )


        Spacer(modifier = Modifier.height(50.dp))

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {


            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(width = 310.dp, height = 400.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    SelectionContainer {
                        Text(
                            text = buildAnnotatedString {
                                append("Your Email: ")
                                withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                                    append(currentUser?.email ?: "")
                                }
                            },
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp, top = 4.dp, end = 8.dp)
                                .offset(y = 12.dp)
                        )
                    }

                    Button(
                        onClick = {
                            auth.signOut()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().offset(y = 40.dp)
                    ) {
                        Text("Logout", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth().offset(y = 40.dp),

                        ) {
                        Text("Delete Account", color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
        
        Text(text = "About Us" , fontSize = 18.sp)

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(width = 310.dp, height = 150.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "App is built by a solo developer. For any queries, you can contact me on Twitter.",
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Account") },
                text = { Text("Caution: Your account and all associated data will be deleted permanently.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            deleteUserAccount()
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showAlert) {
            AlertDialog(
                onDismissRequest = { showAlert = false },
                title = { Text("Alert") },
                text = { Text(alertMessage) },
                confirmButton = {
                    Button(
                        onClick = {
                            showAlert = false
                            if (alertMessage == "Account successfully deleted.") {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
