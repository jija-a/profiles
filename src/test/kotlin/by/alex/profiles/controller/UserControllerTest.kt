package by.alex.profiles.controller

import by.alex.profiles.dto.UserCreateRequest
import by.alex.profiles.exception.DuplicateEntryException
import by.alex.profiles.exception.NotFoundException
import by.alex.profiles.service.UserService
import by.alex.profiles.testutill.TestUser
import by.alex.profiles.testutill.TestUserUtil

import com.fasterxml.jackson.databind.ObjectMapper

import io.mockk.mockk

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing

import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class UserControllerTest {

    @Mock
    private lateinit var userService: UserService

    @InjectMocks
    private lateinit var userController: UserController

    private lateinit var exceptionHandler: GlobalExceptionHandler

    private val objectMapper = ObjectMapper()

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        val messageSource = mockk<MessageSource>(relaxed = true)
        exceptionHandler = GlobalExceptionHandler(messageSource)
        mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setControllerAdvice(exceptionHandler)
            .build()
    }

    @Test
    fun `should create user successfully`() {
        val createRequest = TestUserUtil.buildCreateRequest()
        val createdUser = TestUserUtil.toDto(createRequest)

        `when`(userService.createUser(createRequest)).thenReturn(createdUser)

        val request = MockMvcRequestBuilders.post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest))

        val result = mockMvc.perform(request)

        result.andExpect(status().isCreated)
            .andExpect(content().json(objectMapper.writeValueAsString(createdUser)))
    }

    @Test
    fun `should return 400 if fields are empty when create user`() {
        val createRequest = UserCreateRequest("", "", "", "")

        val request = MockMvcRequestBuilders.post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest))

        val result = mockMvc.perform(request)

        result.andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
    }

    @Test
    fun `should return a list of all users`() {
        val expected = TestUserUtil.createNotEmptyUserList().map { TestUserUtil.toDto(it) }

        `when`(userService.getAllUsers()).thenReturn(expected)

        val request = MockMvcRequestBuilders.get("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)

        val result = mockMvc.perform(request)

        result.andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
    }

    @Test
    fun `should return a user by id`() {
        val userDto = TestUserUtil.toDto(TestUser().build())

        `when`(userService.getUserById(userDto.id!!)).thenReturn(userDto)

        val request = MockMvcRequestBuilders.get("/api/v1/users/${userDto.id}")

        val result = mockMvc.perform(request)

        result.andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(userDto)))
    }

    @Test
    fun `should return 404 error when user is not found`() {
        val userId = 1L
        `when`(userService.getUserById(userId)).thenThrow(NotFoundException::class.java)

        val request = MockMvcRequestBuilders.get("/api/v1/users/$userId")
            .accept(MediaType.APPLICATION_JSON)

        val result = mockMvc.perform(request)

        result.andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
    }

    @Test
    fun `should update a user successfully`() {
        val userId = 1L
        val existingUser = TestUser().build()
        val updateRequest = TestUserUtil.buildUpdateRequest()
        val updatedUser = TestUserUtil.updateAndBuildDto(existingUser, updateRequest)

        `when`(userService.updateUser(userId, updateRequest)).thenReturn(updatedUser)

        val request = MockMvcRequestBuilders.put("/api/v1/users/${userId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updateRequest))

        val result = mockMvc.perform(request)

        result.andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(updatedUser)))
    }

    @Test
    fun `should return 409 when update user update email on existing`() {
        val userId = 1L
        val updateRequest = TestUserUtil.buildUpdateRequest()

        `when`(userService.updateUser(userId, updateRequest)).thenThrow(DuplicateEntryException::class.java)

        val request = MockMvcRequestBuilders.put("/api/v1/users/${userId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updateRequest))

        val result = mockMvc.perform(request)

        result.andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
    }

    @Test
    fun `should return 204 when delete user`() {
        val userId = 1L

        doNothing().`when`(userService).deleteUser(userId)

        val request = MockMvcRequestBuilders.delete("/api/v1/users/${userId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)

        val result = mockMvc.perform(request)

        result.andExpect(status().isNoContent)
    }
}
