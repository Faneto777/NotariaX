package com.example.notariax.data.repository

import com.example.notariax.data.local.UserDao
import com.example.notariax.model.UserEntity

class RoomUserRepository(private val userDao: UserDao) : UserRepository {

    override suspend fun saveUser(username: String, fullName: String, email: String) {
        val user = UserEntity(
            username = username,
            fullName = fullName,
            email = email,
            isLoggedIn = true
        )
        userDao.saveUser(user)
    }

    override suspend fun getLoggedUser(): UserEntity? = userDao.getLoggedUser()

    override suspend fun clearUser() {
        userDao.clearUser()
    }
}
