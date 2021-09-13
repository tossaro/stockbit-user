package com.stockbit.mini.user.modules.splash

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.stockbit.mini.corelib.base.StockbitActivity
import com.stockbit.mini.user.R
import kotlinx.android.synthetic.main.splash_activity.*

class SplashActivity : StockbitActivity() {

    override fun getContentResource(): Int = R.layout.splash_activity

    override fun navHostFragment(): Fragment = contentFragment

    override fun getNavGraphResource(): Int = R.navigation.navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null
            && intent.action == Intent.ACTION_MAIN
        ) finish()
    }

}