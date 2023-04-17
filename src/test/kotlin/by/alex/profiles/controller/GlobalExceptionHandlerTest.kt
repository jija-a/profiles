package by.alex.profiles.controller

import by.alex.profiles.exception.DuplicateEntryException
import by.alex.profiles.exception.EmptyParameterException
import by.alex.profiles.exception.ErrorMessages
import by.alex.profiles.exception.NotFoundException

import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

import java.util.Locale

@ExtendWith(SpringExtension::class)
@SpringBootTest
class GlobalExceptionHandlerTest {

    @Autowired
    private lateinit var messageSource: MessageSource

    private lateinit var globalExceptionHandler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        globalExceptionHandler = GlobalExceptionHandler(messageSource)
    }

    @Test
    fun `should handle NotFoundException`() {
        val ex = NotFoundException(ErrorMessages.NOT_FOUND, arrayOf(1))
        val locale = Locale.ENGLISH
        val expectedMessage = messageSource.getMessage(ErrorMessages.NOT_FOUND, arrayOf(1), locale)

        val response = globalExceptionHandler.handleNotFoundException(ex, locale)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body?.message).isEqualTo(expectedMessage)
    }

    @Test
    fun `should handle DuplicateEntryException`() {
        val ex = DuplicateEntryException(ErrorMessages.DUPLICATE_EMAIL, arrayOf(1))
        val locale = Locale.ENGLISH
        val expectedMessage = messageSource.getMessage(ErrorMessages.DUPLICATE_EMAIL, arrayOf(1), locale)

        val response = globalExceptionHandler.handleDuplicateEntryException(ex, locale)

        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(response.body?.message).isEqualTo(expectedMessage)
    }

    @Test
    fun `should handle EmptyParameterException`() {
        val ex = EmptyParameterException(ErrorMessages.ALL_FIELDS_REQUIRED)
        val locale = Locale.ENGLISH
        val expectedMessage = messageSource.getMessage(ErrorMessages.ALL_FIELDS_REQUIRED, null, locale)

        val response = globalExceptionHandler.handleEmptyParameterException(ex, locale)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.message).isEqualTo(expectedMessage)
    }

    @Test
    fun `should handle Exception`() {
        val ex = Exception()
        val locale = Locale.ENGLISH
        val expectedMessage = messageSource.getMessage(ErrorMessages.INTERNAL, null, locale)

        val response = globalExceptionHandler.handleException(ex, locale)

        assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(response.body?.message).isEqualTo(expectedMessage)
    }
}
