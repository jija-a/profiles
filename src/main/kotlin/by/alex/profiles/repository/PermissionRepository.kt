package by.alex.profiles.repository

import by.alex.profiles.model.Permission
import org.springframework.data.repository.CrudRepository
import java.util.*

interface PermissionRepository : CrudRepository<Permission, Long> {

    fun findByName(name: String): Optional<Permission>
}
