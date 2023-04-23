package by.alex.profiles.controller

import by.alex.profiles.exception.DuplicateEntryException
import by.alex.profiles.exception.ErrorMessages
import by.alex.profiles.exception.NotFoundException

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse

import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException

import java.util.Locale

@RestControllerAdvice
class GlobalExceptionHandler(val messageSource: MessageSource) {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
        responseCode = "400",
        description = "The request body contains invalid or missing fields",
        content = [Content(
            schema = (Schema(type = "errorResponse", implementation = ErrorResponse::class))
        )]
    )
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException, locale: Locale): ErrorResponse {
        val errors = ex.bindingResult
            .allErrors
            .map { it.defaultMessage ?: "Unknown error" }
        return ErrorResponse(HttpStatus.BAD_REQUEST.value(), errors)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
        responseCode = "400",
        description = "The request body could not be read or was missing",
        content = [Content(
            schema = (Schema(type = "errorResponse", implementation = ErrorResponse::class))
        )]
    )
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException, locale: Locale): ErrorResponse {
        val message = messageSource.getMessage(ErrorMessages.REQUEST_BODY_EMPTY, null, locale)
        return ErrorResponse(HttpStatus.BAD_REQUEST.value(), listOf(message))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
        responseCode = "400",
        description = "The request could not be processed due to invalid arguments",
        content = [Content(
            schema = (Schema(type = "errorResponse", implementation = ErrorResponse::class))
        )]
    )
    fun handleMethodArgumentTypeMismatchException(
        ex: MethodArgumentTypeMismatchException,
        locale: Locale
    ): ErrorResponse {
        val message =
            messageSource.getMessage(ErrorMessages.METHOD_ARG_TYPE_MISMATCH, arrayOf(ex.name, ex.requiredType), locale)
        return ErrorResponse(HttpStatus.BAD_REQUEST.value(), listOf(message))
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(
        responseCode = "404",
        description = "The requested resource was not found",
        content = [Content(
            schema = (Schema(type = "errorResponse", implementation = ErrorResponse::class))
        )]
    )
    fun handleNoHandlerFoundException(ex: NoHandlerFoundException, locale: Locale): ErrorResponse {
        val message = messageSource.getMessage(ErrorMessages.NO_HANDLER_FOUND, null, locale)
        return ErrorResponse(HttpStatus.NOT_FOUND.value(), listOf(message))
    }

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Hidden
    fun handleNotFoundException(ex: NotFoundException, locale: Locale): ErrorResponse {
        val message = messageSource.getMessage(ex.messageCode, ex.args, locale)
        return ErrorResponse(HttpStatus.NOT_FOUND.value(), listOf(message))
    }

    @ExceptionHandler(DuplicateEntryException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    fun handleDuplicateEntryException(ex: DuplicateEntryException, locale: Locale): ErrorResponse {
        val message = messageSource.getMessage(ex.messageCode, ex.args, locale)
        return ErrorResponse(HttpStatus.CONFLICT.value(), listOf(message))
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(
        responseCode = "500",
        description = "Indicates that an unexpected error occurred while processing request.",
        content = [Content(
            schema = (Schema(type = "errorResponse", implementation = ErrorResponse::class))
        )]
    )
    fun handleException(ex: Exception, locale: Locale): ErrorResponse {
        val message = messageSource.getMessage(ErrorMessages.INTERNAL, null, locale)
        return ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), listOf(message))
    }
}
