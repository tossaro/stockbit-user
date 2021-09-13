package com.stockbit.mini.user.modules.splash

import androidx.navigation.Navigation
import com.stockbit.mini.corelib.base.StockbitViewModelFragment
import com.stockbit.mini.user.R
import com.stockbit.mini.user.databinding.SplashFragmentBinding
import com.stockbit.mini.userlib.repositories.cache.storage.entity.User

class SplashFragment: StockbitViewModelFragment<SplashFragmentBinding, SplashViewModel>(
    layoutResId = R.layout.splash_fragment,
    SplashViewModel::class
) {

    override fun isActionBarShown(): Boolean = false

    override fun initBinding(binding: SplashFragmentBinding, viewModel: SplashViewModel) {
        super.initBinding(binding, viewModel)
        binding.viewModel = viewModel.also {
            it.loadingIndicator.observe(this, ::loadingIndicator)
            it.alertMessage.observe(this, ::showAlert)
            it.user.observe(this, ::isSignedIn)
        }
    }

    private fun isSignedIn(user: User?) {
        user?.let {
            view?.let { v -> Navigation.findNavController(v).navigate(R.id.actionToStockListFragment) }
        } ?: run {
            view?.let { v -> Navigation.findNavController(v).navigate(R.id.actionToSignInFragment) }
        }
    }

}