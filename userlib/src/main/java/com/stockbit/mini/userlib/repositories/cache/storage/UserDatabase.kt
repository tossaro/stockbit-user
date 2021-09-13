package com.stockbit.mini.userlib.repositories.cache.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stockbit.mini.userlib.repositories.cache.storage.dao.UserDao
import com.stockbit.mini.userlib.repositories.cache.storage.entity.User

@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}