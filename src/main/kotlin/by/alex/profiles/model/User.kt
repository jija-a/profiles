package by.alex.profiles.model

import java.time.LocalDateTime

data class User(
    val id: Long? = null,
    var name: String,
    var surname: String,
    var email: String,
    var password: String,
    val registrationDate: LocalDateTime
)
