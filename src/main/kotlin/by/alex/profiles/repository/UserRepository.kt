package by.alex.profiles.repository

import by.alex.profiles.model.User

import org.springframework.data.jpa.repository.JpaRepository

import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): Optional<User>
}
