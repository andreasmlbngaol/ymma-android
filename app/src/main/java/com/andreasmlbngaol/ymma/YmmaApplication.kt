package com.andreasmlbngaol.ymma

import android.app.Application
import com.andreasmlbngaol.ymma.di.initKoin
import org.koin.android.ext.koin.androidContext

class YmmaApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@YmmaApplication)
        }
    }
}