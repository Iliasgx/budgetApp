package com.umbrella.budgetapp.cache

import android.app.Application
import android.content.Context

class App: Application() {

    companion object {
        var app: Context? = null
        private set
    }

    override fun onCreate() {
        super.onCreate()
        app = applicationContext
    }
}