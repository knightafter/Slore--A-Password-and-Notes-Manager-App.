package com.example.slore



/*
* this data class model is to display the data from firestore
*
* */
sealed class CategoryItem

data class PasswordEntry(
    val heading: String = "",
    val username: String = "",
    val password: String = "",
    val memorableNotes: String = "",
    val message: String = ""
) : CategoryItem()

data class EmailEntry(
    val heading: String = "",
    val sender: String = "",
    val recipient: String = "",
    val subject: String = "",
    val content: String = ""
) : CategoryItem()

data class Note(
    val heading: String = "",
    val content: String = ""
) : CategoryItem()
