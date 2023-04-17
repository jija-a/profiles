package by.alex.profiles.service

import by.alex.profiles.dto.UserCreateRequest
import by.alex.profiles.exception.DuplicateEntryException
import by.alex.profiles.exception.ErrorMessages
import by.alex.profiles.exception.NotFoundException
import by.alex.profiles.repository.UserRepository
import by.alex.profiles.testutill.TestUser
import by.alex.profiles.testutill.TestUserUtil

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

import java.security.InvalidParameterException
import java.util.Optional

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserServiceTest {

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val messageSource = mockk<MessageSource>(relaxed = true)
    private val userService = UserService(userRepository)

    @Test
    fun `should return all users`() {
        val users = TestUserUtil.createNotEmptyUserList()
        every { userRepository.findAll() } returns users

        val expected = users.map { TestUserUtil.toDto(it) }
        val actual = userService.getAllUsers()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return user by id`() {
        val userId = 1L
        val user = TestUser().build()

        every { userRepository.findById(userId) } returns Optional.of(user)

        val expected = TestUserUtil.toDto(user)
        val actual = userService.getUserById(userId)

        assertThat(expected).isEqualTo(actual)
        verify(exactly = 1) { userRepository.findById(userId) }
    }

    @Test
    fun `should throw NotFoundException when user is not found by id`() {
        val userId = 1L

        every { userRepository.findById(userId) } returns Optional.empty()

        val exception = assertThrows(NotFoundException::class.java) {
            userService.getUserById(userId)
        }

        assertThat(exception.messageCode).isEqualTo(ErrorMessages.NOT_FOUND_ERROR)
        assertThat(exception.args).isNotEmpty.containsExactly(userId)
        verify(exactly = 1) { userRepository.findById(userId) }
    }

    @Test
    fun `should create user`() {
        val createRequest = TestUserUtil.buildCreateRequest()
        val user = TestUser().build()

        every { userRepository.existsByEmail(createRequest.email) } returns false
        every { userRepository.save(any()) } returns user

        val actual = userService.createUser(createRequest)

        assertThat(actual.id).isNotNull
        assertThat(actual.name).isEqualTo("${createRequest.firstName} ${createRequest.lastName}")
        assertThat(actual.email).isEqualTo(createRequest.email)
        verify(exactly = 1) { userRepository.existsByEmail(createRequest.email) }
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `should throw exception when user email already exists`() {
        val createRequest = TestUserUtil.buildCreateRequest()

        every { userRepository.existsByEmail(createRequest.email) } returns true

        val exception = assertThrows(DuplicateEntryException::class.java) {
            userService.createUser(createRequest)
        }

        assertThat(exception.messageCode).isEqualTo(ErrorMessages.DUPLICATE_EMAIL)
        assertThat(exception.args).isNotEmpty.containsExactly(createRequest.email)
        verify(exactly = 1) { userRepository.existsByEmail(createRequest.email) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should throw exception when user fields are blank`() {
        val user = UserCreateRequest("", "", "", "")

        val exception = assertThrows(InvalidParameterException::class.java) {
            userService.createUser(user)
        }

        assertThat(exception.message).isEqualTo(ErrorMessages.ALL_FIELDS_REQUIRED)
        verify(exactly = 0) { userRepository.existsByEmail(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should update user`() {
        val userId = 1L
        val existingUser = TestUser().build()
        val updateRequest = TestUserUtil.buildUpdateRequest()
        val expected = TestUserUtil.updateAndBuildDto(existingUser, updateRequest)

        every { userRepository.findById(userId) } returns Optional.of(existingUser)
        every { userRepository.save(any()) } answers { firstArg() }

        val actual = userService.updateUser(userId, updateRequest)

        assertThat(actual).isEqualTo(expected)
        verify(exactly = 1) { userRepository.findById(userId) }
        verify(exactly = 1) { userRepository.save(existingUser) }
    }

    @Test
    fun `should throw not found exception when updating non-existent user`() {
        val userId = 1L
        val updatedUser = TestUserUtil.buildUpdateRequest()

        every { userRepository.findById(userId) } returns Optional.empty()

        val exception = assertThrows(NotFoundException::class.java) {
            userService.updateUser(userId, updatedUser)
        }

        assertThat(exception.messageCode).isEqualTo(ErrorMessages.NOT_FOUND_ERROR)
        assertThat(exception.args).isNotEmpty.containsExactly(userId)
        verify(exactly = 1) { userRepository.findById(userId) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should throw duplicate entry exception when updating with existing email`() {
        val existingUser = TestUser().build()
        val updateRequest = TestUserUtil.buildUpdateRequest()

        every { userRepository.findById(existingUser.id!!) } returns Optional.of(existingUser)
        every { userRepository.findByEmail(updateRequest.email!!) } returns Optional.of(existingUser)

        val exception = assertThrows(DuplicateEntryException::class.java) {
            userService.updateUser(existingUser.id!!, updateRequest)
        }

        assertThat(exception.messageCode).isEqualTo(ErrorMessages.DUPLICATE_EMAIL)
        assertThat(exception.args).isNotEmpty.containsExactly(updateRequest.email)
        verify(exactly = 1) { userRepository.findById(existingUser.id!!) }
        verify(exactly = 1) { userRepository.findByEmail(updateRequest.email!!) }
        verify(exactly = 0) { userRepository.save(any()) }
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

        assertThat(exception.messageCode).isEqualTo(ErrorMessages.NOT_FOUND_ERROR)
        verify(exactly = 1) { userRepository.existsById(userId) }
    }
}
