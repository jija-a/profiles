package by.alex.profiles.exception

class NotFoundException(
    val messageCode: String,
    val args: Array<Any>
) : RuntimeException()
