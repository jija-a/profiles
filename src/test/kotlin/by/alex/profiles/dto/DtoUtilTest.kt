package by.alex.profiles.dto

import by.alex.profiles.dto.DtoUtil.toDto
import by.alex.profiles.dto.DtoUtil.toUser
import by.alex.profiles.testutill.TestUser
import by.alex.profiles.testutill.TestUserUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DtoUtilTest {

    @Test
    fun `should return correct dto object`() {
        val user = TestUser().build()
        val expected = TestUserUtil.toDto(user)

        val actual = user.toDto()

        assertThat(actual).hasNoNullFieldsOrProperties().isEqualTo(expected)
    }

    @Test
    fun `should return correct user object`() {
        val expected = TestUser().build()
        val createRequest = TestUserUtil.buildCreateRequest()

        val actual = createRequest.toUser()

        assertThat(actual.email).isEqualTo(expected.email)
        assertThat(actual.firstName).isEqualTo(expected.firstName)
        assertThat(actual.lastName).isEqualTo(expected.lastName)
        assertThat(actual.password).isEqualTo(expected.password)
    }
}