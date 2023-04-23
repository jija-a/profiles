package by.alex.profiles.testutill

import by.alex.profiles.dto.UserCreateRequest
import by.alex.profiles.dto.UserDto
import by.alex.profiles.dto.UserUpdateRequest
import by.alex.profiles.model.User

import java.time.LocalDateTime

/**
 * Class - builder for a test user entity.
 * If no methods in builder defined
 * e.g. `TestUser().build()` then initializes default fields
 * **/
class TestUser {

    companion object {
        const val registrationDate = "2023-04-10T10:00:00"
    }

    private var id: Long = 1L
    private var email: String = "john.doe@example.com"
    private var firstName: String = "John"
    private var lastName: String = "Doe"
    private var password: String = "password123"
    private var registrationDate: LocalDateTime = LocalDateTime.parse(TestUser.registrationDate)

    fun withId(id: Long): TestUser {
        this.id = id
        return this
    }

    fun build(): User {
        return User(id, firstName, lastName, email, password, mutableSetOf(), registrationDate)
    }
}

object TestUserUtil {

    fun toDto(user: User): UserDto =
        UserDto(user.id, "${user.firstName} ${user.lastName}", user.email, TestUser.registrationDate, emptyList())

    fun updateAndBuildDto(existingUser: User, cr: UserUpdateRequest): UserDto {
        cr.email.takeIf { it != null }?.let { existingUser.email = it }
        cr.password.takeIf { it != null }?.let { existingUser.password = it }
        cr.firstName.takeIf { it != null }?.let { existingUser.firstName = it }
        cr.lastName.takeIf { it != null }?.let { existingUser.lastName = it }
        return toDto(existingUser)
    }

    fun buildCreateRequest() =
        UserCreateRequest("John", "Doe", "john.doe@example.com", "password123")

    fun buildUpdateRequest() =
        UserUpdateRequest(
            firstName = "NewFirstName",
            lastName = null,
            email = "newemail@example.com",
            password = "newpassword"
        )

    fun buildDto() = toDto(TestUser().build())
}
