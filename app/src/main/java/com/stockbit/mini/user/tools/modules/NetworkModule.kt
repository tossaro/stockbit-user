package com.stockbit.mini.user.tools.modules

import com.stockbit.mini.userlib.BuildConfig as UserBuild
import com.stockbit.mini.stocklib.BuildConfig as StockBuild
import com.stockbit.mini.userlib.repositories.remote.UserServiceV1
import com.stockbit.mini.corelib.tools.utils.NetworkUtil
import com.stockbit.mini.stocklib.repositories.remote.StockServiceV1
import org.koin.dsl.module

val NetworkModule = module {
    single { NetworkUtil.buildClient(get()) }
    single { NetworkUtil.buildService<UserServiceV1>(UserBuild.SERVER_V1, get()) }
    single { NetworkUtil.buildService<StockServiceV1>(StockBuild.SERVER_V1, get()) }
}