package by.alex.profiles.controller

import by.alex.profiles.exception.DuplicateEntryException
import by.alex.profiles.exception.ErrorMessages
import by.alex.profiles.exception.NotFoundException
import io.mockk.every
import io.mockk.mockk

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.validation.*
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.servlet.NoHandlerFoundException

import java.lang.reflect.Executable
import java.util.Locale

@ExtendWith(SpringExtension::class)
@SpringBootTest
class GlobalExceptionHandlerTest {

    @Autowired
    private lateinit var messageSource: MessageSource

    private lateinit var exHandler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        exHandler = GlobalExceptionHandler(messageSource)
    }

    @Test
    fun `should handle NotFoundException`() {
        val ex = NotFoundException(ErrorMessages.RESOURCE_NOT_FOUND, arrayOf(1))
        val locale = Locale.ENGLISH
        val expectedMessage = messageSource.getMessage(ErrorMessages.RESOURCE_NOT_FOUND, arrayOf(1), locale)

        val response = exHandler.handleNotFoundException(ex, locale)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body?.messages).containsExactly(expectedMessage)
    }

    @Test
    fun `should handle DuplicateEntryException`() {
        val ex = DuplicateEntryException(ErrorMessages.DUPLICATE_EMAIL, arrayOf(1))
        val locale = Locale.ENGLISH
        val expectedMessage = messageSource.getMessage(ErrorMessages.DUPLICATE_EMAIL, arrayOf(1), locale)

        val response = exHandler.handleDuplicateEntryException(ex, locale)

        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(response.body?.messages).containsExactly(expectedMessage)
    }

    @Test
    fun `should handle MethodArgumentNotValidException`() {
        val error = FieldError("user", "firstName", "Name must not be blank.")
        val locale = Locale.ENGLISH
        val br = mockk<BindingResult> {
            every { allErrors } returns listOf(error)
        }
        val ex = MethodArgumentNotValidException(mockk<Executable>(), br)

        val response = exHandler.handleMethodArgumentNotValidException(ex, locale)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.messages).containsExactly(error.defaultMessage)
    }

    @Test
    fun `should handle HttpMessageNotReadableExceptionTest`() {
        val locale = Locale.ENGLISH
        val exception = HttpMessageNotReadableException("Test error message", mockk<HttpInputMessage>())
        val expectedMessage = messageSource.getMessage(ErrorMessages.REQUEST_BODY_EMPTY, null, locale)

        val response = exHandler.handleHttpMessageNotReadableException(exception, locale)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertThat(response.body?.messages).containsExactly(expectedMessage)
    }

    @Test
    fun `should handle NoHandlerFoundException`() {
        val locale = Locale.ENGLISH
        val exception = NoHandlerFoundException("Test error message", "", mockk())
        val expectedMessage = messageSource.getMessage(ErrorMessages.NO_HANDLER_FOUND, null, locale)

        val response = exHandler.handleNoHandlerFoundException(exception, locale)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertThat(response.body?.messages).containsExactly(expectedMessage)
    }

    @Test
    fun `should handle Exception`() {
        val ex = Exception()
        val locale = Locale.ENGLISH
        val expectedMessage = messageSource.getMessage(ErrorMessages.INTERNAL, null, locale)

        val response = exHandler.handleException(ex, locale)

        assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(response.body?.messages).containsExactly(expectedMessage)
    }
}
