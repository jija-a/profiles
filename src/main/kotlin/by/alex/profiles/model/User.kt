package by.alex.profiles.model

import java.util.Date

data class User(
    val id: Long? = null,
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val registrationDate: Date
)
