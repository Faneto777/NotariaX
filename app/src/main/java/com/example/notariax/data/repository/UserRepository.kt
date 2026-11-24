package com.example.notariax.data.repository

import com.example.notariax.model.UserEntity

interface UserRepository {
    suspend fun saveUser(username: String, fullName: String)

    suspend fun getLoggedUser(): UserEntity?

    suspend fun clearUser()
}
