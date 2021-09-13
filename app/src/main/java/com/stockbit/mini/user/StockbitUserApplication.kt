package com.stockbit.mini.user

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.facebook.stetho.Stetho
import com.stockbit.mini.user.tools.modules.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger
import org.koin.core.module.Module
import timber.log.Timber
import java.util.*

open class StockbitUserApplication: Application() {
    var listOfModules = mutableListOf<Module>()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this);
    }

    override fun onCreate() {
        super.onCreate()
        initDebugTools()
        startKoin {
            if (BuildConfig.DEBUG) androidLogger() else EmptyLogger()
            androidContext(applicationContext)
            initModules()
            modules(listOfModules)
        }
        setLocale()
    }

    private fun initModules() {
        listOfModules.add(HelperModule)
        listOfModules.add(DatabaseModule)
        listOfModules.add(NetworkModule)
        listOfModules.add(RepositoryModule)
        listOfModules.add(ViewModelModule)
    }

    private fun initDebugTools() {
        Timber.plant(Timber.DebugTree())
        Stetho.initializeWithDefaults(this)
    }

    private fun setLocale() {
        val locale = Locale("id")
        Locale.setDefault(locale)
        val config = resources.configuration
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}