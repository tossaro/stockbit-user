package com.stockbit.mini.stocklib.repositories.cache.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stockbit.mini.stocklib.repositories.cache.storage.entity.Stock

@Dao
interface StockDao {
    @Query("SELECT * FROM stock LIMIT :limit OFFSET :offset")
    suspend fun load(offset: Int, limit: Int): List<Stock>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(stock: List<Stock>)
}