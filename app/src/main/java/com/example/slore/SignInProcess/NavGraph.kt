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
        composable("password") { PasswordScreen(navController = navController) }
        composable("thoughts") { ThoughtsScreen(navController = navController) }
        composable("notes") { NotesScreen(navController = navController) }
        composable("Emails") { EmailsScreen(navController = navController) }
        composable("home") { HomeScreen(navController) }
        composable("forgotPassword") { ForgotPasswordScreen(navController = navController) }
        composable("openCategory") { OpenCategoryScreen(navController = navController) }
        composable("openTimeline") { OpenTimelineScreen(navController = navController) }
        composable("passwords") { PasswordsScreenCategory(navController = navController) }//using this composable in the OpenCategoryScreen composable
        composable("emailsscreen") { EmailsEntryScreenCategory(navController = navController) }//using this composable in the OpenCategoryScreen composable
        composable("hello") { ThoughtsEntryScreenCategory(navController = navController) }//using this composable in the OpenCategoryScreen composable
        composable("notes1") { NotesEntryScreenCategory(navController = navController) }//using this composable in the OpenCategoryScreen composable

        composable("thoughtDetail/{thoughtId}") { backStackEntry ->
            val thoughtId = backStackEntry.arguments?.getString("thoughtId")
            if (thoughtId != null) {
                ThoughtDetailScreen(navController = navController, thoughtId = thoughtId)
            }
        }

        composable("passwordDetail/{passwordId}") { backStackEntry ->
            val passwordId = backStackEntry.arguments?.getString("passwordId")
            if (passwordId != null) {
                PasswordDetailScreen(navController = navController, passwordId = passwordId)
            }
        }

        composable("emailDetail/{emailId}") { backStackEntry ->
            val emailId = backStackEntry.arguments?.getString("emailId")
            if (emailId != null) {
                EmailDetailScreen(navController = navController, emailId = emailId)
            }
        }


        composable("noteDetail/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            if (noteId != null) {
                NoteDetailScreen(navController = navController, noteId = noteId)
            }
        }

    }
}
