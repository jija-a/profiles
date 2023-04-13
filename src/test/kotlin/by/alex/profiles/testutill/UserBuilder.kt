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

    fun withEmail(email: String): TestUser {
        this.email = email
        return this
    }

    fun withFirstName(firstName: String): TestUser {
        this.firstName = firstName
        return this
    }

    fun withLastName(lastName: String): TestUser {
        this.lastName = lastName
        return this
    }

    fun withPassword(password: String): TestUser {
        this.password = password
        return this
    }

    fun withRegistrationDate(registrationDate: LocalDateTime): TestUser {
        this.registrationDate = registrationDate
        return this
    }

    fun build(): User {
        return User(id, firstName, lastName, email, password, registrationDate)
    }
}

object TestUserUtil {

    fun toDto(user: User): UserDto =
        UserDto(user.id, "${user.firstName} ${user.lastName}", user.email, TestUser.registrationDate)

    fun toDto(cr: UserCreateRequest): UserDto {
        val user: User = TestUser()
            .withFirstName(cr.firstName)
            .withLastName(cr.lastName)
            .withEmail(cr.email)
            .withPassword(cr.password)
            .build()
        return toDto(user)
    }

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
            firstName = "Jane",
            lastName = null,
            email = "janedoe@test.com",
            password = "newpassword"
        )

    fun createNotEmptyUserList() =
        listOf(
            TestUser().withId(1L).build(),
            TestUser().withId(2L).build()
        )
}
