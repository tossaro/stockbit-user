package com.stockbit.mini.user.tools.modules

import com.stockbit.mini.stocklib.repositories.StockRepository
import com.stockbit.mini.userlib.repositories.UserRepository
import org.koin.dsl.module

val RepositoryModule = module {
    single { UserRepository(get(), get(), get()) }
    single { StockRepository(get(), get(), get()) }
}