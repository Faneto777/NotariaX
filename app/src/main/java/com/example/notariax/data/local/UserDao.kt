package com.example.notariax.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserEntity)

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getLoggedUser(): UserEntity?

    @Query("DELETE FROM users")
    suspend fun clearUser()
}
