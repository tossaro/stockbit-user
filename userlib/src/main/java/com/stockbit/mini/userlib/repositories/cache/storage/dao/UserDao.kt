package com.stockbit.mini.userlib.repositories.cache.storage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.stockbit.mini.userlib.repositories.cache.storage.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun load(): User

    @Insert(onConflict = REPLACE)
    suspend fun save(user: User): Long

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}