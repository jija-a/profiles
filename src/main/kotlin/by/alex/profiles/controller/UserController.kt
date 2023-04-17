package by.alex.profiles.controller

import by.alex.profiles.dto.UserCreateRequest
import by.alex.profiles.dto.UserDto
import by.alex.profiles.dto.UserUpdateRequest
import by.alex.profiles.service.UserService

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

import java.net.URI

@Controller
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun addUser(@RequestBody userDto: UserCreateRequest): ResponseEntity<UserDto> {
        val createdUser = userService.createUser(userDto)
        return ResponseEntity.created(URI("/api/v1/users/${createdUser.id}")).body(createdUser)
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: Long): ResponseEntity<UserDto> {
        val user = userService.getUserById(userId)
        return ResponseEntity.ok(user)
    }

    @PutMapping("/{userId}")
    fun updateUser(
        @PathVariable userId: Long,
        @RequestBody updateRequest: UserUpdateRequest
    ): ResponseEntity<UserDto> {
        val updatedUser = userService.updateUser(userId, updateRequest)
        return ResponseEntity.ok(updatedUser)
    }

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: Long): ResponseEntity<Any> {
        userService.deleteUser(userId)
        return ResponseEntity.noContent().build()
    }
}
