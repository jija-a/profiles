package by.alex.profiles.controller

import by.alex.profiles.exception.DuplicateEntryException
import by.alex.profiles.exception.ErrorMessages
import by.alex.profiles.exception.NotFoundException

import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.NoHandlerFoundException

import java.util.Locale

@ControllerAdvice
class GlobalExceptionHandler(val messageSource: MessageSource) {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException, locale: Locale): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(ex.messageCode, ex.args, locale)
        val errorResponse = ErrorResponse(HttpStatus.NOT_FOUND.value(), listOf(message))
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(DuplicateEntryException::class)
    fun handleDuplicateEntryException(ex: DuplicateEntryException, locale: Locale): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(ex.messageCode, ex.args, locale)
        val errorResponse = ErrorResponse(HttpStatus.CONFLICT.value(), listOf(message))
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        ex: MethodArgumentNotValidException,
        locale: Locale
    ): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult
            .allErrors
            .map { it.defaultMessage ?: "Unknown error" }
        val errorResponse = ErrorResponse(HttpStatus.BAD_REQUEST.value(), errors)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        locale: Locale
    ): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(ErrorMessages.REQUEST_BODY_EMPTY, null, locale)
        val errorResponse = ErrorResponse(HttpStatus.BAD_REQUEST.value(), listOf(message))
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        locale: Locale
    ): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(ErrorMessages.NO_HANDLER_FOUND, null, locale)
        val errorResponse = ErrorResponse(HttpStatus.NOT_FOUND.value(), listOf(message))
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, locale: Locale): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(ErrorMessages.INTERNAL, null, locale)
        val errorResponse = ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), listOf(message))
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
