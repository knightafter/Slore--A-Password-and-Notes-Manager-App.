package com.example.slore



/*
* this data class model is to display the data from firestore
*
* */
sealed class CategoryItem

data class PasswordEntry(
    var id: String = "",
    var heading: String = "",
    var username: String = "",
    var password: String = "",
    var memorableNotes: String = "",
    var message: String = ""
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


