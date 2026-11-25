package com.example.notariax.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val fullName: String,
    @ColumnInfo(defaultValue = "''")
    val email: String = "",
    val isLoggedIn: Boolean
)
