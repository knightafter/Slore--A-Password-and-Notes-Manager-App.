package com.example.slore

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.slore.SignInProcess.ForgotPasswordScreen
import com.example.slore.SignInProcess.HomeScreen
import com.example.slore.SignInProcess.LoginScreen
import com.example.slore.SignInProcess.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    NavHost(
        navController = navController,
        startDestination = if (auth.currentUser != null) "main" else "login"
    ) {
        composable("login") { LoginScreen(navController = navController) }
        composable("signup") { SignUpScreen(navController = navController) }
        composable("main") { MainContent(navController = navController) }
        composable("password") { PasswordScreen() }
        composable("thoughts") { ThoughtsScreen() }
        composable("notes") { NotesScreen() }
        composable("makeYourOwn/{inputText}") { backStackEntry ->
            val inputText = backStackEntry.arguments?.getString("inputText")
            MakeYourOwnScreen(inputText)
        }
        composable("home") { HomeScreen(navController) }
        composable("forgotPassword") {
            ForgotPasswordScreen(navController = navController)
        }
    }
}
