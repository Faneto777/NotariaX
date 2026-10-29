package com.example.notariax.data.repository

import com.example.notariax.data.local.UserDao
import com.example.notariax.data.local.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun saveUser(username: String, fullName: String) {
        val user = UserEntity(
            username = username,
            fullName = fullName,
            isLoggedIn = true
        )
        userDao.saveUser(user)
    }

    suspend fun getLoggedUser(): UserEntity? {
        return userDao.getLoggedUser()
    }

    suspend fun clearUser() {
        userDao.clearUser()
    }
}
