package by.alex.profiles.controller

import java.time.LocalDateTime

data class ErrorResponse(
    val status: Int,
    val messages: List<String>,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
