package by.alex.profiles.dto

import by.alex.profiles.model.User

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

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
    @field:NotBlank(message = "{user.name.notBlank}")
    @field:Size(min = 1, max = 50, message = "{user.name.size}")
    val firstName: String,
    @field:NotBlank(message = "{user.name.notBlank}")
    @field:Size(min = 1, max = 50, message = "{user.name.size}")
    val lastName: String,
    @field:Email(message = "{user.email.valid}")
    @field:NotBlank(message = "{user.email.notBlank}")
    @field:Size(min = 1, max = 100, message = "{user.email.size}")
    val email: String,
    @field:NotBlank(message = "{user.password.notBlank}")
    @field:Size(min = 6, max = 20, message = "{user.password.size}")
    val password: String
) : Serializable

data class UserUpdateRequest(
    @field:Size(min = 1, max = 50, message = "{user.name.size}")
    val firstName: String?,
    @field:Size(min = 1, max = 50, message = "{user.name.size}")
    val lastName: String?,
    @field:Email(message = "{user.email.valid}")
    @field:Size(min = 1, max = 100, message = "{user.email.size}")
    val email: String?,
    @field:Size(min = 6, max = 20, message = "{user.password.size}")
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
}
