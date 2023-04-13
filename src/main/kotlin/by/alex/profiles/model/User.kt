package by.alex.profiles.model

import java.io.Serializable
import java.time.LocalDateTime

data class User(
    val id: Long? = null,
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String,
    var registrationDate: LocalDateTime = LocalDateTime.now(),
    var role: Role = Role.USER
) : Serializable
