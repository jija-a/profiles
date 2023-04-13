package by.alex.profiles.service

import by.alex.profiles.exception.DuplicateEntryException
import by.alex.profiles.exception.NotFoundException
import by.alex.profiles.model.User
import by.alex.profiles.repository.UserRepository
import org.springframework.stereotype.Service
import java.security.InvalidParameterException

@Service
class UserService(private val userRepository: UserRepository) {

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun getUserById(id: Long): User {
        return userRepository.findById(id).orElseThrow { NotFoundException("User with id=$id not found") }
    }

    fun createUser(user: User): User {
        if (user.id != null) {
            throw InvalidParameterException("User ID must be null when creating a new user")
        }
        if (userRepository.existsByEmail(user.email)) {
            throw DuplicateEntryException("User with email ${user.email} already exists")
        }
        if (user.name.isBlank() || user.surname.isBlank() || user.email.isBlank() || user.password.isBlank()) {
            throw InvalidParameterException("All fields are required")
        }
        return userRepository.save(user)
    }

    fun updateUser(id: Long, user: User): User {
        val existingUser = userRepository.findById(id).orElseThrow { NotFoundException("User with id=$id not found") }
        if (user.name.isNotBlank()) {
            existingUser.name = user.name
        }
        if (user.surname.isNotBlank()) {
            existingUser.surname = user.surname
        }
        if (user.email.isNotBlank()) {
            if (userRepository.findByEmail(user.email).isPresent) {
                throw DuplicateEntryException("User with email ${user.email} already exists")
            }
            existingUser.email = user.email
        }
        if (user.password.isNotBlank()) {
            existingUser.password = user.password
        }
        return userRepository.save(existingUser)
    }

    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw NotFoundException("User with id=$id not found")
        }
        userRepository.deleteById(id)
    }
}
