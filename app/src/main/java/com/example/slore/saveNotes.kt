package com.example.slore


data class Note(
    val heading: String = "",
    val content: String = ""
)

data class PasswordEntry(
    val heading: String,
    val username: String,
    val password: String,
    val memorableNotes: String,
    val message: String  // New field
)


data class EmailEntry(
    val heading: String,
    val sender: String,
    val recipient: String,
    val subject: String,
    val content: String
)

data class MakeYourOwnEntry(
    val heading: String,
    val content: String
)

