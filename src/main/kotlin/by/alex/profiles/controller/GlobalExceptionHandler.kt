package by.alex.profiles.controller

import by.alex.profiles.exception.DuplicateEntryException
import by.alex.profiles.exception.EmptyParameterException
import by.alex.profiles.exception.ErrorMessages
import by.alex.profiles.exception.NotFoundException
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.*

@ControllerAdvice
class GlobalExceptionHandler(val messageSource: MessageSource) {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException, locale: Locale): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(ex.messageCode, ex.args, locale)
        val errorResponse = ErrorResponse(HttpStatus.NOT_FOUND.value(), message)
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(DuplicateEntryException::class)
    fun handleDuplicateEntryException(ex: DuplicateEntryException, locale: Locale): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(ex.messageCode, ex.args, locale)
        val errorResponse = ErrorResponse(HttpStatus.CONFLICT.value(), message)
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(EmptyParameterException::class)
    fun handleEmptyParameterException(ex: EmptyParameterException, locale: Locale): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(ex.messageCode, null, locale)
        val errorResponse = ErrorResponse(HttpStatus.BAD_REQUEST.value(), message)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, locale: Locale): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(ErrorMessages.INTERNAL, null, locale)
        val errorResponse = ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message)
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
