package by.alex.profiles.controller

import by.alex.profiles.dto.UserCreateRequest
import by.alex.profiles.dto.UserDto
import by.alex.profiles.dto.UserUpdateRequest
import by.alex.profiles.service.UserService

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @Operation(summary = "Create user", description = "Creates a new user with the provided information.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "User created successfully. Returns the created user data.",
                content = [Content(
                    schema = (Schema(type = "userDto", implementation = UserDto::class))
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "User data failed to pass validation.",
                content = [Content(
                    schema = (Schema(type = "errorResponse", implementation = ErrorResponse::class))
                )]
            ),
            ApiResponse(
                responseCode = "409",
                description = "User with the same email address already exists.",
                content = [Content(
                    schema = (Schema(type = "errorResponse", implementation = ErrorResponse::class))
                )]
            )
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addUser(@Validated @RequestBody createRequest: UserCreateRequest): UserDto {
        return userService.createUser(createRequest)
    }

    @Operation(summary = "Get all users", description = "Returns a list of all existing users.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The list of users is returned successfully.",
                content = [Content(
                    schema = Schema(type = "list", implementation = UserDto::class)
                )]
            )]
    )
    @GetMapping
    fun getAllUsers(): List<UserDto> {
        return userService.getAllUsers()
    }

    @Operation(summary = "Get user by ID", description = "Returns a user with specified ID.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The user is returned successfully.",
                content = [Content(
                    schema = Schema(type = "userDto", implementation = UserDto::class)
                )]
            )]
    )
    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: Long): UserDto {
        return userService.getUserById(userId)
    }

    @Operation(
        summary = "Update user",
        description = "Updates a user with the provided information."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "User updated successfully. Returns the updated user data.",
                content = [Content(
                    schema = (Schema(type = "userDto", implementation = UserDto::class))
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Indicates that the user data failed to pass validation.",
                content = [Content(
                    schema = (Schema(type = "errorResponse", implementation = ErrorResponse::class))
                )]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Indicates that a user with the same email address already exists.",
                content = [Content(
                    schema = (Schema(type = "errorResponse", implementation = ErrorResponse::class))
                )]
            )
        ]
    )
    @PutMapping("/{userId}")
    fun updateUser(
        @PathVariable userId: Long,
        @Validated @RequestBody updateRequest: UserUpdateRequest
    ): UserDto {
        return userService.updateUser(userId, updateRequest)
    }

    @Operation(
        summary = "Delete user by ID",
        description = "Deletes the user with the specified ID."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "The user was successfully deleted.",
            ),
            ApiResponse(
                responseCode = "404",
                description = "The user with the specified ID was not found.",
                content = [Content(
                    schema = (Schema(type = "errorResponse", implementation = ErrorResponse::class))
                )]
            )
        ]
    )
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable userId: Long) {
        userService.deleteUser(userId)
    }
}
