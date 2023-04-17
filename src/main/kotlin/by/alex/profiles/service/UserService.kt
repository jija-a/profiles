package by.alex.profiles.service

import by.alex.profiles.dto.DtoUtil.toDto
import by.alex.profiles.dto.DtoUtil.toUser
import by.alex.profiles.dto.UserCreateRequest
import by.alex.profiles.dto.UserDto
import by.alex.profiles.dto.UserUpdateRequest
import by.alex.profiles.exception.DuplicateEntryException
import by.alex.profiles.exception.ErrorMessages
import by.alex.profiles.exception.NotFoundException
import by.alex.profiles.model.User
import by.alex.profiles.repository.UserRepository

import org.springframework.stereotype.Service

import java.security.InvalidParameterException

@Service
class UserService(private val userRepository: UserRepository) {

    fun getAllUsers(): List<UserDto> {
        return userRepository.findAll().map { it.toDto() }
    }

    fun getUserById(id: Long): UserDto {
        return userRepository.findById(id)
            .orElseThrow { NotFoundException(ErrorMessages.NOT_FOUND_ERROR, arrayOf(id)) }
            .toDto()
    }

    fun createUser(user: UserCreateRequest): UserDto {
        validateUserCreateRequest(user)
        return userRepository.save(user.toUser()).toDto()
    }

    fun updateUser(id: Long, user: UserUpdateRequest): UserDto {
        val existingUser = userRepository.findById(id)
            .orElseThrow { NotFoundException(ErrorMessages.NOT_FOUND_ERROR, arrayOf(id)) }
        updateUser(user, existingUser)
        return userRepository.save(existingUser).toDto()
    }

    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw NotFoundException(ErrorMessages.NOT_FOUND_ERROR, arrayOf(id))
        }
        userRepository.deleteById(id)
    }

    private fun validateUserCreateRequest(user: UserCreateRequest) {
        if (user.firstName.isBlank() || user.lastName.isBlank() || user.email.isBlank() || user.password.isBlank()) {
            throw InvalidParameterException(ErrorMessages.ALL_FIELDS_REQUIRED)
        }
        if (userRepository.existsByEmail(user.email)) {
            throw DuplicateEntryException(ErrorMessages.DUPLICATE_EMAIL, arrayOf(user.email))
        }
    }

    private fun updateUser(user: UserUpdateRequest, existingUser: User) {
        existingUser.apply {
            user.firstName?.takeIf { it.isNotBlank() }?.let { firstName = it }
            user.lastName?.takeIf { it.isNotBlank() }?.let { lastName = it }
            user.email?.takeIf { it.isNotBlank() }?.let {
                if (userRepository.findByEmail(it).isPresent) {
                    throw DuplicateEntryException(ErrorMessages.DUPLICATE_EMAIL, arrayOf(it))
                }
                email = it
            }
            user.password?.takeIf { it.isNotBlank() }?.let { password = it }
        }
    }
}
