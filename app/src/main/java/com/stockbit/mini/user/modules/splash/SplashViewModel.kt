package com.stockbit.mini.user.modules.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.stockbit.mini.corelib.base.StockbitViewModel
import com.stockbit.mini.userlib.repositories.UserRepository
import com.stockbit.mini.userlib.repositories.cache.storage.entity.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(private val userRepository: UserRepository) : StockbitViewModel() {
    var user = MutableLiveData<User>()

    init {
        viewModelScope.launch {
            delay(200)
            user.value = userRepository.getUserLocal()
        }
    }
}