package by.alex.profiles.model

import java.io.Serializable

enum class Role(val permissions: Set<Permission>) : Serializable {
    USER(setOf(Permission.READ_USER)),
    ADMIN(
        setOf(
            Permission.CREATE_USER,
            Permission.READ_USER,
            Permission.UPDATE_USER,
            Permission.DELETE_USER
        )
    )
}
