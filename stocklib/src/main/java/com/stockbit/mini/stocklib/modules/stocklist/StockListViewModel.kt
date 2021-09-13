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
    var stocks = MutableLiveData<MutableList<Stock>>()
    var user = MutableLiveData<User>()
    var isFromLocal = true
    var limit = 50

    init {
        viewModelScope.launch {
            stocks.value = stockRepository.getStocksLocal(1, limit).toMutableList()
        }
    }

    fun getStocks(page: Int) {
        loadingIndicator.value = true
        viewModelScope.launch {
            when (val response = stockRepository.getStocks(limit, page)) {
                is NetworkResponse.Success -> {
                    response.body.data.let {
                        if (isFromLocal) stocks.value = mutableListOf()
                        val stocksTemp = stocks.value ?: mutableListOf()
                        it.forEach { coin ->
                            val mCoin = coin.coin_info
                            mCoin.price = coin.display?.USD?.PRICE ?: "Error"
                            mCoin.status = coin.display?.USD?.CHANGEPCTHOUR ?: "Error"
                            val m = coin.display?.USD?.MKTCAPPENALTY ?: "Error"
                            mCoin.status += " ($m)"
                            stocksTemp.add(mCoin)
                        }
                        stocks.value = stocksTemp
                        stockRepository.setStocksLocal(stocksTemp)
                        loadingIndicator.value = false
                    }
                }
                is NetworkResponse.ServerError -> {
                    loadingIndicator.value = false
                    alertMessage.value = response.body?.message.orEmpty()
                }
                is NetworkResponse.NetworkError -> {
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