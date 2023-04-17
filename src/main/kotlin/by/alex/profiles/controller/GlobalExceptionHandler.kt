package by.alex.profiles.controller

import by.alex.profiles.exception.DuplicateEntryException
import by.alex.profiles.exception.ErrorMessages
import by.alex.profiles.exception.NotFoundException

import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

import java.util.Locale

@RestControllerAdvice
class GlobalExceptionHandler(val messageSource: MessageSource) {

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(ex: NotFoundException, locale: Locale): ErrorResponse {
        val message = messageSource.getMessage(ex.messageCode, ex.args, locale)
        return ErrorResponse(HttpStatus.NOT_FOUND.value(), listOf(message))
    }

    @ExceptionHandler(DuplicateEntryException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDuplicateEntryException(ex: DuplicateEntryException, locale: Locale): ErrorResponse {
        val message = messageSource.getMessage(ex.messageCode, ex.args, locale)
        return ErrorResponse(HttpStatus.CONFLICT.value(), listOf(message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException, locale: Locale): ErrorResponse {
        val errors = ex.bindingResult
            .allErrors
            .map { it.defaultMessage ?: "Unknown error" }
        return ErrorResponse(HttpStatus.BAD_REQUEST.value(), errors)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException, locale: Locale): ErrorResponse {
        val message = messageSource.getMessage(ErrorMessages.REQUEST_BODY_EMPTY, null, locale)
        return ErrorResponse(HttpStatus.BAD_REQUEST.value(), listOf(message))
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoHandlerFoundException(ex: NoHandlerFoundException, locale: Locale): ErrorResponse {
        val message = messageSource.getMessage(ErrorMessages.NO_HANDLER_FOUND, null, locale)
        return ErrorResponse(HttpStatus.NOT_FOUND.value(), listOf(message))
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(ex: Exception, locale: Locale): ErrorResponse {
        val message = messageSource.getMessage(ErrorMessages.INTERNAL, null, locale)
        return ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), listOf(message))
    }
}
