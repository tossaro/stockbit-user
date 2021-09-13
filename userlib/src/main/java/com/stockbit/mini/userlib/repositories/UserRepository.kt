package com.stockbit.mini.userlib.repositories

import android.content.Context
import com.stockbit.mini.corelib.repositories.StockbitRepository
import com.stockbit.mini.userlib.repositories.cache.storage.dao.UserDao
import com.stockbit.mini.userlib.repositories.cache.storage.entity.User
import com.stockbit.mini.userlib.repositories.remote.UserServiceV1
import com.stockbit.mini.userlib.repositories.remote.request.SignInRequest
import com.stockbit.mini.userlib.repositories.remote.request.SignInWithProviderRequest
import com.stockbit.mini.userlib.tools.utils.DeviceUtil

class UserRepository(
    val context: Context,
    private val userServiceV1: UserServiceV1,
    private val userDao: UserDao
) : StockbitRepository(context) {

    suspend fun signInRequest(request: SignInRequest) =
        userServiceV1.signIn(request.copy(uuid = DeviceUtil(context).getDeviceId()))

    suspend fun signInWithProviderRequest(request: SignInWithProviderRequest) =
        userServiceV1.signInWithProvider(request.copy(uuid = DeviceUtil(context).getDeviceId()))

    suspend fun getUserLocal(): User = userDao.load()
    suspend fun setUserLocal(user: User): Long = userDao.save(user)
    suspend fun deleteAllUserLocal() = userDao.deleteAll()
}