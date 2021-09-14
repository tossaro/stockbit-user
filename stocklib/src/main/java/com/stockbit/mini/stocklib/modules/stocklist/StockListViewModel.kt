package com.stockbit.mini.stocklib.modules.stocklist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.stockbit.mini.corelib.base.StockbitViewModel
import com.stockbit.mini.stocklib.repositories.StockRepository
import com.stockbit.mini.stocklib.repositories.cache.storage.entity.Stock
import com.stockbit.mini.userlib.repositories.UserRepository
import com.stockbit.mini.userlib.repositories.cache.storage.entity.User
import kotlinx.coroutines.launch

class StockListViewModel(
    private val userRepository: UserRepository,
    private val stockRepository: StockRepository
) : StockbitViewModel() {
    var stocks = MutableLiveData<MutableList<Stock>?>()
    var user = MutableLiveData<User>()
    var isFromLocal = true
    var limit = 50

    init {
        loadingIndicator.value = true
    }

    fun getStocks(page: Int) {
        if (isFromLocal) getStocksLocal(page)
        else getStocksRemote(page)
    }

    fun getStocksLocal(page: Int) {
        viewModelScope.launch {
            val stocksTemp = mutableListOf<Stock>()
            val stocksLocal = stockRepository.getStocksLocal(limit * page, limit).toMutableList()
            stocksLocal.forEach { coin -> stocksTemp.add(coin) }
            stocks.value = stocksTemp
            loadingIndicator.value = false
        }
    }

    fun getStocksRemote(page: Int) {
        viewModelScope.launch {
            loadingIndicator.value = true
            when (val response = stockRepository.getStocks(limit, page)) {
                is NetworkResponse.Success -> {
                    response.body.data.let {
                        if (isFromLocal) {
                            stocks.value = null
                            isFromLocal = false
                        }
                        val stocksTemp = mutableListOf<Stock>()
                        it.forEach { coin ->
                            val mCoin = coin.coin_info
                            mCoin.price = String.format("%.5f", coin.raw?.USD?.TOPTIERVOLUME24HOUR)
                            mCoin.status = String.format("%.2f", coin.raw?.USD?.CHANGE24HOUR)
                            val m = String.format("%.2f", coin.raw?.USD?.CHANGEPCTHOUR)
                            mCoin.status += " ($m%)"
                            stocksTemp.add(mCoin)
                        }
                        stocks.value = stocksTemp
                        stockRepository.setStocksLocal(stocksTemp)
                        loadingIndicator.value = false
                    }
                }
                is NetworkResponse.ServerError -> {
                    isFromLocal = true
                    stocks.value = null
                    getStocksLocal(1)
                    loadingIndicator.value = false
                    alertMessage.value = response.body?.message.orEmpty()
                }
                is NetworkResponse.NetworkError -> {
                    isFromLocal = true
                    stocks.value = null
                    getStocksLocal(1)
                    loadingIndicator.value = false
                    alertMessage.value = response.error.message.orEmpty()
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userRepository.deleteAllUserLocal()
            user.value = userRepository.getUserLocal()
        }
    }
}