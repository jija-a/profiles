package by.alex.profiles.service

import by.alex.profiles.exception.DuplicateEntryException
import by.alex.profiles.exception.NotFoundException
import by.alex.profiles.model.User
import by.alex.profiles.repository.UserRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.InvalidParameterException
import java.time.LocalDateTime
import java.util.*

class UserServiceTest {

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val userService = UserService(userRepository)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `should return all users`() {
        val user1 = User(
            id = 1,
            name = "John",
            surname = "Doe",
            email = "johndoe@example.com",
            password = "password123",
            registrationDate = LocalDateTime.now()
        )
        val user2 = User(
            id = 2,
            name = "John",
            surname = "Doe",
            email = "johndoe@example.com",
            password = "password456",
            registrationDate = LocalDateTime.now()
        )
        every { userRepository.findAll() } returns listOf(user1, user2)

        val result = userService.getAllUsers()

        assertEquals(listOf(user1, user2), result)
    }

    @Test
    fun `should return user by id`() {
        val userId = 1L
        val user = User(
            id = userId,
            name = "John",
            surname = "Doe",
            email = "johndoe@example.com",
            password = "password123",
            registrationDate = LocalDateTime.now()
        )
        every { userRepository.findById(userId) } returns Optional.of(user)

        val result = userService.getUserById(userId)

        assertEquals(user, result)
    }

    @Test
    fun `should throw NotFoundException when user is not found by id`() {
        val userId = 1L
        every { userRepository.findById(userId) } returns Optional.empty()

        val exception = assertThrows(NotFoundException::class.java) {
            userService.getUserById(userId)
        }
        assertEquals("User with id=$userId not found", exception.message)
    }

    @Test
    fun `should create user`() {
        val user = User(
            name = "John",
            surname = "Doe",
            email = "johndoe@example.com",
            password = "password123",
            registrationDate = LocalDateTime.now()
        )
        every { userRepository.existsByEmail(user.email) } returns false
        every { userRepository.save(user) } returns user

        val result = userService.createUser(user)

        assertEquals(user, result)
        verify(exactly = 1) { userRepository.save(user) }
    }

    @Test
    fun `should throw exception when user id is not null`() {
        val user = User(
            id = 1,
            name = "John",
            surname = "Doe",
            email = "johndoe@example.com",
            password = "password123",
            registrationDate = LocalDateTime.now()
        )

        val exception = assertThrows(InvalidParameterException::class.java) {
            userService.createUser(user)
        }
        assertEquals("User ID must be null when creating a new user", exception.message)
    }

    @Test
    fun `should throw exception when user email already exists`() {
        val user = User(
            name = "John",
            surname = "Doe",
            email = "johndoe@example.com",
            password = "password123",
            registrationDate = LocalDateTime.now()
        )
        every { userRepository.existsByEmail(user.email) } returns true

        val exception = assertThrows(DuplicateEntryException::class.java) {
            userService.createUser(user)
        }
        assertEquals("User with email ${user.email} already exists", exception.message)
    }

    @Test
    fun `should throw exception when user fields are blank`() {
        val user =
            User(name = "", surname = "", email = "", password = "", registrationDate = LocalDateTime.now())

        every { userRepository.existsByEmail(user.email) } returns false

        val exception = assertThrows(InvalidParameterException::class.java) {
            userService.createUser(user)
        }
        assertEquals("All fields are required", exception.message)
    }

    @Test
    fun `should update user`() {
        val existingUser = User(
            id = 1,
            name = "Jane",
            surname = "Doe",
            email = "janedoe@test.com",
            password = "oldpassword",
            registrationDate = LocalDateTime.now()
        )
        val updatedUser = User(
            id = 1,
            name = "Jane",
            surname = "Doe",
            email = "janedoe@test.com",
            password = "newpassword",
            registrationDate = LocalDateTime.now()
        )
        every { userRepository.findById(existingUser.id!!) } returns Optional.of(existingUser)
        every { userRepository.findByEmail(updatedUser.email) } returns Optional.empty()
        every {
            userRepository.save(existingUser)
        } returns
                existingUser.copy(
                    name = "Jane",
                    surname = "Doe",
                    email = "janedoe@test.com",
                    password = "newpassword"
                )

        val result = userService.updateUser(existingUser.id!!, updatedUser)

        assertEquals(result.name, updatedUser.name)
        assertEquals(result.surname, updatedUser.surname)
        assertEquals(result.email, updatedUser.email)
        assertEquals(result.password, updatedUser.password)
    }

    @Test
    fun `should throw not found exception when updating non-existent user`() {
        val nonExistentUserId = 1L
        val updatedUser = User(
            name = "Jane",
            surname = "Doe",
            email = "janedoe@test.com",
            password = "password123",
            registrationDate = LocalDateTime.now()
        )

        every { userRepository.findById(nonExistentUserId) } returns Optional.empty()

        val exception = assertThrows(NotFoundException::class.java) {
            userService.updateUser(nonExistentUserId, updatedUser)
        }
        assertEquals("User with id=$nonExistentUserId not found", exception.message)
    }

    @Test
    fun `should throw duplicate entry exception when updating with existing email`() {
        val existingUser = User(1, "John", "Doe", "johndoe@test.com", "oldpassword", LocalDateTime.now())
        val updatedUser = User(
            name = "Jane",
            surname = "Doe",
            email = "janedoe@test.com",
            password = "password123",
            registrationDate = LocalDateTime.now()
        )
        every { userRepository.findById(existingUser.id!!) } returns Optional.of(existingUser)
        every { userRepository.findByEmail(updatedUser.email) } returns Optional.of(existingUser)

        val exception = assertThrows(DuplicateEntryException::class.java) {
            userService.updateUser(existingUser.id!!, updatedUser)
        }

        assertEquals("User with email ${updatedUser.email} already exists", exception.message)
    }

    @Test
    fun `deleteUser should delete user with specified id`() {
        val userId = 1L

        every { userRepository.existsById(userId) } returns true

        userService.deleteUser(userId)

        verify(exactly = 1) { userRepository.deleteById(userId) }
    }

    @Test
    fun `deleteUser should throw NotFoundException if user with specified id does not exist`() {
        val userId = 1L

        every { userRepository.existsById(userId) } returns false

        val exception = assertThrows(NotFoundException::class.java) {
            userService.deleteUser(userId)
        }

        assertEquals("User with id=$userId not found", exception.message)
    }
}
