package by.alex.profiles.exception

class DuplicateEntryException(
    val messageCode: String,
    val args: Array<Any>
) : RuntimeException()

