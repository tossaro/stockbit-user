package com.stockbit.mini.user.tools.modules

import com.stockbit.mini.stocklib.modules.stocklist.StockListViewModel
import com.stockbit.mini.user.modules.splash.SplashViewModel
import com.stockbit.mini.userlib.modules.signin.SignInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { SignInViewModel(get()) }
    viewModel { StockListViewModel(get(), get()) }
}