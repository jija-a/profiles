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

@Service
class UserService(private val userRepository: UserRepository) {

    fun getAllUsers(): List<UserDto> {
        return userRepository.findAll().map { it.toDto() }
    }

    fun getUserById(id: Long): UserDto {
        return userRepository.findById(id)
            .orElseThrow { NotFoundException(ErrorMessages.RESOURCE_NOT_FOUND, arrayOf(id)) }
            .toDto()
    }

    fun createUser(createRequest: UserCreateRequest): UserDto {
        throwDuplicateIfEmailExists(createRequest.email)
        return userRepository.save(createRequest.toUser()).toDto()
    }

    fun updateUser(id: Long, user: UserUpdateRequest): UserDto {
        val existingUser = userRepository.findById(id)
            .orElseThrow { NotFoundException(ErrorMessages.RESOURCE_NOT_FOUND, arrayOf(id)) }
        updateUser(user, existingUser)
        return userRepository.save(existingUser).toDto()
    }

    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw NotFoundException(ErrorMessages.RESOURCE_NOT_FOUND, arrayOf(id))
        }
        userRepository.deleteById(id)
    }

    private fun updateUser(updateRequest: UserUpdateRequest, existingUser: User) {
        existingUser.apply {
            updateRequest.firstName?.takeIf { it.isNotBlank() }?.let { firstName = it }
            updateRequest.lastName?.takeIf { it.isNotBlank() }?.let { lastName = it }
            updateRequest.email?.takeIf { it.isNotBlank() }?.let {
                throwDuplicateIfEmailExists(updateRequest.email)
                email = it
            }
            updateRequest.password?.takeIf { it.isNotBlank() }?.let { password = it }
        }
    }

    private fun throwDuplicateIfEmailExists(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw DuplicateEntryException(ErrorMessages.DUPLICATE_EMAIL, arrayOf(email))
        }
    }
}
