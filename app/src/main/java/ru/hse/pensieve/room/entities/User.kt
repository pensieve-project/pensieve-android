package ru.hse.pensieve.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: UUID,
    var username: String,
    var description: String?,
    var avatar: ByteArray?
)