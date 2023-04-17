package by.alex.profiles.dto

import by.alex.profiles.model.User

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty

import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class UserDto @JsonCreator constructor(
    @field:JsonProperty("id") val id: Long?,
    @field:JsonProperty("name") val name: String,
    @field:JsonProperty("email") val email: String,
    @field:JsonProperty("registrationDate") val registrationDate: String
) : Serializable {
    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    }
}

data class UserCreateRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
) : Serializable


data class UserUpdateRequest(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val password: String?
) : Serializable

object DtoUtil {
    fun User.toDto(): UserDto {
        val registrationDateString = this.registrationDate.format(UserDto.formatter)
        return UserDto(
            id = id,
            name = "$firstName $lastName",
            email = email,
            registrationDate = registrationDateString
        )
    }

    fun UserCreateRequest.toUser(): User {
        return User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            registrationDate = LocalDateTime.now()
        )
    }

    fun UserUpdateRequest.toUser(): User {
        return User(
            firstName = firstName ?: this.firstName.orEmpty(),
            lastName = lastName ?: this.lastName.orEmpty(),
            email = email ?: this.email.orEmpty(),
            password = password ?: this.password.orEmpty(),
            registrationDate = LocalDateTime.now()
        )
    }
}
