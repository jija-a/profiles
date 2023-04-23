package by.alex.profiles.model

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "permissions")
data class Permission(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false, unique = true)
    var name: String,
) : Serializable
