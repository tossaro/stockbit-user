package com.stockbit.mini.stocklib.repositories

import android.content.Context
import com.stockbit.mini.corelib.repositories.StockbitRepository
import com.stockbit.mini.stocklib.repositories.cache.storage.dao.StockDao
import com.stockbit.mini.stocklib.repositories.cache.storage.entity.Stock
import com.stockbit.mini.stocklib.repositories.remote.StockServiceV1

class StockRepository(
    val context: Context,
    private val stockServiceV1: StockServiceV1,
    private val stockDao: StockDao
) : StockbitRepository(context) {

    suspend fun getStocks(limit: Int, page: Int) = stockServiceV1.stocks(limit, page, "USD")
    suspend fun getStocksLocal(offset: Int, limit: Int): List<Stock> = stockDao.load(offset, limit)
    suspend fun setStocksLocal(stocks: MutableList<Stock>) = stockDao.save(stocks)
}