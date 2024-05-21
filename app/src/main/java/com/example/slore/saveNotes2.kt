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
    val id: String = "",
    val heading: String = "",
    val username: String = "",
    val password: String = "",
    val message: String = ""
)

data class Note(
    val id: String = "",
    val heading: String = "",
    val content: String = ""
)




