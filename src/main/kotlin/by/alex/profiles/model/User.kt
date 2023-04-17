package by.alex.profiles.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

import java.time.LocalDateTime

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var registrationDate: LocalDateTime = LocalDateTime.now(),
    var role: Role = Role.USER
) {
    constructor() : this(null, "", "", "", "", LocalDateTime.now(), Role.USER)
}
