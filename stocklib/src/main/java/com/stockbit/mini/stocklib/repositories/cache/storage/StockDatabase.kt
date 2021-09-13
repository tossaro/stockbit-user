package com.stockbit.mini.stocklib.repositories.cache.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stockbit.mini.stocklib.repositories.cache.storage.dao.StockDao
import com.stockbit.mini.stocklib.repositories.cache.storage.entity.Stock

@Database(entities = [Stock::class], version = 1)
abstract class StockDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao
}