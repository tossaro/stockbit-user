package com.stockbit.mini.user.tools.modules

import android.app.Application
import androidx.room.Room
import com.stockbit.mini.stocklib.repositories.cache.storage.StockDatabase
import com.stockbit.mini.stocklib.repositories.cache.storage.dao.StockDao
import com.stockbit.mini.userlib.repositories.cache.storage.UserDatabase
import com.stockbit.mini.userlib.repositories.cache.storage.dao.UserDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val DatabaseModule = module {
    fun provideUserDb(application: Application): UserDatabase {
        return Room.databaseBuilder(application, UserDatabase::class.java, "users")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideUserDao(database: UserDatabase): UserDao {
        return  database.userDao()
    }

    single { provideUserDb(androidApplication()) }
    single { provideUserDao(get()) }

    fun provideStockDb(application: Application): StockDatabase {
        return Room.databaseBuilder(application, StockDatabase::class.java, "stocks")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideStockDao(database: StockDatabase): StockDao {
        return  database.stockDao()
    }

    single { provideStockDb(androidApplication()) }
    single { provideStockDao(get()) }
}