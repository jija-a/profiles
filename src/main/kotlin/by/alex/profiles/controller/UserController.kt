package by.alex.profiles.controller

import by.alex.profiles.dto.UserCreateRequest
import by.alex.profiles.dto.UserDto
import by.alex.profiles.dto.UserUpdateRequest
import by.alex.profiles.service.UserService
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addUser(@Validated @RequestBody userDto: UserCreateRequest): UserDto {
        return userService.createUser(userDto)
    }

    @GetMapping
    fun getAllUsers(): List<UserDto> {
        return userService.getAllUsers()
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: Long): UserDto {
        return userService.getUserById(userId)
    }

    @PutMapping("/{userId}")
    fun updateUser(@PathVariable userId: Long, @Validated @RequestBody updateRequest: UserUpdateRequest): UserDto {
        return userService.updateUser(userId, updateRequest)
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable userId: Long) {
        userService.deleteUser(userId)
    }
}
